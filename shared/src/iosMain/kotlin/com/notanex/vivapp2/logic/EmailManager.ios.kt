@file:OptIn(ExperimentalForeignApi::class)
package com.notanex.vivapp2.logic

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.*
import platform.Foundation.*
import platform.CoreGraphics.*

@OptIn(BetaInteropApi::class)
actual fun sendEmailWithAttachment(
    subject: String,
    body: String,
    attachment: EmailAttachment
) {
    val fileManager = NSFileManager.defaultManager
    val tempDir = fileManager.temporaryDirectory
    val fileUrl = tempDir.URLByAppendingPathComponent(attachment.fileName)!!

    val nsContent = NSString.create(string = attachment.content)
    val data = nsContent.dataUsingEncoding(NSUTF8StringEncoding)
    data?.writeToURL(fileUrl, true)

    val activityItems = listOf(body, fileUrl)

    val activityController = UIActivityViewController(
        activityItems = activityItems,
        applicationActivities = null
    )

    activityController.setValue(subject, "subject")

    val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController

    if (rootViewController != null) {
        if (UIDevice.currentDevice.userInterfaceIdiom == UIUserInterfaceIdiomPad) {
            activityController.popoverPresentationController?.sourceView = rootViewController.view
            activityController.popoverPresentationController?.sourceRect = CGRectMake(0.0, 0.0, 1.0, 1.0)
        }

        rootViewController.presentViewController(
            activityController,
            animated = true,
            completion = null
        )
    }
}