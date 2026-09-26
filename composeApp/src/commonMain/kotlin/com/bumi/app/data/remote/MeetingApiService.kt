package com.bumi.app.data.remote

import com.bumi.app.data.response.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.util.reflect.typeInfo

class MeetingApiService(private val client: HttpClient) {

    suspend fun createBooking(request: BookingRequest): SimpleResponse {
        return client.post("create-booking") {
            setBody(request, typeInfo<BookingRequest>())
        }.body()
    }

    suspend fun getBookings(): List<BookingDto> {
        return client.get("get-bookings").body()
    }

    suspend fun updateStatus(id: String, status: String): SimpleResponse {
        return client.put("update-status/$id") {
            setBody(mapOf("status" to status))
        }.body()
    }

    suspend fun deleteBooking(id: String): SimpleResponse {
        return client.delete("delete-booking/$id").body()
    }

    suspend fun getBookedSlots(tanggal: String): List<String> {
        return client.get("booked-slots") {
            url { parameters.append("tanggal", tanggal) }
        }.body()
    }
}
