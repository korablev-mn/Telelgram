package com.example.telelgram.utilits

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

const val READ_CONTACT = Manifest.permission.READ_CONTACTS  // предоставление доступа
const val PERMISSION_REQUEST = 200

fun checkPermissions(permission: String): Boolean {
    return if (Build.VERSION.SDK_INT >= 23 &&
        ContextCompat.checkSelfPermission(
            APP_ACTIVITY,
            permission
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(APP_ACTIVITY, arrayOf(permission), PERMISSION_REQUEST)
        false
    } else true
}