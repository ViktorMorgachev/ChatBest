package com.pet.chat.helpers

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Observer
import androidx.work.Data
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.pet.chat.App
import com.pet.chat.network.data.base.Message
import com.pet.chat.ui.MainChatModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.isActive
import org.json.JSONObject
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

fun Any.toJsonString(): String {
    val gson = Gson()
    val gsonPretty = GsonBuilder().setPrettyPrinting().create()
    val jsonString: String = gson.toJson(this)
    print("Socket send data ${jsonString}")
    return jsonString
}

fun String.toJsonObject(): JSONObject {
    return JSONObject(this)
}

fun Any.toSocketData(): JSONObject {
    return this.toJsonString().toJsonObject()
}

fun Message.isOwn(): Boolean {
    return MainChatModule.chatsPrefs?.userID == this.user_id
}

inline fun <reified E> List<E>.addLast(data: E): List<E> {
    val newList = mutableListOf<E>()
    this.forEach {
        newList.add(it)
    }
    newList.add(data)
    return newList
}

inline fun <reified E> List<E>.addAll(list: List<E>): List<E> {
    val newList = mutableListOf<E>()
    this.forEach {
        newList.add(it)
    }
    newList.addAll(list)
    return newList
}

@OptIn(InternalCoroutinesApi::class)
public suspend inline fun <T> Flow<T>.collect(scope: CoroutineScope, crossinline action: suspend (value: T) -> Unit): Unit =
    collect(object : FlowCollector<T> {
        override suspend fun emit(value: T) {
            if (scope.isActive){
                action(value)
            }
        }
    })

@Composable
fun <R, T : R> SingleLiveEvent<T>.observeAsState(initial: R): State<R> {
    val lifecycleOwner = LocalLifecycleOwner.current
    val state = remember { mutableStateOf(initial) }
    DisposableEffect(this, lifecycleOwner) {
        val observer = Observer<T> { state.value = it }
        observe(lifecycleOwner, observer)
        onDispose { removeObserver(observer) }
    }
    return state
}

inline fun <reified E> List<E>.removeWithInstance(it: E): List<E> {
    val newList = mutableListOf<E>()
    this.forEach {
        newList.add(it)
    }
    newList.remove(it)
    return newList
}
inline fun <reified E> List<E>.replaceWithInstance(it: E, replacement: E): List<E> {
    val newList = mutableListOf<E>()
    this.forEach {
        newList.add(it)
    }
    newList.remove(it)
    newList.add(replacement)
    return newList
}

