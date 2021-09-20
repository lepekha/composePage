package ua.com.compose.instagram_planer.view.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ua.com.compose.instagram_planer.data.Image
import ua.com.compose.instagram_planer.data.User

class RemoveImageUseCase(private val database: InstagramPlanerDatabase) {

    suspend fun execute(image: Image) {
        return withContext(Dispatchers.IO) {
            database.imageDao?.delete(image = image)
        }
    }
}