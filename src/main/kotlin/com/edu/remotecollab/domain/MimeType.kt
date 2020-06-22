package com.edu.remotecollab.domain

enum class MimeType(private val value: String, private val fileExtensions: List<String>) {
    TXT("text/plain",
            listOf("txt")),
    BMP("image/bmp",
            listOf("bmp")),
    GIF("image/gif",
            listOf("gif")),
    JPEG("image/jpeg",
            listOf("jpg", "jpeg")),
    TIFF("image/tiff",
            listOf("tif", "tiff")),
    PDF("application/pdf",
            listOf("pdf")),
    PPT("application/vnd.ms-powerpoint",
            listOf("ppt")),
    PPTX("application/vnd.openxmlformats-officedocument.presentationml.presentation",
            listOf("pptx")),
    XLS("application/vnd.ms-excel",
            listOf("xls")),
    XLSM("application/vnd.ms-excel.sheet.macroEnabled.12",
            listOf("xlsm")),
    XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            listOf("xlsx")),
    DOC("application/msword",
            listOf("doc")),
    DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            listOf("docx")),
    DAT("zz-application/zz-winassoc-dat",
        listOf("dat")),
    TSV("text/tab-separated-values",
        listOf("tsv")),
    XML("application/xml",
        listOf("xml"));

    companion object {
        fun findValueByFileExtension(fileExtension: String): String =
                MimeType.values().find { it.fileExtensions.contains(fileExtension) }?.value ?: ""
    }

}