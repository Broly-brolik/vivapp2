package com.notanex.vivapp2.logic

import android.content.Intent
import androidx.core.content.FileProvider
import com.notanex.vivapp2.MyScanApplication
import java.io.File

actual fun sendEmailWithAttachment(
    subject: String,
    body: String,
    attachment: EmailAttachment
) {
    val context = MyScanApplication.INSTANCE

    val file = File(context.cacheDir, attachment.fileName)
    file.writeText(attachment.content)

    val authority = "${context.packageName}.fileprovider"
    val uri = FileProvider.getUriForFile(context, authority, file)

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = attachment.mimeType
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, body)
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // Required when using Application context
    }

    val chooser = Intent.createChooser(intent, "Send email...")
    chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(chooser)
}