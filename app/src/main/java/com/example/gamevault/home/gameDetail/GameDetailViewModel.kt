package com.example.gamevault.home.gameDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamevault.core.ResponseService
import com.example.gamevault.core.model.GameDetail
import com.example.gamevault.core.network.GameRepository
import com.example.gamevault.core.network.GameService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameDetailViewModel(
    private val service: GameService = GameRepository()
) : ViewModel() {

    private val _detailState = MutableStateFlow<ResponseService<GameDetail>?>(null)
    val detailState: StateFlow<ResponseService<GameDetail>?> = _detailState.asStateFlow()

    fun loadDetail(gameId: Int) {
        viewModelScope.launch {
            _detailState.value = ResponseService.Loading
            _detailState.value = service.getGameDetail(gameId.toString())
        }
    }
}
