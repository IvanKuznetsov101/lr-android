package com.vsu.test.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.vsu.test.R
import com.vsu.test.Screen
import com.vsu.test.domain.model.EventWithDetails
import com.vsu.test.presentation.viewmodel.EventViewModel
import com.vsu.test.presentation.viewmodel.ProfileViewModel
import com.yandex.mapkit.logo.VerticalAlignment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LightRoomBottomSheetHandler(
    eventWithDetails: EventWithDetails,
    profileViewModel: ProfileViewModel,
    eventViewModel: EventViewModel,
    endsAfter: String,
    onDismiss: () -> Unit,
    navController: NavController
) {
    val isLoading by eventViewModel.loading.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(sheetState) {
        sheetState.show() // Показываем bottom sheet сразу
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        sheetState = sheetState,
        modifier = Modifier.fillMaxHeight() // Оставляем, чтобы bottom sheet мог быть полноэкранным
    ) {
        if (isLoading) {
            LoadingIndicator()
        } else {
            LightRoomBottomSheetContent(
                eventWithDetails = eventWithDetails,
                imagesUrls = eventWithDetails.eventImagesUrls ?: emptyList(),
                eventViewModel = eventViewModel,
                endsAfter = endsAfter,
                profileViewModel = profileViewModel,
                navController = navController
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
    profileViewModel: ProfileViewModel,
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight() // Заполняем доступную высоту
            .verticalScroll(rememberScrollState()) // Прокрутка всего контента
            .padding(16.dp)
    ) {
        // Секция с изображениями
        val pagerState = rememberPagerState(initialPage = 0) { imagesUrls.size }
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                AsyncImage(
                    model = imagesUrls[page],
                    contentDescription = "Изображение мероприятия",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(40.dp)),
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
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(imagesUrls.size) { index ->
                    val colorIn = if (pagerState.currentPage == index) Color.Black else Color.White
                    val colorAround = if (pagerState.currentPage == index) Color.White else Color.Black
                    Box(
                        modifier = Modifier
                            .size(16.dp) // внешний круг
                            .clip(CircleShape)
                            .background(colorAround),
                        contentAlignment = Alignment.Center // центрируем внутренний круг
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp) // внутренний круг меньше
                                .clip(CircleShape)
                                .background(colorIn)
                        )
                    }

                }
            }
        }


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val profileId = eventWithDetails.profileWithDetails.profile.id.toString()
                        navController.navigate(Screen.Profile.route(profileId))
                    }
            ) {
                AsyncImage(
                    model = eventWithDetails.profileWithDetails.profileImageUrl,
                    contentDescription = "Изображение профиля",
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.profile_image_placeholder),
                    error = painterResource(R.drawable.placeholder),
                    imageLoader = profileViewModel.imageLoader
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column() {
                    Text(eventWithDetails.profileWithDetails.profile.fullName)
                    Row {
                        StarRating(rating = eventWithDetails.profileWithDetails.ratingWithCount.averageRating)
                        Text("(${eventWithDetails.profileWithDetails.ratingWithCount.count})")
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Column(
                Modifier
                    .shadow(elevation = 4.dp, shape = RoundedCornerShape(24.dp), clip = true)
                    .background(color = Color.White, shape = RoundedCornerShape(24.dp))
                    .clip(RoundedCornerShape(24.dp))
                    .padding(12.dp)
            )
            {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = eventWithDetails.event.title ?: "Без названия",
                        style = TextStyle(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 34.sp,
                            color = Color.Black
                        )
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    val ageLimit = eventWithDetails.event.ageLimit.toString()
                    Column() {
                        Text(
                            text = "$ageLimit+",
                            style = TextStyle(
                                fontWeight = FontWeight.Normal,
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        )
                        Text(
                            text = "${eventWithDetails.visitorsCount} people",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.Gray
                        )
                    }

                }
                Text(
                    text = "Ends after: $endsAfter",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }


            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = eventWithDetails.event.description ?: "Описание отсутствует",
                style = TextStyle(fontSize = 16.sp, color = Color.Black),
                modifier = Modifier.padding(top = 8.dp)
            )
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