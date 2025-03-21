package com.vsu.test.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun LocationButton(modifier: Modifier = Modifier,
                   onClick: () -> Unit) {
    Row(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFF202044), Color(0xFF7B4D78))
                )
            )
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
@Composable
fun CombinedActions(modifier: Modifier = Modifier,
                            leftButton: () -> Unit,
                            rightButton: () -> Unit,
                            leftIcon: ImageVector,
                            rightIcon: ImageVector
) {
    Row(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(28.dp)) // 28.dp = половина высоты, даёт форму овала
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFF202044), Color(0xFF7B4D78))
                )
            )
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
            contentDescription = "leftButton",
            tint = Color.White,
            modifier = Modifier
                .size(24.dp)
                .clickable { rightButton() }
        )
        Spacer(modifier = Modifier.width(30.dp))

    }
}
@Composable
fun BackButton(modifier: Modifier = Modifier,
                    button: () -> Unit
) {
    Row(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFF202044), Color(0xFF7B4D78))
                )
            )
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
        Spacer(modifier = Modifier.width(32.dp))
    }
}
@Composable
fun DefaultButton(onClick:() -> Unit, text: String){
    Button(modifier = Modifier
        .height(50.dp)
        .width(300.dp)
        .background(
            Brush.horizontalGradient(
                listOf(Color(0xFF202044), Color(0xFF7B4D78))
            ),
            shape = RoundedCornerShape(64.dp)
        ),

        onClick = onClick
    ) {
        Text(
            text = text,
            fontSize = 16.sp)
    }
}
