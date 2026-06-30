package com.cherrymooncake.wardrobe_android.feature.ootd.presentation.viewmodel

import app.cash.turbine.test
import com.cherrymooncake.wardrobe_android.core.network.source.ITokenLocalSource
import com.cherrymooncake.wardrobe_android.feature.ootd.domain.model.AiRecommendationDomainModel
import com.cherrymooncake.wardrobe_android.feature.ootd.domain.model.DailyOutfitDomainModel
import com.cherrymooncake.wardrobe_android.feature.ootd.domain.model.WeatherDomainModel
import com.cherrymooncake.wardrobe_android.feature.ootd.domain.usecase.*
import com.cherrymooncake.wardrobe_android.feature.ootd.presentation.mvi.OotdEvent
import com.cherrymooncake.wardrobe_android.feature.ootd.presentation.mvi.OotdIntent
import com.cherrymooncake.wardrobe_android.feature.outfits.domain.model.OutfitDomainModel
import com.cherrymooncake.wardrobe_android.feature.outfits.domain.usecase.SyncOutfitsUseCase
import com.cherrymooncake.wardrobe_android.utils.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

class OotdViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var getDailyOutfitUseCase: GetDailyOutfitUseCase
    private lateinit var setDailyOutfitUseCase: SetDailyOutfitUseCase
    private lateinit var deleteDailyOutfitUseCase: DeleteDailyOutfitUseCase
    private lateinit var getRandomOutfitUseCase: GetRandomOutfitUseCase
    private lateinit var syncOutfitsUseCase: SyncOutfitsUseCase
    private lateinit var getWeatherUseCase: GetWeatherUseCase
    private lateinit var getAiRecommendationUseCase: GetAiRecommendationUseCase
    private lateinit var tokenSource: ITokenLocalSource
    private lateinit var viewModel: OotdViewModel

    @Before
    fun setup() {
        getDailyOutfitUseCase = mockk()
        setDailyOutfitUseCase = mockk()
        deleteDailyOutfitUseCase = mockk()
        getRandomOutfitUseCase = mockk()
        syncOutfitsUseCase = mockk()
        getWeatherUseCase = mockk()
        getAiRecommendationUseCase = mockk()
        tokenSource = mockk(relaxed = true)

        viewModel = OotdViewModel(
            getDailyOutfitUseCase, setDailyOutfitUseCase, deleteDailyOutfitUseCase,
            getRandomOutfitUseCase, syncOutfitsUseCase, getWeatherUseCase,
            getAiRecommendationUseCase, tokenSource
        )
    }

    @Test
    fun loadData_emptyDate_usesTodayAndLoadsOutfit() = runTest {
        val outfit = OutfitDomainModel("1", "Look", null, 800, 600, null, null, emptyList(), emptyList())
        val todayStr = LocalDate.now().toString()
        coEvery { getDailyOutfitUseCase(todayStr) } returns DailyOutfitDomainModel(todayStr, outfit)

        viewModel.processIntent(OotdIntent.LoadData(""))

        assertEquals(todayStr, viewModel.state.value.targetDate)
        assertEquals("Look", viewModel.state.value.outfit?.name)
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun saveCity_updatesSourceAndLoadsWeather() = runTest {
        val weather = WeatherDomainModel("Minsk", 20.0, "Ясно", 0, "Тепло")
        coEvery { getWeatherUseCase("Minsk") } returns weather

        viewModel.processIntent(OotdIntent.SaveCity("Minsk"))

        verify { tokenSource.saveCity("Minsk") }
        assertEquals("Minsk", viewModel.state.value.savedCity)
        assertFalse(viewModel.state.value.showCityDialog)
        assertEquals(20.0, viewModel.state.value.weather?.temperature)
    }

    @Test
    fun askAiStylist_success_updatesState() = runTest {
        val outfit = OutfitDomainModel("1", "Look", null, 800, 600, null, null, emptyList(), emptyList())
        val recommendation = AiRecommendationDomainModel("1", "Perfect", outfit)

        coEvery { getAiRecommendationUseCase("test", null) } returns recommendation

        viewModel.processIntent(OotdIntent.UpdateAiPrompt("test"))
        viewModel.processIntent(OotdIntent.AskAiStylist())

        assertFalse(viewModel.state.value.isAiLoading)
        assertEquals("Perfect", viewModel.state.value.aiExplanation)
        assertEquals("Look", viewModel.state.value.aiRecommendedOutfit?.name)
    }

    @Test
    fun setOutfit_success_emitsSnackbar() = runTest {
        val date = LocalDate.now().toString()
        val outfit = OutfitDomainModel("1", "Look", null, 800, 600, null, null, emptyList(), emptyList())

        viewModel.processIntent(OotdIntent.LoadData(date))
        coEvery { getDailyOutfitUseCase(date) } returns DailyOutfitDomainModel(date, null)
        coEvery { setDailyOutfitUseCase(date, "1") } returns DailyOutfitDomainModel(date, outfit)

        viewModel.event.test {
            viewModel.processIntent(OotdIntent.SelectOutfit("1"))

            val event = awaitItem()
            assertTrue(event is OotdEvent.ShowSnackbar)
            assertEquals("Образ установлен!", (event as OotdEvent.ShowSnackbar).message)
            assertFalse(viewModel.state.value.showSelectDialog)
            cancelAndIgnoreRemainingEvents()
        }
    }
}