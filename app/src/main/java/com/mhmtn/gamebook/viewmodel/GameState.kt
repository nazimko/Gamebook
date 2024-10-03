package com.mhmtn.gamebook.viewmodel

import com.mhmtn.gamebook.model.GameListItem

data class GameState(
    var games : List<GameListItem> = emptyList(),
    val errorMessage : String = "",
    var isLoading : Boolean = false
)
