package de.hdmstuttgart.travelbook

import android.app.Application
import de.hdmstuttgart.travelbook.models.AppDb
import de.hdmstuttgart.travelbook.models.PhotoItemRepository
import de.hdmstuttgart.travelbook.models.TravelbookRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class Application: Application() {

    val applicationScope = CoroutineScope((SupervisorJob()))

    // objekte sollen nur erzeugt werden wenn sie gebruacht werden (by lazy)
    val appDb by lazy { AppDb.getINSTANCE(this, applicationScope) }
    val travelbookRepository by lazy { TravelbookRepository(appDb.travelBookDataBase())}
    val photoItemRepository by lazy { PhotoItemRepository(appDb.photoItemDataBase()) }


}
