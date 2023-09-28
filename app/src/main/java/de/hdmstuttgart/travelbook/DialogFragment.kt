package de.hdmstuttgart.travelbook

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import de.hdmstuttgart.travelbook.databinding.DialogdeleteTravelbookBinding
import de.hdmstuttgart.travelbook.databinding.DialogeditTravelbookBinding
import de.hdmstuttgart.travelbook.databinding.TravelbookItemBinding
import de.hdmstuttgart.travelbook.models.TravelbookModel
import de.hdmstuttgart.travelbook.models.TravelbookViewModel

class DialogFragment: DialogFragment() {

private lateinit var travelbookViewModel: TravelbookViewModel
    var bindingEdit: DialogeditTravelbookBinding = DialogeditTravelbookBinding.inflate(layoutInflater)
    var bindingItem: TravelbookItemBinding = TravelbookItemBinding.inflate(layoutInflater)
    var bindingDelete: DialogdeleteTravelbookBinding = DialogdeleteTravelbookBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        travelbookViewModel = requireActivity().run {
            ViewModelProvider(this)[TravelbookViewModel::class.java]
        }
    }

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            // Dialogtyp aus den Fragment-Argumenten abrufen
            val dialogType = arguments?.getString("dialogType")

            val layoutResId = getLayoutResId(dialogType)
            val rootView = inflater.inflate(layoutResId, container, false)

            // Ansichten initialisieren und Logik implementieren

            return rootView
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            // Ansichten finden und Logik implementieren
            val saveButton = view.findViewById<Button>(R.id.saveButton)
            val deleteButton = view.findViewById<Button>(R.id.deleteButton)

            val dialogTyp = arguments?.getString("dialogType")

            when(dialogTyp){
                "create" -> {
                    saveButton.setOnClickListener {
                        //createTravelbook(titleEditText)
                        //dialog.dismiss()
                    }
                }
            }
            deleteButton.setOnClickListener {
                // Logik für "Löschen"-Button
            }
        }

        private fun getLayoutResId(dialogType: String?): Int {
            return when (dialogType) {
                "create" -> R.layout.dialogadd_travelbook
                "edit" -> R.layout.dialogedit_travelbook
                "delete" -> R.layout.dialogdelete_travelbook
                else -> throw IllegalArgumentException("Invalid dialog type")
            }
        }

    private fun createTravelbook(title: EditText){
        lateinit var userInput: String
        if(TextUtils.isEmpty(title.text)) {
            Log.d("MainActivity", "no travelbook created")
        } else{
            userInput = title.text.toString()
            var travelbookModel = TravelbookModel(title = userInput)

            travelbookViewModel.insertTravelbook(travelbookModel)
            Log.d("MainActivity", "new Travelbook $userInput created")

        }
    }

}