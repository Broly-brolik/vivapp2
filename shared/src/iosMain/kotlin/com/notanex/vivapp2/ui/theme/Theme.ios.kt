package com.notanex.vivapp2.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
internal actual fun SystemAppearance(isDark: Boolean, statusBarColor: Color) {
    // No-op for MVP. iOS status bar styling is handled via Info.plist
    // or the native UIViewController bounds by default.
}
