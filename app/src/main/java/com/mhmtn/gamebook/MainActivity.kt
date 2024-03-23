package com.mhmtn.gamebook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.WebAsset
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mhmtn.gamebook.ui.theme.GamebookTheme
import com.mhmtn.gamebook.view.GameDetailScreen
import com.mhmtn.gamebook.view.GameListScreen
import com.mhmtn.gamebook.view.LatestGameScreen
import com.mhmtn.gamebook.view.PCGameScreen
import com.mhmtn.gamebook.view.WebGameScreen
import com.mhmtn.gamebook.viewmodel.PCGameViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()
            val navController = rememberNavController()
            GamebookTheme {
                ModalNavigationDrawer(
                    gesturesEnabled = true,
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet (
                            drawerShape = RectangleShape
                        ) {
                            Box(contentAlignment = Alignment.Center
                                ,modifier = Modifier
                                .fillMaxWidth()
                                ){
                                Image(
                                    modifier = Modifier.size(size = 200.dp),
                                    painter = painterResource(id = R.drawable.baseline_videogame_asset_24),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    alignment = Alignment.Center
                                )
                            }
                            NavigationDrawerItem(label = { Text(
                                text = "All Games",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            ) }, selected = false , onClick = {
                                scope.launch {
                                    drawerState.close()
                                    navController.navigate("games_list_screen")
                                }
                            },
                                modifier = Modifier
                                    .requiredHeight(45.dp),
                                icon = { Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                ) }
                            )
                            Spacer(modifier = Modifier.padding(8.dp))
                            NavigationDrawerItem(label = { Text(text = "PC Games", fontSize = 17.sp, fontWeight = FontWeight.Bold) }, selected = false , onClick = {
                                scope.launch {
                                    drawerState.close()
                                    navController.navigate("pc_games_filter_screen")
                                }
                            },
                                modifier = Modifier
                                    .requiredHeight(45.dp),
                                icon = { Icon(
                                    imageVector = Icons.Default.DesktopWindows,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                ) }
                            )
                            Spacer(modifier = Modifier.padding(8.dp))
                            NavigationDrawerItem(label = { Text(text = "Web Browser Games", fontSize = 17.sp, fontWeight = FontWeight.Bold) }, selected = false , onClick = {
                                scope.launch {
                                    drawerState.close()
                                    navController.navigate("web_games_filter_screen")
                                }
                            },
                                modifier = Modifier
                                    .requiredHeight(45.dp),
                                icon = { Icon(imageVector = Icons.Default.WebAsset, contentDescription = null, tint = MaterialTheme.colorScheme.primary ) }
                            )
                            Spacer(modifier = Modifier.padding(8.dp))
                            NavigationDrawerItem(label = { Text(text = "Latest Games", fontSize = 17.sp, fontWeight = FontWeight.Bold) }, selected = false , onClick = {
                                scope.launch {
                                    drawerState.close()
                                    navController.navigate("latest_games_filter_screen")
                                } },
                                modifier = Modifier
                                    .requiredHeight(45.dp),
                                icon = { Icon(
                                    imageVector = Icons.AutoMirrored.Default.TrendingUp,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                ) }
                            )
                        }
                    }
                ) {
                    Scaffold (
                        topBar = {
                            TopAppBar(title = {
                                Text(text = "Gamebook", textAlign = TextAlign.Center)
                            },  modifier = Modifier.fillMaxWidth(),
                                navigationIcon = {
                                    IconButton(onClick = {
                                        scope.launch {
                                            drawerState.open()
                                        }
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Menu,
                                            contentDescription = null
                                        )
                                    }
                                }
                            )
                        }
                    ) {
                        NavHost(navController = navController, startDestination = "games_list_screen",
                            modifier = Modifier.padding(it)){

                            composable("games_list_screen"){
                                GameListScreen(navController)
                            }

                            composable("game_detail_screen/{gameId}", arguments = listOf(
                                navArgument("gameId"){
                                    type = NavType.IntType
                                }
                            )){
                                val gameId = remember {
                                    it.arguments?.getInt("gameId")
                                }

                                GameDetailScreen(
                                    id = gameId ?: 0,
                                    navController=navController)
                            }
                            composable("pc_games_filter_screen"){
                                PCGameScreen(navController)
                            }
                            composable("web_games_filter_screen"){
                                WebGameScreen(navController)
                            }

                            composable("latest_games_filter_screen"){
                                LatestGameScreen(navController)
                            }

                        }
                    }
                }

            }
        }
    }
}

