package com.aep.songsrfp2.Utils

import android.app.Activity
import android.os.Build
import android.widget.Toast

inline fun isAtLeastAndroid(versionCode: Int, action: () -> Unit) {
    if (Build.VERSION.SDK_INT >= versionCode) {
        action()
    }
}

fun Activity.message(message: String, duration: Int = Toast.LENGTH_SHORT){
    Toast.makeText(
        this,
        message,
        duration
    ).show()
}