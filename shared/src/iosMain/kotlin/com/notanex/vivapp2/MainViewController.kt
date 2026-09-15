package com.notanex.vivapp2

import androidx.compose.ui.window.ComposeUIViewController
import platform.Foundation.currentLocale
import platform.Foundation.languageCode

fun MainViewController() = ComposeUIViewController {
    val systemLang = platform.Foundation.NSLocale.currentLocale.languageCode ?: "en"
    App(systemLanguageCode = systemLang)
}