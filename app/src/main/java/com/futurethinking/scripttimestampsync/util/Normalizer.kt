package com.futurethinking.scripttimestampsync.util
object Normalizer {
    fun cleanLine(raw: String): String {
        var s = raw.replace(Regex("^\\s*\\d+[.)、:-]?\\s*"), "")
        s = s.replace(Regex("^\\s*[IVXLCDMivxlcdm]{1,7}[.)、:-]\\s*"), "")
        s = s.replace(Regex("^\\s*[•●▪*-]\\s*"), "")
        return s.trim()
    }
    fun tokens(text: String): List<String> = text.lowercase()
        .replace(Regex("[^\\p{L}\\p{N}\\s]"), " ")
        .split(Regex("\\s+")).filter { it.isNotBlank() }
}