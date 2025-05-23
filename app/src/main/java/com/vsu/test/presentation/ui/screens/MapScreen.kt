@file:OptIn(ExperimentalMaterial3Api::class)
@file:Suppress("DEPRECATION")

package com.vsu.test.presentation.ui.screens

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.vsu.test.R
import com.vsu.test.data.api.model.dto.EventDTO
import com.vsu.test.data.api.model.dto.LightRoomDTO
import com.vsu.test.presentation.ui.components.CRUDEvent
import com.vsu.test.presentation.ui.components.CombinedActions
import com.vsu.test.presentation.ui.components.DefaultButton
import com.vsu.test.presentation.ui.components.LightRoomBottomSheetHandler
import com.vsu.test.presentation.ui.components.ListItem
import com.vsu.test.presentation.ui.components.LocationButton
import com.vsu.test.presentation.viewmodel.EventViewModel
import com.vsu.test.presentation.viewmodel.MapViewModel
import com.vsu.test.presentation.viewmodel.ProfileViewModel
import com.vsu.test.utils.TimeUtils
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.CameraUpdateReason
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider

import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onNavigateToMore: () -> Unit,
    navController: NavController
) {
    val context = LocalContext.current
    val viewModel: MapViewModel = hiltViewModel()
    val profileViewModel: ProfileViewModel = hiltViewModel()
    val eventViewModel: EventViewModel = hiltViewModel()
    val mapView = remember { MapView(context) }
    val imageProvider = ImageProvider.fromResource(context, R.drawable.ic_lightroom)


    val checkLocationPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[ACCESS_FINE_LOCATION] == true &&
            permissions[ACCESS_COARSE_LOCATION] == true
        ) {
            viewModel.onPermissionGranted(mapView)
            mapView.onStart() // Вызываем onStart здесь, если он зависит от разрешений
        }
    }

    LaunchedEffect(Unit) {
        if (checkSelfPermission(context, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED &&
            checkSelfPermission(context, ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED
        ) {
            viewModel.onPermissionGranted(mapView)
            mapView.onStart() // Вызываем onStart здесь, если разрешения уже есть
        } else {
            checkLocationPermission.launch(
                arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)
            )
        }
    }

    val points by viewModel.points.collectAsState()

    val cameraListener = createCameraListener(viewModel)
    val placemarkCollection = remember { mapView.map.mapObjects.addCollection() }
    val lightRoomSheetState = rememberModalBottomSheetState()
    var selectedLightRoomDTO by remember { mutableStateOf<LightRoomDTO?>(null) }
    val placemarkTapListener = MapObjectTapListener { mapObject, _ ->
        selectedLightRoomDTO = mapObject.userData as? LightRoomDTO
        true
    }
    val isLoading by eventViewModel.loading.collectAsState()

    LaunchedEffect(points) {
        placemarkCollection.clear()
        points.forEach { lightRoomDTO ->
            val point = Point(lightRoomDTO.latitude, lightRoomDTO.longitude)
            val placemark = placemarkCollection.addPlacemark(
                point,
                imageProvider,
                IconStyle().setScale(0.3f)
            )
            placemark.userData = lightRoomDTO // Сохраняем DTO в placemark
            placemark.addTapListener(placemarkTapListener)
        }
    }
    LaunchedEffect(selectedLightRoomDTO) {
        if (selectedLightRoomDTO != null) {
            lightRoomSheetState.show()
        } else {
            lightRoomSheetState.hide()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { setupMapView(mapView, cameraListener) },
            modifier = Modifier.fillMaxSize()
        )

        val eventWithDetails by eventViewModel.eventWithDetails.collectAsState()

        selectedLightRoomDTO?.let { lightRoomDTO ->
            LaunchedEffect(lightRoomDTO) {
                eventViewModel.getEventWithDetailsByLightRoom(lightRoomDTO)
            }
        }

        eventWithDetails?.let { details ->
            if (selectedLightRoomDTO != null) {
                LightRoomBottomSheetHandler(
                    eventWithDetails = details,
                    eventViewModel = eventViewModel,
                    profileViewModel = profileViewModel,
                    onDismiss = {
                        selectedLightRoomDTO = null
                    },
                    endsAfter = TimeUtils.formatTimeDifference(details.lightRoom.endTime),
                    navController = navController
                )
            }

        }

        MapControls(
            viewModel = viewModel,
            eventViewModel = eventViewModel,
            onNavigateToMore = onNavigateToMore
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            mapView.map.removeCameraListener(cameraListener)
            mapView.onStop()
            MapKitFactory.getInstance().onStop()
        }
    }
}

private fun setupMapView(mapView: MapView, cameraListener: CameraListener): MapView {
    mapView.map.apply {
        isNightModeEnabled = true
        addCameraListener(cameraListener)
    }
    return mapView
}

@Composable
private fun createCameraListener(viewModel: MapViewModel): CameraListener = remember {
    object : CameraListener {
        override fun onCameraPositionChanged(
            map: Map,
            position: CameraPosition,
            reason: CameraUpdateReason,
            finished: Boolean
        ) {
            viewModel.onCameraPositionChanged(map, position, reason, finished)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapControls(
    viewModel: MapViewModel,
    eventViewModel: EventViewModel,
    onNavigateToMore: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showEventsSheet by remember { mutableStateOf(false) }
    var sheetContent by remember { mutableStateOf("list") }
    var selectedEvent by remember { mutableStateOf<EventDTO?>(null) }
    val scope = rememberCoroutineScope()
    val events by eventViewModel.events.collectAsState(initial = emptyList())
    val isLoading by eventViewModel.loading.collectAsState(initial = false)
    val error by eventViewModel.error.collectAsState()
    val context = LocalContext.current
    val profileHasEvent = eventViewModel.profileHasEvent.collectAsState()

    LaunchedEffect(error) {
        if (error != null) {
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            viewModel.error.value = null
        }
    }

    LaunchedEffect(showEventsSheet) {
        if (showEventsSheet) {
            eventViewModel.loadEvents()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LocationButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 30.dp, y = (-85).dp),
            { viewModel.onMyLocationClick() }
        )
        CombinedActions(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 32.dp, y = (-40).dp),
            leftButton = {
                if (viewModel.checkVisitorInfo()) {
                    onNavigateToMore()
                } else {
                    showEventsSheet = true
                    sheetContent = "list"
                }
            },
            rightButton = { onNavigateToMore() },
            Icons.Default.Add,
            Icons.Default.Menu
        )

        if (showEventsSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showEventsSheet = false
                    sheetContent = "list"
                    selectedEvent = null
                },
                containerColor = Color.White,
                sheetState = sheetState
            ) {
                when (sheetContent) {
                    "list" -> {
                        if (isLoading) {
                            LoadingScreen()
                        } else {
                            if (profileHasEvent.value) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("You already have an event created")
                                    Spacer(modifier = Modifier.height(16.dp))
                                    DefaultButton(
                                        onClick = onNavigateToMore,
                                        text = "Open",
                                        icon = null
                                    )
                                }

                            } else {
                                if (events.isEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "Тут пока пусто",
                                            fontSize = 18.sp,
                                            color = Color.Gray
                                        )
                                    }
                                } else {
                                    LazyColumn(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp)

                                    ) {
                                        items(events, key = { it.id }) { event ->
                                            ListItem(
                                                eventDTO = event,
                                                onClick = {
                                                    eventViewModel.getImagesUrlsByEventId(event.id)
                                                    selectedEvent = event
                                                    sheetContent = "edit"
                                                },
                                                onCloseClick = {
                                                    eventViewModel.deleteEvent(event.id)
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                        }

                        Box(modifier = Modifier.fillMaxSize()) {
                            CombinedActions(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .offset(x = 32.dp, y = (-40).dp),
                                leftButton = {
                                    sheetContent = "create"
                                },
                                rightButton = {
                                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                                        showEventsSheet = false
                                    }
                                },
                                Icons.Default.Add,
                                Icons.Default.ArrowBack
                            )
                        }
                    }

                    "create" -> {
                        CRUDEvent(
                            eventDTO = null,
                            onSave = { newEvent, images ->
                                eventViewModel.createEventAndCreateLightRoom(
                                    newEvent,
                                    viewModel.getCurrentUserCoordinates(),
                                    images
                                )
                                sheetContent = "list"
                                showEventsSheet = false
                            },
                            onCancel = { newEvent, images ->
                                eventViewModel.createEvent(newEvent, images)
                                sheetContent = "list"
                            },
                            eventViewModel = eventViewModel
                        )
                    }

                    "edit" -> {
                        selectedEvent?.let { event ->
                            CRUDEvent(
                                eventDTO = event,
                                onSave = { updatedEvent, images ->
                                    eventViewModel.updateEventAndCreateLightRoom(
                                        updatedEvent,
                                        images,
                                        viewModel.getCurrentUserCoordinates()
                                    )
                                    sheetContent = "list"
                                    showEventsSheet = false
                                },
                                onCancel = { updatedEvent, images ->
                                    eventViewModel.updateEvent(updatedEvent, images)
                                    sheetContent = "list"
                                },
                                eventViewModel = eventViewModel
                            )
                        }
                    }
                }
            }
        }
    }
}
