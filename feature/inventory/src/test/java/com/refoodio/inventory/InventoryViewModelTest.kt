package com.refoodio.inventory

import com.refoodio.inventory.domain.model.Product
import com.refoodio.inventory.domain.repository.FakeInventoryRepository
import com.refoodio.inventory.domain.use_case.DeleteProductUseCase
import com.refoodio.inventory.domain.use_case.GetProductsUseCase
import com.refoodio.inventory.domain.use_case.InsertProductUseCase
import com.refoodio.inventory.domain.use_case.InventoryUseCases
import com.refoodio.inventory.presentation.inventory_list.InventoryContract
import com.refoodio.inventory.presentation.inventory_list.InventoryViewModel
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class InventoryViewModelTest {

    // ViewModel asenkron çalıştığı için zamanı kontrol etmemizi sağlar
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: InventoryViewModel
    private lateinit var fakeRepository: FakeInventoryRepository // Test için sahte depo
    private lateinit var useCases: InventoryUseCases
    @Before
    fun setUp() {
// Coroutines'in "Main" thread olarak bizim testDispatcher'ımızı kullanmasını sağla
        Dispatchers.setMain(testDispatcher)

        // 2. Bağımlılıkları manuel oluştur (Hilt yerine biz veriyoruz)
        fakeRepository = FakeInventoryRepository()

        useCases = InventoryUseCases(
            getProducts = GetProductsUseCase(fakeRepository),
            insertProduct = InsertProductUseCase(fakeRepository),
            deleteProduct = DeleteProductUseCase(fakeRepository)
        )

        viewModel = InventoryViewModel(useCases)
    }
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    @Test
    fun `AddProduct event with empty name should update state with error`() = runTest {
        // GİRDİ: Boş isimli bir ürün ekleme isteği
        val invalidProduct = Product(name = "", quantity = 1.0, expiryDate = 0L)
        
        viewModel.handleEvent(InventoryContract.Event.AddProduct(invalidProduct))

        // 🚀 KRİTİK SATIR: Asenkron işlerin (Flow/Coroutine) bitmesini bekle
        // Bu olmazsa assert satırı "yarışı" kazanır veerrorMessage hala null görünür.
        testDispatcher.scheduler.advanceUntilIdle()

        // BEKLENTİ: State içindeki errorMessage boş olmamalı
        val currentState = viewModel.state.value
        assert(currentState.errorMessage != null)
        assert(currentState.errorMessage == "Ürün adı boş olamaz!")
    }

    @Test
    fun `AddProduct event with zero or negative quantity should update state with error`() = runTest {
        // 1. GİRDİ: Miktar 0 veya negatif
        val invalidProduct = Product(name = "Elma", quantity = 0.0, expiryDate = 0L)

        // 2. İŞLEM
        viewModel.handleEvent(InventoryContract.Event.AddProduct(invalidProduct))

        testDispatcher.scheduler.advanceUntilIdle()

        // 3. BEKLENTİ: Miktar hatası
        val currentState = viewModel.state.value
        assertEquals("Miktar 0'dan büyük olmalı!", currentState.errorMessage)
    }
}