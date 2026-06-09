package com.example.gamevault.home.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamevault.core.AuthRepository
import com.example.gamevault.core.ResponseService
import com.example.gamevault.core.repositories.UserRepository
import com.example.gamevault.core.repositories.UserService
import com.example.gamevault.onboarding.personal.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AccountViewModel(
    private val userService: UserService = UserRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _profileState = MutableStateFlow<ResponseService<UserProfile>?>(null)
    val profileState: StateFlow<ResponseService<UserProfile>?> = _profileState.asStateFlow()

    /** Lee el perfil guardado en el registro desde Firestore. */
    fun loadProfile() {
        val uid = authRepository.currentUser?.uid
        if (uid == null) {
            _profileState.value = ResponseService.Error("Sesión no válida")
            return
        }
        viewModelScope.launch {
            _profileState.value = ResponseService.Loading
            _profileState.value = userService.getUserInfo(uid)
        }
    }

    fun logout() = authRepository.signOut()
}
