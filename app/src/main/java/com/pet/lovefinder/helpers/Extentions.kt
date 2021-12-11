package com.pet.lovefinder.helpers

import androidx.work.Data
import androidx.work.WorkInfo
import androidx.work.WorkManager
import java.util.concurrent.ExecutionException

fun WorkManager.isWorkScheduled(tag: String): Boolean {
    val statuses = getWorkInfosByTag(tag)
    return try {
        var running = false
        val workInfoList = statuses.get()
        for (workInfo in workInfoList) {
            val state = workInfo.state
            running = state == WorkInfo.State.RUNNING || state == WorkInfo.State.ENQUEUED
        }
        running
    } catch (e: ExecutionException) {
        e.printStackTrace()
        false
    } catch (e: InterruptedException) {
        e.printStackTrace()
        false
    }
}

fun workDataOf(vararg pairs: Pair<String, Any?>): Data {
    val data = Data.Builder()
    pairs.forEach { pair ->
        when (pair.second) {
            is String -> data.putString(pair.first, pair.second as String)
            is Float -> data.putFloat(pair.first, pair.second as Float)
            is Double -> data.putDouble(pair.first, pair.second as Double)
            is Int -> data.putInt(pair.first, pair.second as Int)
            is Boolean -> data.putBoolean(pair.first, pair.second as Boolean)
        }
    }
    return data.build()
}