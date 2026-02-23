package com.bumi.app.data.repository

import com.bumi.app.data.MeetingRemoteDataSource
import com.bumi.app.data.mapper.toDomain
import com.bumi.app.data.response.BookingRequest
import com.bumi.app.domain.Irepository.MeetingRepository
import com.bumi.app.domain.model.Meeting
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class MeetingRepositoryImpl(
    private val remoteDataSource: MeetingRemoteDataSource,
    private val ioDispatcher: CoroutineDispatcher
) : MeetingRepository {

    override suspend fun getAllBookings(): List<Meeting> = withContext(ioDispatcher) {
        try {
            val response = remoteDataSource.fetchBookings()
            response.map { it.toDomain() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun postBooking(
        pic: String,
        unit: String,
        tujuan: String,
        tanggal: String,
        jamList: List<String>
    ): Result<Unit> = withContext(ioDispatcher) {
        try {
            val request = BookingRequest(
                pic_name = pic,
                unit_name = unit,
                meeting_purpose = tujuan,
                booking_date = tanggal,
                selected_times = jamList
            )
            // Di Ktor, jika tidak crash/exception, berarti request sampai ke server
            val response = remoteDataSource.createBooking(request)

            // Cek logic success berdasarkan response body (misal ada field status)
            if (response.status == "success" || response.message.contains("Success", true)) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateStatus(id: String, status: String): Result<Unit> = withContext(ioDispatcher) {
        try {
            val response = remoteDataSource.updateStatus(id, status)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteBooking(id: String): Result<Unit> = withContext(ioDispatcher) {
        try {
            val response = remoteDataSource.deleteBooking(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getBookedSlots(tanggal: String): List<String> = withContext(ioDispatcher) {
        try {
            remoteDataSource.getBookedSlots(tanggal)
        } catch (e: Exception) {
            emptyList()
        }
    }
}