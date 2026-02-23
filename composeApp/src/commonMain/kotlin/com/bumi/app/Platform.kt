package com.bumi.app

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform