package com.mhmtn.gamebook.view

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Web
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import com.mhmtn.gamebook.model.GameListItem

@Composable
fun GameFilterCard(
    game:GameListItem,
    navController: NavController
) {
    Card(
        shape = MaterialTheme.shapes.small,
        modifier = Modifier
            .padding(8.dp)
            .clickable { navController.navigate("game_detail_screen/${game.id}") }
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface)
    ) {

        Row (
            modifier = Modifier.fillMaxSize()
        ){
            SubcomposeAsyncImage(
                model = game.thumbnail,
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxHeight(fraction = 0.3f),
                loading = {
                    ConstraintLayout(modifier = Modifier.fillMaxSize(fraction = 0.5f)) {
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
                })

            Column {
                Text(text = game.title,
                    maxLines = 1,
                    modifier = Modifier
                        .padding(5.dp)
                        .align(alignment = Alignment.CenterHorizontally)
                        .basicMarquee(),
                    style = MaterialTheme.typography.bodyMedium,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.padding(3.dp))
                Text(text = game.short_description,
                    modifier = Modifier
                        .fillMaxWidth(fraction = 0.85f)
                        .padding(all = 5.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.padding(10.dp))

                Row (
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(end = 5.dp)
                ){
                    Surface(modifier = Modifier.border(
                        width = 1.dp,
                        shape = RoundedCornerShape(25.dp),
                        color = MaterialTheme.colorScheme.primary
                    ),
                        shape = RoundedCornerShape(25.dp),
                        color = MaterialTheme.colorScheme.primary,

                        ) {
                        Text(
                            modifier = Modifier.padding(all =5.dp),
                            text = game.genre,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.padding(end = 3.dp))
                    val resource = if(game.platform.contains("windows", ignoreCase = true)){
                        Icons.Default.DesktopWindows
                    }
                    else{
                        Icons.Default.Web
                    }

                    Icon(imageVector = resource, contentDescription =  null,
                        tint = MaterialTheme.colorScheme.onPrimary)

                }
            }
        }
    }
}