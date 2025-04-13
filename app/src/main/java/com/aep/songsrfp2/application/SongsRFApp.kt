package com.aep.songsrfp2.application

import android.app.Application
import com.aep.songsrfp2.data.SongRepository
import com.aep.songsrfp2.data.remote.RetrofitHelper

class SongsRFApp : Application() {
    private val retrofit by lazy{
        RetrofitHelper().getRetrofit()
    }

    val repository by lazy {
        SongRepository(retrofit)
    }
}