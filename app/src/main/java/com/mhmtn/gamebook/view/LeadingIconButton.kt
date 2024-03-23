package com.mhmtn.gamebook.view

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun LeadingIconButton(
    modifier: Modifier = Modifier,
    text:String,
    iconsize : Dp = 24.dp,
    onClick : () -> Unit
) {

    Button(onClick = { onClick() }) {
     
        Icon(imageVector = Icons.AutoMirrored.Filled.Login, contentDescription = "Play",
            modifier = Modifier
                .padding(end = 5.dp)
                .requiredSize(iconsize))

        Text(text = text,
            color = Color.White,
            modifier = Modifier.wrapContentHeight(align = Alignment.CenterVertically))
        
    }

}