package com.vsu.test.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.vsu.test.MainActivity
import com.vsu.test.data.storage.TokenManager
import com.vsu.test.data.api.model.dto.EventDTO
import com.vsu.test.domain.model.LocationData
import com.vsu.test.domain.usecase.GetEventByLightRoomIdUseCase
import com.vsu.test.domain.usecase.UpdateProfileCoordinatesAndGetIdsUseCase

import com.vsu.test.utils.NetworkResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LocationService : Service() {

    @Inject
    lateinit var updateProfileCoordinatesUseCase: UpdateProfileCoordinatesAndGetIdsUseCase
    @Inject
    lateinit var getEventByLightRoomIdUseCase: GetEventByLightRoomIdUseCase
    @Inject
    lateinit var tokenManager: TokenManager
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val scope = CoroutineScope(Dispatchers.IO)
    private lateinit var notificationManager: NotificationManager
    private lateinit var locationCallback: LocationCallback

    companion object {
        private const val FOREGROUND_CHANNEL_ID = "ForegroundServiceChannel"
        private const val EVENT_CHANNEL_ID = "EventNotificationChannel"
        private const val NOTIFICATION_ID = 1 // Для сервиса
        private const val EVENT_NOTIFICATION_ID = 2 // Для мероприятий
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("LocationService", "Service created")
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannels()
        val notification = buildServiceNotification("Приложение работает")
        Log.d("LocationService", "Starting foreground service")
        startForeground(NOTIFICATION_ID, notification)
        Log.d("LocationService", "Foreground service started")
        startLocationUpdates()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("LocationService", "Service started")
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startLocationUpdates() {
        if (!hasLocationPermissions()) {
            Log.e(
                "LocationService",
                "Insufficient permissions for location access, stopping service"
            )
            stopSelf()
            return
        }

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            30000
        ).build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
                val isTrackingEnabled = prefs.getBoolean("tracking_enabled", false)

                if (!isTrackingEnabled) {
                    Log.d("LocationService", "Tracking disabled, stopping service")
                    stopSelf()
                    return
                }

                val location = locationResult.lastLocation
                if (location != null) {
                    scope.launch {
                        try {
                            val locationData = LocationData(
                                tokenManager.getId(),
                                location.latitude,
                                longitude = location.longitude
                            )
                            Log.d(
                                "LocationService",
                                "Coordinates: id=${locationData.id}, lat=${locationData.latitude}, lon=${locationData.longitude}"
                            )
                            val response = updateProfileCoordinatesUseCase.invoke(locationData)
                            handleServerResponse(response)
                            updateServiceNotification("Tracking: ${location.latitude}, ${location.longitude}")
                        } catch (e: Exception) {
                            Log.e("LocationService", "Error: ${e.message}")
                            updateServiceNotification("Error: ${e.message}")
                        }
                    }
                } else {
                    Log.d("LocationService", "Location unavailable")
                    updateServiceNotification("Location unavailable")
                }
            }
        }

        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                mainLooper
            )
        } catch (e: SecurityException) {
            Log.e("LocationService", "SecurityException: ${e.message}, stopping service")
            stopSelf()
        }
    }

    private fun handleServerResponse(response: NetworkResult<List<Long>>) {
        if (response is NetworkResult.Error || response.data.isNullOrEmpty()) {
            Log.d("LocationService", "Server response is empty, no action taken")
            return
        }
        Log.d("LocationService", "Server returned event IDs: $response")

        scope.launch {
            val eventIds = response.data!!
            val events = mutableListOf<EventDTO>()

            eventIds.forEach { id ->
                val eventResult = getEventByLightRoomIdUseCase.invoke(id)
                if (eventResult is NetworkResult.Success && eventResult.data != null) {
                    events.add(eventResult.data)
                } else {
                    Log.e("LocationService", "Failed to fetch event for ID: $id")
                }
            }

            Log.d("LocationService", "Fetched events: $events")
            if (events.isNotEmpty()) {
                val eventNames = events.map { it.title }
                showEventNotification(eventNames)
            }
        }
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val foregroundChannel = NotificationChannel(
                FOREGROUND_CHANNEL_ID,
                "Работа в фоне",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                setSound(null, null)
                enableVibration(false)
            }
            val eventChannel = NotificationChannel(
                EVENT_CHANNEL_ID,
                "Мероприятия",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(foregroundChannel)
            manager.createNotificationChannel(eventChannel)
        }
    }

    private fun buildServiceNotification(text: String): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("screen", "settings")
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, FOREGROUND_CHANNEL_ID)
            .setContentTitle("Отслеживание местоположения")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .setOngoing(true) // Нельзя смахнуть
        val notification = builder.build()
        Log.d(
            "LocationService",
            "Service notification created with ongoing=${notification.flags and Notification.FLAG_ONGOING_EVENT != 0}"
        )
        return notification
    }

    private fun updateServiceNotification(text: String) {
        val notification = buildServiceNotification(text)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun showEventNotification(eventNames: List<String?>) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("screen", "more")
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val eventsText = eventNames.joinToString("\n")

        val notification = NotificationCompat.Builder(this, EVENT_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Рядом находятся мероприятия")
            .setContentText(if (eventNames.size > 1) "${eventNames.size} мероприятий" else eventNames.first())
            .setStyle(NotificationCompat.BigTextStyle().bigText(eventsText))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true) // Можно смахнуть
            .build()

        notificationManager.notify(EVENT_NOTIFICATION_ID, notification)
        Log.d("LocationService", "Event notification shown with events: $eventsText")
    }

    private fun hasLocationPermissions(): Boolean {
        val fineLocationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val backgroundLocationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

        return fineLocationPermission && backgroundLocationPermission
    }

    override fun onDestroy() {
        Log.d("LocationService", "Service destroyed")
        fusedLocationClient.removeLocationUpdates(locationCallback)
        scope.cancel()
        super.onDestroy()
    }
}