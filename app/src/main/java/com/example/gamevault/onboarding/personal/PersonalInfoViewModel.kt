package com.example.gamevault.onboarding.personal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamevault.core.ResponseService
import com.example.gamevault.core.models.UserProfile
import com.example.gamevault.core.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PersonalInfoViewModel : ViewModel() {
    private val userRepository = UserRepository()
    private val _saveState = MutableStateFlow<ResponseService<Unit>?>(null)
    val saveState: StateFlow<ResponseService<Unit>?> = _saveState.asStateFlow()

    fun validateName(value: String): String? {
        if (value.isBlank()) return "Este campo es requerido"
        if (value.length < 2) return "Minimo 2 caracteres"
        if (!value.all { it.isLetter() || it.isWhitespace() }) return "Solo se permiten letras"
        return null
    }

    fun validatePhone(phone: String): String? {
        if (phone.isBlank()) return "El celular es requerido"
        if (!phone.all { it.isDigit() || it == '+' || it.isWhitespace() }) return "Ingresa un celular valido"
        if (phone.filter { it.isDigit() }.length < 10) return "Ingresa un celular valido"
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

    fun saveProfile(
        uid: String,
        nombre: String,
        apellidos: String,
        celular: String,
        fechaNacimiento: String
    ) {
        viewModelScope.launch {
            _saveState.value = ResponseService.Loading
            _saveState.value = userRepository.saveUserInfo(
                UserProfile(
                    id = uid,
                    firstName = nombre,
                    lastName = apellidos,
                    phone = celular,
                    birthDate = fechaNacimiento
                )
            )
        }
    }

    fun clearSaveState() {
        _saveState.value = null
    }
}
