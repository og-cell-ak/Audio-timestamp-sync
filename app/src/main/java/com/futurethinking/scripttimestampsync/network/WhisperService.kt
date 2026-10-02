package com.futurethinking.scripttimestampsync.network
import android.content.Context
import android.net.Uri
import com.futurethinking.scripttimestampsync.model.TranscriptWord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
class WhisperService(private val context:Context){
    private val client=OkHttpClient()
    suspend fun transcribe(uri:Uri,apiKey:String):List<TranscriptWord>=withContext(Dispatchers.IO){
        val temp=File.createTempFile("audio_",".bin",context.cacheDir)
        context.contentResolver.openInputStream(uri).use{input->requireNotNull(input).copyTo(temp.outputStream())}
        try{
            val body=MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file",temp.name,temp.asRequestBody("application/octet-stream".toMediaType()))
                .addFormDataPart("model","whisper-1").addFormDataPart("response_format","verbose_json")
                .addFormDataPart("timestamp_granularities[]","word").build()
            val req=Request.Builder().url("https://api.openai.com/v1/audio/transcriptions").header("Authorization","Bearer ${apiKey.trim()}").post(body).build()
            client.newCall(req).execute().use{r->
                val raw=r.body?.string().orEmpty()
                if(!r.isSuccessful)error("Whisper error ${r.code}: ${JSONObject(raw).optString("message",raw.take(300))}")
                val arr=JSONObject(raw).optJSONArray("words")?:error("Whisper ne word timestamps nahi diye.")
                buildList{for(i in 0 until arr.length()){val o=arr.getJSONObject(i);add(TranscriptWord(o.optString("word"),(o.optDouble("start")*1000).toLong(),(o.optDouble("end")*1000).toLong()))}}
            }
        }finally{temp.delete()}
    }
}