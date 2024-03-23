package com.mhmtn.gamebook.repo

import com.mhmtn.gamebook.model.GameDetail
import com.mhmtn.gamebook.model.GameList
import com.mhmtn.gamebook.service.GameAPI
import com.mhmtn.gamebook.util.Resource
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class GameRepo @Inject constructor(
    private val api : GameAPI
){
    suspend fun getGameList() : Resource<GameList>{
        val response = try {
            api.getGameList()
        }catch (e:Exception){
            return Resource.Error("Error.")
        }
        return Resource.Success(response)
    }

    suspend fun getGameDetail(id:Int) : Resource<GameDetail>{
        val response = try {
            api.getGameDetail(id = id)
        }catch (e:Exception){
            return Resource.Error("Error.")
        }
        return Resource.Success(response)
    }

    suspend fun getGameListByPlatform(platform : String) : Resource<GameList>{
        val response = try {
            api.getGameListByPlatform(platform = platform)
        }catch (e:Exception){
            return Resource.Error("Error.")
        }
        return Resource.Success(response)
    }

    suspend fun getGameListByDate(sort : String) : Resource<GameList>{
        val response = try {
            api.getGameListByDate(sort = sort)
        }catch (e:Exception){
            return Resource.Error("Error.")
        }
        return Resource.Success(response)
    }

}