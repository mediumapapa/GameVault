package com.example.gamevault.onboarding.signup

import android.util.Patterns
import androidx.lifecycle.ViewModel

class RegisterViewModel : ViewModel() {
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
}
