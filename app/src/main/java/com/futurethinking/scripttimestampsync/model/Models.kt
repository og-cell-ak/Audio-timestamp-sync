package com.futurethinking.scripttimestampsync.model
data class TranscriptWord(val word: String, val startMs: Long, val endMs: Long)
data class TimestampedLine(val text: String, val startMs: Long, val endMs: Long, val confidence: Float, val needsReview: Boolean)
data class AppFiles(val pdfUri: android.net.Uri? = null, val audioUri: android.net.Uri? = null)