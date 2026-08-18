package com.nativemediaplayer.service

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class PlayerState(
    val uri: String = "",
    val title: String = "",
    val isPlaying: Boolean = false,
    val progress: Float = 0f
)

object PlayerService {
    private val _state = MutableStateFlow(PlayerState())
    val state: StateFlow<PlayerState> = _state

    fun updateState(newState: PlayerState) { _state.value = newState }
    fun updateProgress(p: Float) { _state.value = _state.value.copy(progress = p) }
    fun setPlaying(playing: Boolean) { _state.value = _state.value.copy(isPlaying = playing) }
}
