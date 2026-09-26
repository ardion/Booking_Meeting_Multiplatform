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

class MeetingViewModel(
    private val useCases: MeetingUseCases
) : ViewModel() {

    private val _meetingsState = MutableStateFlow<Resource<List<Meeting>>>(Resource.Idle)
    val meetingsState: StateFlow<Resource<List<Meeting>>> = _meetingsState.asStateFlow()

    private val _createBookingResult = MutableStateFlow<Resource<Unit>>(Resource.Idle)
    val createBookingResult: StateFlow<Resource<Unit>> = _createBookingResult.asStateFlow()

    fun getAllBookings() {
        viewModelScope.launch {
            useCases.getBookings().collect { resource ->
                _meetingsState.value = resource
            }
        }
    }

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
                getAllBookings()
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
