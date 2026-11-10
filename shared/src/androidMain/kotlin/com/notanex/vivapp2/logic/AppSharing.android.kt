package com.notanex.vivapp2.logic

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import com.notanex.vivapp2.MyScanApplication

actual fun shareText(text: String, subject: String, chooserTitle: String) {
    val context = MyScanApplication.INSTANCE
    val send = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, text)
    }
    val chooser = Intent.createChooser(send, chooserTitle).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(chooser)
}

actual val canOpenStoreListing: Boolean = true

actual fun openStoreListing(): Boolean {
    val context = MyScanApplication.INSTANCE
    val market = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${context.packageName}"))
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    return try {
        context.startActivity(market)
        true
    } catch (_: ActivityNotFoundException) {
        // No Play Store app installed: fall back to the browser.
        try {
            context.startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse(AppLinks.PLAY_STORE_URL))
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
            true
        } catch (_: ActivityNotFoundException) {
            false
        }
    }
}
