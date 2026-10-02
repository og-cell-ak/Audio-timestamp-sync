package com.futurethinking.scripttimestampsync.util
import com.futurethinking.scripttimestampsync.model.TranscriptWord
import com.futurethinking.scripttimestampsync.model.TimestampedLine
import kotlin.math.max
import kotlin.math.min
class AlignmentEngine(private val dictionary:Dictionary) {
    private fun sim(a:String,b:String):Float {
        if(a==b)return 1f
        if(dictionary.variants(a).any{it==b}||dictionary.variants(b).any{it==a})return .92f
        if(a.length>=4&&b.length>=4&&(a.contains(b)||b.contains(a)))return .78f
        val n=a.length
        val m=b.length
        if(max(n,m)>18)return 0f
        val dp=IntArray(m+1){it}
        for(i in 1..n){var prev=dp[0];dp[0]=i;for(j in 1..m){val old=dp[j];dp[j]=min(min(dp[j]+1,dp[j-1]+1),prev+if(a[i-1]==b[j-1])0 else 1);prev=old}}
        return 1f-dp[m].toFloat()/max(n,m)
    }
    fun align(lines:List<String>,words:List<TranscriptWord>):List<TimestampedLine>{
        if(words.isEmpty())return lines.mapIndexed{i,l->TimestampedLine(l,i*1000L,(i+1)*1000L,0f,true)}
        var cursor=0;val out=mutableListOf<TimestampedLine>();val avg=((words.last().endMs-words.first().startMs).coerceAtLeast(1000L)/max(1,lines.size))
        for((idx,line)in lines.withIndex()){
            val target=Normalizer.tokens(line)
            if(target.isEmpty()){out+=TimestampedLine(line,0,0,0f,true);continue}
            var bs=cursor;var be=min(words.lastIndex,cursor+target.size+8);var scoreBest=-1f;val maxStart=min(words.lastIndex,cursor+80)
            for(st in cursor..maxStart){val maxLen=min(words.size-st,max(2,target.size+10));for(len in max(1,target.size-3)..maxLen){val en=st+len-1;if(en>=words.size)break;var sc=0f;for(k in target.indices){val wi=st+min(len-1,(k.toFloat()/target.size*len).toInt());sc+=sim(target[k],Normalizer.tokens(words[wi].word).firstOrNull()?:"")};sc/=target.size;if(sc>scoreBest){scoreBest=sc;bs=st;be=en}}}
            val review=scoreBest<.48f;val start=if(review&&idx>0)out.last().endMs else words[bs].startMs;val end=if(review&&idx>0)(start+avg).coerceAtMost(words.last().endMs)else words[be].endMs
            out+=TimestampedLine(line,start,end,scoreBest.coerceIn(0f,1f),review);cursor=min(words.size,max(cursor+1,be+1))
        }
        return out
    }
}