package com.futurethinking.scripttimestampsync.data
import android.content.Context
import android.net.Uri
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
class PdfRepository(private val context:Context){
    fun extract(uri:Uri):List<String>{
        PDFBoxResourceLoader.init(context)
        context.contentResolver.openInputStream(uri).use{input->requireNotNull(input){"PDF open nahi hua."};PDDocument.load(input).use{doc->return PDFTextStripper().getText(doc).lines().map{it.trim()}.filter{it.isNotBlank()}}}
    }
}