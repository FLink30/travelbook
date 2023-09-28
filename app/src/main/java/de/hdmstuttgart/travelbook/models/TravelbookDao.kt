package de.hdmstuttgart.travelbook.models


import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TravelbookDao{
    // wir werden über observer darüber informiert, wenn sich etwas verändert hat

    ///////////////////////////////////////////////////////////////////////////////////////
    // Read

    @Query("SELECT * FROM travelbookmodel")
    fun observeAll(): Flow<List<TravelbookModel>>?
    @Query("SELECT * FROM travelbookmodel")
    suspend fun getAll(): List<TravelbookModel>?
    @Query("SELECT * FROM travelbookmodel WHERE id = :id")
    suspend fun getTravelbookById(id: Int?): TravelbookModel?

    ///////////////////////////////////////////////////////////////////////////////////////
    // Insert

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(travelbookModel: TravelbookModel)

    ///////////////////////////////////////////////////////////////////////////////////////
    // Update

    @Update
    suspend fun update(travelbookModel: TravelbookModel)

    ///////////////////////////////////////////////////////////////////////////////////////
    // Delete

    @Delete
    suspend fun delete(travelbookModel: TravelbookModel)
    @Query("DELETE FROM travelbookmodel")
    suspend fun deleteAll()
}