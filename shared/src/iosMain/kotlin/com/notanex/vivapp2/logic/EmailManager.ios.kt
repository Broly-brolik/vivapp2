package com.notanex.vivapp2.logic

actual fun sendEmailWithAttachment(subject: String, body: String, attachment: EmailAttachment) {
    println("DEBUG: iOS Email sending not implemented yet. Content: ${attachment.content}")
}