package com.refoodio.inventory

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.refoodio.inventory.data.local.ProductDao
import com.refoodio.inventory.data.local.ProductEntity
import com.refoodio.inventory.data.local.RefoodioDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProductDaoTest {
    private var database: RefoodioDatabase?=null
    private lateinit var dao: ProductDao

    @Before
    fun setup() {
        // Bellek içi (In-memory) veritabanı kuruyoruz ki testler birbirini etkilemesin
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RefoodioDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = requireNotNull(database?.productDao())
    }

    @Test
    fun insertProduct_shouldReturnProductInFlow() = runTest {
        val product = ProductEntity(id = 1, name = "Elma", expiryDate = 123456L, quantity = 2.0)
        dao.insertProduct(product)

        val allProducts = dao.getProductsFlow().first() // Flow'un ilk değerini al
        assert(allProducts.contains(product))
    }
    @Test
    fun deleteProduct_removesFromDatabase() = runTest {
        // 1. Önce bir ürün ekle
        val product = ProductEntity(name = "Süt", expiryDate = 123456L, quantity = 1.0)
        dao.insertProduct(product)

        // 2. Eklediğimiz ürünü geri alalım (ID'sini doğrulamak için)
        val insertedProduct = dao.getProductsFlow().first().first()

        // 3. SİLELİM (Bu metod henüz yok, hata verecek!)
        dao.deleteProduct(insertedProduct)

        // 4. Listenin boş olduğunu doğrula
        val products = dao.getProductsFlow().first()
        assert(products.isEmpty())
    }


    @After
    fun tearDown() {
        database?.close()
    }
}