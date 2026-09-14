package com.notanex.vivapp2.logic

data class EmailAttachment(
    val fileName: String,
    val content: String,
    val mimeType: String = "text/csv"
)

expect fun sendEmailWithAttachment(
    subject: String,
    body: String,
    attachment: EmailAttachment
)