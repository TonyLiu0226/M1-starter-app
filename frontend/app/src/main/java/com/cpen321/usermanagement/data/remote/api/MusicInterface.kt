package com.cpen321.usermanagement.data.remote.api

import com.cpen321.usermanagement.data.remote.dto.ArtistSearchRequest
import com.cpen321.usermanagement.data.remote.dto.ArtistSearchResponse
import com.cpen321.usermanagement.data.remote.dto.TrackDownloadRequest
import com.cpen321.usermanagement.data.remote.dto.TrackDownloadResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface MusicInterface {
    @POST("music/search-artists")
    suspend fun searchArtists(@Body request: ArtistSearchRequest): Response<ArtistSearchResponse>

    @POST("music/download-track")
    suspend fun downloadTrack(@Body request: TrackDownloadRequest): Response<TrackDownloadResponse>
} 