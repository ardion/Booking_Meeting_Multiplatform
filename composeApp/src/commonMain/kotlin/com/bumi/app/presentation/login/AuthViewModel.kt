package com.bumi.app.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumi.app.data.response.LoginRequest
import com.bumi.app.domain.model.AuthUser
import com.bumi.app.domain.usecase.LoginUseCase
import com.bumi.app.utils.Resource
import com.bumi.app.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Hapus @HiltViewModel dan @Inject
class AuthViewModel(
    private val loginUseCase: LoginUseCase,
    private val sessionManager: SessionManager
) : ViewModel(){
    private val _loginState = MutableStateFlow<Resource<AuthUser>>(Resource.Idle)
    val loginState = _loginState.asStateFlow()

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _loginState.value = Resource.Loading

            val input = LoginRequest(username = username, password = password)
            // loginUseCase(input) memanggil fungsi invoke yang sudah kita buat
            _loginState.value = loginUseCase(input)
        }
    }

    fun saveSession(user: AuthUser) {
        // sessionManager ini nanti akan kita buat versi Multiplatform-nya
        sessionManager.saveLoginStatus(user.id, user.nama, user.role)
    }

    fun resetState() {
        _loginState.value = Resource.Idle
    }
}