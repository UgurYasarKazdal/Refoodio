package com.refoodio.inventory

import com.refoodio.core.domain.model.catalog.FoodItem
import com.refoodio.core.domain.use_case.catalog.GetFoodSuggestionsUseCase
import com.refoodio.core.domain.use_case.inventory.addInventory.InsertProductUseCase
import com.refoodio.core.domain.use_case.inventory.addInventory.InventoryAddUseCases
import com.refoodio.inventory.presentation.add_inventory.InventoryAddViewModel
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class InventoryAddViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val suggestionsUseCase = mockk<GetFoodSuggestionsUseCase>() // UseCase'i mockla

    private val addUseCase = mockk<InsertProductUseCase>()
    private lateinit var viewModel: InventoryAddViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        viewModel = InventoryAddViewModel(InventoryAddUseCases(addUseCase, suggestionsUseCase))
    }

    @Test
    fun `when query typed, uiState should be updated via usecase`() = runTest {
        val mockData = listOf(createMockFoodItem(name = "Elma"))
        coEvery { suggestionsUseCase.invoke("Elm") } returns flowOf(mockData)

        // 2. State'i "canlandır" (Bu kısım çok kritik!)
        // backgroundScope sayesinde test bitince otomatik iptal olur
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.state.collect {}
        }

        viewModel.onQueryChanged("Elm")

        advanceTimeBy(301)
        runCurrent() // Kuyruktaki tüm işleri hemen bitir

        // 5. Assert
        val currentState = viewModel.state.value
        assert(currentState.suggestions.isNotEmpty()) { "Liste hala boş! ViewModel akışı tetiklemedi." }
        assertEquals("Elma", currentState.suggestions[0].name)


    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}

fun createMockFoodItem(
    id: Int = 1,
    name: String = "Elma"
) = FoodItem(
    id = id,
    name = name,
    alternativeNames = emptyList(),
    category = "Meyve",
    commonPairings = emptyList(),
    defaultShelfLife = 7,
    openedShelfLife = null,
    isFreezable = false,
    isEssential = true,
    isLiquid = false,
    minQuantityAlert = 1,
    nutritionalHighlight = "Vitamin C",
    recommendedLocation = "Buzdolabı",
    storageNote = "Yıkamadan saklayın",
    unit = "Adet"
)