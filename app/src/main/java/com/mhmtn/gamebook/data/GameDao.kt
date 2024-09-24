package com.mhmtn.gamebook.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Query
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import com.mhmtn.gamebook.model.FavoriteGame
import com.mhmtn.gamebook.model.GameListItem
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {

    @Query("SELECT id FROM games WHERE id IN (:gameIds)")
    fun getFavoriteGames(gameIds: List<Int>): Flow<List<Int>>

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favoriteGame: FavoriteGame)

    @Delete
    suspend fun deleteFavorite(favoriteMovie: FavoriteGame)

    @Query("SELECT * FROM games")
    fun getAllFavoriteGameList(): Flow<List<FavoriteGame>>

}