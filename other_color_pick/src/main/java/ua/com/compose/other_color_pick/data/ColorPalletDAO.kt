package ua.com.compose.other_color_pick.data

import androidx.room.*

@Dao
interface ColorPalletDAO {
    @Query("SELECT * FROM colorPallet")
    fun getAll(): List<ColorPallet>

    @Query("SELECT * FROM colorPallet WHERE id = :id")
    fun getById(id: Long): ColorPallet?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(color: ColorPallet): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(colors: List<ColorPallet>): LongArray

    @Update
    fun update(color: ColorPallet)

    @Query("DELETE FROM colorPallet WHERE id = :id")
    fun deleteById(id: Long)

    @Query("DELETE FROM colorPallet")
    fun deleteAll()
}