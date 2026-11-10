package com.notanex.vivapp2.logic

import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.*
import platform.Foundation.*
import platform.CoreGraphics.*

@OptIn(ExperimentalForeignApi::class)
actual fun shareText(text: String, subject: String, chooserTitle: String) {
    val controller = UIActivityViewController(activityItems = listOf(text), applicationActivities = null)
    controller.setValue(subject, "subject")
    val root = UIApplication.sharedApplication.keyWindow?.rootViewController ?: return
    if (UIDevice.currentDevice.userInterfaceIdiom == UIUserInterfaceIdiomPad) {
        controller.popoverPresentationController?.sourceView = root.view
        controller.popoverPresentationController?.sourceRect = CGRectMake(0.0, 0.0, 1.0, 1.0)
    }
    root.presentViewController(controller, animated = true, completion = null)
}

// No App Store ID yet; enable once the iOS app is live.
actual val canOpenStoreListing: Boolean = false

actual fun openStoreListing(): Boolean = false
