package com.mhmtn.gamebook.model

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "games")
data class FavoriteGame(
    @PrimaryKey val id : Int
)

