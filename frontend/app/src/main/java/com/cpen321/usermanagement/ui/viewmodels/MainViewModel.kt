package com.cpen321.usermanagement.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cpen321.usermanagement.data.remote.dto.ArtistSearchResponse
import com.cpen321.usermanagement.data.repository.MusicApiRepository
import com.cpen321.usermanagement.service.MusicPlayerService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.TimeoutCancellationException
import javax.inject.Inject

data class MainUiState(
    val successMessage: String? = null,
    // Music discovery fields
    val isLoadingMusic: Boolean = false,
    val artistName: String = "",
    val foundArtist: ArtistSearchResponse? = null,
    val downloadUrl: String? = null,
    val musicErrorMessage: String? = null
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val musicApiRepository: MusicApiRepository,
    private val musicPlayerService: MusicPlayerService
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    fun setSuccessMessage(message: String) {
        _uiState.value = _uiState.value.copy(successMessage = message)
    }

    fun clearSuccessMessage() {
        _uiState.value = _uiState.value.copy(successMessage = null)
    }

    // Music Discovery Functions
    fun updateArtistName(name: String) {
        _uiState.value = _uiState.value.copy(
            artistName = name,
            musicErrorMessage = null
        )
    }

    fun findSimilarMusic() {
        val artistName = _uiState.value.artistName.trim()
        if (artistName.isEmpty()) {
            _uiState.value = _uiState.value.copy(
                musicErrorMessage = "Please enter an artist name"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoadingMusic = true,
                musicErrorMessage = null,
                foundArtist = null,
                downloadUrl = null
            )

            try {
                // Add overall timeout of 45 seconds for the entire operation
                withTimeout(45000) {
                    // Step 1: Search for artist
                    val artistResponse = musicApiRepository.searchArtists(artistName)
                    
                    if (artistResponse.isSuccessful && artistResponse.body() != null) {
                        val artist = artistResponse.body()!!
                        _uiState.value = _uiState.value.copy(foundArtist = artist)

                        // Step 2: Get track from artist's genres
                        if (artist.genres.isNotEmpty()) {
                            var found = false
                            var lastError: String? = null
                            
                            for (genre in artist.genres) {
                                try {
                                    val trackResponse = musicApiRepository.downloadTrack(genre, 5)
                                    
                                    if (trackResponse.isSuccessful && trackResponse.body() != null) {
                                        val downloadUrl = trackResponse.body()!!.url
                                        _uiState.value = _uiState.value.copy(
                                            downloadUrl = downloadUrl,
                                            isLoadingMusic = false
                                        )
                                        
                                        // Immediately play the downloaded track
                                        val artistName = _uiState.value.foundArtist?.name ?: "Unknown Artist"
                                        musicPlayerService.playDownloadedTrack(downloadUrl, "Similar to $artistName")
                                        found = true
                                        break
                                    } else {
                                        lastError = "Failed to download track for $genre (HTTP ${trackResponse.code()})"
                                    }
                                } catch (e: Exception) {
                                    lastError = when {
                                        e.message?.contains("timeout", ignoreCase = true) == true -> 
                                            "Request timed out while downloading $genre tracks"
                                        e.message?.contains("connect", ignoreCase = true) == true -> 
                                            "Unable to connect to music service"
                                        else -> "Network error for $genre: ${e.message}"
                                    }
                                    // Continue to next genre instead of failing completely
                                    continue
                                }
                            }
                            
                            if (!found) {
                                _uiState.value = _uiState.value.copy(
                                    musicErrorMessage = lastError ?: "No tracks found for this artist's style",
                                    isLoadingMusic = false
                                )
                            }
                        } else {
                            _uiState.value = _uiState.value.copy(
                                musicErrorMessage = "Artist found but no genre information available",
                                isLoadingMusic = false
                            )
                        }
                    } else {
                        _uiState.value = _uiState.value.copy(
                            musicErrorMessage = "Artist not found",
                            isLoadingMusic = false
                        )
                    }
                }
            } catch (e: TimeoutCancellationException) {
                _uiState.value = _uiState.value.copy(
                    musicErrorMessage = "Operation timed out. The music service may be slow. Please try again.",
                    isLoadingMusic = false
                )
            } catch (e: Exception) {
                val errorMessage = when {
                    e.message?.contains("timeout", ignoreCase = true) == true -> 
                        "Request timed out. Please check your connection and try again."
                    e.message?.contains("connect", ignoreCase = true) == true -> 
                        "Unable to connect to music service. Please try again later."
                    else -> "Network error: ${e.message}"
                }
                _uiState.value = _uiState.value.copy(
                    musicErrorMessage = errorMessage,
                    isLoadingMusic = false
                )
            }
        }
    }

    fun clearMusicError() {
        _uiState.value = _uiState.value.copy(musicErrorMessage = null)
    }

    fun stopMusicLoading() {
        _uiState.value = _uiState.value.copy(
            isLoadingMusic = false,
            musicErrorMessage = "Operation cancelled"
        )
    }

    fun clearMusicResults() {
        _uiState.value = _uiState.value.copy(
            foundArtist = null,
            downloadUrl = null,
            musicErrorMessage = null
        )
    }
    
    /**
     * Reset the music discovery state completely (used when user logs in)
     */
    fun resetMusicDiscoveryState() {
        _uiState.value = MainUiState()
    }
}
