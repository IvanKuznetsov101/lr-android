package com.vsu.test.presentation.viewmodel

import android.graphics.PointF
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vsu.test.R
import com.vsu.test.data.api.model.dto.LightRoomDTO
import com.vsu.test.domain.usecase.LightRoomUseCase
import com.vsu.test.utils.NetworkResult
import com.yandex.mapkit.Animation
import com.yandex.mapkit.Animation.Type.SMOOTH
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.CameraUpdateReason
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.image.ImageProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val lightRoomUseCase: LightRoomUseCase
) : ViewModel(), UserLocationObjectListener, CameraListener {

    private val _visibleArea = MutableStateFlow<BoundingBox?>(null)
    val points = MutableStateFlow<List<LightRoomDTO>>(emptyList())
    val loading = MutableStateFlow(false)
    val error = MutableStateFlow<String?>(null)

    private var mapView: MapView? = null
    private lateinit var userLocationLayer: UserLocationLayer
    private var routeStartLocation = Point(0.0, 0.0)
    var permissionLocation = false
    var followUserLocation = false
    val userCoordinates = MutableStateFlow<Point?>(null)

    init {
        _visibleArea
            .debounce(300)
            .onEach { box ->
                box?.let { loadPoints(it) }
            }
            .launchIn(viewModelScope)
    }

    /** Инициализация карты после получения разрешений */
    fun onPermissionGranted(mapView: MapView) {
        this.mapView = mapView
        val mapKit = MapKitFactory.getInstance()
        userLocationLayer = mapKit.createUserLocationLayer(mapView.mapWindow)
        userLocationLayer.isVisible = true
        userLocationLayer.isHeadingEnabled = true
        userLocationLayer.setObjectListener(this)
        mapView.mapWindow.map.addCameraListener(this)
        cameraUserPosition()
        permissionLocation = true
    }


    fun onMyLocationClick() {
        if (permissionLocation) {
            cameraUserPosition()
            followUserLocation = true
        }
    }

    /** Обновление видимой области карты */
    fun updateVisibleArea(box: BoundingBox) {
        _visibleArea.value = box
    }
    fun getCurrentUserCoordinates(): Point? {
        return userLocationLayer.cameraPosition()?.target
    }
    /** Загрузка точек в видимой области */
    private fun loadPoints(box: BoundingBox) {
        viewModelScope.launch {
            loading.value = true
            try {
                val response = lightRoomUseCase.invoke(box)
                if (response is NetworkResult.Success) {
                    points.value = response.data ?: emptyList()
                } else if (response is NetworkResult.Error) {
                    error.value = response.message
                }
            } catch (e: Exception) {
                error.value = e.message
            } finally {
                loading.value = false
            }
        }
    }


    /** Перемещение камеры к местоположению пользователя */
    private fun cameraUserPosition() {
        mapView?.let {
            if (userLocationLayer.cameraPosition() != null) {
                routeStartLocation = userLocationLayer.cameraPosition()!!.target
                it.mapWindow.map.move(
                    CameraPosition(routeStartLocation, 16f, 0f, 0f),
                    Animation(SMOOTH, 1f),
                    null
                )
            } else {
                it.mapWindow.map.move(CameraPosition(Point(0.0, 0.0), 16f, 0f, 0f))
            }
        }
    }

    /** Обработка добавления объекта местоположения пользователя */
    override fun onObjectAdded(userLocationView: UserLocationView) {
        setAnchor()
        val bitmap = ImageProvider.fromResource(mapView!!.context, R.drawable.ic_dot_rose_24dp)
        userLocationView.apply {
            pin.setIcon(bitmap)
            pin.setIconStyle(IconStyle().setFlat(true))
            arrow.setIcon(bitmap)
            arrow.setIconStyle(IconStyle().setFlat(true))
            accuracyCircle.fillColor = ActivityCompat.getColor(mapView!!.context, R.color.colorAccuracyCircle)
        }
    }

    override fun onObjectRemoved(p0: UserLocationView) {
        // Можно оставить пустым, если не требуется обработка
    }

    override fun onObjectUpdated(p0: UserLocationView, p1: ObjectEvent) {
        userCoordinates.value = p0.accuracyCircle.geometry.center
    }

    /** Обработка изменений положения камеры */
    override fun onCameraPositionChanged(
        map: Map,
        position: CameraPosition,
        reason: CameraUpdateReason,
        finished: Boolean
    ) {
        if (finished) {
            val visibleRegion = map.visibleRegion
            val boundingBox = BoundingBox(
                Point(visibleRegion.bottomLeft.latitude, visibleRegion.bottomLeft.longitude),
                Point(visibleRegion.topRight.latitude, visibleRegion.topRight.longitude)
            )
            updateVisibleArea(boundingBox)
            if (followUserLocation) {
                setAnchor()
            }
        } else {
            if (!followUserLocation) {
                noAnchor()
            }
        }
    }

    /** Установка якоря для слоя пользователя */
    private fun setAnchor() {
        mapView?.let {
            userLocationLayer.setAnchor(
                PointF(it.width * 0.5f, it.height * 0.5f),
                PointF(it.width * 0.5f, it.height * 0.83f)
            )
        }
        followUserLocation = false
    }

    /** Сброс якоря */
    private fun noAnchor() {
        userLocationLayer.resetAnchor()
    }
}