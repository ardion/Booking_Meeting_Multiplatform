package com.bumi.app.data.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BookingRequest(
    @SerialName("pic_name") val pic_name: String,
    @SerialName("unit_name") val unit_name: String,
    @SerialName("meeting_purpose") val meeting_purpose: String,
    @SerialName("booking_date") val booking_date: String,
    @SerialName("selected_times") val selected_times: List<String>
)

@Serializable
data class BookingDto(
    @SerialName("id")
    val id: String,

    @SerialName("pic_name")
    val pic_name: String,

    @SerialName("unit_name")
    val unit_name: String,

    @SerialName("meeting_purpose")
    val meeting_purpose: String,

    @SerialName("booking_date")
    val booking_date: String,

    @SerialName("jam_dipilih")
    val jam_dipilih: String,

    // Sesuai temuan Logcat kamu: Pakai huruf besar "STATUS"
    @SerialName("STATUS")
    val status: String
)

@Serializable
data class SimpleResponse(
    @SerialName("status") val status: String,
    @SerialName("message") val message: String
)