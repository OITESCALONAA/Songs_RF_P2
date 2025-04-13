package com.aep.songsrfp2.data.remote

import com.aep.songsrfp2.data.remote.model.SongDto
import retrofit2.http.GET
import retrofit2.http.Path

interface SongsApi {
    //Listado de canciones en apiary
    @GET("songs")
    suspend fun getSongs(): List<SongDto>

    //Detalle de cada canción en apiary
    @GET("songs/song_detail/{id}")
    suspend fun getSong(
        @Path("id")
        id: String?
    ): SongDto
}