package com.mhmtn.gamebook.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mhmtn.gamebook.model.GameListItem
import com.mhmtn.gamebook.repo.GameRepo
import com.mhmtn.gamebook.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WebGameViewModel @Inject constructor(
    private val repo : GameRepo
) : ViewModel() {

    private val _state = MutableStateFlow(GameState())
    val state: StateFlow<GameState> = _state.asStateFlow()

    init {
        loadGamesByPlatform("browser")
    }

     private fun loadGamesByPlatform(platform : String){
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }
            val result = repo.getGameListByPlatform(platform = platform)

            when(result){
                is Resource.Success -> {
                    val s = result.data!!.mapIndexed { index, gameListItem ->
                        GameListItem(
                            gameListItem.developer,
                            gameListItem.freetogame_profile_url,
                            gameListItem.game_url,
                            gameListItem.genre,
                            gameListItem.id,
                            gameListItem.platform,
                            gameListItem.publisher,
                            gameListItem.release_date,
                            gameListItem.short_description,
                            gameListItem.thumbnail,
                            gameListItem.title,)
                    }
                    _state.update {
                        it.copy(games = s, isLoading = false)
                    }
                }

                is Resource.Error -> {
                    _state.update {
                        it.copy(errorMessage = result.message ?: "Error.", isLoading = false)
                    }
                }

                else -> {}
            }
        }
    }
}