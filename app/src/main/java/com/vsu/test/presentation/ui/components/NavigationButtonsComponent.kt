package com.vsu.test.presentation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vsu.test.R
import kotlin.random.Random


//@Composable
//fun LocationButton(modifier: Modifier = Modifier,
//                   onClick: () -> Unit) {
//    Row(
//        modifier = modifier
//            .height(56.dp)
//            .clip(RoundedCornerShape(28.dp))
//            .background(
//                Brush.horizontalGradient(
//                    listOf(Color(0xFF202044), Color(0xFF7B4D78))
//                )
//            )
//            .padding(horizontal = 16.dp),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Icon(
//            imageVector = Icons.Default.MyLocation,
//            contentDescription = "Location",
//            tint = Color.White,
//            modifier = Modifier.clickable(onClick = onClick)
//        )
//        Spacer(modifier = Modifier.width(24.dp))
//    }
//}
@Composable
fun LocationButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .width(82.dp)
            .height(56.dp)
            .clip(RoundedCornerShape(28.dp))
    ) {
        Image(
            painter = painterResource(id = R.drawable.stars_bacground),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(28.dp))
        )
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.MyLocation,
                contentDescription = "Location",
                tint = Color.White,
                modifier = Modifier.clickable(onClick = onClick)
            )
            Spacer(modifier = Modifier.width(24.dp))
        }
    }
}
@Composable
fun CombinedActions(
    modifier: Modifier = Modifier,
    leftButton: () -> Unit,
    rightButton: () -> Unit,
    leftIcon: ImageVector,
    rightIcon: ImageVector
) {
    Box(
        modifier = modifier
            .width(115.dp)
            .height(56.dp)
            .clip(RoundedCornerShape(28.dp)) // Делаем капсулу
    ) {
        Image(
            painter = painterResource(id = R.drawable.stars_bacground),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(28.dp))
        )
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = leftIcon,
                contentDescription = "leftButton",
                tint = Color.White,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { leftButton() }
            )
            Spacer(modifier = Modifier.width(12.dp))
            Icon(
                imageVector = rightIcon,
                contentDescription = "rightButton",
                tint = Color.White,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { rightButton() }
            )
        }
    }
}
@Composable
fun BackButton(
    modifier: Modifier = Modifier,
    button: () -> Unit
) {
    Box(
        modifier = modifier
            .width(82.dp)
            .height(56.dp)
            .clip(RoundedCornerShape(28.dp))
    ) {
        Image(
            painter = painterResource(id = R.drawable.stars_bacground),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(28.dp))
        )
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = "backButton",
                tint = Color.White,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { button() }
            )
        }
    }
}
@Composable
fun DefaultButton(
    onClick: () -> Unit,
    text: String,
    icon: ImageVector?
) {
    val offsetX = remember { Random.nextFloat() * 50f - 25f }
    val offsetY = remember { Random.nextFloat() * 50f - 25f }
    val scale = remember { 1.5f } // Увеличенный масштаб

    Box(
        modifier = Modifier
            .height(50.dp)
            .width(250.dp)
            .clip(RoundedCornerShape(64.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.star_background_max),
            contentDescription = null,
            contentScale = ContentScale.Crop, // Используем Crop, чтобы сохранить пропорции
            alignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(64.dp))
                .graphicsLayer(
                    translationX = offsetX,
                    translationY = offsetY,
                    scaleX = scale,
                    scaleY = scale
                )
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = "buttonIcon",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                fontSize = 16.sp,
                color = Color.White
            )
        }
    }
}