package com.refoodio.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.refoodio.home.domain.GetHomeStatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHomeStats: GetHomeStatsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeContract.State())
    val state = _state.asStateFlow()

    init {
        loadStats()
    }

    fun handleEvent(event: HomeContract.Event) {
        when (event) {
            HomeContract.Event.Refresh -> loadStats()
        }
    }

    private fun loadStats() {
        viewModelScope.launch {
            getHomeStats()
                .catch { _state.update { it.copy(isLoading = false) } }
                .collect { stats ->
                    _state.update { it.copy(stats = stats, isLoading = false) }
                }
        }
    }
}
