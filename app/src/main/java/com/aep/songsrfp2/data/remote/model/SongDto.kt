package com.aep.songsrfp2.data.remote.model

import com.google.gson.annotations.SerializedName

data class SongDto(
    @SerializedName("id")
    var id: String? = null,
    @SerializedName("title")
    var title: String? = null,
    @SerializedName("artist")
    var artist: String? = null,
    @SerializedName("genre")
    var genre: String? = null,
    @SerializedName("album")
    var album: String? = null,
    @SerializedName("release_date")
    var releaseDate: String? = null,
    @SerializedName("record_label")
    var recordLabel: String? = null,
    @SerializedName("image")
    var image: String? = null,
    @SerializedName("video")
    var video: String? = null,
    @SerializedName("lat")
    var lat: Double? = null,
    @SerializedName("lng")
    var lng: Double? = null
)
