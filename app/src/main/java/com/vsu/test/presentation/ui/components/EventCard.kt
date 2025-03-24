package com.vsu.test.presentation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.vsu.test.R
import com.vsu.test.data.api.model.dto.EventDTO
import com.vsu.test.presentation.viewmodel.EventViewModel


@Composable
fun EventCard(eventDTO: EventDTO,
              textOnButton: String,
              eventViewModel: EventViewModel,
              visitorCount: Int,
              onClickButton: ()-> Unit,
              onClickCard: ()-> Unit)
{
    val imagesUrls by eventViewModel.selectedEventImagesUrls.collectAsState(initial = emptyList())
    val isLoading by eventViewModel.loading.collectAsState(initial = false)
    val event by eventViewModel.event.collectAsState()


    eventViewModel.getImagesByEventId(eventDTO.id)
    if (isLoading) {
        Box() {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    } else {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            onClick = { onClickCard() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                Box {
                    AsyncImage(
                        model = imagesUrls.firstOrNull() ?: R.drawable.placeholder,
                        contentDescription = "Изображение мероприятия",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(R.drawable.placeholder),
                        error = painterResource(R.drawable.placeholder),
                        imageLoader = eventViewModel.imageLoader
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .align(Alignment.BottomCenter)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.White)
                                )
                            )
                    )
                }

                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = eventDTO.title.toString(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "$visitorCount people",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(8.dp))


                    Text(
                        text = eventDTO.description.toString(),
                        maxLines = 6, // Ограничение по количеству строк
                        overflow = TextOverflow.Ellipsis, // Добавляет ... если текст длинный
                        fontSize = 14.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "ends after: 999 hours",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { onClickButton() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                    ) {
                        Text(text = textOnButton, color = Color.White)
                    }
                }
            }
        }
    }

}

