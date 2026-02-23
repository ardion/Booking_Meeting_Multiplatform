package com.bumi.app.presentation.booking

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumi.app.domain.model.Meeting
import com.bumi.app.domain.usecase.MeetingUseCases
import com.bumi.app.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// @HiltViewModel dihapus, kita pakai Koin di AppModule nantinya
class MeetingViewModel(
    private val useCases: MeetingUseCases
) : ViewModel() {

    // State untuk List Booking (Read)
    private val _meetingsState = MutableStateFlow<Resource<List<Meeting>>>(Resource.Idle)
    val meetingsState: StateFlow<Resource<List<Meeting>>> = _meetingsState.asStateFlow()

    // State untuk hasil Create Booking (Write)
    private val _createBookingResult = MutableStateFlow<Resource<Unit>>(Resource.Idle)
    val createBookingResult: StateFlow<Resource<Unit>> = _createBookingResult.asStateFlow()

    /**
     * Mengambil semua data booking dari server
     */
    fun getAllBookings() {
        viewModelScope.launch {
            useCases.getBookings().collect { resource ->
                _meetingsState.value = resource
            }
        }
    }

    /**
     * Mengirim data booking baru ke server
     */
    fun createBooking(
        pic: String,
        unit: String,
        tujuan: String,
        tanggal: String,
        jamList: List<String>
    ) {
        viewModelScope.launch {
            _createBookingResult.value = Resource.Loading
            val result = useCases.createBooking(pic, unit, tujuan, tanggal, jamList)

            result.onSuccess {
                _createBookingResult.value = Resource.Success(Unit)
                getAllBookings() // Refresh list
            }.onFailure { error ->
                _createBookingResult.value = Resource.Error(error.message ?: "Gagal membuat booking")
            }
        }
    }

    fun updateStatus(id: String, status: String) {
        viewModelScope.launch {
            useCases.updateStatus(id, status).onSuccess {
                getAllBookings()
            }.onFailure { e ->
                // Di KMP gunakan println() sebagai pengganti Log.e untuk debug sederhana
                println("ViewModel Update Failed: ${e.message}")
            }
        }
    }

    fun deleteBooking(id: String) {
        viewModelScope.launch {
            useCases.deleteBooking(id).onSuccess {
                getAllBookings()
            }.onFailure { e ->
                println("ViewModel Delete Failed: ${e.message}")
            }
        }
    }

    var bookedSlots by mutableStateOf<List<String>>(emptyList())
        private set

    fun onDateSelected(tanggal: String) {
        viewModelScope.launch {
            val result = useCases.getBookedSlots(tanggal)
            bookedSlots = result
        }
    }

    fun resetCreateState() {
        _createBookingResult.value = Resource.Idle
    }
}