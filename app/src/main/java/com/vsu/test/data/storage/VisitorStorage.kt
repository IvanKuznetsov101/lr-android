package com.vsu.test.data.storage

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VisitorStorage @Inject constructor(@ApplicationContext context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("visitor_info", Context.MODE_PRIVATE)

    fun saveVisitorInfo(visitorInfo: VisitorInfo) {
        if (visitorInfo.visitorId!= null && visitorInfo.lightRoomId!= null && visitorInfo.profileId!= null )
            prefs.edit().apply {
                putLong("VISITOR_ID", visitorInfo.visitorId)
                putLong("LIGHTROOM_ID", visitorInfo.lightRoomId)
                putLong("PROFILE_ID", visitorInfo.profileId)
                commit()
        } else {
            clearVisitorInfo()
        }
    }

    fun getVisitorId(): Long? {
        return if (prefs.contains("VISITOR_ID")) {
            prefs.getLong("VISITOR_ID", 0L)
        } else {
            null
        }
    }
    fun getVisitorInfo(): VisitorInfo? {
        if (prefs.contains("VISITOR_ID")){
            return VisitorInfo(
                visitorId = prefs.getLong("VISITOR_ID", 0L),
                lightRoomId = prefs.getLong("LIGHTROOM_ID", 0L),
                profileId = prefs.getLong("PROFILE_ID", 0L)
            )
        }
        return null
    }
    fun clearVisitorInfo(){
        prefs.edit().apply(){
            remove("VISITOR_ID")
            remove("LIGHTROOM_ID")
            remove("PROFILE_ID")
        } .apply()
    }

    fun clearVisitorId() {
        prefs.edit().remove("VISITOR_ID").apply()
    }

    fun equals(id:Long): Boolean{
        return prefs.getLong("VISITOR_ID", 0L) == id
    }
}
data class VisitorInfo(
    val visitorId: Long?,
    val lightRoomId: Long?,
    val profileId: Long?
)