package com.cherrymooncake.wardrobe_android.core.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch


abstract class BaseViewModel<S : IUiState, I : IUiIntent, E : ISingleFlowEvent>(
    initialState: S
) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state.asStateFlow()

    private val _event = Channel<E>()
    val event: Flow<E> = _event.receiveAsFlow()

    abstract fun processIntent(intent: I)

    protected fun updateState(reducer: S.() -> S) {
        _state.value = _state.value.reducer()
    }

    protected fun sendEvent(event: E) {
        viewModelScope.launch {
            _event.send(event)
        }
    }
}