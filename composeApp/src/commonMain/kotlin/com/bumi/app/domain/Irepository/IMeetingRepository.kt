package com.bumi.app.domain.Irepository

import com.bumi.app.domain.model.Meeting

interface MeetingRepository {
    suspend fun getAllBookings(): List<Meeting>
    suspend fun postBooking(
        pic: String, unit: String, tujuan: String, tanggal: String, jamList: List<String>
    ): Result<Unit>

    suspend fun updateStatus(id: String, status: String): Result<Unit>
    suspend fun deleteBooking(id: String): Result<Unit>

    suspend fun getBookedSlots(tanggal: String): List<String>
}