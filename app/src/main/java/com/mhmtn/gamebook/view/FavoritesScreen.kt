package com.mhmtn.gamebook.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.mhmtn.gamebook.viewmodel.FavoritesViewModel

@Composable
fun FavoritesScreen(
    navController: NavController,
    viewModel: FavoritesViewModel = hiltViewModel()
) {

    val gameList by remember {
        viewModel.gameList
    }
    val error by remember { viewModel.errorMessage }
    val isLoading by remember { viewModel.isLoading }

    val screenHeight = LocalContext.current.resources.displayMetrics.heightPixels.dp /
            LocalDensity.current.density

    Column(modifier = Modifier.fillMaxSize()) {

        LazyVerticalGrid(columns = GridCells.Fixed(2)) {
            items(items = gameList.toList()) { game ->
                GameCard(
                    modifier = Modifier
                        .padding(all = 8.dp)
                        .requiredHeight(height = screenHeight * 0.45f),
                    game = game,
                    navController = navController,
                    onFavoriteClick = {
                        viewModel.onFavoriteClick(game)
                    }
                )
            }
        }

        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            if (isLoading && gameList.isNotEmpty()) {
                CircularProgressIndicator()
            }
            if (error.isNotEmpty()) {
                Text(text = "Error.", color = Color.Red)
            }
        }
    }
}