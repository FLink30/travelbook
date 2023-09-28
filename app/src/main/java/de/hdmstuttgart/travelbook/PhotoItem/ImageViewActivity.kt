package de.hdmstuttgart.travelbook.PhotoItem

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import de.hdmstuttgart.travelbook.Application
import de.hdmstuttgart.travelbook.R
import de.hdmstuttgart.travelbook.R.layout.activity_image_view
import de.hdmstuttgart.travelbook.databinding.ActivityImageViewBinding
import de.hdmstuttgart.travelbook.models.PhotoItemModel
import de.hdmstuttgart.travelbook.models.TravelbookViewModel
import de.hdmstuttgart.travelbook.models.TravelbookViewModelFactory
import de.hdmstuttgart.travelbook.models.sdk29AndUp
import java.io.IOException

@Suppress("DEPRECATION")
class ImageViewActivity : AppCompatActivity() {

    private val travelbookViewModel: TravelbookViewModel by viewModels {
        TravelbookViewModelFactory(
            (application as Application).travelbookRepository,
            (application as Application).photoItemRepository
        )
    }

    private val CAMERA_PERMISSION_REQUEST_CODE = 1000
    private lateinit var imageView: ImageView
    private lateinit var commentEditText: EditText
    private var travelbookId: Int? = null
    private var imageUri: Uri? = null

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val newImage = result.data?.extras?.get("data") as Bitmap
            imageView.setImageBitmap(newImage)
            imageUri = savePhotoToExternalStorage(newImage)

        } else {
            onBackPressed()
        }
    }

    private val uploadImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val newImageUri = result.data?.data
            imageView.setImageURI(newImageUri)
            val inputStream = newImageUri?.let { this.contentResolver.openInputStream(it) }
            val bitmap = BitmapFactory.decodeStream(inputStream)
            imageUri = savePhotoToExternalStorage(bitmap)
        } else {
            onBackPressed()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityImageViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        travelbookId = intent.getIntExtra("TravelbookId", 0);
        val travelbookName = intent.getStringExtra("TravelbookName")

        val actionbar = supportActionBar
        actionbar!!.title = travelbookName
        actionbar.setDisplayHomeAsUpEnabled(true)
        actionbar.setDisplayHomeAsUpEnabled(true)

        // Initialisiere die Views
        imageView = binding.imageView
        commentEditText = binding.commentEditText


        // Setze den Placeholder für das EditText
        commentEditText.hint = getString(R.string.add_comment_placeholder)
        commentEditText.imeOptions = EditorInfo.IME_ACTION_DONE

        commentEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                commentEditText.clearFocus()
                true
            } else {
                false
            }
        }


        // Button-Click-Listener zum Speichern des Kommentars und Zurückkehren zur vorherigen View
        val saveButton: Button = binding.saveButton
        saveButton.setOnClickListener {
            val comment = commentEditText.text.toString()
            var photoString = imageUri.toString()

            var photoItemModel = PhotoItemModel(0, photoString, comment, travelbookId!!)

            travelbookViewModel.insertPhotoItem(photoItemModel)
            onBackPressed()
        }

        val addImageType = AddImageType.fromValue(intent.getIntExtra("AddImageType", 0));
        when (addImageType) {
            AddImageType.CAPTURE -> requestCameraPermission()
            AddImageType.UPLOAD -> uploadImage()
            else -> {requestCameraPermission()}
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun captureImage() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        takePictureLauncher.launch(takePictureIntent)
    }

    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
        } else {
            captureImage()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                captureImage()
            } else {
                // Berechtigung wurde verweigert
                Toast.makeText(this, "Kamera-Berechtigung verweigert", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadImage() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        uploadImageLauncher.launch(galleryIntent)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    fun isExternalStorageWritable(): Boolean {
        val state = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED == state
    }

    fun savePhotoToExternalStorage(bitmap: Bitmap): Uri? {
        if (isExternalStorageWritable()){
            val imageCollection = sdk29AndUp {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } ?: MediaStore.Images.Media.EXTERNAL_CONTENT_URI

            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "photo_${System.currentTimeMillis()}.jpg")
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.WIDTH, bitmap.width)
                put(MediaStore.Images.Media.HEIGHT, bitmap.height)
            }

            return try {
                val uri = contentResolver.insert(imageCollection, contentValues)?.also { uri ->
                    contentResolver.openOutputStream(uri).use { outputStream ->
                        if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)) {
                            throw IOException("Couldn't save bitmap")
                        }
                    }
                } ?: throw IOException("Couldn't create MediaStore entry")
                return uri
            } catch (e: IOException) {
                e.printStackTrace()
                return null
            }

        }else {
            Log.d("ImageViewActivity", "ExternalStorage is not writable")
            return null
        }
    }

    fun hideKeyboardFrom(context: Context, view: View) {
        val inputMethodManager = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        hideKeyboardFrom(this, currentFocus ?: View(this))
        return super.onTouchEvent(event)
    }
}

enum class AddImageType {
    CAPTURE {
        override fun getValue(): Int = 0
            },
    UPLOAD {
        override fun getValue(): Int = 1
    };

    abstract fun getValue(): Int

    companion object {
        fun fromValue(value: Int): AddImageType? {
            return values().find { it.getValue() == value }
        }
    }
}