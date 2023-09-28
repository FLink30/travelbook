package de.hdmstuttgart.travelbook.models

import android.os.Build

// helperfunction stellt sicher ob wir die app in sdk 29 oder drüber ausführen
// wenn die API 29 ist -> returnt lambda funktion
inline fun <T> sdk29AndUp (onSdk29: () -> T): T?{
    // wenn version größer als 29 ist wird die lambda funktion aufgerufen
    return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
        onSdk29()
    }
    // wenn version nicht größer als 29 ist
 else null

}