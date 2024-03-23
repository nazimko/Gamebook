package com.mhmtn.gamebook.service

import com.mhmtn.gamebook.model.GameDetail
import com.mhmtn.gamebook.model.GameList
import com.mhmtn.gamebook.util.Constants.SORT_BY
import retrofit2.http.GET
import retrofit2.http.Query

interface GameAPI {

    //https://www.freetogame.com/api/games
    //https://www.freetogame.com/api/game?id=345
    //https://www.freetogame.com/api/games?platform=pc

    @GET("games")
    suspend fun getGameList(

    ) : GameList

    @GET("game")
    suspend fun getGameDetail(
        @Query("id")id : Int
    ) : GameDetail

    @GET("games")
    suspend fun getGameListByPlatform(
        @Query("platform") platform : String
    ) : GameList

    @GET("games")
    suspend fun getGameListByDate(
        @Query(SORT_BY) sort : String
    ) : GameList

}