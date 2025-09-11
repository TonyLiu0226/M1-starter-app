package com.cpen321.usermanagement.data.repository

import com.cpen321.usermanagement.data.remote.api.MusicInterface
import com.cpen321.usermanagement.data.remote.dto.ArtistSearchRequest
import com.cpen321.usermanagement.data.remote.dto.ArtistSearchResponse
import com.cpen321.usermanagement.data.remote.dto.TrackDownloadRequest
import com.cpen321.usermanagement.data.remote.dto.TrackDownloadResponse
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicApiRepository @Inject constructor(
    private val musicInterface: MusicInterface
) {
    suspend fun searchArtists(artistName: String): Response<ArtistSearchResponse> {
        return musicInterface.searchArtists(ArtistSearchRequest(artistName))
    }

    suspend fun downloadTrack(genre: String, count: Int = 5): Response<TrackDownloadResponse> {
        return musicInterface.downloadTrack(TrackDownloadRequest(genre, count))
    }
} 