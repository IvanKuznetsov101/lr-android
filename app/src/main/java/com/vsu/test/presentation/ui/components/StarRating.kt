package com.vsu.test.presentation.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType.Companion.Text

@Composable
fun StarRating(rating: Double, maxStars: Int = 5) {
    Row {
        val fullStars = rating.toInt()
        val hasHalfStar = rating - fullStars >= 0.5
        val emptyStars = maxStars - fullStars - if (hasHalfStar) 1 else 0

        repeat(fullStars) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = "Full Star",
                tint = Color.Black
            )
        }

        if (hasHalfStar) {
            Icon(
                imageVector = Icons.Filled.StarHalf,
                contentDescription = "Half Star",
                tint = Color.Black
            )
        }

        repeat(emptyStars) {
            Icon(
                imageVector = Icons.Outlined.Star,
                contentDescription = "Empty Star",
                tint = Color.Gray
            )
        }
    }
}