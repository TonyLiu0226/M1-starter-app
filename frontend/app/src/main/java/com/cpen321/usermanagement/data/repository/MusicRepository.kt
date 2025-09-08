package com.cpen321.usermanagement.data.repository

import com.cpen321.usermanagement.R
import com.cpen321.usermanagement.data.model.Song
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicRepository @Inject constructor() {
    
    private val songs = listOf(
        Song(1, "Song 1", R.raw.song1),
        Song(2, "Song 2", R.raw.song2),
        Song(3, "Song 3", R.raw.song3),
        Song(4, "Song 4", R.raw.song4),
        Song(5, "Song 5", R.raw.song5),
        Song(6, "Song 6", R.raw.song6),
        Song(7, "Song 7", R.raw.song7),
        Song(8, "Song 8", R.raw.song8),
        Song(9, "Song 9", R.raw.song9),
        Song(10, "Song 10", R.raw.song10),
        Song(11, "Song 11", R.raw.song11),
        Song(12, "Song 12", R.raw.song12),
        Song(13, "Song 13", R.raw.song13),
        Song(14, "Song 14", R.raw.song14),
        Song(15, "Song 15", R.raw.song15),
        Song(16, "Song 16", R.raw.song16),
        Song(17, "Song 17", R.raw.song17),
    )
    
    fun getAllSongs(): List<Song> = songs
    
    fun getShuffledSongs(): List<Song> = songs.shuffled()
    
    fun getSongById(id: Int): Song? = songs.find { it.id == id }
} 