package ua.com.compose.api.analytics

import android.os.Bundle

lateinit var analytics: Analytics

interface Analytics {

    object Event {
        val COLOR_TYPE = "COLOR_TYPE"
        val COLOR_COPY = "COLOR_COPY"
        val COLOR_DRAG_AND_DROP = "COLOR_DRAG_AND_DROP"

        val OPEN_SETTINGS = "OPEN_SETTINGS"
        val OPEN_PALETTE_EXPORT = "OPEN_PALETTE_EXPORT"
        val OPEN_NEW_GALLERY = "OPEN_NEW_GALLERY"
        val OPEN_OLD_GALLERY = "OPEN_OLD_GALLERY"

        val CREATE_PALETTE = "CREATE_PALETTE"

        val CREATE_COLOR_CAMERA = "CREATE_COLOR_FROM_CAMERA"
        val CREATE_COLOR_IMAGE = "CREATE_COLOR_FROM_IMAGE"
        val CREATE_COLOR_PALETTE = "CREATE_COLOR_FROM_PALETTE"
        val CREATE_COLOR_FROM_INFO = "CREATE_COLOR_FROM_INFO"
    }

    fun send(event: AnalyticsEvent)
}

interface AnalyticsEvent {
    val key: String
    val data: Map<String, Any>
}

fun Map<String, Any>.toBundle() =
    Bundle().apply {
        forEach { (key, value) ->
            when (value) {
                is String -> putString(key, value)
                is Int -> putInt(key, value)
                is Boolean -> putBoolean(key, value)
                is Double -> putDouble(key, value)
                is Float -> putFloat(key, value)
                else -> throw IllegalArgumentException("Unknown data type: ${value::class.simpleName}")
            }
        }
    }
open class SimpleEvent(override val key: String) : AnalyticsEvent {
    override val data: Map<String, Any> = hashMapOf()
    override fun toString(): String = "AnalyticsEvent { key = $key, data = $data }"
}

open class Event(key: String, vararg params: Pair<String, Any>): SimpleEvent(key) {
    override val data = params.toMap()
}