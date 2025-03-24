package com.vsu.test.presentation.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.vsu.test.MainActivity
import com.vsu.test.R
import com.vsu.test.data.api.model.dto.EventDTO
import com.vsu.test.domain.usecase.MoreState
import com.vsu.test.presentation.ui.components.CombinedActions
import com.vsu.test.presentation.ui.components.EventCard
import com.vsu.test.presentation.viewmodel.EventViewModel
import com.vsu.test.presentation.viewmodel.MoreViewModel
import com.vsu.test.service.LocationService

@Composable
fun MoreScreen(
    viewModel: MoreViewModel = hiltViewModel(),
    eventViewModel: EventViewModel = hiltViewModel(),
    onNavigateToSettings: () -> Unit,
    onNavigateToMap: () -> Unit
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState() // Слушаем состояние из ViewModel

    // Инициализация состояния только один раз при первом рендере
    LaunchedEffect(Unit) {
        if (state is MoreState.NoEvents) { // Вызываем обновление только если состояние не инициализировано
            viewModel.updateState(context)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "More",
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            ContentBox(state = state, viewModel = viewModel, eventViewModel = eventViewModel, context = context)
            Spacer(modifier = Modifier.height(16.dp))
        }
        CombinedActions(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 32.dp, y = (-40).dp),
            leftButton = onNavigateToSettings,
            rightButton = onNavigateToMap,
            leftIcon = Icons.Default.Settings,
            rightIcon = Icons.Default.ArrowBackIosNew
        )
    }
}

@Composable
private fun ContentBox(
    state: MoreState,
    viewModel: MoreViewModel,
    eventViewModel: EventViewModel,
    context: Context
) {
    Box(
        modifier = Modifier
            .height(700.dp)
            .width(320.dp)
            .shadow(elevation = 16.dp, shape = RoundedCornerShape(64.dp), clip = true)
            .background(colorResource(R.color.customGray), shape = RoundedCornerShape(64.dp))
            .clip(RoundedCornerShape(64.dp))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            when (state) {
                is MoreState.UserEvent -> {
                    EventCard(
                        eventDTO = state.event,
                        textOnButton = "Delete",
                        eventViewModel = eventViewModel,
                        visitorCount = 999,
                        onClickButton = {
                            viewModel.deleteLightRoomByEventId(state.event.id, context)
                        },
                        onClickCard = {}
                    )
                }
                is MoreState.EventsInRadius -> {
                    EventCarouselScreen(events = state.events, eventViewModel = eventViewModel)
                }
                is MoreState.NoEvents -> {
                    PlaceholderContent()
                }
            }
        }
    }
}

@Composable
private fun EventCarouselScreen(
    events: List<EventDTO>,
    eventViewModel: EventViewModel
) {
    val pagerState = rememberPagerState { events.size }

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { page ->
        val event = events[page]
        EventCard(
            eventDTO = event,
            textOnButton = "Join",
            eventViewModel = eventViewModel,
            visitorCount = 999,
            onClickButton = { /* Handle join logic */ },
            onClickCard = { /* Handle card click */ }
        )
    }
}

@Composable
private fun PlaceholderContent() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.placeholder),
            contentDescription = "Empty placeholder",
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.Crop
        )
        Text("Тут пока пусто", fontSize = 16.sp)
    }
}