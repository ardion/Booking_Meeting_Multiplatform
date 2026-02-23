package com.bumi.app.domain.usecase

import com.bumi.app.domain.Irepository.MeetingRepository
import com.bumi.app.domain.model.Meeting
import com.bumi.app.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Wrapper untuk mempermudah injeksi semua UseCase ke dalam ViewModel.
 */
data class MeetingUseCases(
    val getBookings: GetBookingsUseCase,
    val createBooking: CreateBookingUseCase,
    val updateStatus: UpdateStatusUseCase,
    val deleteBooking: DeleteBookingUseCase,
    val getBookedSlots: GetBookedSlotsUseCase
)

class GetBookingsUseCase(private val repository: MeetingRepository) {
    operator fun invoke(): Flow<Resource<List<Meeting>>> = flow {
        emit(Resource.Loading)
        try {
            val meetings = repository.getAllBookings()
            emit(Resource.Success(meetings))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Terjadi kesalahan koneksi"))
        }
    }
}

class CreateBookingUseCase(private val repository: MeetingRepository) {
    suspend operator fun invoke(
        pic: String,
        unit: String,
        tujuan: String,
        tanggal: String,
        jamList: List<String>
    ): Result<Unit> {
        return if (pic.isBlank() || unit.isBlank() || jamList.isEmpty()) {
            Result.failure(Exception("Data tidak boleh kosong"))
        } else {
            repository.postBooking(pic, unit, tujuan, tanggal, jamList)
        }
    }
}

class UpdateStatusUseCase(private val repository: MeetingRepository) {
    suspend operator fun invoke(id: String, status: String): Result<Unit> {
        return repository.updateStatus(id, status)
    }
}

class DeleteBookingUseCase(private val repository: MeetingRepository) {
    suspend operator fun invoke(id: String): Result<Unit> {
        return repository.deleteBooking(id)
    }
}

class GetBookedSlotsUseCase(private val repository: MeetingRepository) {
    suspend operator fun invoke(tanggal: String): List<String> {
        return repository.getBookedSlots(tanggal)
    }
}