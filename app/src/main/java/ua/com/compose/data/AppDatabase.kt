package ua.com.compose.data

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [ColorItem::class, ColorPallet::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun colorItemDao(): ColorItemDAO?
    abstract fun colorPalletDao(): ColorPalletDAO?
}