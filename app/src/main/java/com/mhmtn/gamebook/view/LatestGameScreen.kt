package com.mhmtn.gamebook.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.mhmtn.gamebook.model.GameListItem
import com.mhmtn.gamebook.viewmodel.LatestGamesViewModel
import com.mhmtn.gamebook.viewmodel.PCGameViewModel

@Composable
fun LatestGameScreen(
    navController: NavController,
    viewModel: LatestGamesViewModel = hiltViewModel()
) {

    Column (modifier = Modifier.fillMaxSize()) {
        LatestGameFilterList(navController)
    }

}

@Composable
fun LatestGameFilterList(
    navController: NavController,
    viewModel: LatestGamesViewModel = hiltViewModel()
) {

    val gameList = viewModel.gameList
    val error = remember { viewModel.errorMessage}
    val isLoading = remember { viewModel.isLoading }

    LatestGameFilterListView(games = gameList.value, navController = navController )

    Box (contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        if (isLoading){
            CircularProgressIndicator()
        }
        if (error.isNotEmpty()){
            Text(text = "Error.")
        }
    }

}

@Composable
fun LatestGameFilterListView(games : List<GameListItem>, navController: NavController) {

    LazyColumn(
        modifier = Modifier
            .padding(all = 8.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = 10.dp)
        ){

        items(items = games) {
            GameFilterCard(game = it, navController =  navController)
        }

    }
}