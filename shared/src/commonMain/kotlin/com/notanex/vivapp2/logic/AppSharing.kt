package com.notanex.vivapp2.logic

/** Public store links used by the "Share Vivapp" and "Rate & feedback" menu items. */
object AppLinks {
    const val PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=com.notanex.vivapp2"
}

/** Opens the platform share sheet with plain text (messaging apps, e-mail, social, …). */
expect fun shareText(text: String, subject: String, chooserTitle: String)

/**
 * Opens the app's own store page so the user can rate it and leave feedback.
 * Returns false when the platform has no store page to open yet.
 */
expect fun openStoreListing(): Boolean

/** Whether [openStoreListing] can do anything on this platform. */
expect val canOpenStoreListing: Boolean
