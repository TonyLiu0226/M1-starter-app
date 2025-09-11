package com.cpen321.usermanagement.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cpen321.usermanagement.data.remote.dto.ArtistSearchResponse
import com.cpen321.usermanagement.data.repository.MusicApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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
    private val musicApiRepository: MusicApiRepository
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
                // Step 1: Search for artist
                val artistResponse = musicApiRepository.searchArtists(artistName)
                
                if (artistResponse.isSuccessful && artistResponse.body() != null) {
                    val artist = artistResponse.body()!!
                    _uiState.value = _uiState.value.copy(foundArtist = artist)

                    // Step 2: Get track from artist's genres
                    if (artist.genres.isNotEmpty()) {
                        val genre = artist.genres.first() // Use first genre
                        val trackResponse = musicApiRepository.downloadTrack(genre, 5)
                        
                        if (trackResponse.isSuccessful && trackResponse.body() != null) {
                            val downloadUrl = trackResponse.body()!!.url
                            _uiState.value = _uiState.value.copy(
                                downloadUrl = downloadUrl,
                                isLoadingMusic = false
                            )
                        } else {
                            _uiState.value = _uiState.value.copy(
                                musicErrorMessage = "No tracks found for this artist's style",
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
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    musicErrorMessage = "Network error: ${e.message}",
                    isLoadingMusic = false
                )
            }
        }
    }

    fun clearMusicError() {
        _uiState.value = _uiState.value.copy(musicErrorMessage = null)
    }

    fun clearMusicResults() {
        _uiState.value = _uiState.value.copy(
            foundArtist = null,
            downloadUrl = null,
            musicErrorMessage = null
        )
    }
}
