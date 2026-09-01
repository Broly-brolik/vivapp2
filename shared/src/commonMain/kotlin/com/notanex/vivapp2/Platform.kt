package com.notanex.vivapp2

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform