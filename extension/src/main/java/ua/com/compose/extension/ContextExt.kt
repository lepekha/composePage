package ua.com.compose.extension

import android.content.*
import androidx.core.content.ContextCompat
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.MediaStore
import android.util.TypedValue
import android.widget.Toast
import androidx.annotation.AttrRes
import java.io.File
import java.io.FileOutputStream
import kotlin.concurrent.thread

fun Context?.toast(text: CharSequence, duration: Int = Toast.LENGTH_LONG) = this?.let {
    Toast.makeText(it, text, duration).show()
}

fun Context.clipboardCopy(text: String){
    val clipboard: ClipboardManager = ContextCompat.getSystemService(this, ClipboardManager::class.java) as ClipboardManager
    val clip: ClipData = ClipData.newPlainText("Compose", text)
    clipboard.setPrimaryClip(clip)
}

fun Context.getDiagonalInches(): Double {
    val metrics = this.resources.displayMetrics
    val yInches = metrics.heightPixels / metrics.densityDpi.toFloat()
    val xInches = metrics.widthPixels / metrics.densityDpi.toFloat()
    return Math.sqrt((xInches * xInches + yInches * yInches).toDouble())
}

fun Context.getFloat(resId: Int): Float {
    val outValue = TypedValue()
    resources.getValue(resId, outValue, true)
    return outValue.float
}

fun Context.getColorFromAttr(
        @AttrRes attrColor: Int,
        typedValue: TypedValue = TypedValue(),
        resolveRefs: Boolean = true
): Int {
    theme.resolveAttribute(attrColor, typedValue, resolveRefs)
    return typedValue.data
}

fun Context.getResIdFromAttr(
        @AttrRes attr: Int,
        typedValue: TypedValue = TypedValue(),
        resolveRefs: Boolean = true
): Int {
    theme.resolveAttribute(attr, typedValue, resolveRefs)
    return typedValue.resourceId
}

fun Context.getDrawableFromAttr(
        @AttrRes attr: Int
): Drawable? {
    val attrs = intArrayOf(attr)
    val typedArray = theme.obtainStyledAttributes(attrs)
    return typedArray.getDrawable(0)
}

fun Context.getDimensionFromAttr(
        @AttrRes attr: Int
): Float? {
    val attrs = intArrayOf(attr)
    val typedArray = theme.obtainStyledAttributes(attrs)
    return typedArray.getDimension(0, 0f)
}

fun Context.openUrl(urlString: String) {
    val url = if (!urlString.startsWith("http://") && !urlString.startsWith("https://")) {
        "http://$urlString"
    } else {
        urlString
    }

    val browserIntent = Intent(Intent.ACTION_VIEW)
    browserIntent.data = Uri.parse(url)
    browserIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    try {
        startActivity(browserIntent)
    } catch (e: Exception) {
    }
}

fun Context.openAppInPlayStore() {
    val appPackageName = packageName
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName"))
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    } catch (e: android.content.ActivityNotFoundException) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName"))
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

}

@Suppress("DEPRECATION")
fun Context.vibrate(milliseconds: Long = 500){
    val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        vibrator.vibrate(milliseconds)
    }
}

enum class EVibrate(internal val long: Long){
    NONE(long = 0L),
    BUTTON(long = 8L),
    SLIDER(long = 4L),
    BUTTON_LONG(long = 30L)
}

@Suppress("DEPRECATION")
fun Context.vibrate(type: EVibrate){
    val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(type.long, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        vibrator.vibrate(type.long)
    }
}

fun Context.createInstagramIntent(uri: Uri) {
    val share = Intent(Intent.ACTION_SEND)
    share.type = "image/*"
    share.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    share.putExtra(Intent.EXTRA_STREAM, uri)
    share.setPackage("com.instagram.android")
    startActivity(Intent.createChooser(share, "Share to"))
}

fun Context.createImageIntent(uri: Uri) {
    val share = Intent(Intent.ACTION_SEND)
    share.type = "image/*"
    share.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    share.putExtra(Intent.EXTRA_STREAM, uri)
    startActivity(Intent.createChooser(share, "Share to"))
}

fun Context.saveBitmap(bitmap: Bitmap, prefix: String = "", quality: Int = 90, sizePercent: Int = 100) {
    val millis = System.currentTimeMillis()
    val fname = "${prefix}compose_$millis.jpg"
    val resolver = this.contentResolver
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, fname)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/Compose")
    }

    val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues) ?: return

    resolver.openOutputStream(uri).use { out ->
        Bitmap.createScaledBitmap(bitmap, (bitmap.width * sizePercent) / 100, (bitmap.height * sizePercent) / 100, false).compress(Bitmap.CompressFormat.JPEG, quality, out)
    }

    MediaScannerConnection.scanFile(this, arrayOf(uri.path), arrayOf("image/jpg"), null)
}


fun Context.createTempUri(bitmap: Bitmap, quality: Int = 90, sizePercent: Int = 100, name: String = "compose_instagram"): Uri {
    val root = this.cacheDir.path
    val myDir = File("$root/temp")
    myDir.mkdirs()
    val fname = "$name.jpg"
    val file = File(myDir, fname)
    if (file.exists()) file.delete()
    FileOutputStream(file).use { out ->
        Bitmap.createScaledBitmap(bitmap, (bitmap.width * sizePercent) / 100, (bitmap.height * sizePercent) / 100, false).compress(Bitmap.CompressFormat.JPEG, quality, out)
    }
    return Uri.fromFile(file)
}