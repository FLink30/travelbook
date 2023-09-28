package de.hdmstuttgart.travelbook

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import de.hdmstuttgart.travelbook.models.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.junit.After

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.concurrent.CountDownLatch


@RunWith(AndroidJUnit4::class)
@SmallTest
class DBTest {


    private lateinit var appDb: AppDb
    private lateinit var travelbookDao: TravelbookDao
    private lateinit var photoItemDao: PhotoItemDao

    @Before
    fun createDatabase() {
        // DB in temporärem Speicher, die für jeden Testdurchlauf neu erstellt wird
        // Tests isoliert ausführen
        appDb = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(),
            AppDb::class.java
        ).allowMainThreadQueries().build()

        travelbookDao = appDb.travelBookDataBase()
        photoItemDao = appDb.photoItemDataBase()
    }

    @After
    @Throws(IOException::class)
    fun closeDatabase() {
        appDb.close()
    }


    /////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////
    // TRAVELBOOK-DAO


    /////////////////////////////////////////////////////////////////////////
    // SAMPLE
    private fun createAndInsertSampleTravelbooks(): Array<TravelbookModel> = runBlocking{
        var travelbooks = arrayOf<TravelbookModel>(TravelbookModel(id = 1, title = "France 22"),
            TravelbookModel(id = 2, title = "France 22"),
            TravelbookModel(id = 3, title = "England 22"))

        travelbooks.forEach{
            travelbookDao.insert(it)
        }
        return@runBlocking travelbooks
    }


    /////////////////////////////////////////////////////////////////////////
    // OBSERVE ALL
    @Test
    fun observeAllTravelbooks_positive() = runBlocking {
        var travelbooks = createAndInsertSampleTravelbooks()
        var firstTravelbook = travelbooks[0]
        var secondTravelbook = travelbooks[1]

        val latch = CountDownLatch(1)
        val job = async(Dispatchers.IO) {
            val observedItems = travelbookDao.observeAll()
            var firstObservedTravelbookModel: TravelbookModel? = observedItems?.first()?.first()
            assertEquals(firstTravelbook, firstObservedTravelbookModel)
            assertNotEquals(secondTravelbook, firstObservedTravelbookModel)
            latch.countDown()
        }
        latch.await()
        job.cancelAndJoin()
    }

    /////////////////////////////////////////////////////////////////////////
    // GET TRAVELBOOK BY ID

    @Test
    fun getTravelbookById_positive() = runBlocking {
        var travelbooks = createAndInsertSampleTravelbooks()

        val expectedTravelbookModel = travelbooks[0]
        val unexpectedTravelbookModel = travelbooks[1]

        val latch = CountDownLatch(1)
        val job = async(Dispatchers.IO) {
            var retrievedTravelbookModel = travelbookDao.getTravelbookById(expectedTravelbookModel.id)
            assertEquals(expectedTravelbookModel,retrievedTravelbookModel)
            latch.countDown()
        }
        latch.await()
        job.cancelAndJoin()
    }


    @Test
    fun getTravelbookById_negative() = runBlocking {
        var travelbooks = createAndInsertSampleTravelbooks()

        val expectedTravelbookModel = travelbooks[0]
        val unexpectedTravelbookModel = travelbooks[1]


        val latch = CountDownLatch(1)
        val job = async(Dispatchers.IO) {
            var retrievedTravelbookModel = travelbookDao.getTravelbookById(expectedTravelbookModel.id)
            assertNotEquals(unexpectedTravelbookModel,retrievedTravelbookModel)
            latch.countDown()
        }
        latch.await()
        job.cancelAndJoin()
    }


    /////////////////////////////////////////////////////////////////////////
    // INSERT TRAVELBOOK

    @Test
    fun insertTravelbook_positive() = runBlocking {
        val travelbookModel = TravelbookModel(title = "USA 23")


        // Insert data into the database
        travelbookDao.insert(travelbookModel)

        var latch = CountDownLatch(1)

        val job = async(Dispatchers.IO){
            travelbookDao.observeAll()?.collect() {
                assertTrue(it.contains(travelbookModel))
                latch.countDown()
            }
            latch.await()

        }
        job.cancelAndJoin()
    }

    @Test
    fun insertTravelbook_negative() = runBlocking {
        val travelbookModel = TravelbookModel(title = "USA 23")


        // Insert data into the database
        travelbookDao.insert(travelbookModel)

        var latch = CountDownLatch(1)

        val job = async(Dispatchers.IO){
            travelbookDao.observeAll()?.collect() {
                assertFalse(it.isEmpty())
                latch.countDown()
            }
            latch.await()

        }
        job.cancelAndJoin()
    }

    /////////////////////////////////////////////////////////////////////////
    // UPDATE TRAVELBOOK

    @Test
    fun updateTravelbook_positive() = runBlocking {
        var travelbooks = createAndInsertSampleTravelbooks()

        val travelbookModel = travelbooks[0]
        val oldTitle = travelbookModel.title
        val updatedTitle =  "Updated Title"
        travelbookModel.title = updatedTitle

        val latch = CountDownLatch(1)
        val job = async(Dispatchers.IO) {
            travelbookDao.update(travelbookModel)
            var retrievedTravelbookModel = travelbookDao.getTravelbookById(travelbookModel.id)
            assertEquals(travelbookModel.title,updatedTitle)
            assertNotEquals(travelbookModel.title, oldTitle )

            latch.countDown()
        }
        latch.await()
        job.cancelAndJoin()
    }


    /////////////////////////////////////////////////////////////////////////
    // DELETE TRAVELBOOK


    @Test
    fun deleteTravelbook_positive() = runBlocking {
        var travelbooks = createAndInsertSampleTravelbooks()

        val travelbookModelShouldBeDeleted = travelbooks[0]
        val travelbookModelShouldNotBeDeleted = travelbooks[1]

        val latch = CountDownLatch(1)
        val job = async(Dispatchers.IO) {
            travelbookDao.delete(travelbookModelShouldBeDeleted)
            travelbookDao.observeAll()?.collect() {
                assertTrue(it.contains(travelbookModelShouldNotBeDeleted))
                latch.countDown()
            }
        }
        latch.await()
        job.cancelAndJoin()
    }


    @Test
    fun deleteTravelbook_negative() = runBlocking {
        var travelbooks = createAndInsertSampleTravelbooks()

        val travelbookModelShouldBeDeleted = travelbooks[0]
        val travelbookModelShouldNotBeDeleted = travelbooks[1]

        travelbookDao.insert(travelbookModelShouldBeDeleted)
        travelbookDao.insert(travelbookModelShouldNotBeDeleted)

        val latch = CountDownLatch(1)
        val job = async(Dispatchers.IO) {
            travelbookDao.delete(travelbookModelShouldBeDeleted)
            travelbookDao.observeAll()?.collect() {
                assertFalse(it.contains(travelbookModelShouldBeDeleted))
                latch.countDown()
            }
        }
        latch.await()
        job.cancelAndJoin()
    }

    /////////////////////////////////////////////////////////////////////////
    // DELETE ALL TRAVELBOOKS
    @Test
    fun deleteAllTravelbooks_positive() = runBlocking {
        createAndInsertSampleTravelbooks()
        travelbookDao.deleteAll()

        val latch = CountDownLatch(1)
        val job = async(Dispatchers.IO) {
            travelbookDao.observeAll()?.collect {
                (assertTrue(it.isEmpty()))
                latch.countDown()
            }
        }
        latch.await()
        job.cancelAndJoin()
    }


    /////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////
    // PHOTO ITEM-DAO


    /////////////////////////////////////////////////////////////////////////
    // CREATE SAMPLE
    private fun createAndInsertSamplePhotoItems(): Array<PhotoItemModel> = runBlocking{
        var travelbooks = createAndInsertSampleTravelbooks()

        var firstTravelbookModelId = travelbooks[0].id
        var secondTravelbookModelId = travelbooks[1].id


        var photoItems = arrayOf<PhotoItemModel>(PhotoItemModel(id = 1, photo = null, comment = "What a wonderful day", travelbookId = firstTravelbookModelId),
            PhotoItemModel(id = 2, photo = null, comment = "It was nice to meet them", travelbookId = firstTravelbookModelId),
            PhotoItemModel(id = 3, photo = null, comment = "I liked the nature a lot", travelbookId = secondTravelbookModelId))

        photoItems.forEach{
            photoItemDao.insert(it)
        }
        return@runBlocking photoItems
    }

    /////////////////////////////////////////////////////////////////////////
    // OBSERVE ALL PHOTO ITEMS
    @Test
    fun observeAllPhotoItems_successfully() = runBlocking {
        var photoItems = createAndInsertSamplePhotoItems()
        var firstPhotoItem = photoItems[0]
        var secondPhotoItem = photoItems[1]

        val latch = CountDownLatch(1)
        val job = async(Dispatchers.IO) {
            val observedItems = photoItemDao.observeAll()
            var firstObservedPhotoItem: PhotoItemModel? = observedItems.first().first()
            assertEquals(firstPhotoItem, firstObservedPhotoItem)
            assertNotEquals(secondPhotoItem, firstObservedPhotoItem)
            latch.countDown()
        }
        latch.await()
        job.cancelAndJoin()
    }

    /////////////////////////////////////////////////////////////////////////
    // GET ALL PHOTOITEMS BY FOREIGN KEY
    @Test
    fun getAllPhotoItemsByFk_positive() = runBlocking {
        var photoItems = createAndInsertSamplePhotoItems()

        // PK von Travelbooks in DB
        var firstTravelbookModelId = 1
        var secondTravelbookModelId = 2

        // For every travelbook a list of photoitems

        var photoItemsOfFirstTravelbook = listOf(PhotoItemModel(id = 1, photo = null, comment = "What a wonderful day", travelbookId = firstTravelbookModelId),
            PhotoItemModel(id = 2, photo = null, comment = "It was nice to meet them", travelbookId = firstTravelbookModelId))

        val latch = CountDownLatch(1)
        val job = async(Dispatchers.IO) {
            var retrievedPhotoItemsFirstTravelbook: Flow<List<PhotoItemModel>> =  photoItemDao.getAllPhotoItemsByFk(firstTravelbookModelId)
            var firstRetrievedPhotoItem: PhotoItemModel = retrievedPhotoItemsFirstTravelbook.first().first()
            assertEquals(photoItemsOfFirstTravelbook.first(),firstRetrievedPhotoItem)
            latch.countDown()
        }
        latch.await()
        job.cancelAndJoin()
    }

    @Test
    fun getAllPhotoItemsByFk_negative() = runBlocking {
        var photoItems = createAndInsertSamplePhotoItems()

        // PK von Travelbooks in DB
        var firstTravelbookModelId = 1
        var secondTravelbookModelId = 2

        // For every travelbook a list of photoitems

        var photoItemsOfFirstTravelbook = listOf(PhotoItemModel(id = 1, photo = null, comment = "What a wonderful day", travelbookId = firstTravelbookModelId),
            PhotoItemModel(id = 2, photo = null, comment = "It was nice to meet them", travelbookId = firstTravelbookModelId))
        var photoItemsOfSecondTravelbook = arrayOf(PhotoItemModel(id = 3, photo = null, comment = "I liked the nature a lot", travelbookId = secondTravelbookModelId))

        val latch = CountDownLatch(1)
        val job = async(Dispatchers.IO) {
            var retrievedPhotoItemsFirstTravelbook: Flow<List<PhotoItemModel>> =  photoItemDao.getAllPhotoItemsByFk(firstTravelbookModelId)
            var firstRetrievedPhotoItem: PhotoItemModel = retrievedPhotoItemsFirstTravelbook.first().first()
            assertNotEquals(photoItemsOfSecondTravelbook.first(),firstRetrievedPhotoItem)
            latch.countDown()
        }
        latch.await()
        job.cancelAndJoin()
    }

    /////////////////////////////////////////////////////////////////////////
    // GET PHOTOITEM BY ID
    @Test
    fun getPhotoItemById_positive() = runBlocking {
        var photoItems = createAndInsertSamplePhotoItems()
        var wantedPhotoItem = photoItems[0]
        var notWantedPhotoItem = photoItems[1]

        val latch = CountDownLatch(1)
        val job = async(Dispatchers.IO) {
            var retrievedPhotoItem = photoItemDao.getPhotoItemById(wantedPhotoItem.id)
            assertEquals(wantedPhotoItem,retrievedPhotoItem)
            latch.countDown()
        }
        latch.await()
        job.cancelAndJoin()
    }

    @Test
    fun getPhotoItemById_negative() = runBlocking {
        var photoItems = createAndInsertSamplePhotoItems()
        var wantedPhotoItem = photoItems[0]
        var notWantedPhotoItem = photoItems[1]

        val latch = CountDownLatch(1)
        val job = async(Dispatchers.IO) {
            var retrievedPhotoItem = photoItemDao.getPhotoItemById(wantedPhotoItem.id)
            assertNotEquals(notWantedPhotoItem,retrievedPhotoItem)
            latch.countDown()
        }
        latch.await()
        job.cancelAndJoin()
    }


    /////////////////////////////////////////////////////////////////////////
    // INSERT PHOTOITEM
    @Test
    fun insertPhotoItem_positive() = runBlocking {
        var photoItems = createAndInsertSamplePhotoItems()
        var firstPhotoItem = photoItems[0]

        var latch = CountDownLatch(1)

        val job = async(Dispatchers.IO){
            photoItemDao.observeAll().collect() {
                assertTrue(it.contains(firstPhotoItem))
                latch.countDown()
            }
            latch.await()
        }
        job.cancelAndJoin()
    }

    @Test
    fun insertPhotoItem_negative() = runBlocking {
        var latch = CountDownLatch(1)

        val job = async(Dispatchers.IO){
            photoItemDao.observeAll().collect() {
                assertFalse(it.isEmpty())
                latch.countDown()
            }
            latch.await()

        }
        job.cancelAndJoin()
    }

    /////////////////////////////////////////////////////////////////////////
    // UPDATE PHOTOITEM

    @Test
    fun updatePhotoItem_positive() = runBlocking {
        var photoItems = createAndInsertSamplePhotoItems()


        val photoItemModel = photoItems[0]
        val oldPhoto = "old/Path/photo.jpeg"
        val updatedPhoto =  "updated/Path/photo.jpeg"
        photoItemModel.photo = updatedPhoto

        val latch = CountDownLatch(1)
        val job = async(Dispatchers.IO) {
            photoItemDao.update(photoItemModel)
            var retrievedPhotoItemModel = photoItemDao.getPhotoItemById(photoItemModel.id)
            assertEquals(photoItemModel.photo,updatedPhoto)
            assertNotEquals(photoItemModel.photo, oldPhoto )
            latch.countDown()
        }
        latch.await()
        job.cancelAndJoin()
    }

    /////////////////////////////////////////////////////////////////////////
    // DELETE PHOTOITEM


    @Test
    fun deletePhotoItem_positive() = runBlocking {
        var photoItems = createAndInsertSamplePhotoItems()
        var photoItemModelShouldBeDeleted = photoItems[0]
        var photoItemModelShouldNotBeDeleted = photoItems[1]


        val latch = CountDownLatch(1)
        val job = async(Dispatchers.IO) {
            photoItemDao.delete(photoItemModelShouldBeDeleted)
            photoItemDao.observeAll().collect() {
                assertTrue(it.contains(photoItemModelShouldNotBeDeleted))
                assertTrue(!(it.contains(photoItemModelShouldBeDeleted)))
                latch.countDown()
            }
        }
        latch.await()
        job.cancelAndJoin()
    }


    @Test
    fun deletePhotoItem_negative() = runBlocking {
        var photoItems = createAndInsertSamplePhotoItems()
        var photoItemModelShouldBeDeleted = photoItems[0]
        var photoItemModelShouldNotBeDeleted = photoItems[1]


        val latch = CountDownLatch(1)
        val job = async(Dispatchers.IO) {
            photoItemDao.delete(photoItemModelShouldBeDeleted)
            photoItemDao.observeAll().collect() {
                assertFalse(it.contains(photoItemModelShouldBeDeleted))
                latch.countDown()
            }
        }
        latch.await()
        job.cancelAndJoin()
    }


    /////////////////////////////////////////////////////////////////////////
    // DELETE ALL PHOTOITEMS
    @Test
    fun deleteAllPhotoItems_positive() = runBlocking {
        var photoItems = createAndInsertSamplePhotoItems()
        var photoItemModelShouldBeDeleted = photoItems[0]

        photoItemDao.deleteAll()

        val latch = CountDownLatch(1)
        val job = async(Dispatchers.IO) {
            photoItemDao.observeAll().collect {
                assertTrue(it.isEmpty())
                latch.countDown()
            }
        }
        latch.await()
        job.cancelAndJoin()
    }

    @Test
    fun deleteAllPhotoItems_negative() = runBlocking {
        var photoItems = createAndInsertSamplePhotoItems()
        var photoItemModelShouldBeDeleted = photoItems[0]

        photoItemDao.deleteAll()

        val latch = CountDownLatch(1)
        val job = async(Dispatchers.IO) {
            photoItemDao.observeAll().collect {
                assertFalse(it.contains(photoItemModelShouldBeDeleted))
                latch.countDown()
            }
        }
        latch.await()
        job.cancelAndJoin()
    }
}