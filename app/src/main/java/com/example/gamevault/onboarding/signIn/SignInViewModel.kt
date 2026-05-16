package com.example.gamevault.onboarding.signIn

import android.util.Patterns
import androidx.lifecycle.ViewModel

class SignInViewModel : ViewModel() {
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

    fun isLoginFormValid(email: String, password: String): Boolean {
        return validateEmail(email) == null && validatePassword(password) == null
    }
}
