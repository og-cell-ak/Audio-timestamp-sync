package com.futurethinking.scripttimestampsync
import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.futurethinking.scripttimestampsync.data.PdfRepository
import com.futurethinking.scripttimestampsync.model.TimestampedLine
import com.futurethinking.scripttimestampsync.network.WhisperService
import com.futurethinking.scripttimestampsync.util.AlignmentEngine
import com.futurethinking.scripttimestampsync.util.Dictionary
import com.futurethinking.scripttimestampsync.util.Normalizer
import com.futurethinking.scripttimestampsync.util.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
class ScriptViewModel(app:Application):AndroidViewModel(app){
    private val prefs=app.getSharedPreferences("settings",0);private val dictionary by lazy{Dictionary(app)}
    private val _pdf=MutableStateFlow<Uri?>(null);val pdf=_pdf.asStateFlow();private val _audio=MutableStateFlow<Uri?>(null);val audio=_audio.asStateFlow()
    private val _lines=MutableStateFlow<List<TimestampedLine>>(emptyList());val lines=_lines.asStateFlow();private val _progress=MutableStateFlow(0f);val progress=_progress.asStateFlow()
    private val _busy=MutableStateFlow(false);val busy=_busy.asStateFlow();private val _error=MutableStateFlow<String?>(null);val error=_error.asStateFlow()
    val apiKey get()=prefs.getString("api_key","").orEmpty();val millis get()=prefs.getBoolean("millis",true)
    fun setApiKey(v:String){prefs.edit().putString("api_key",v).apply()};fun setMillis(v:Boolean){prefs.edit().putBoolean("millis",v).apply()}
    fun setPdf(u:Uri){_pdf.value=u;_error.value=null};fun setAudio(u:Uri){_audio.value=u;_error.value=null};fun clearError(){_error.value=null}
    fun process(){
        val p=pdf.value;val a=audio.value;val key=apiKey
        if(p==null||a==null){_error.value="PDF aur audio dono select karo.";return}
        if(key.isBlank()){_error.value="Settings me OpenAI API key enter karo.";return}
        viewModelScope.launch{
            _busy.value=true;_progress.value=.05f;_error.value=null
            try{
                val cleaned=PdfRepository(getApplication()).extract(p).map{Normalizer.cleanLine(it)}.filter{it.isNotBlank()}
                _progress.value=.25f
                val words=WhisperService(getApplication()).transcribe(a,key)
                _progress.value=.65f
                _lines.value=AlignmentEngine(dictionary).align(cleaned,words);_progress.value=1f
            }catch(e:Exception){_error.value=e.message?:"Processing failed."}finally{_busy.value=false}
        }
    }
    fun txt()=_lines.value.joinToString("\n"){"${it.text} [${Timestamp.format(it.startMs,millis)}]"}
    fun srt()=_lines.value.mapIndexed{i,l->"${i+1}\n${Timestamp.format(l.startMs,false).replace('.',',')} --> ${Timestamp.format(l.endMs,false).replace('.',',')}\n${l.text}\n"}.joinToString("\n")
    fun csv()="Line,Start,End,Confidence,Needs Review\n"+_lines.value.mapIndexed{i,l->"${i+1},${Timestamp.format(l.startMs,millis)},${Timestamp.format(l.endMs,millis)},${"%.3f".format(l.confidence)},${l.needsReview}"}.joinToString("\n")
}