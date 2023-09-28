package de.hdmstuttgart.travelbook.models

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext


class TravelbookRepository(private val travelbookDao: TravelbookDao) {

   var allTravelbookModels: Flow<List<TravelbookModel>>? = travelbookDao.observeAll()

    companion object {
        private lateinit var ourInstance: TravelbookRepository
        fun getInstance(travelbookDao: TravelbookDao): TravelbookRepository {
            ourInstance = TravelbookRepository(travelbookDao)
            return ourInstance
        }
    }


    ///////////////////////////////////////////////////////////////////////////////////////
    // Read

    suspend fun getAllTravelbooks(): Flow<List<TravelbookModel>>? {
        var allTravelbookModels: Flow<List<TravelbookModel>>? = null
        withContext(Dispatchers.IO) {
            allTravelbookModels = travelbookDao.observeAll()
        }
        return allTravelbookModels
    }

    suspend fun getTravelbookById(travelbookId: Int): TravelbookModel? {
        var travelbookModel: TravelbookModel? = null
        withContext(Dispatchers.IO) {
             travelbookModel = travelbookDao.getTravelbookById(travelbookId)
        }
        return travelbookModel
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    // Insert


    suspend fun insertTravelbook(travelbookModel: TravelbookModel) {
        withContext(Dispatchers.IO){
            travelbookDao.insert(travelbookModel)
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    // update
    suspend fun updateTravelbook(travelbookModel: TravelbookModel) {
        withContext(Dispatchers.IO){
            travelbookDao.update(travelbookModel)
        }
    }



    ///////////////////////////////////////////////////////////////////////////////////////
    // delete


    suspend fun deleteTravelbook(travelbookModel: TravelbookModel){
        withContext(Dispatchers.IO){
            travelbookDao.delete(travelbookModel)
        }
    }


    suspend fun deleteAllTravelbooks() {
        withContext(Dispatchers.IO) {
            travelbookDao.deleteAll()
        }
    }

}
