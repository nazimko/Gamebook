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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameListViewModel @Inject constructor(
    private val repo : GameRepo
) : ViewModel() {

    var gameList = mutableStateOf<List<GameListItem>>(listOf())
    var isLoading = mutableStateOf(false)
    var errorMessage = mutableStateOf("")

    private var initialGameList = listOf<GameListItem>()
    private var isSearchStarting = true

    init {
        loadGames()
    }

    fun searchGameList(query : String){

        val listToSearch = if(isSearchStarting){
            gameList.value
        }else {
            initialGameList
        }

        viewModelScope.launch (Dispatchers.Default) {
            if(query.isEmpty()){
                gameList.value = initialGameList
                isSearchStarting = true
                return@launch
            }

            val results = listToSearch.filter {
                it.title.contains(query.trim(),ignoreCase = true)
            }

            if (isSearchStarting){
                initialGameList = gameList.value
                isSearchStarting = false
            }
            gameList.value = results
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

    fun loadGames(){
        viewModelScope.launch {
            isLoading.value = true
            val result = repo.getGameList()
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
                            gameListItem.title)
                    }
                    val gameIds = result.data.map { it.id }

                    repo.getFavoriteGames(gameIds).collect{
                        val updatedGames = result.data.map {game->
                            game.copy(isFavorite = it.contains(game.id))
                        }
                        gameList.value = updatedGames
                        errorMessage.value = ""
                        isLoading.value = false
                    }
                }

                is Resource.Error -> {
                    errorMessage.value = result.message ?: "Error."
                    isLoading.value = false
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
            gameList.value = gameList.value.map {
                if (it.id == game.id) it.copy(isFavorite = !game.isFavorite) else it
            }
        }
    }

}