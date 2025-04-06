package com.vsu.test.presentation.ui.components
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.vsu.test.R
import com.vsu.test.data.api.model.dto.ReviewWithProfile
import com.vsu.test.presentation.viewmodel.ReviewsViewModel

@Composable
fun ReviewCard(
    review: ReviewWithProfile,
    reviewsViewModel: ReviewsViewModel,
    onClick:() -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .shadow(2.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable{onClick()},
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )

    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Верхняя строка с аватаркой, именем и датой
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = review.image,
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.placeholder),
                    error = painterResource(R.drawable.placeholder),
                    imageLoader = reviewsViewModel.imageLoader
                )

                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = review.fullName,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = review.createdAt?: "",
                        color = Color.Gray
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            StarRating(review.rating.toDouble())
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = review.text,
            )
        }
    }
}