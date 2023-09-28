package de.hdmstuttgart.travelbook

import de.hdmstuttgart.travelbook.PhotoItem.PhotoItemOverviewActivity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Button
import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import de.hdmstuttgart.travelbook.PhotoItem.AddImageType
import de.hdmstuttgart.travelbook.databinding.ActivityMainBinding
import de.hdmstuttgart.travelbook.databinding.DialogdeleteTravelbookBinding
import de.hdmstuttgart.travelbook.databinding.DialogaddTravelbookBinding
import de.hdmstuttgart.travelbook.databinding.DialogeditTravelbookBinding
import de.hdmstuttgart.travelbook.models.TravelbookModel
import de.hdmstuttgart.travelbook.models.TravelbookViewModel
import de.hdmstuttgart.travelbook.models.TravelbookViewModelFactory


class MainActivity : AppCompatActivity(), RecyclerViewInterface {
    lateinit var binding: ActivityMainBinding

    // funktioniert
    private val travelbookViewModel: TravelbookViewModel by viewModels{
       TravelbookViewModelFactory((application as Application).travelbookRepository, (application as Application).photoItemRepository)
   }
  lateinit var adapter: TravelbookAdapter

//    val dialogFragment =  EditTravelbookDialogFragment()
    /////////////////////////////////////////////////////////////////////////
    // LIFECYCLE

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        var addTravelbook: Button = binding.addTravelbook

        addTravelbook.setOnClickListener {
            showCreateDialog()
        }

        var homeRecyclerView = binding.homeRecyclerView

        homeRecyclerView.layoutManager =
            GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)

        adapter = TravelbookAdapter(travelbookViewModel.allTravelbooks, this)
        homeRecyclerView.setAdapter(adapter)

        travelbookViewModel.allTravelbooks?.observe(this) { allTravelbooks ->
            allTravelbooks?.let { adapter.notifyDataSetChanged() }
        }
    }

        override fun onResume() {
            super.onResume()
            adapter.notifyDataSetChanged()
        }

    /////////////////////////////////////////////////////////////////////////
    // EVENTS

    override fun onItemClick(position: Int) {
        val intent = Intent(this, PhotoItemOverviewActivity::class.java)
        val travelbookModel = travelbookViewModel.allTravelbooks!!.value!!.get(position)
        intent.putExtra("TravelbookId", travelbookModel.id)
        intent.putExtra("TravelbookName", travelbookModel.title)
        startActivity(intent)
    }

    override fun onItemLongClick(position: Int) {
        //dialogFragment.show(supportFragmentManager, "dialog")
        showEditDialog(position)
        Log.d("MainActivity", "onItemLongClick")
    }

    /////////////////////////////////////////////////////////////////////////
    // HELPERMETHODS

    // hier sollte man noch binding verwenden
    // funktioniert
        @SuppressLint("SuspiciousIndentation")
        private fun showCreateDialog() {
            val binding: DialogaddTravelbookBinding = DialogaddTravelbookBinding.inflate(layoutInflater)
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setView(binding.root)

            val dialog = dialogBuilder.create()
            val dialogView = binding.root

            val titleEditText: EditText = binding.titleEditText
            val createButton: Button =  binding.createTravelbookButton

            createButton.setOnClickListener {
            createTravelbook(titleEditText)
                dialog.dismiss()
            }

        dialog.setView(dialogView)

            dialog.show()
        }

    // funktioniert
    private fun createTravelbook(title: EditText){
        lateinit var userInput: String
        if(TextUtils.isEmpty(title.text)) {
            setResult(Activity.RESULT_CANCELED)
            Log.d("MainActivity", "no travelbook created")
        } else{
            userInput = title.text.toString()
            var travelbookModel = TravelbookModel(title = userInput)

            travelbookViewModel.insertTravelbook(travelbookModel)
            Log.d("MainActivity", "new Travelbook $userInput created")

        }
    }


    // deine Methode um das Travelbook zu löschen
    fun showEditDialog(position: Int) {
        val binding: DialogeditTravelbookBinding = DialogeditTravelbookBinding.inflate(layoutInflater)
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setView(binding.root)

        val titleEditText: EditText = binding.titleEditText
        val deleteButton: Button = binding.deleteButton
        val saveButton: Button = binding.saveButton

        val dialog = dialogBuilder.create()
        val dialogView = binding.root

        val travelbookModel = travelbookViewModel.allTravelbooks!!.value!!.get(position)
        titleEditText.setText(travelbookModel.title)

        deleteButton.setOnClickListener {
            showDeleteDialog(travelbookModel)
            dialog.dismiss()
        }

        saveButton.setOnClickListener {
            val newTitle = binding.titleEditText.text.toString()
            if (newTitle.isNotBlank()) {
                travelbookModel.title = newTitle
                travelbookViewModel.updateTravelbook(travelbookModel)
                dialog.dismiss()
            } else {
                // Zeige eine Fehlermeldung an, wenn der Titel leer ist
                Toast.makeText(this, "Der Titel darf nicht leer sein", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.setView(dialogView)  // Hier wird das Dialog-View-Layout gesetzt

        dialog.show()
    }

    fun showDeleteDialog(travelbookModel: TravelbookModel) {
        val binding: DialogdeleteTravelbookBinding = DialogdeleteTravelbookBinding.inflate(layoutInflater)
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setView(binding.root)

        val deleteButton: Button = binding.deleteButton
        val saveButton: Button = binding.saveButton

        val dialog = dialogBuilder.create()
        val dialogView = binding.root

        deleteButton.setOnClickListener {
            travelbookViewModel.deleteTravelbook(travelbookModel)
            dialog.dismiss()
        }

        saveButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setView(dialogView)  // Hier wird das Dialog-View-Layout gesetzt

        dialog.show()
    }


}



