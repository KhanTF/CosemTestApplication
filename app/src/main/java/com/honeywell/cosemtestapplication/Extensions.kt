package com.honeywell.cosemtestapplication

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

fun Activity.isGranted(grantResult: IntArray): Boolean {
    for (result in grantResult) {
        if (result != PackageManager.PERMISSION_GRANTED) return false
    }
    return true
}

@Suppress("SameParameterValue")
fun Activity.isGranted(permissions: Array<String>): Boolean {
    for (permission in permissions) {
        if (ActivityCompat.checkSelfPermission(
                this, permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }
    }
    return true
}