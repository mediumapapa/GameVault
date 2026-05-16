package com.example.gamevault.onboarding.personal

import androidx.lifecycle.ViewModel

class PersonalInfoViewModel : ViewModel() {
    fun validateName(value: String): String? {
        if (value.isBlank()) return "Este campo es requerido"
        return null
    }

    fun validatePhone(phone: String): String? {
        if (phone.isBlank()) return "El celular es requerido"
        if (phone.length < 10) return "Ingresa un celular valido"
        return null
    }

    fun validateBirthDate(birthDate: String): String? {
        if (birthDate.isBlank()) return "La fecha es requerida"
        return null
    }

    fun isFormValid(
        nombre: String,
        apellidos: String,
        celular: String,
        fechaNacimiento: String
    ): Boolean {
        return validateName(nombre) == null &&
            validateName(apellidos) == null &&
            validatePhone(celular) == null &&
            validateBirthDate(fechaNacimiento) == null
    }
}
