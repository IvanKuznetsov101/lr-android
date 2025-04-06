package com.vsu.test.presentation.ui.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.vsu.test.R
import com.vsu.test.data.api.model.dto.EventDTO
import com.vsu.test.domain.model.EventWithDetails
import com.vsu.test.domain.usecase.MoreState
import com.vsu.test.presentation.ui.components.CombinedActions
import com.vsu.test.presentation.ui.components.EventCard
import com.vsu.test.presentation.ui.components.LightRoomBottomSheetHandler
import com.vsu.test.presentation.viewmodel.EventViewModel
import com.vsu.test.presentation.viewmodel.MoreViewModel
import com.vsu.test.presentation.viewmodel.ProfileViewModel
import com.vsu.test.utils.TimeUtils

@Composable
fun MoreScreen(
    viewModel: MoreViewModel = hiltViewModel(),
    eventViewModel: EventViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel(),
    onNavigateToSettings: () -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToProfile:() -> Unit,
    navController: NavController,
) {

    val context = LocalContext.current
    val state by viewModel.state.collectAsState()


    LaunchedEffect(Unit) {
        if (state is MoreState.NoEvents) {
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
            ContentBox(state = state,
                viewModel = viewModel,
                eventViewModel = eventViewModel,
                profileViewModel = profileViewModel,
                context = context,
                navController = navController)
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
    profileViewModel: ProfileViewModel,
    context: Context,
    navController: NavController
) {
    val isSheetOpen = remember { mutableStateOf(false) } // Управляем состоянием
    val selectedEvent = remember { mutableStateOf<EventDTO?>(null) } // Выбранное событие

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
                        imagesUrls = state.eventWithDetails.eventImagesUrls ?: emptyList(),
                        eventDTO = state.eventWithDetails.event,
                        textOnButton = "Delete",
                        eventViewModel = eventViewModel,
                        visitorCount = state.eventWithDetails.visitorsCount,
                        onClickButton = {
                            viewModel.deleteLightRoomById(state.eventWithDetails.lightRoom.id, context)
                        },
                        onClickCard = {
                            isSheetOpen.value = true
                            selectedEvent.value = state.eventWithDetails.event
                        },
                        endsAfter = TimeUtils.formatTimeDifference(state.eventWithDetails.lightRoom.endTime)
                    )
                    if (isSheetOpen.value) {
                        LightRoomBottomSheetHandler(
                            eventWithDetails = state.eventWithDetails,
                            profileViewModel = profileViewModel,
                            eventViewModel = eventViewModel,
                            onDismiss = { isSheetOpen.value = false },
                            endsAfter = TimeUtils.formatTimeDifference(state.eventWithDetails.lightRoom.endTime),
                            navController = navController
                        )
                    }


                }
                is MoreState.EventsInRadius -> {
                    EventCarouselScreen(
                        eventsWithDetails = state.eventsWithDetails,
                        eventViewModel = eventViewModel,
                        profileViewModel = profileViewModel,
                        moreViewModel = viewModel,
                        context = context,
                        navController = navController,
                        isSheetOpen = isSheetOpen, // Передаем состояние
                        selectedEvent = selectedEvent
                    )
                }
                is MoreState.NoEvents -> {
                    PlaceholderContent()
                }
                is MoreState.Loading -> {
                    LoadingScreen()
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
            }

        }
    }
}

@Composable
private fun EventCarouselScreen(
    eventsWithDetails: List<EventWithDetails>,
    eventViewModel: EventViewModel,
    profileViewModel: ProfileViewModel,
    moreViewModel: MoreViewModel,
    context: Context,
    navController: NavController,
    isSheetOpen: MutableState<Boolean>,
    selectedEvent: MutableState<EventDTO?>
) {
    val pagerState = rememberPagerState { eventsWithDetails.size }

    val eventsWithDetailsState = remember { mutableStateListOf(*eventsWithDetails.toTypedArray()) }

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { page ->
        val eventWithDetails = eventsWithDetailsState[page]
        val textOnButton = if (!eventWithDetails.isHere) "Join" else "You're here"
        val onClick: () -> Unit = if (!eventWithDetails.isHere) {
            {
                moreViewModel.createVisitor(eventWithDetails.lightRoom, context)
                eventsWithDetailsState[page] = eventWithDetails.copy(isHere = true) //todo
            }
        } else {
            {}
        }

        EventCard(
            imagesUrls = eventWithDetails.eventImagesUrls ?: emptyList(),
            eventDTO = eventWithDetails.event,
            textOnButton = textOnButton,
            eventViewModel = eventViewModel,
            visitorCount = eventWithDetails.visitorsCount,
            onClickButton = onClick,
            onClickCard = {
                isSheetOpen.value = true
                selectedEvent.value = eventWithDetails.event
            },
            endsAfter = TimeUtils.formatTimeDifference(eventWithDetails.lightRoom.endTime)
        )
        if (isSheetOpen.value) {
            LightRoomBottomSheetHandler(
                eventWithDetails = eventWithDetails,
                profileViewModel = profileViewModel,
                eventViewModel = eventViewModel,
                onDismiss = { isSheetOpen.value = false },
                endsAfter = TimeUtils.formatTimeDifference(eventWithDetails.lightRoom.endTime),
                navController = navController
            )
        }
    }
}

@Composable
private fun PlaceholderContent() {
    Box(
        modifier = Modifier
            .size(width = 320.dp, height = 700.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.light_room_placeholder),
            contentDescription = "Empty placeholder",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Text(
            text = "Тут пока пусто",
            fontSize = 20.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(32.dp),
            color = Color.White
        )
    }


}