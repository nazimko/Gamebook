package com.mhmtn.gamebook.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mhmtn.gamebook.model.GameListItem
import com.mhmtn.gamebook.repo.GameRepo
import com.mhmtn.gamebook.util.Constants.RELEASE_DATE
import com.mhmtn.gamebook.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LatestGamesViewModel @Inject constructor(
    private val repo : GameRepo
) : ViewModel() {


    var gameList = mutableStateOf<List<GameListItem>>(listOf())
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf("")


    init {
        loadGamesByDate(RELEASE_DATE)
    }

    fun loadGamesByDate(sort : String){
        viewModelScope.launch {
            isLoading = true
            val result = repo.getGameListByDate(sort = sort)

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
                    gameList.value = s
                    errorMessage = ""
                    isLoading = false
                }

                is Resource.Error -> {
                    errorMessage = result.message!!
                    isLoading = false
                }

                else -> {}
            }
        }
    }
}