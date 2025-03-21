package com.vsu.test.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.vsu.test.R
import com.vsu.test.data.api.model.dto.LightRoomDTO
import com.vsu.test.presentation.viewmodel.EventViewModel

@Composable
fun LightRoomBottomSheetContent(lightRoomDTO: LightRoomDTO,
                                eventViewModel: EventViewModel
) {

    val imagesUrls by eventViewModel.selectedEventImagesUrls.collectAsState(initial = emptyList())
    val isLoading by eventViewModel.loading.collectAsState(initial = false)
    val event by eventViewModel.event.collectAsState()

    LaunchedEffect(lightRoomDTO.id) {
        eventViewModel.getEventByLightRoomId(lightRoomDTO.id)
    }
    LaunchedEffect (event.id ){
        if(event.id != 0L)
            eventViewModel.getImagesByEventId(event.id)
    }
    if (isLoading) {
        Box(Modifier.fillMaxWidth()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                val pagerState = rememberPagerState(initialPage = 0) {
                    imagesUrls.size
                }
                Box(
                    modifier = Modifier
                        .weight(7f)
                        .fillMaxWidth()
                ) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        AsyncImage(
                            model = imagesUrls[page],
                            contentDescription = "Изображение мероприятия",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(R.drawable.placeholder),
                            error = painterResource(R.drawable.placeholder),
                            imageLoader = eventViewModel.imageLoader
                        )
                    }
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        repeat(imagesUrls.size) { index ->
                            val color =
                                if (pagerState.currentPage == index) Color.Black else Color.Gray
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .padding(2.dp)
                            )
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .weight(3f)
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = event.title.toString(),
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color.Black
                            )
                        )
                        Text(
                            text = event.ageLimit.toString(),
                            style = TextStyle(
                                fontWeight = FontWeight.Normal,
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        )
                    }
                    Text(
                        text = event.description.toString(),
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = Color.Black
                        ),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

        }
    }
}

