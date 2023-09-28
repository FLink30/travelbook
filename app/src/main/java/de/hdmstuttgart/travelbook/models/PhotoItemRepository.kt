package de.hdmstuttgart.travelbook.models

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext


class PhotoItemRepository(private val photoItemDao: PhotoItemDao) {

    var allPhotoItemModels: Flow<List<PhotoItemModel>>? = photoItemDao.observeAll()

    companion object {
        private lateinit var ourInstance: PhotoItemRepository
        fun getInstance(photoItemDao: PhotoItemDao): PhotoItemRepository {
            ourInstance = PhotoItemRepository(photoItemDao)
            return ourInstance
        }
    }



    ///////////////////////////////////////////////////////////////////////////////////////
    // read

    suspend fun getAllPhotoItems(): Flow<List<PhotoItemModel>>? {
        var allPhotoItemModels: Flow<List<PhotoItemModel>>? = null
        withContext(Dispatchers.IO) {
            allPhotoItemModels = photoItemDao.observeAll()
        }
        return allPhotoItemModels
    }

    suspend fun observeAll(): Flow<List<PhotoItemModel>>? {
        var photoItems: Flow<List<PhotoItemModel>>? = null
        withContext(Dispatchers.IO) {
            photoItems =  photoItemDao.observeAll()
        }
        return photoItems
    }

    suspend fun getPhotoItemById(photoItemId: Int): PhotoItemModel? {
        var photoItemModel: PhotoItemModel? = null
        withContext(Dispatchers.IO) {
            photoItemModel =  photoItemDao.getPhotoItemById(photoItemId)
        }
        return photoItemModel
    }

    fun getAllPhotoItemsOf(travelbookId: Int): Flow<List<PhotoItemModel>>? {
        var photoItemModels: Flow<List<PhotoItemModel>>?
        photoItemModels = photoItemDao.getAllPhotoItemsByFk(travelbookId = travelbookId)
        return photoItemModels
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    // insert

    suspend fun insertPhotoItem(photoItemModel: PhotoItemModel) {
        withContext(Dispatchers.IO) {
            photoItemDao.insert(photoItemModel)
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    // update
    suspend fun updatePhotoItem(photoItemModel: PhotoItemModel) {
        withContext(Dispatchers.IO) {
            photoItemDao.update(photoItemModel)
        }
    }



    ///////////////////////////////////////////////////////////////////////////////////////
    // delete

        suspend fun deletePhotoItem(photoItemModel: PhotoItemModel) {
            withContext(Dispatchers.IO) {
                photoItemDao.delete(photoItemModel)
            }
        }


        suspend fun deleteAllPhotoItems() {
            withContext(Dispatchers.IO) {
                photoItemDao.deleteAll()
            }


        }





}
