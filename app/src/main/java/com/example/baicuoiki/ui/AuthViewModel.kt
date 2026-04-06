package com.example.baicuoiki.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baicuoiki.data.User
import com.example.baicuoiki.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<AuthState>(AuthState.Idle)
    val loginState: StateFlow<AuthState> = _loginState

    private val _registerState = MutableStateFlow<AuthState>(AuthState.Idle)
    val registerState: StateFlow<AuthState> = _registerState

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _loginState.value = AuthState.Error("Vui lòng nhập đầy đủ thông tin")
            return
        }

        viewModelScope.launch {
            _loginState.value = AuthState.Loading
            val user = userRepository.getUserByUsername(username)
            if (user != null && user.passwordHash == password) {
                _currentUser.value = user
                _loginState.value = AuthState.Success
            } else {
                _loginState.value = AuthState.Error("Sai tên đăng nhập hoặc mật khẩu")
            }
        }
    }

    fun register(username: String, password: String, fullName: String) {
        if (username.isBlank() || password.isBlank() || fullName.isBlank()) {
            _registerState.value = AuthState.Error("Vui lòng nhập đầy đủ thông tin")
            return
        }

        viewModelScope.launch {
            _registerState.value = AuthState.Loading
            if (userRepository.isUsernameTaken(username)) {
                _registerState.value = AuthState.Error("Tên đăng nhập đã tồn tại")
            } else {
                val newUser = User(username = username, passwordHash = password, fullName = fullName)
                userRepository.registerUser(newUser)
                _registerState.value = AuthState.Success
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        _loginState.value = AuthState.Idle
    }

    fun resetState() {
        _loginState.value = AuthState.Idle
        _registerState.value = AuthState.Idle
    }
}
