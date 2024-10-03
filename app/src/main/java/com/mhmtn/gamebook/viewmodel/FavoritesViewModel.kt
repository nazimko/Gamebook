package com.mhmtn.gamebook.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mhmtn.gamebook.model.GameDetail
import com.mhmtn.gamebook.model.GameListItem
import com.mhmtn.gamebook.repo.GameRepo
import com.mhmtn.gamebook.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor (
    private val repo : GameRepo
) : ViewModel() {

    var gameList = mutableStateOf<Set<GameListItem>>(setOf())
    var isLoading = mutableStateOf(false)
    var errorMessage = mutableStateOf("")

    init {
        getFavoriteList()
    }

    fun getFavoriteList(){
        viewModelScope.launch {
            isLoading.value = true
            repo.getFavoriteGameList().collect{
                it.forEach {game->
                    val result = repo.getGameDetail(game.id)
                    when(result) {
                        is Resource.Success -> {
                            val x = result.data!!
                             gameList.value += GameListItem(
                                x.developer,
                                x.freetogame_profile_url,
                                x.game_url,
                                x.genre,
                                x.id,
                                x.platform,
                                x.publisher,
                                x.release_date,
                                x.short_description,
                                x.thumbnail,
                                x.title,
                                true
                            )
                            isLoading.value = false
                            errorMessage.value = ""
                        }

                        is Resource.Error -> {
                            errorMessage.value = result.message ?: "Error."
                            isLoading.value = false
                        }

                        is Resource.Loading -> TODO()
                    }
                }
            }
        }
    }

    fun onFavoriteClick(gameListItem: GameListItem){
        viewModelScope.launch {
            repo.removeFavorite(gameListItem)
            gameList.value -= gameListItem
        }
    }
}