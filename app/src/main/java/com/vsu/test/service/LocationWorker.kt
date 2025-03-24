//package com.vsu.test.service
//
//import android.Manifest
//import android.content.Context
//import android.content.pm.PackageManager
//import android.os.Build
//import android.util.Log
//import androidx.core.content.ContextCompat
//import androidx.hilt.work.HiltWorker
//import androidx.work.CoroutineWorker
//import androidx.work.WorkerParameters
//import com.google.android.gms.location.FusedLocationProviderClient
//import com.google.android.gms.location.LocationServices
//import com.vsu.test.data.TokenManager
//import com.vsu.test.domain.model.LocationData
//import com.vsu.test.domain.usecase.UpdateProfileCoordinatesUseCase
//import com.vsu.test.utils.NetworkResult
//import dagger.assisted.Assisted
//import dagger.assisted.AssistedFactory
//import dagger.assisted.AssistedInject
//import kotlinx.coroutines.tasks.await
//
//
//@HiltWorker
//class LocationWorker @AssistedInject constructor(
//    @Assisted val context: Context,
//    @Assisted params: WorkerParameters,
//    private val updateCoordinatesUseCase: UpdateProfileCoordinatesUseCase,
//    private val tokenManager: TokenManager
//) : CoroutineWorker(context, params) {
//
//    private val fusedLocationClient: FusedLocationProviderClient =
//        LocationServices.getFusedLocationProviderClient(context)
//
//    override suspend fun doWork(): Result {
//        Log.d("LocationWorker", "in do work")
//        // Check if required permissions are granted
//        if (!hasLocationPermissions(context)) {
//            Log.e("LocationWorker", "Insufficient permissions for location access")
//            return Result.failure() // Exit with failure if permissions are missing
//        }
//
//        // Attempt to get location and update coordinates
//        return try {
//            val location = fusedLocationClient.lastLocation.await()
//            if (location != null) {
//                val locationData = LocationData(
//                    id = tokenManager.getId(),
//                    latitude = location.latitude,
//                    longitude = location.longitude
//                )
//                val result = updateCoordinatesUseCase.invoke(locationData)
//                when (result) {
//                    is NetworkResult.Success -> {
//                        Log.d("LocationWorker", "Coordinates updated successfully: ${result.data}")
//                        Result.success()
//                    }
//
//                    is NetworkResult.Error -> {
//                        Log.e("LocationWorker", "Error: ${result.message}")
//                        Result.retry() // Retry on network errors
//                    }
//
//                    else -> {
//                        Log.e("LocationWorker", "Unexpected result")
//                        Result.retry()
//                    }
//                }
//            } else {
//                Log.e("LocationWorker", "Location unavailable")
//                Result.retry() // Retry if location is null
//            }
//        } catch (e: SecurityException) {
//            // Handle permission-related exceptions
//            Log.e("LocationWorker", "SecurityException: ${e.message}")
//            Result.failure() // Exit with failure if permission is denied/revoked
//        } catch (e: Exception) {
//            // Handle other unexpected errors
//            Log.e("LocationWorker", "Exception: ${e.message}")
//            Result.retry() // Retry for other issues
//        }
//    }
//}
//private fun hasLocationPermissions(context: Context): Boolean {
//    val fineLocationPermission = ContextCompat.checkSelfPermission(
//        context,
//        Manifest.permission.ACCESS_FINE_LOCATION
//    ) == PackageManager.PERMISSION_GRANTED
//
//    val backgroundLocationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//        ContextCompat.checkSelfPermission(
//            context,
//            Manifest.permission.ACCESS_BACKGROUND_LOCATION
//        ) == PackageManager.PERMISSION_GRANTED
//    } else {
//        true // Для Android 9 и ниже это разрешение не требуется
//    }
//
//    return fineLocationPermission && backgroundLocationPermission
//}
//
//@AssistedFactory
//interface Factory {
//    fun create(context: Context, params: WorkerParameters): LocationWorker?
//}
//data class UpdateProfileCoordinatesRequest(
//    val id: Long,
//    val latitude: Double,
//    val longitude: Double
//)