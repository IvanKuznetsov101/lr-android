//package com.vsu.test.data.repository
//
//import android.Manifest.permission.ACCESS_COARSE_LOCATION
//import android.Manifest.permission.ACCESS_FINE_LOCATION
//import android.content.pm.PackageManager.PERMISSION_GRANTED
//import android.location.Location
//import android.os.Looper
//import androidx.core.content.ContextCompat.checkSelfPermission
//import com.google.android.gms.location.FusedLocationProviderClient
//import com.google.android.gms.location.LocationCallback
//import com.google.android.gms.location.LocationRequest
//import com.google.android.gms.location.LocationResult
//import com.google.android.gms.location.Priority
//import kotlinx.coroutines.suspendCancellableCoroutine
//import javax.inject.Inject
//import kotlin.coroutines.resume
//
//class LocationRepository @Inject constructor(
//    private val fusedLocationClient: FusedLocationProviderClient
//) {
//    suspend fun getCurrentLocation(): Location? {
//        return suspendCancellableCoroutine { continuation ->
//            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000).build()
//            val locationCallback = object : LocationCallback() {
//                override fun onLocationResult(locationResult: LocationResult) {
//                    val location = locationResult.lastLocation
//                    continuation.resume(location)
//                    fusedLocationClient.removeLocationUpdates(this) // Останавливаем обновления
//                }
//            }
//
//            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
//            continuation.invokeOnCancellation {
//                fusedLocationClient.removeLocationUpdates(locationCallback) // Очистка при отмене
//            }
//        }
//    }
//}