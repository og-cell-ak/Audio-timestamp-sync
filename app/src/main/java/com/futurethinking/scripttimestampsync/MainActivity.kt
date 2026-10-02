package com.futurethinking.scripttimestampsync
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.io.OutputStreamWriter

class MainActivity:ComponentActivity(){
    private val vm by viewModels<ScriptViewModel>()
    override fun onCreate(savedInstanceState:Bundle?){super.onCreate(savedInstanceState);setContent{App(vm)}}
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable fun App(vm:ScriptViewModel){
    var screen by remember{mutableStateOf("home")};var exportMode by remember{mutableStateOf("txt")}
    val pdf by vm.pdf.collectAsState();val audio by vm.audio.collectAsState();val lines by vm.lines.collectAsState();val busy by vm.busy.collectAsState();val progress by vm.progress.collectAsState();val error by vm.error.collectAsState()
    val context=LocalContext.current
    val pdfPicker=rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()){it?.let(vm::setPdf)}
    val audioPicker=rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()){it?.let(vm::setAudio)}
    val export=rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/plain")){uri->if(uri!=null)context.contentResolver.openOutputStream(uri)?.use{out->OutputStreamWriter(out).use{w->w.write(when(exportMode){"srt"->vm.srt();"csv"->vm.csv();else->vm.txt()})}}}
    Scaffold(topBar={TopAppBar(title={Text("Script Timestamp Sync")},actions={TextButton(onClick={if(!busy)screen="settings"}){Text("Settings")}})}){pad->
        Column(Modifier.padding(pad).padding(16.dp).fillMaxSize(),verticalArrangement=Arrangement.spacedBy(12.dp)){
            if(screen=="settings")SettingsScreen(vm){screen="home"}
            else if(lines.isNotEmpty())ResultScreen(vm,lines,{mode,name->exportMode=mode;export.launch(name)}){screen="home"}
            else HomeScreen(pdf,audio,busy,progress,{pdfPicker.launch(arrayOf("application/pdf"))},{audioPicker.launch(arrayOf("audio/mpeg","audio/wav","audio/x-wav","audio/mp4","audio/*"))}){vm.process()}
            if(error!=null){Text(error!!,color=MaterialTheme.colorScheme.error);TextButton(onClick=vm::clearError){Text("Dismiss")}}
        }
    }
}
@Composable private fun HomeScreen(pdf:android.net.Uri?,audio:android.net.Uri?,busy:Boolean,progress:Float,pdfPicker:()->Unit,audioPicker:()->Unit,process:()->Unit){
    Text("PDF script + audio ko automatically timestamp sync karo.",style=MaterialTheme.typography.titleMedium)
    Button(onClick=pdfPicker,enabled=!busy,modifier=Modifier.fillMaxWidth()){Text(if(pdf==null)"Upload PDF Script" else "PDF Selected")}
    Button(onClick=audioPicker,enabled=!busy,modifier=Modifier.fillMaxWidth()){Text(if(audio==null)"Upload Audio (MP3/WAV/M4A)" else "Audio Selected")}
    Button(onClick=process,enabled=!busy&&pdf!=null&&audio!=null,modifier=Modifier.fillMaxWidth()){Text("Process")}
    if(busy){LinearProgressIndicator(progress={progress},modifier=Modifier.fillMaxWidth());Text("Processing ${(progress*100).toInt()}%")}
}
@Composable private fun ResultScreen(vm:ScriptViewModel,lines:List<com.futurethinking.scripttimestampsync.model.TimestampedLine>,export:(String,String)->Unit,onBack:()->Unit){
    Column(Modifier.fillMaxSize(),verticalArrangement=Arrangement.spacedBy(8.dp)){
    Text("Result",style=MaterialTheme.typography.headlineSmall)
    Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){Button(onClick={export("txt","script_timestamp_sync.txt")}){Text("TXT")};Button(onClick={export("srt","script_timestamp_sync.srt")}){Text("SRT")};Button(onClick={export("csv","script_timestamp_sync.csv")}){Text("CSV")}}
    LazyColumn(Modifier.weight(1f)){itemsIndexed(lines){i,l->Card(Modifier.fillMaxWidth().padding(vertical=4.dp)){Column(Modifier.padding(12.dp)){Text("${i+1}. ${l.text}");Text("[${com.futurethinking.scripttimestampsync.util.Timestamp.format(l.startMs,vm.millis)}]  confidence ${"%.0f".format(l.confidence*100)}%");if(l.needsReview)Text("Needs Review",color=MaterialTheme.colorScheme.error)}}}}
    TextButton(onClick=onBack){Text("Back")}
    }
}
@Composable private fun SettingsScreen(vm:ScriptViewModel,onBack:()->Unit){
    var key by remember{mutableStateOf(vm.apiKey)};var millis by remember{mutableStateOf(vm.millis)};var mode by remember{mutableStateOf(vm.languageMode)}
    Text("Settings",style=MaterialTheme.typography.headlineSmall)
    OutlinedTextField(key,{key=it},label={Text("OpenAI API key")},modifier=Modifier.fillMaxWidth(),singleLine=true)
    Text("Language mode")
    Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){
        FilterChip(selected=mode=="auto",onClick={mode="auto"},label={Text("Auto")})
        FilterChip(selected=mode=="hi",onClick={mode="hi"},label={Text("Hindi")})
        FilterChip(selected=mode=="en",onClick={mode="en"},label={Text("English")})
    }
    Row{Text("Timestamp milliseconds");Spacer(Modifier.weight(1f));Switch(millis,{millis=it})}
    Button(onClick={vm.setApiKey(key);vm.setMillis(millis);vm.setLanguageMode(mode);onBack()},modifier=Modifier.fillMaxWidth()){Text("Save")}
}