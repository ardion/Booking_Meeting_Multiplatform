//package com.bumi.app
//
//import com.russhwolf.settings.Settings
//
//
//class SessionManager {
//    // Berkat library 'no-arg', inisialisasi ini sekarang valid
//    private val settings: Settings = Settings()
//
//    fun isLoggedIn(): Boolean {
//        return settings.getBoolean("is_logged_in", false)
//    }
//
//    fun setLoggedIn(value: Boolean) {
//        settings.putBoolean("is_logged_in", value)
//    }
//}