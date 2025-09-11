package com.cpen321.usermanagement.data.remote.dto

data class ArtistSearchRequest(
    val artist: String
)

data class ArtistSearchResponse(
    val id: String,
    val name: String,
    val genres: List<String>
)

data class TrackDownloadRequest(
    val genre: String,
    val count: Int
)

data class TrackDownloadResponse(
    val url: String
) 