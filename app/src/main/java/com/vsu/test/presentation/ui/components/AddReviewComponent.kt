package com.vsu.test.presentation.ui.components

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable

import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Column


import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.vsu.test.R
import com.vsu.test.Screen
import com.vsu.test.presentation.viewmodel.ReviewsViewModel
@Composable
fun ReviewDialog(
    modifier: Modifier,
    onDismiss: () -> Unit,
    reviewsViewModel: ReviewsViewModel,
    onSuccess: () -> Unit,
    navController: NavController,
    context: Context
) {
    val reviewsState by reviewsViewModel.reviewsState.collectAsState()
    when (val state = reviewsState) {

        is ReviewsViewModel.ReviewsState.EditReview -> {
            ReviewDialogContent(
                reviewsViewModel = reviewsViewModel,
                onSuccess = { onSuccess() },
                onDismiss = onDismiss,
                navController = navController,
                modifier = modifier
            )
        }
        is ReviewsViewModel.ReviewsState.Error -> {
            Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()

        }
        ReviewsViewModel.ReviewsState.Loading ->
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        else -> Unit
    }
}

@Composable
fun ReviewDialogContent(
    reviewsViewModel: ReviewsViewModel,
    onSuccess: () -> Unit,
    onDismiss: () -> Unit,
    navController: NavController,
    modifier: Modifier
){
    val reviewsState by reviewsViewModel.reviewsState.collectAsState()
    val reviewWithProfile = (reviewsState as? ReviewsViewModel.ReviewsState.EditReview)?.review ?: return
    val errorMessage = (reviewsState as? ReviewsViewModel.ReviewsState.EditReview)?.validationErrorMessage
    Box(
        modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable { onDismiss() }
        )
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp)

        ) {
            Surface(
                shape = RoundedCornerShape(40.dp),
                color = Color.White,
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .wrapContentSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Leave a review",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Normal
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            val profileId = reviewWithProfile.toProfileId.toString()
                            navController.navigate(Screen.Profile.route(profileId))
                        }
                    ) {
                        AsyncImage(
                            model = reviewWithProfile.image,
                            contentDescription = "Profile Image",
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(R.drawable.placeholder),
                            error = painterResource(R.drawable.placeholder),
                            imageLoader = reviewsViewModel.imageLoader
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Text(
                            text = reviewWithProfile.fullName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row {
                        for (i in 1..5) {
                            Icon(
                                imageVector = if (i <= reviewWithProfile.rating) Icons.Filled.Star else Icons.Outlined.StarOutline,
                                contentDescription = "Звезда $i",
                                tint = Color.Black,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clickable { reviewsViewModel.updateRating(i) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = reviewWithProfile.text,
                        onValueChange = { reviewsViewModel.updateText(it)},
                        label = { Text("Your feedback") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(32.dp),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Black,
                            unfocusedIndicatorColor = Color.Gray,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            cursorColor = Color.White,
                            focusedLabelColor = Color.Black,
                        )
                    )
                    if (errorMessage != null){
                        Spacer(modifier = Modifier.height(16.dp))
                        ErrorMessageBox(errorMessage)
                    }
                }
            }
        }
        CombinedActions(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 32.dp, y = (-40).dp)
                .zIndex(1f),
            leftButton = { reviewsViewModel.submitReview {
                onSuccess()
            } },
            rightButton = { onDismiss() },
            Icons.Default.Done,
            Icons.Default.ArrowBack
        )
    }
}