package com.bumi.app.data

import com.bumi.app.data.remote.MeetingApiService
import com.bumi.app.data.response.BookingDto
import com.bumi.app.data.response.BookingRequest
import com.bumi.app.data.response.SimpleResponse

interface MeetingRemoteDataSource {
    suspend fun fetchBookings(): List<BookingDto>
    suspend fun createBooking(request: BookingRequest): SimpleResponse
    suspend fun updateStatus(id: String, status: String): SimpleResponse
    suspend fun deleteBooking(id: String): SimpleResponse
    suspend fun getBookedSlots(tanggal: String): List<String>
}

class MeetingRemoteDataSourceImpl(
    private val apiService: MeetingApiService
) : MeetingRemoteDataSource {

    override suspend fun fetchBookings(): List<BookingDto> {
        return apiService.getBookings()
    }

    override suspend fun createBooking(request: BookingRequest): SimpleResponse {
        return apiService.createBooking(request)
    }

    override suspend fun updateStatus(id: String, status: String): SimpleResponse {
        // Logika mapOf sudah dipindah ke ApiService (Ktor version)
        return apiService.updateStatus(id, status)
    }

    override suspend fun deleteBooking(id: String): SimpleResponse {
        return apiService.deleteBooking(id)
    }

    override suspend fun getBookedSlots(tanggal: String): List<String> {
        return apiService.getBookedSlots(tanggal)
    }
}