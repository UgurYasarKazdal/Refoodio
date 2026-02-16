package com.refoodio.core.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.refoodio.core.database.RefoodioDatabase
import com.refoodio.core.database.dao.catalog.FoodCatalogDao
import com.refoodio.core.database.dao.catalog.FoodSuggestionDao
import com.refoodio.core.database.entity.catalog.FoodCatalogItemEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class FoodSuggestionDaoTest {

    private lateinit var db: RefoodioDatabase
    private lateinit var dao: FoodSuggestionDao
    private lateinit var catalogDao: FoodCatalogDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        // In-Memory DB: Uygulama kapanınca veri silinir, test için mükemmeldir.
        db = Room.inMemoryDatabaseBuilder(context, RefoodioDatabase::class.java)
            .allowMainThreadQueries().build()
        dao = db.foodSuggestionDao()
        catalogDao = db.foodCatalogDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun searchFoods_returnsCorrectResults() = runBlocking {
        // 1. Önce test verilerini ana tabloya ekleyelim
        val items = listOf(
            createTestFood(id = 1, name = "Elma", category = "Meyve"),
            createTestFood(id = 2, name = "Armut", category = "Meyve"),
            createTestFood(id = 3, name = "Ekmek", category = "Tahıl"),
            createTestFood(id = 4, name = "Elma Suyu", category = "İçecek"),
            createTestFood(id = 5, name = "Elma Sirkesi", category = "İçecek")
        )
        catalogDao.insertAll(items)

        // 2. FTS üzerinden "Elm" terimiyle arama yapalım
        // Not: MATCH sorgusu için "Elm*" formatını kullanacağız
        val results = dao.searchSuggestions("Elm*").first()

        // 3. Doğrula
        val names = results.map { it.name }
        assertTrue(names.contains("Elma"))
        assertTrue(names.contains("Elma Suyu"))
        assertTrue(names.contains("Elma Sirkesi"))

        assertEquals(3, results.size)
    }

    private fun createTestFood(
        id: Int, name: String?, category: String? = "Genel"
    ): FoodCatalogItemEntity {
        return FoodCatalogItemEntity(
            id = id,
            name = name,
            category = category,
            alternativeNames = null,
            commonPairings = null,
            defaultShelfLife = null,
            freezable = null,
            isEssential = null,
            isLiquid = null,
            minQuantityAlert = null,
            nutritionalHighlight = null,
            openedShelfLife = null, // Değişken adın openedShelfLife ise düzeltmeyi unutma
            recommendedLocation = null,
            reminderFrequency = null,
            seasonality = null,
            storageNote = null,
            storageTempIdeal = null,
            suggestedPreparation = null,
            unit = null
        )
    }
}

