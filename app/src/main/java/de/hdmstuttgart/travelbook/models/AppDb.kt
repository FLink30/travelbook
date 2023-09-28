package de.hdmstuttgart.travelbook.models

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized
import kotlinx.coroutines.launch

// wir verwenden die SQLLite Datenbank die in Android schon drin ist
// erbt von RoomDatabase
@Database(entities = [TravelbookModel::class, PhotoItemModel::class], version = 1, exportSchema = false) // we want a database as a singleton
abstract class AppDb : RoomDatabase() {

    // alle Tabellen der DB werden definiert
    abstract fun photoItemDataBase(): PhotoItemDao
    abstract fun travelBookDataBase(): TravelbookDao

    // jede instanz der DB ist ziemlich ressourcenintensiv - über das companion object, stellen wir sicher, dass wir nur ein Objekt erhalten
    // singleton pattern
    // statisch
    companion object {

        @Volatile
        private var INSTANCE: AppDb? = null


        // immer wenn App neu installiert wird soll die db neu befült werden , daüfr muss erstmal alles gelöscht werden
        // dafür muss man einen Callback machen und die onCreate() im Callback überschreiben
        // onCreate muss dafür einen corountine launchen
        // man muss einen CorountineScope der Instanz übergeben, damit man später einen Corountine launchen kann

        @OptIn(InternalCoroutinesApi::class)
        fun getINSTANCE(context: Context, scope: CoroutineScope): AppDb {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDb::class.java, "travelbookDb"
                )
                        // bevor die Datenbank instanziiert wird, werden zunächst alle Daten gelöscht
                    .addCallback(TravelbookCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }



    private class TravelbookCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        // um die Datenbank zu befüllen
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
          INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.travelBookDataBase(), database.photoItemDataBase())
                }
            }
        }

        suspend fun populateDatabase(travelbookDao: TravelbookDao, photoItemDao: PhotoItemDao) {
            // Delete all content here.
            travelbookDao.deleteAll()
            photoItemDao.deleteAll()



            var travelbookModel = TravelbookModel(1, "Frankreich 23")
            travelbookDao.insert(travelbookModel)

        }



}



}