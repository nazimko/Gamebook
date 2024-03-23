package com.mhmtn.gamebook.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import com.mhmtn.gamebook.model.GameListItem
import com.mhmtn.gamebook.viewmodel.LatestGamesViewModel
import com.mhmtn.gamebook.viewmodel.PCGameViewModel
import kotlinx.coroutines.delay

@Composable
fun LatestGameScreen(
    navController: NavController,
    viewModel: LatestGamesViewModel = hiltViewModel()
) {

    Column(modifier = Modifier.fillMaxSize()) {
        LatestGameFilterList(navController)
    }

}

@Composable
fun LatestGameFilterList(
    navController: NavController,
    viewModel: LatestGamesViewModel = hiltViewModel()
) {

    val gameList = viewModel.gameList
    val error = remember { viewModel.errorMessage }
    val isLoading = remember { viewModel.isLoading }
    val pagerState = rememberPagerState(pageCount = { viewModel.gameList.value.getUrls().size })

    LatestGameFilterListView(games = gameList.value, navController = navController, pagerState, viewModel)

    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            CircularProgressIndicator()
        }
        if (error.isNotEmpty()) {
            Text(text = "Error.")
        }
    }

}

@Composable
fun LatestGameFilterListView(
    games: List<GameListItem>,
    navController: NavController,
    pagerState: PagerState,
    viewModel: LatestGamesViewModel
) {

    val screenHeight = LocalContext.current.resources.displayMetrics.heightPixels.dp /
            LocalDensity.current.density

    LaunchedEffect(Unit){
        while (true) {
            delay(4000L)
            val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
            pagerState.scrollToPage(nextPage)
        }
    }

    LazyColumn(
        modifier = Modifier
            .padding(all = 8.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = 10.dp)
    ) {

        header {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth()
            ) { index ->
                Card(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    SubcomposeAsyncImage(
                        model = viewModel.gameList.value.getUrls()[index],
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .size(250.dp)
                            .padding(vertical = 8.dp, horizontal = 12.dp)
                            .align(alignment = Alignment.CenterHorizontally)
                            .clip(shape = MaterialTheme.shapes.medium),
                        loading = {
                            ConstraintLayout(modifier = Modifier.fillMaxSize()) {
                                val indicatorRef = createRef()
                                CircularProgressIndicator(
                                    modifier = Modifier.constrainAs(indicatorRef) {
                                        top.linkTo(parent.top)
                                        bottom.linkTo(parent.bottom)
                                        start.linkTo(parent.start)
                                        end.linkTo(parent.end)
                                    }
                                )
                            }
                        },
                        error = {
                            Icon(
                                imageVector = Icons.Default.Info, contentDescription = null,
                                tint = Color.Red
                            )
                        }

                    )
                }
            }
            Spacer(modifier = Modifier.height(30.dp))
        }

        items(items = games) {
            GameFilterCard(
                game = it,
                navController = navController,
                modifier = Modifier
                    .padding(all = 8.dp)
                    .requiredHeight(height = screenHeight * 0.45f))
        }

    }
}