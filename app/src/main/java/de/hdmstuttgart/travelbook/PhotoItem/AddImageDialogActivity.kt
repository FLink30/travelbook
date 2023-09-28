package de.hdmstuttgart.travelbook.PhotoItem

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import de.hdmstuttgart.travelbook.R
import de.hdmstuttgart.travelbook.databinding.ActivityAddImageDialogBinding

class AddImageDialogActivity : AppCompatActivity() {

    private var travelbookId: Int? = null
    private var travelbookName: String? = null
    private var readPermissionGranted = false
    private var writePermissionGranted = false
    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAddImageDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        travelbookId = intent.getIntExtra("TravelbookId", 0);
        travelbookName = intent.getStringExtra("TravelbookName")

        val actionbar = supportActionBar
        actionbar!!.title = travelbookName
        actionbar.setDisplayHomeAsUpEnabled(true)
        actionbar.setDisplayHomeAsUpEnabled(true)

        val captureButton: Button = binding.captureButton
        val uploadButton: Button = binding.uploadButton
        permissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){ permissions ->
            readPermissionGranted = permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: readPermissionGranted
            writePermissionGranted = permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: writePermissionGranted

        }
        updateOrRequestPermission()

        captureButton.setOnClickListener {
            val intent = Intent(this, ImageViewActivity::class.java)
            intent.putExtra("AddImageType", AddImageType.CAPTURE.getValue());
            intent.putExtra("TravelbookId", travelbookId)
            intent.putExtra("TravelbookName", travelbookName)
            startActivity(intent)
        }

        uploadButton.setOnClickListener {
            val intent = Intent(this, ImageViewActivity::class.java)
            intent.putExtra("AddImageType", AddImageType.UPLOAD.getValue());
            intent.putExtra("TravelbookId", travelbookId)
            intent.putExtra("TravelbookName", travelbookName)
            startActivity(intent)
        }
    }

    private fun updateOrRequestPermission(){
        val hasReadPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        val hasWritePermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        val minSDK29 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

        readPermissionGranted = hasReadPermission
        writePermissionGranted = hasWritePermission || minSDK29

        // PERMISSION REQUEST

        val permissionsToRequest = mutableListOf<String>()
        if(!writePermissionGranted){
            permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if(!readPermissionGranted){
            permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if(permissionsToRequest.isNotEmpty()){
            permissionsLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

}