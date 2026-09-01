package com.bumi.app.utils

import com.russhwolf.settings.Settings

class SessionManager(private val settings: Settings) {

    companion object {
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_ROLE = "user_role"
        private const val KEY_LOGIN_TIME = "login_time"
    }

    fun saveLoginStatus(userId: String, userName: String, role: String) {
        settings.putString(KEY_USER_ID, userId)
        settings.putString(KEY_USER_NAME, userName)
        settings.putString(KEY_USER_ROLE, role)
        settings.putBoolean(KEY_IS_LOGGED_IN, true)
        settings.putLong(KEY_LOGIN_TIME, 0L)
    }

    fun isLoggedIn(): Boolean {
        return settings.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun getUserId(): String {
        return settings.getString(KEY_USER_ID, "")
    }

    fun getUserName(): String {
        return settings.getString(KEY_USER_NAME, "")
    }

    fun getUserRole(): String {
        return settings.getString(KEY_USER_ROLE, "user")
    }

    fun logout() {
        settings.clear()
    }
}