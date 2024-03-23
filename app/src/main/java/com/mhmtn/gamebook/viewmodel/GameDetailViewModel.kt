package com.mhmtn.gamebook.viewmodel

import androidx.lifecycle.ViewModel
import com.mhmtn.gamebook.model.GameDetail
import com.mhmtn.gamebook.repo.GameRepo
import com.mhmtn.gamebook.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GameDetailViewModel @Inject constructor(
    private val repo : GameRepo
) : ViewModel(){

    suspend fun getGame(id:Int) : Resource<GameDetail>{
        return repo.getGameDetail(id)
    }

}