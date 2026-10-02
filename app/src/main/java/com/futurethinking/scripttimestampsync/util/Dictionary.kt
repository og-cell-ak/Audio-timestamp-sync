package com.futurethinking.scripttimestampsync.util
import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.Locale
class Dictionary(context: Context) {
    private val words=HashSet<String>(140_000)
    init { context.assets.open("hindi_english_dict.txt").use { input -> BufferedReader(InputStreamReader(input,Charsets.UTF_8)).useLines { it.forEach { w -> val x=w.trim().lowercase(Locale.ROOT); if(x.isNotEmpty()) words.add(x) } } } }
    fun contains(word:String)=words.contains(word.lowercase(Locale.ROOT))
    fun variants(word:String): Sequence<String> = sequence {
        val w=word.lowercase(Locale.ROOT); if(contains(w)) yield(w)
        val v=w.replace("aa","a").replace("ee","i").replace("oo","u").replace("q","k").replace("x","ks"); if(v!=w && contains(v)) yield(v)
        mapOf("kya" to "kia","hai" to "he","nahi" to "nahin","acha" to "achha","kaise" to "kese","main" to "mein")[w]?.let { if(contains(it)) yield(it) }
    }
}