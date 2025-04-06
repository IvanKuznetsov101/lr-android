package com.vsu.test.presentation.ui.components

import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.vsu.test.R
import com.vsu.test.presentation.ui.screens.LoadingScreen
import com.vsu.test.presentation.viewmodel.EventViewModel


@Composable
fun PhotoPicker(
    currentImages: CurrentImages,
    onAddUris: (List<Uri>) -> Unit,
    onRemoveUri: (Uri) -> Unit,
    onMarkUrlForDeletion: (String) -> Unit,
    eventViewModel: EventViewModel
) {
    when (currentImages) {
        is CurrentImages.Loading -> {
            LoadingScreen()
        }

        is CurrentImages.NoImages -> {
            val multipleLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 5)
            ) { uris ->
                onAddUris(uris)
            }
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        multipleLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray, RoundedCornerShape(8.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add image"
                    )
                }
            }
        }

        is CurrentImages.Images -> {
            val imagesUris = currentImages.imagesUris.orEmpty()
            val imagesUrls = currentImages.imagesUrls.orEmpty().filter { !it.isDelete }
            val totalImages = imagesUris.size + imagesUrls.size
            val maxSelectable = 5 - totalImages
            val canAddMore = maxSelectable > 0

            // Declare launchers as nullable variables
            var multipleLauncher: ManagedActivityResultLauncher<PickVisualMediaRequest, List<Uri>>? =
                null
            var singleLauncher: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>? = null

            // Create multiple launcher if 2 or more images can be selected
            if (maxSelectable >= 2) {
                multipleLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = maxSelectable)
                ) { uris ->
                    onAddUris(uris)
                }
            }


// Create single launcher if exactly 1 image can be selected
            if (maxSelectable == 1) {
                singleLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.PickVisualMedia()
                ) { uri ->
                    if (uri != null) {
                        onAddUris(listOf(uri))
                    }
                }
            }

            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Display existing URI-based images
                imagesUris.forEach { uri ->
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                            .clickable { onRemoveUri(uri) }
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(uri),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                        )
                    }
                }

                // Display existing URL-based images
                imagesUrls.forEach { imageUrl ->
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                            .clickable { onMarkUrlForDeletion(imageUrl.imageUrl) }
                    ) {
                        AsyncImage(
                            model = imageUrl.imageUrl,
                            contentDescription = "Изображение мероприятия",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(R.drawable.placeholder),
                            error = painterResource(R.drawable.placeholder),
                            imageLoader = eventViewModel.imageLoader
                        )
                    }
                }

                // Show add button if more images can be added
                if (canAddMore) {
                    IconButton(
                        onClick = {
                            if (maxSelectable >= 2) {
                                multipleLauncher?.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            } else if (maxSelectable == 1) {
                                singleLauncher?.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                        },
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.LightGray, RoundedCornerShape(8.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add image"
                        )
                    }
                }
            }
        }

        else -> {}
    }
}