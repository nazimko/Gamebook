package com.mhmtn.gamebook.viewmodel

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mhmtn.gamebook.model.GameListItem
import com.mhmtn.gamebook.repo.GameRepo
import com.mhmtn.gamebook.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameListViewModel @Inject constructor(
    private val repo : GameRepo
) : ViewModel() {
    private val _state = MutableStateFlow(GameState())
    val state: StateFlow<GameState> = _state.asStateFlow()

    private var initialGameList = listOf<GameListItem>()
    private var isSearchStarting = true

    init {
        loadGames()
    }

    fun searchGameList(query : String){

        val listToSearch = if(isSearchStarting){
            _state.value.games
        }else {
            initialGameList
        }

        viewModelScope.launch (Dispatchers.Default) {
            if(query.isEmpty()){
                _state.value = _state.value.copy(games = initialGameList)
                isSearchStarting = true
                return@launch
            }

            val results = listToSearch.filter {
                it.title.contains(query.trim(),ignoreCase = true)
            }

            if (isSearchStarting){
                initialGameList = _state.value.games
                isSearchStarting = false
            }
            _state.value = _state.value.copy(games = results)
        }
    }

    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }
    }

    private fun loadGames(){
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }
            val result = repo.getGameList()
            when(result){
                is Resource.Success -> {
                    val gameIds = result.data!!.map { it.id }
                    repo.getFavoriteGames(gameIds).collect{
                        val updatedGames = result.data.map {game->
                            game.copy(isFavorite = it.contains(game.id))
                        }
                        _state.update {
                            it.copy(games = updatedGames, isLoading = false)
                        }
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

    fun onFavoriteClick(game: GameListItem) {
        viewModelScope.launch {
            if (game.isFavorite) {
                repo.removeFavorite(game)
            } else {
                repo.addFavorite(game)
            }
            _state.update {currentState->
                val games = currentState.games.map {
                    if (it.id == game.id) game.copy(isFavorite = !it.isFavorite) else it
                }
                currentState.copy(games = games)
            }
        }
    }
}