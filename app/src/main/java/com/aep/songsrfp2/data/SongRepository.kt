package com.aep.songsrfp2.data

import com.aep.songsrfp2.data.remote.SongsApi
import com.aep.songsrfp2.data.remote.model.SongDto
import retrofit2.Retrofit

class SongRepository(
    private val retrofit: Retrofit
) {
    //Creamos nuestra instancia al api para acceder a los endpoints
    private val songApi = retrofit.create(SongsApi::class.java)

    suspend fun getSongs(): List<SongDto> = songApi.getSongs()

    suspend fun getSong(id: String?): SongDto = songApi.getSong(id)
}