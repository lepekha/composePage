package ua.com.compose.instagram_planer.view.domain

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ua.com.compose.extension.getPath
import ua.com.compose.file_storage.FileStorage
import ua.com.compose.instagram_planer.data.Image
import ua.com.compose.instagram_planer.data.User
import java.io.File

class ImageChangeUriUseCase(private val context: Context, private val database: InstagramPlanerDatabase) {

    suspend fun execute(user: User, image: Image, uri: Uri) {
        return withContext(Dispatchers.IO) {
            FileStorage.removeFile(fileName = image.imageName, dirName = "${database.INSTAGRAM_PLANNER_DIR}/${user.id}")
            val newUri = FileStorage.copyFileToDir(file = File(uri.getPath(context = context)), dirName = "${database.INSTAGRAM_PLANNER_DIR}/${user.id}", fileName = uri.hashCode().toString())
            image.uri = newUri.toString()
            image.imageName = uri.hashCode().toString()
            database.imageDao?.update(image = image)
        }
    }
}