package com.mhmtn.gamebook.view

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Web
import androidx.compose.material.icons.rounded.DesktopWindows
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.mhmtn.gamebook.model.GameDetail
import com.mhmtn.gamebook.util.Resource
import com.mhmtn.gamebook.viewmodel.GameDetailViewModel
import com.mhmtn.gamebook.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun GameDetailScreen(
    id:Int,
    navController: NavController,
    viewModel : GameDetailViewModel = hiltViewModel()
) {


    val game by produceState<Resource<GameDetail>>(initialValue = Resource.Loading()){
        value=viewModel.getGame(id = id)
    }

    val uriHandler = LocalUriHandler.current

    when(game){
        is Resource.Success -> {

            val gameItem = game.data!!
            val pagerState = rememberPagerState(pageCount = gameItem.screenshots.size)

            LaunchedEffect(Unit){
                while (true) {
                    delay(4000L)
                    val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
                    pagerState.scrollToPage(nextPage)
                }
            }
            val scope = rememberCoroutineScope()
            var showMore by remember { mutableStateOf(false) }

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                NavBar(
                    title = gameItem.title
                ){
                    navController.navigateUp()
                }

                Spacer(modifier = Modifier.height(height = 20.dp))
                
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(state = rememberScrollState())
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier.wrapContentSize()
                    ){
                    HorizontalPager(state = pagerState, modifier = Modifier
                        .wrapContentSize()
                    ) {currentPage->
                        Card (
                            modifier = Modifier
                                .wrapContentSize()
                                .padding(16.dp),
                            elevation = CardDefaults.cardElevation(8.dp)
                        ) {
                            SubcomposeAsyncImage(
                                model = gameItem.screenshots[currentPage].image,
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
                                            modifier = Modifier.constrainAs(indicatorRef){
                                                top.linkTo(parent.top)
                                                bottom.linkTo(parent.bottom)
                                                start.linkTo(parent.start)
                                                end.linkTo(parent.end)
                                            }
                                        )
                                    }
                                },
                                error = {
                                    Icon(imageVector = Icons.Default.Info, contentDescription = null,
                                        tint = Color.Red)
                                }

                            )
                        }
                    }
                        IconButton(
                            onClick = {
                                val nextPage = pagerState.currentPage + 1
                                if(nextPage < gameItem.screenshots.size ) {
                                    scope.launch {
                                        pagerState.scrollToPage(nextPage)
                                    }
                                }
                                      },
                            modifier = Modifier
                                .padding(30.dp)
                                .size(30.dp)
                                .align(Alignment.CenterEnd)
                                .clip(CircleShape),
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = Color(0x6F373737)
                            )
                        ) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next",
                                modifier = Modifier.fillMaxSize(),
                                tint = Color.LightGray)
                        }

                        IconButton(
                            onClick = {
                                val prevPage = pagerState.currentPage -1
                                if(prevPage >= 0 ) {
                                    scope.launch {
                                        pagerState.scrollToPage(prevPage)
                                    }
                                }
                            },
                            modifier = Modifier
                                .padding(30.dp)
                                .size(30.dp)
                                .align(Alignment.CenterStart)
                                .clip(CircleShape),
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = Color(0x52373737)
                            )
                        ) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Previous",
                                modifier = Modifier.fillMaxSize(),
                                tint = Color.LightGray)
                        }
                }

                    PageIndicator(
                        pageCount = gameItem.screenshots.size,
                        currentPage = pagerState.currentPage
                    )

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = "About ${gameItem.title} ",
                            style = MaterialTheme.typography.headlineLarge,
                            fontFamily = FontFamily(Font(R.font.acme)),
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(vertical = 10.dp)
                        )

                    Column (
                        modifier = Modifier
                            .animateContentSize(animationSpec = tween(100))
                            .clickable(interactionSource = remember {
                                MutableInteractionSource()
                            }, indication = null)
                            {showMore = !showMore}
                    ) {
                        if (showMore){
                            Text(
                                text = gameItem.description,
                                modifier = Modifier.padding(vertical = 4.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        } else
                        {
                            Text(
                                text = gameItem.description,
                                modifier = Modifier.padding(vertical = 4.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 4
                            )
                        }
                    }

                        Spacer(modifier = Modifier.height(30.dp))

                        Text(
                            text = "Extra ",
                            style = MaterialTheme.typography.headlineMedium,
                            fontFamily = FontFamily(Font(R.font.acme)),
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(vertical = 10.dp)
                        )

                        ExtraRow(
                            firstTitle =  "Title",
                            textColor = MaterialTheme.colorScheme.onSurface,
                            informationContent = "Developer")

                        ExtraRow(
                            firstTitle = gameItem.title,
                            textColor = MaterialTheme.colorScheme.onBackground,
                            informationContent = gameItem.developer)

                        Spacer(modifier = Modifier.height(20.dp))

                        ExtraRow(
                            firstTitle =  "Publisher",
                            textColor = MaterialTheme.colorScheme.onSurface,
                            informationContent = "Release Date")

                        ExtraRow(
                            firstTitle = gameItem.publisher,
                            textColor = MaterialTheme.colorScheme.onBackground,
                            informationContent = gameItem.release_date)

                        Spacer(modifier = Modifier.height(20.dp))

                        ExtraRow(
                            firstTitle =  "Genre",
                            textColor = MaterialTheme.colorScheme.onSurface,
                            informationContent = "Platform")

                        ExtraRow(
                            firstTitle = gameItem.genre,
                            textColor = MaterialTheme.colorScheme.onBackground,
                            informationContent = gameItem.platform,
                            icon = {
                                val resource = if(gameItem.platform.contains("windows", ignoreCase = true)){
                                    Icons.Default.DesktopWindows
                                }
                                else{
                                    Icons.Default.Web
                                }
                                Box(modifier = Modifier.padding(end = 5.dp)){
                                    Icon(imageVector = resource, contentDescription =  null,
                                        tint = Color.LightGray)
                                }
                            }
                            )

                        Spacer(modifier = Modifier.height(height = 30.dp))


                    gameItem.minimum_system_requirements?.let {

                            Text(
                                text = "Minimum System Requirements",
                                fontFamily = FontFamily(Font(R.font.acme)),
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(height = 20.dp))

                            Text(
                                text = "OS",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = it.os,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onBackground
                            )

                            Spacer(modifier = Modifier.height(height = 15.dp))

                            Text(
                                text = "Memory",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Text(
                                text = it.memory,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(height = 15.dp))

                            Text(
                                text = "Storage",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Text(
                                text = it.storage,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(height = 15.dp))

                            Text(
                                text = "Graphics",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Text(
                                text = it.graphics,
                                style =MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onBackground
                            )


                            Spacer(modifier = Modifier.height(height = 15.dp))
                            Text(
                                text = "All material on this page is copyrigthed by (${gameItem.developer}).",
                                fontSize = 11.sp ,
                                color = Color.Gray
                            )

                            Spacer(modifier = Modifier.padding(20.dp))

                        }

                        LeadingIconButton(
                            text = " Play The Game" ,
                            modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
                        ) {
                            uriHandler.openUri(gameItem.game_url)
                        }

                }

            }
        }

        is Resource.Error -> {

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = game.message!!)
            }
        }

        is Resource.Loading -> {

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
        }
    }

}

@Composable
fun PageIndicator(pageCount: Int, currentPage: Int) {

    Row (
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        repeat(pageCount){
            IndicatorDots(isSelected = it == currentPage)
        }
    }

}

@Composable
fun IndicatorDots(isSelected: Boolean) {

    val size = animateDpAsState(targetValue = if(isSelected) 12.dp else 10.dp, label = "")

    Box(modifier = Modifier
        .padding(2.dp)
        .size(size.value)
        .clip(CircleShape)
        .background(if (isSelected) Color(0xff373737) else Color(0xA8373737))
    )
    
}

@Composable
fun NavBar(
    title:String,
    onBackPress : () ->Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { onBackPress() }) {
            Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground
            )
        }

        Text(text = "Detail's of the $title")
        Spacer(modifier = Modifier.requiredWidth(26.dp))
    }


}

