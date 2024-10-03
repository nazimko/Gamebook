package com.mhmtn.gamebook.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.mhmtn.gamebook.model.GameListItem
import com.mhmtn.gamebook.viewmodel.GameListViewModel
import com.mhmtn.gamebook.viewmodel.GameState
import kotlinx.coroutines.delay

@Composable
fun GameListScreen(
    navController: NavController,
    viewModel: GameListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    Column(modifier = Modifier.fillMaxSize()) {

        SearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            viewModel.searchGameList(it)
        }

        GameList(navController = navController, state = state)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    onSearch: (String) -> Unit = {}
) {
    var text by remember { mutableStateOf("") }

    Box(modifier = modifier) {
        TextField(value = text, onValueChange = {
            text = it
            onSearch(it)
        }, singleLine = true,
            textStyle = TextStyle(color = MaterialTheme.colorScheme.tertiary),
            shape = RoundedCornerShape(12.dp),
            label = { Text(text = "Search Game") },
            placeholder = { Text(text = "Search..") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 6.dp)
                .background(color = Color.White, CircleShape),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Visibility Icon"
                )
            }
        )
    }
}


@Composable
fun GameList(
    navController: NavController,
    state:GameState,
    viewModel: GameListViewModel = hiltViewModel()
) {
    val pagerState = rememberPagerState(pageCount = { state.games.getUrls().size })

    GameListView(
        games = state.games,
        navController = navController,
        pagerState = pagerState,
        viewModel = viewModel
    )

    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        if (state.isLoading) {
            CircularProgressIndicator()
        }
        if (state.errorMessage.isNotEmpty()) {
            Text(text = "Error.", color = Color.Red)
        }
    }

}

@Composable
fun GameListView(
    games: List<GameListItem>,
    navController: NavController,
    pagerState: PagerState,
    viewModel: GameListViewModel
) {

    val context = LocalContext.current

    val screenHeight = context.resources.displayMetrics.heightPixels.dp /
            LocalDensity.current.density

    if (viewModel.isInternetAvailable(context = context) && games.isNotEmpty()){
        LaunchedEffect(Unit) {
            while (true) {
                delay(7500L)
                val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
                pagerState.scrollToPage(nextPage)
            }
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2)
    ) {
        header {
            val carouselItems = games.getUrls()
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
                        model = carouselItems[index].url,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .size(250.dp)
                            .padding(vertical = 8.dp, horizontal = 12.dp)
                            .align(alignment = Alignment.CenterHorizontally)
                            .clip(shape = MaterialTheme.shapes.medium)
                            .clickable {
                                navController.navigate("game_detail_screen/${carouselItems[index].id}")
                            },
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

        items(items = games) { game ->
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
}

data class CarouselItemData(val url: String, val id: Int)

fun List<GameListItem>.getUrls(): List<CarouselItemData> {
    return takeRandomElements(numberOfElements = 5).mapIndexed { index, gameListItem ->
        CarouselItemData(gameListItem.thumbnail,gameListItem.id)
    }
}
fun <T> List<T>.takeRandomElements(numberOfElements: Int): List<T> {
    return if (numberOfElements > size) this
    else asSequence().shuffled().take(numberOfElements).toList()
}
fun LazyGridScope.header(
    content: @Composable LazyGridItemScope.() -> Unit
) {
    item(
        span = { GridItemSpan(maxLineSpan) },
        content = content
    )
}