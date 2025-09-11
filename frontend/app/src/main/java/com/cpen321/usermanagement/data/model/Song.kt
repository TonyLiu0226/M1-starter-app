package com.cpen321.usermanagement.data.model

data class Song(
    val id: Int,
    val title: String,
    val resourceId: Int? = null,
    val url: String? = null
) {
    init {
        require(resourceId != null || url != null) {
            "Song must have either a resourceId or url"
        }
    }
} 