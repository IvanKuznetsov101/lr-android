package com.vsu.test.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.vsu.test.R
import com.vsu.test.Screen
import com.vsu.test.data.api.model.dto.ReviewWithProfile
import com.vsu.test.domain.model.ProfileWithDetails
import com.vsu.test.presentation.ui.components.BackButton
import com.vsu.test.presentation.ui.components.ReviewCard
import com.vsu.test.presentation.ui.components.StarRating
import com.vsu.test.presentation.viewmodel.ProfileViewModel
import com.vsu.test.presentation.viewmodel.ReviewsViewModel

@Composable
fun ReviewScreen(
    profileId: Long,
    onBackClick: () -> Unit,
    reviewsViewModel: ReviewsViewModel = hiltViewModel(),
    navController: NavController
) {
    val state by reviewsViewModel.reviewsState.collectAsState()
    LaunchedEffect(profileId) {
        reviewsViewModel.loadReviews(profileId)
    }

    when (state) {
        is ReviewsViewModel.ReviewsState.Loading -> LoadingScreen()
        is ReviewsViewModel.ReviewsState.Success -> ReviewScreenContent(
            (state as ReviewsViewModel.ReviewsState.Success).profile,
            (state as ReviewsViewModel.ReviewsState.Success).reviews,
            reviewsViewModel,
            onBackClick,
            navController
        )

        is ReviewsViewModel.ReviewsState.Error -> ErrorScreen((state as ProfileViewModel.ProfileState.Error).message)
        else -> Unit
    }
}

@Composable
fun ReviewScreenContent(
    profileWithDetails: ProfileWithDetails,
    reviews: List<ReviewWithProfile>?,
    reviewsViewModel: ReviewsViewModel,
    onBackClick: () -> Unit,
    navController: NavController
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Reviews",
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                AsyncImage(
                    model = profileWithDetails.profileImageUrl,
                    contentDescription = "Изображение профиля",
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.placeholder),
                    error = painterResource(R.drawable.placeholder),
                    imageLoader = reviewsViewModel.imageLoader
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(" ${profileWithDetails.profile.fullName} ")

                    Row {
                        StarRating(profileWithDetails.ratingWithCount.averageRating)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "(${profileWithDetails.ratingWithCount.count.toString()})",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }


            }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(items = reviews.orEmpty()) { review ->
                    ReviewCard(
                        review = review,
                        reviewsViewModel = reviewsViewModel,
                        onClick = {
                            val profileId = review.fromProfileId.toString()
                            navController.navigate(Screen.Profile.route(profileId))
                        })
                }
            }
        }
        BackButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 32.dp, y = (-40).dp),
            button = { onBackClick() }
        )
    }
}