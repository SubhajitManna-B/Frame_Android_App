package com.ovaku.utils.permissionHandler

import com.ovaku.R


sealed class AppPermission(val permissionName: String, val requestCode: Int, val deniedMessageId: Int
, val explanationMessageId: Int) {
    companion object {
        val permissions: List<AppPermission> by lazy {
            listOf(
                ACCESS_COARSE_LOCATION,
                ACCESS_FINE_LOCATION
            )
        }
    }

    object ACCESS_FINE_LOCATION : AppPermission(
        android.Manifest.permission.ACCESS_FINE_LOCATION, 42,
        R.string.permission_required_text, R.string.permission_required_text
    )

    object ACCESS_COARSE_LOCATION : AppPermission(
        android.Manifest.permission.ACCESS_COARSE_LOCATION, 43,
        R.string.permission_required_text, R.string.permission_required_text
    )

    object ACCESS_BACKGROUND_LOCATION : AppPermission(
        android.Manifest.permission.ACCESS_BACKGROUND_LOCATION, 43,
        R.string.permission_required_text, R.string.permission_required_text
    )

    object CAMERA : AppPermission(
        android.Manifest.permission.CAMERA, 43,
        R.string.permission_required_text, R.string.permission_required_text
    )
}
