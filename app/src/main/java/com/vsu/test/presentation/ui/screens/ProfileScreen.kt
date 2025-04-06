package com.vsu.test.presentation.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.vsu.test.R
import com.vsu.test.Screen
import com.vsu.test.data.api.model.dto.LastEvent
import com.vsu.test.domain.model.ProfileWithDetails
import com.vsu.test.presentation.ui.components.BackButton
import com.vsu.test.presentation.ui.components.CombinedActions
import com.vsu.test.presentation.ui.components.LastEventCard
import com.vsu.test.presentation.ui.components.ReviewDialog
import com.vsu.test.presentation.ui.components.StarRating
import com.vsu.test.presentation.viewmodel.ProfileViewModel
import com.vsu.test.presentation.viewmodel.ReviewsViewModel

@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel = hiltViewModel(),
    reviewsViewModel: ReviewsViewModel = hiltViewModel(),
    profileId: Long,
    isOwnProfile: Boolean,
    onEditProfile: () -> Unit,
    onBackButton: () -> Unit,
    navController: NavController
) {
    val context = LocalContext.current
    val state by profileViewModel.profileState.collectAsState()
    var showReview by remember { mutableStateOf(false) }

    LaunchedEffect(profileId) {
        profileViewModel.loadProfile(profileId, isOwnProfile)
    }

    when (state) {
        is ProfileViewModel.ProfileState.Loading -> LoadingScreen()
        is ProfileViewModel.ProfileState.Success -> ProfileContent(
            profileViewModel,
            (state as ProfileViewModel.ProfileState.Success).profile,
            isOwnProfile,
            onEditProfile,
            onBackButton,
            navController,
            (state as ProfileViewModel.ProfileState.Success).events,
            reviewsViewModel,
            showReview = showReview,
            onShowReviewChange = { showReview = it },
            context = context
        )

        is ProfileViewModel.ProfileState.Error -> ErrorScreen((state as ProfileViewModel.ProfileState.Error).message)
    }

}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(50.dp),
            color = Color(0xFF000000),
            strokeWidth = 12.dp
        )
    }
}

@Composable
fun ErrorScreen(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Ошибка: $message", color = Color.Red)
    }
}

@Composable
fun ProfileContent(
    profileViewModel: ProfileViewModel,
    profile: ProfileWithDetails,
    isOwnProfile: Boolean,
    onEditProfile: () -> Unit,
    onBackButton: () -> Unit,
    navController: NavController,
    lastEvents: List<LastEvent>?,
    reviewsViewModel: ReviewsViewModel,
    showReview: Boolean,
    onShowReviewChange: (Boolean) -> Unit,
    context: Context
) {
    var combinedVisible by remember { mutableStateOf<Boolean>(true) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        if (isOwnProfile) {
            if (combinedVisible) {
                CombinedActions(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = 32.dp, y = (-40).dp)
                        .zIndex(1f),
                    leftButton = onEditProfile,
                    rightButton = onBackButton,
                    Icons.Default.Edit,
                    Icons.Default.ArrowBack
                )
            }

        } else {
            BackButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 32.dp, y = (-40).dp)
                    .zIndex(1f),
                button = { onBackButton() }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .zIndex(0f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Profile",
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            AsyncImage(
                model = profile.profileImageUrl,
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(350.dp)
                    .clip(RoundedCornerShape(40.dp)),

                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.profile_image_placeholder),
                error = painterResource(R.drawable.placeholder),
                imageLoader = profileViewModel.imageLoader

            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = profile.profile.fullName,
                fontWeight = FontWeight.Normal,
                fontSize = 25.sp
            )
            Text(
                text = "@${profile.profile.username}",
                fontWeight = FontWeight.Normal,
                color = Color.Gray,
                fontSize = 15.sp
            )

            Row(modifier = Modifier
                .clickable {
                    val profileId = profile.profile.id.toString()
                    navController.navigate(Screen.Reviews.route(profileId))
                }) {
                StarRating(profile.ratingWithCount.averageRating)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "(${profile.ratingWithCount.count.toString()})",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }


            if (isOwnProfile) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Recent events attended",
                    color = Color.Gray,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal
                )
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(4.dp)
                ) {
                    items(items = lastEvents.orEmpty()) { event ->
                        LastEventCard(
                            event = event,
                            profileViewModel = profileViewModel,
                            onClick = {
                                reviewsViewModel.loadReviewForEdit(event.profileId)
                                onShowReviewChange(!showReview)
                            })
                    }
                }
            }
        }
        if (showReview) {
            combinedVisible = false
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable {
                        onShowReviewChange(!showReview)
                        combinedVisible = true
                    }


            )

            AnimatedVisibility(
                visible = showReview,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {

                ReviewDialog(
                    modifier = Modifier
                        .fillMaxSize()
                        .zIndex(2f),
                    onSuccess = {
                        onShowReviewChange(!showReview)
                        combinedVisible = true
                    },
                    onDismiss = {
                        onShowReviewChange(!showReview)
                        combinedVisible = true
                    },
                    reviewsViewModel = reviewsViewModel,
                    navController = navController,
                    context = context
                )

            }
        }


    }
}