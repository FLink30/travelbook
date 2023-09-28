package de.hdmstuttgart.travelbook.PhotoItem

import de.hdmstuttgart.travelbook.models.PhotoItemModel

interface TextChangedListener {
    fun onPhotoItemChanged(photoItem: PhotoItemModel)
}