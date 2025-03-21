package com.vsu.test.presentation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vsu.test.R
import com.vsu.test.data.api.model.dto.EventDTO

@Composable
fun ListItem(
    eventDTO: EventDTO,
    onClick: () -> Unit,
    onCloseClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.ic_lightroom),
            contentDescription = "Изображение события",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = eventDTO.title ?: "Без названия",
                fontSize = 20.sp,
                color = Color.Black
            )
            Text(
                text = eventDTO.description ?: "Описание отсутствует",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Удалить",
            modifier = Modifier
                .size(24.dp)
                .clickable { onCloseClick() },
            tint = Color.Black
        )
    }
}

