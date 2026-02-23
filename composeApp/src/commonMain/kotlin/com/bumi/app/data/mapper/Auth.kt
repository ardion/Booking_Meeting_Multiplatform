package com.bumi.app.data.mapper

import com.bumi.app.data.response.UserData
import com.bumi.app.domain.model.AuthUser

fun UserData.toDomain(): AuthUser {
    return AuthUser(
        id = this.id,
        username = this.username,
        nama = this.nama,
        role = this.role
    )
}