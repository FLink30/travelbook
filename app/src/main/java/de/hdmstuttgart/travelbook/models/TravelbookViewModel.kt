package de.hdmstuttgart.travelbook.models

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class TravelbookViewModel(private val travelbookRepository: TravelbookRepository, private val photoItemRepository: PhotoItemRepository): ViewModel() {
    // Using LiveData and caching what allWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allTravelbooks: LiveData<List<TravelbookModel>>? =
        travelbookRepository.allTravelbookModels?.asLiveData()
    val allPhotoItems: LiveData<List<PhotoItemModel>>? =
        photoItemRepository.allPhotoItemModels?.asLiveData()


    // Launching a new coroutine to insert the data in a non-blocking way

    fun insertTravelbook(travelbookModel: TravelbookModel) =
        viewModelScope.launch(Dispatchers.IO) {
            travelbookRepository.insertTravelbook(travelbookModel)
        }

    fun deleteTravelbook(travelbookModel: TravelbookModel) =
        viewModelScope.launch(Dispatchers.IO) {
            travelbookRepository.deleteTravelbook(travelbookModel)
        }


    fun deleteAllTravelbook() =
        viewModelScope.launch(Dispatchers.IO) {
            travelbookRepository.deleteAllTravelbooks()
        }

    fun getTravelbookById(travelbookId: Int): TravelbookModel? {
        var travelbookModel: TravelbookModel? = null
        viewModelScope.launch(Dispatchers.IO) {
            travelbookModel = travelbookRepository.getTravelbookById(travelbookId)
        }
        return travelbookModel
    }


    fun updateTravelbook(travelbookModel: TravelbookModel) =
        viewModelScope.launch(Dispatchers.IO) {
            travelbookRepository.updateTravelbook(travelbookModel)
        }

    fun getAllPhotoItemsOf(travelbookId: Int): LiveData<List<PhotoItemModel>>? {
        var resultLiveData: LiveData<List<PhotoItemModel>>
        var flow: Flow<List<PhotoItemModel>>?
        flow = photoItemRepository.getAllPhotoItemsOf(travelbookId)
        resultLiveData = flow!!.asLiveData()
        return resultLiveData
    }


    fun insertPhotoItem(photoItemModel: PhotoItemModel) =
            viewModelScope.launch(Dispatchers.IO) {
                photoItemRepository.insertPhotoItem(photoItemModel)
            }
    fun deletePhotoItem(photoItemModel: PhotoItemModel) =
            viewModelScope.launch(Dispatchers.IO) {
                photoItemRepository.deletePhotoItem(photoItemModel)
            }

    fun deleteallPhotoItem() =
            viewModelScope.launch(Dispatchers.IO) {
                photoItemRepository.deleteAllPhotoItems()
            }

    fun getPhotoItemById(photoItemModel: PhotoItemModel) =
        viewModelScope.launch(Dispatchers.IO) {
            photoItemRepository.getPhotoItemById(photoItemModel.id)
        }

    fun updatePhotoItem(photoItemModel: PhotoItemModel) =
            viewModelScope.launch {
                photoItemRepository.updatePhotoItem(photoItemModel)
            }

    fun <T> Flow<T>.asLiveData(): LiveData<T> {
        val liveData = MutableLiveData<T>()
        viewModelScope.launch(Dispatchers.Main) {
            collect { value ->
                liveData.value = value
            }
        }
        return liveData
    }


}

    class TravelbookViewModelFactory(
        private val travelbookRepository: TravelbookRepository,
        private val photoItemRepository: PhotoItemRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TravelbookViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return TravelbookViewModel(travelbookRepository, photoItemRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
