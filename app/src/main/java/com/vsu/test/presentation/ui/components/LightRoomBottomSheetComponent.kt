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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
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
import com.vsu.test.domain.model.EventWithDetails
import com.vsu.test.presentation.viewmodel.EventViewModel
import com.vsu.test.presentation.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LightRoomBottomSheetHandler(
    eventWithDetails: EventWithDetails,
    profileViewModel: ProfileViewModel,
    eventViewModel: EventViewModel,
    endsAfter: String,
    onDismiss: () -> Unit
) {

    val isLoading by eventViewModel.loading.collectAsState()

    ModalBottomSheet(onDismissRequest = onDismiss) {
        if (isLoading) {
            LoadingIndicator()
        } else {
            LightRoomBottomSheetContent(
                eventWithDetails = eventWithDetails,
                imagesUrls = eventWithDetails.eventImagesUrls ?: emptyList(),
                eventViewModel = eventViewModel,
                endsAfter = endsAfter,
                profileViewModel  = profileViewModel,
            )
        }
    }
}

@Composable
fun LightRoomBottomSheetContent(
    eventWithDetails: EventWithDetails,
    imagesUrls: List<String>,
    eventViewModel: EventViewModel,
    endsAfter: String,
    profileViewModel: ProfileViewModel
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            val pagerState = rememberPagerState(initialPage = 0) { imagesUrls.size }
            Box(
                modifier = Modifier
                    .weight(7f)
                    .fillMaxWidth()
            ) {
                HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
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
                        val color = if (pagerState.currentPage == index) Color.Black else Color.Gray
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
                Row(modifier = Modifier.fillMaxWidth()) {
                    AsyncImage(
                        model = eventWithDetails.profileWithDetails.profileImageUrl,
                        contentDescription = "Изображение профиля",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(R.drawable.placeholder),
                        error = painterResource(R.drawable.placeholder),
                        imageLoader = profileViewModel.imageLoader
                    )
                    Text(eventWithDetails.profileWithDetails.profile.fullName)
                    StarRating(eventWithDetails.profileWithDetails.ratingWithCount.averageRating)
                    Text(eventWithDetails.profileWithDetails.ratingWithCount.count.toString())
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = eventWithDetails.event.title ?: "Без названия",
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.Black
                        )
                    )
                    val ageLimit = eventWithDetails.event.ageLimit.toString()
                    Text(
                        text = ("Возрастное ограничение:  $ageLimit"),
                        style = TextStyle(
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    )
                }
                Text(
                    text = "${eventWithDetails.visitorsCount} people",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Gray
                )

                Text(
                    text = "ends after: ${endsAfter}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = eventWithDetails.event.description ?: "Описание отсутствует",
                    style = TextStyle(fontSize = 14.sp, color = Color.Black),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
    }
}