package de.hdmstuttgart.travelbook.models

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoItemDao{

    ///////////////////////////////////////////////////////////////////////////////////////
    // Read

    @Query("SELECT * FROM photoitemmodel")
    fun observeAll(): Flow<List<PhotoItemModel>>

    @Query("SELECT * FROM photoitemmodel WHERE id = :id")
    suspend fun getPhotoItemById(id: Int?): PhotoItemModel?

    @Query("SELECT * FROM photoitemmodel WHERE travelbook_fk = :travelbookId")
     fun getAllPhotoItemsByFk(travelbookId: Int?): Flow<List<PhotoItemModel>>


    ///////////////////////////////////////////////////////////////////////////////////////
    // Insert


    // hier braucht es kein SQL!
    // OnConflict.-.. meint, dass wenn man genau das gleiche Objekt einspeichern möchte, das nicht geht
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(photoItemModel: PhotoItemModel)

    ///////////////////////////////////////////////////////////////////////////////////////
    // Update

    @Update
    suspend fun update(photoItemModel: PhotoItemModel)

    ///////////////////////////////////////////////////////////////////////////////////////
    // Delete


    @Delete
    suspend fun delete(photoItemModel: PhotoItemModel)

    @Query("DELETE FROM photoitemmodel")
    suspend fun deleteAll()

}