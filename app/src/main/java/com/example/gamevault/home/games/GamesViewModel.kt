package com.example.gamevault.home.games

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamevault.core.ResponseService
import com.example.gamevault.core.model.Game
import com.example.gamevault.core.network.GameRepository
import com.example.gamevault.core.network.GameService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GamesViewModel(
    private val service: GameService = GameRepository()
) : ViewModel() {

    private val _gamesState = MutableStateFlow<ResponseService<List<Game>>?>(null)
    val gamesState: StateFlow<ResponseService<List<Game>>?> = _gamesState.asStateFlow()

    /**
     * Carga el listado. Si query es nula o vacía trae los juegos en
     * tendencia (ordenados por -added); si no, busca por nombre.
     */
    fun loadGames(query: String? = null) {
        viewModelScope.launch {
            _gamesState.value = ResponseService.Loading
            val cleaned = query?.trim()?.takeIf { it.isNotEmpty() }
            _gamesState.value = service.listGames(
                search = cleaned,
                pageSize = 20,
                ordering = if (cleaned == null) "-added" else null
            )
        }
    }
}
