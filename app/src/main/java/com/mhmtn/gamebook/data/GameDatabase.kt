package com.mhmtn.gamebook.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mhmtn.gamebook.model.FavoriteGame
import com.mhmtn.gamebook.model.GameListItem

@Database(entities = [FavoriteGame::class], version = 1)
abstract class GameDatabase : RoomDatabase (){
    abstract val dao: GameDao
}