package ua.com.compose.data.adobecolor.tool

import java.nio.ByteBuffer

fun Int.to2Bytes() = ByteArray(2).apply {
    set(0, ((this@to2Bytes shr 8) and 0xFF).toByte())
    set(1, (this@to2Bytes and 0xFF).toByte())
}

fun Int.to4Bytes() = ByteArray(4).apply {
    set(0, ((this@to4Bytes shr 24) and 0xFF).toByte())
    set(1, ((this@to4Bytes shr 16) and 0xFF).toByte())
    set(2, ((this@to4Bytes shr 8) and 0xFF).toByte())
    set(3, (this@to4Bytes and 0xFF).toByte())
}

fun Float.to4Bytes(): ByteArray = ByteBuffer.allocate(4).putFloat(this).array()

