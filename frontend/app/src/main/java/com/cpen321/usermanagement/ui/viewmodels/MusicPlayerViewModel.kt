package com.cpen321.usermanagement.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cpen321.usermanagement.data.repository.MusicRepository
import com.cpen321.usermanagement.service.MusicPlayerService
import com.cpen321.usermanagement.service.PlayerState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicPlayerViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    private val musicPlayerService: MusicPlayerService
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(MusicPlayerUiState())
    val uiState: StateFlow<MusicPlayerUiState> = _uiState.asStateFlow()
    
    init {
        // Initialize with shuffled playlist
        initializePlaylist()
        
        // Observe player state changes
        viewModelScope.launch {
            musicPlayerService.playerState.collect { playerState ->
                _uiState.value = _uiState.value.copy(
                    playerState = playerState,
                    isLoading = false
                )
            }
        }
        
        // Update progress periodically
        startProgressUpdater()
    }
    
    private fun initializePlaylist() {
        val shuffledSongs = musicRepository.getShuffledSongs()
        musicPlayerService.setPlaylist(shuffledSongs)
    }
    
    fun togglePlayPause() {
        musicPlayerService.togglePlayPause()
    }
    
    fun nextTrack() {
        musicPlayerService.nextTrack()
    }
    
    fun rewindToStart() {
        musicPlayerService.rewindToStart()
    }
    
    fun seekTo(position: Float) {
        val durationMs = _uiState.value.playerState.duration
        val positionMs = (position * durationMs).toLong()
        musicPlayerService.seekTo(positionMs)
    }
    
    fun shufflePlaylist() {
        val shuffledSongs = musicRepository.getShuffledSongs()
        musicPlayerService.setPlaylist(shuffledSongs)
    }
    
    fun playDownloadedTrack(url: String, artistName: String = "Unknown Artist") {
        val title = "Similar to $artistName"
        musicPlayerService.playDownloadedTrack(url, title)
    }
    
    private fun startProgressUpdater() {
        viewModelScope.launch {
            while (isActive) {
                if (_uiState.value.playerState.isPlaying) {
                    val currentPosition = musicPlayerService.getCurrentPosition()
                    _uiState.value = _uiState.value.copy(
                        playerState = _uiState.value.playerState.copy(
                            currentPosition = currentPosition
                        )
                    )
                    
                    // Check if track should advance to next
                    musicPlayerService.checkAndHandleTrackCompletion()
                }
                delay(100) // Update every 100ms for more precise end detection
            }
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        musicPlayerService.release()
    }
}

data class MusicPlayerUiState(
    val playerState: PlayerState = PlayerState(),
    val isLoading: Boolean = true
) 