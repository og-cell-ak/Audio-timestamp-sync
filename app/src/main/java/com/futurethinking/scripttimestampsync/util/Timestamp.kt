package com.futurethinking.scripttimestampsync.util
object Timestamp {
    fun format(ms: Long, millis: Boolean = true): String {
        val safe=ms.coerceAtLeast(0); val h=safe/3600000; val m=(safe%3600000)/60000; val s=(safe%60000)/1000; val x=safe%1000
        return if(millis) "%02d:%02d:%02d.%03d".format(h,m,s,x) else "%02d:%02d:%02d".format(h,m,s)
    }
}