package com.refoodio.home.presentation

import com.refoodio.core.domain.model.home.HomeStats

interface HomeContract {

    data class State(
        val stats: HomeStats = HomeStats(),
        val isLoading: Boolean = true
    )

    sealed interface Event {
        data object Refresh : Event
    }
}
