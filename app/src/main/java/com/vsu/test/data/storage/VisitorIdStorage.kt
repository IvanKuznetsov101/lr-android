package com.vsu.test.data.storage

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VisitorIdStorage @Inject constructor(@ApplicationContext context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("visitor_prefs", Context.MODE_PRIVATE)

    fun saveVisitorId(visitorId: Long) {
        prefs.edit().putLong("VISITOR_ID", visitorId).apply()
    }

    fun getVisitorId(): Long? {
        return if (prefs.contains("VISITOR_ID")) {
            prefs.getLong("VISITOR_ID", 0L)
        } else {
            null
        }
    }

    fun clearVisitorId() {
        prefs.edit().remove("VISITOR_ID").apply()
    }
}