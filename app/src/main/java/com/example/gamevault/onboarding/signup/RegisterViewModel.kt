package com.example.gamevault.onboarding.signup

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamevault.core.AuthRepository
import com.example.gamevault.core.ResponseService
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {
    private val authRepository = AuthRepository()
    private val _registerState = MutableStateFlow<ResponseService<FirebaseUser>?>(null)
    val registerState: StateFlow<ResponseService<FirebaseUser>?> = _registerState.asStateFlow()

    fun validateEmail(email: String): String? {
        if (email.isBlank()) return "El correo es requerido"
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) return "Correo invalido"
        return null
    }

    fun validatePassword(password: String): String? {
        if (password.isBlank()) return "La contrasena es requerida"
        if (password.length < 6) return "Minimo 6 caracteres"
        return null
    }

    fun validateConfirmPassword(password: String, confirmPassword: String): String? {
        if (confirmPassword.isBlank()) return "Confirma tu contrasena"
        if (password != confirmPassword) return "Las contrasenas no coinciden"
        return null
    }

    fun isRegisterFormValid(email: String, password: String, confirmPassword: String): Boolean {
        return validateEmail(email) == null &&
            validatePassword(password) == null &&
            validateConfirmPassword(password, confirmPassword) == null
    }

    fun requestSignUp(email: String, password: String) {
        viewModelScope.launch {
            _registerState.value = ResponseService.Loading
            _registerState.value = authRepository.requestSignUp(email, password)
        }
    }

    fun clearRegisterState() {
        _registerState.value = null
    }
}
