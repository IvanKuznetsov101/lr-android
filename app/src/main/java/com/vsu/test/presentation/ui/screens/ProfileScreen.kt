package com.vsu.test.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.vsu.test.R
import com.vsu.test.domain.model.ProfileWithDetails
import com.vsu.test.presentation.ui.components.StarRating
import com.vsu.test.presentation.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel = hiltViewModel(),
    profileId: Long,
    isOwnProfile: Boolean,
    onEditProfile: () -> Unit
) {

    val state by profileViewModel.profileState.collectAsState()

    LaunchedEffect(profileId) {
        profileViewModel.loadProfile(profileId)
    }

    when (state) {
        is ProfileViewModel.ProfileState.Loading -> LoadingScreen()
        is ProfileViewModel.ProfileState.Success -> ProfileContent(profileViewModel, (state as ProfileViewModel.ProfileState.Success).profile, isOwnProfile, onEditProfile)
        is ProfileViewModel.ProfileState.Error -> ErrorScreen((state as ProfileViewModel.ProfileState.Error).message)
    }
}
@Composable
fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
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
    onEditProfile: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
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
                .size(80.dp)
                .clip(CircleShape),

            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.placeholder),
            error = painterResource(R.drawable.placeholder),
            imageLoader = profileViewModel.imageLoader

        )
        Text(
            text = profile.profile.fullName,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "@${profile.profile.username}",
            fontWeight = FontWeight.Normal,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(){
            StarRating(profile.ratingWithCount.averageRating)
            Text(profile.ratingWithCount.count.toString())
        }


        if (isOwnProfile) {
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onEditProfile) {
                Text("Edit Profile")
            }
        }
    }
}