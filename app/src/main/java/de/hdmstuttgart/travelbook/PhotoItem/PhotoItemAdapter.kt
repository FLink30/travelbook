package de.hdmstuttgart.travelbook.PhotoItem

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import de.hdmstuttgart.travelbook.Application
import de.hdmstuttgart.travelbook.R
import de.hdmstuttgart.travelbook.databinding.ItemPhotoItemBinding
import de.hdmstuttgart.travelbook.models.PhotoItemModel
import de.hdmstuttgart.travelbook.models.TravelbookViewModel
import de.hdmstuttgart.travelbook.models.TravelbookViewModelFactory
import java.io.File
import java.io.IOException

class PhotoItemAdapter(private var photoItems: LiveData<List<PhotoItemModel>>? = null) :
    RecyclerView.Adapter<PhotoItemAdapter.PhotoItemViewHolder>() {

    private val photoItemsList: LiveData<List<PhotoItemModel>>? = photoItems
    private var textChangeListener: TextChangedListener? = null

    fun setTextChangeListener(listener: TextChangedListener) {
        textChangeListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPhotoItemBinding.inflate(inflater, parent, false)
        return PhotoItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoItemViewHolder, position: Int) {
        val photoItemsList = photoItemsList?.value
        var photoItem: PhotoItemModel? = photoItemsList?.get(position)
        photoItem?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int {
        return photoItemsList?.value?.size ?: 0
    }

    inner class PhotoItemViewHolder(private val binding: ItemPhotoItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val context: Context = itemView.context
        fun bind(photoItem: PhotoItemModel) {
            val commentEditText: EditText = binding.commentEditText
            val photoImageView: ImageView = binding.photoImageView
            val layoutParams: ViewGroup.LayoutParams = photoImageView.layoutParams
            layoutParams.height = layoutParams.width
            photoImageView.layoutParams = layoutParams

            if (photoItem.comment != null) {
                commentEditText.setText(photoItem.comment)
            }

            commentEditText.imeOptions = EditorInfo.IME_ACTION_DONE

            commentEditText.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    photoItem.comment = commentEditText.text.toString()
                    textChangeListener?.onPhotoItemChanged(photoItem)
                    commentEditText.clearFocus()
                    true
                } else {
                    false
                }
            }

            val imageBitmap: Bitmap? = photoItem.photo?.let { getBitmapFromFile(context, it) };

            photoImageView.setImageBitmap(imageBitmap)
        }
    }

    fun getBitmapFromFile(context: Context, filePath: String): Bitmap? {
        val uri = Uri.parse(filePath)
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            return BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }
}