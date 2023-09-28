package de.hdmstuttgart.travelbook.PhotoItem

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import de.hdmstuttgart.travelbook.Application
import de.hdmstuttgart.travelbook.R
import de.hdmstuttgart.travelbook.databinding.ActivityPhotoItemOverviewBinding
import de.hdmstuttgart.travelbook.models.PhotoItemModel
import de.hdmstuttgart.travelbook.models.TravelbookViewModel
import de.hdmstuttgart.travelbook.models.TravelbookViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PhotoItemOverviewActivity : AppCompatActivity() {

    private lateinit var floatingActionButton: FloatingActionButton
    private lateinit var binding: ActivityPhotoItemOverviewBinding
    private var photoItemsLiveData: LiveData<List<PhotoItemModel>>? = null

    private val travelbookViewModel: TravelbookViewModel by viewModels{
        TravelbookViewModelFactory((application as Application).travelbookRepository, (application as Application).photoItemRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoItemOverviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        val travelbookID = intent.getIntExtra("TravelbookId", 0)
        val travelbookName = intent.getStringExtra("TravelbookName")

        var photoItems: LiveData<List<PhotoItemModel>>? = travelbookViewModel.getAllPhotoItemsOf(travelbookID)



        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))

        val adapter = PhotoItemAdapter(photoItems)
        adapter.setTextChangeListener(object : TextChangedListener {
            override fun onPhotoItemChanged(photoItem: PhotoItemModel) {
                travelbookViewModel.updatePhotoItem(photoItem)
            }
        })
        recyclerView.adapter = adapter



        //actionbar
        val actionbar = supportActionBar
        actionbar!!.title = travelbookName
        actionbar.setDisplayHomeAsUpEnabled(true)
        actionbar.setDisplayHomeAsUpEnabled(true)

        floatingActionButton = binding.floatingActionButton
        floatingActionButton.setOnClickListener {
            val intent = Intent(this, AddImageDialogActivity::class.java)
            intent.putExtra("TravelbookId", travelbookID)
            intent.putExtra("TravelbookName", travelbookName)
            startActivity(intent)
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

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}

