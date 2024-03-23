package com.mhmtn.gamebook.view

import androidx.compose.foundation.background
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
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.mhmtn.gamebook.model.GameListItem
import com.mhmtn.gamebook.viewmodel.GameListViewModel

@Composable
fun GameListScreen(
    navController: NavController,
    viewModel: GameListViewModel = hiltViewModel()
) {

    Column(modifier = Modifier.fillMaxSize()) {

        SearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            viewModel.searchGameList(it)
        }

        GameList(navController = navController)
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
    viewModel: GameListViewModel = hiltViewModel()
) {
    val gameList = viewModel.gameList
    val error = remember { viewModel.errorMessage }
    val isLoading = remember { viewModel.isLoading }
    val pagerState = rememberPagerState(pageCount = { viewModel.gameList.value.getUrls().size })

    GameListView(
        games = gameList.value,
        navController = navController,
        pagerState = pagerState,
        viewModel = viewModel
    )

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
fun GameListView(
    games: List<GameListItem>,
    navController: NavController,
    pagerState: PagerState,
    viewModel: GameListViewModel
) {

    val screenHeight = LocalContext.current.resources.displayMetrics.heightPixels.dp /
            LocalDensity.current.density

    LazyVerticalGrid(
        columns = GridCells.Fixed(2)
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
        
        items(items = games) { game ->
            GameCard(
                modifier = Modifier
                    .padding(all = 8.dp)
                    .requiredHeight(height = screenHeight * 0.45f),
                game = game,
                navController = navController
            )
        }
    }
}

fun List<GameListItem>.getUrls(): List<String> {
    return takeRandomElements(numberOfElements = 5).map { it.thumbnail }
}

fun <T> List<T>.takeRandomElements(numberOfElements: Int): List<T> {
    return if (numberOfElements > size) this
    else asSequence().shuffled().take(numberOfElements).toList()
}

fun LazyGridScope.header(
    content : @Composable LazyGridItemScope.() -> Unit
){
    item(
        span = { GridItemSpan(maxLineSpan) },
        content = content
    )
}