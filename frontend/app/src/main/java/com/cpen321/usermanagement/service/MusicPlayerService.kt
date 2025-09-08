package com.cpen321.usermanagement.service

import android.content.Context
import android.net.Uri
import androidx.annotation.RawRes
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.cpen321.usermanagement.data.model.Song
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

data class PlayerState(
    val isPlaying: Boolean = false,
    val currentSong: Song? = null,
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
    val playlist: List<Song> = emptyList(),
    val currentIndex: Int = 0
)

@Singleton
class MusicPlayerService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var exoPlayer: ExoPlayer? = null
    
    private val _playerState = MutableStateFlow(PlayerState())
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()
    
    private var currentPlaylist: List<Song> = emptyList()
    private var currentIndex: Int = 0
    
    init {
        initializePlayer()
    }
    
    private fun initializePlayer() {
        exoPlayer = ExoPlayer.Builder(context).build().apply {
            addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    updatePlayerState(isPlaying = isPlaying)
                }
                
                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    updatePlayerState()
                }
                
                override fun onPlaybackStateChanged(playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_ENDED -> {
                            // Auto play next song
                            nextTrack()
                        }
                        Player.STATE_READY -> {
                            updatePlayerState(
                                duration = exoPlayer?.duration ?: 0L
                            )
                        }
                    }
                }
            })
            repeatMode = Player.REPEAT_MODE_ALL
        }
    }
    
    fun setPlaylist(songs: List<Song>) {
        currentPlaylist = songs
        currentIndex = 0
        val mediaItems = songs.map { song ->
            MediaItem.fromUri(getRawResourceUri(song.resourceId))
        }
        exoPlayer?.setMediaItems(mediaItems)
        exoPlayer?.prepare()
        updatePlayerState(
            currentSong = songs.firstOrNull(),
            playlist = songs,
            currentIndex = 0
        )
    }
    
    fun play() {
        exoPlayer?.play()
    }
    
    fun pause() {
        exoPlayer?.pause()
    }
    
    fun togglePlayPause() {
        if (_playerState.value.isPlaying) {
            pause()
        } else {
            play()
        }
    }
    
    fun nextTrack() {
        if (currentPlaylist.isNotEmpty()) {
            currentIndex = (currentIndex + 1) % currentPlaylist.size
            exoPlayer?.seekToNext()
            updatePlayerState(
                currentSong = currentPlaylist[currentIndex],
                currentIndex = currentIndex
            )
        }
    }
    
    fun rewindToStart() {
        // Rewind to the beginning of the current track
        exoPlayer?.seekTo(0)
        updatePlayerState(currentPosition = 0L)
    }
    
    fun seekTo(positionMs: Long) {
        exoPlayer?.seekTo(positionMs)
    }
    
    fun getCurrentPosition(): Long {
        return exoPlayer?.currentPosition ?: 0L
    }
    
    fun release() {
        exoPlayer?.release()
        exoPlayer = null
    }
    
    private fun updatePlayerState(
        isPlaying: Boolean = _playerState.value.isPlaying,
        currentSong: Song? = _playerState.value.currentSong,
        currentPosition: Long = getCurrentPosition(),
        duration: Long = _playerState.value.duration,
        playlist: List<Song> = _playerState.value.playlist,
        currentIndex: Int = _playerState.value.currentIndex
    ) {
        _playerState.value = _playerState.value.copy(
            isPlaying = isPlaying,
            currentSong = currentSong,
            currentPosition = currentPosition,
            duration = duration,
            playlist = playlist,
            currentIndex = currentIndex
        )
    }
    
    private fun getRawResourceUri(@RawRes resourceId: Int): Uri {
        return Uri.parse("android.resource://${context.packageName}/$resourceId")
    }
} 