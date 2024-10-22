package com.ovaku.utils.permissionHandler

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.ovaku.utils.permissionHandler.AppPermission

fun Fragment.isGranted(permission: AppPermission) = run {
    context?.let {
        (PermissionChecker.checkSelfPermission(it, permission.permissionName)
                == PermissionChecker.PERMISSION_GRANTED)
    } ?: false
}

fun Fragment.shouldShowRationale(permission: AppPermission) = run {
    shouldShowRequestPermissionRationale(permission.permissionName)
}

fun Fragment.requestPermission(permission: AppPermission) {
    requestPermissions(arrayOf(permission.permissionName), permission.requestCode)
}

fun AppCompatActivity.isGranted(permission: AppPermission) = run {
    this?.let {
        (ActivityCompat.checkSelfPermission(
            it, permission.permissionName
        ) == PermissionChecker.PERMISSION_GRANTED)
    } ?: false
}

fun AppCompatActivity.shouldRequestPermissionRationale(permission: AppPermission) =
    ActivityCompat.shouldShowRequestPermissionRationale(this, permission.permissionName)

fun AppCompatActivity.requestAllPermissions(permission: AppPermission) {
    ActivityCompat.requestPermissions(
        this,
        arrayOf(permission.permissionName),
        permission.requestCode
    )
}

fun Fragment.handlePermission(
    permission: AppPermission,
    onGranted: (AppPermission) -> Unit,
    onDenied: (AppPermission) -> Unit,
    onRationaleNeeded: ((AppPermission) -> Unit)? = null) {
    when {
        isGranted(permission) ->onGranted.invoke(permission)
        shouldShowRationale(permission) ->  onRationaleNeeded?.invoke(permission)
        else ->  onDenied.invoke(permission)
    }
}

fun AppCompatActivity.handlePermission(
    permission: AppPermission,
    onGranted: (AppPermission) -> Unit,
    onDenied: (AppPermission) -> Unit,
    onRationaleNeeded: ((AppPermission) -> Unit)? = null) {
    when {
        isGranted(permission) ->onGranted.invoke(permission)
        shouldRequestPermissionRationale(permission) ->  onRationaleNeeded?.invoke(permission)
        else ->  onDenied.invoke(permission)
    }
}

fun Fragment.handlePermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray,
    onPermissionGranted: (AppPermission) -> Unit,
    onPermissionDenied: ((AppPermission) -> Unit)? = null,
    onPermissionDeniedPermanently: ((AppPermission) -> Unit)? = null) {
    AppPermission.permissions.find { it.requestCode == requestCode }?.let { appPermission ->
        val permissionGrantResult = mapPermissionsAndResults(permissions, grantResults
        )[appPermission.permissionName]
        when {
            PermissionChecker.PERMISSION_GRANTED == permissionGrantResult -> {
                onPermissionGranted(appPermission)
            }
            shouldShowRationale(appPermission) -> onPermissionDenied?.invoke(appPermission)
            else -> {
                goToAppDetailsSettings()
                onPermissionDeniedPermanently?.invoke(appPermission)
            }
        }
    }
}

fun AppCompatActivity.handlePermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray,
    onPermissionGranted: (AppPermission) -> Unit,
    onPermissionDenied: ((AppPermission) -> Unit)? = null,
    onPermissionDeniedPermanently: ((AppPermission) -> Unit)? = null) {
    AppPermission.permissions.find { it.requestCode == requestCode }?.let { appPermission ->
        val permissionGrantResult = mapPermissionsAndResults(permissions, grantResults
        )[appPermission.permissionName]
        when {
            PermissionChecker.PERMISSION_GRANTED == permissionGrantResult -> {
                onPermissionGranted(appPermission)
            }
            shouldRequestPermissionRationale(appPermission) -> onPermissionDenied?.invoke(appPermission)
            else -> {
                goToAppDetailsSettings()
                onPermissionDeniedPermanently?.invoke(appPermission)
            }
        }
    }
}

fun Fragment.goToAppDetailsSettings() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context?.packageName, null)
    }
    activity?.let {
        it.startActivityForResult(intent, 0)
    }
}

fun AppCompatActivity.goToAppDetailsSettings() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", packageName, null)
    }
    startActivityForResult(intent, 0)
}

private fun mapPermissionsAndResults(permissions: Array<out String>, grantResults: IntArray
): Map<String, Int> = permissions.mapIndexedTo(mutableListOf<Pair<String, Int>>()
) { index, permission -> permission to grantResults[index] }.toMap()


const val DENIED ="denied"
const val EXPLAINED ="explained"

/**
 * [Permission] Permission name
 * [Granted] Successful application
 * [Denied] Rejected and unchecked do not ask again
 * [Explained] Rejected and checked Do not ask again
 */
inline fun Fragment.requestPermission(
    permission: String,
    crossinline granted: (permission: String) -> Unit = {},
    crossinline denied: (permission: String) -> Unit = {},
    crossinline explained: (permission: String) -> Unit = {}
) {
    registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
        when {
            result -> granted.invoke(permission)
            shouldShowRequestPermissionRationale(permission) -> denied.invoke(permission)
            else -> explained.invoke(permission)
        }
    }.launch(permission)
}

/**
 * [Permissions] Permissions array
 * [AllGranted] All permissions are successfully applied
 * [Denied] A list of permissions that have been rejected without checking Do not ask again, and at the same time denied without checking Do not ask again
 * [Explained] The permission list that was rejected and checked Don’t ask again, and was rejected and checked Don’t ask again
 */
inline fun Fragment.requestMultiplePermissions(
    permissions: Array<String>,
    crossinline allGranted: () -> Unit = {},
    crossinline denied: (List<String>) -> Unit = {},
    crossinline explained: (List<String>) -> Unit = {}
) {
    registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result->
        //Filter elements whose value is false and convert them to list
        val deniedList = result.filter { !it.value }.map { it.key }
        when {
            deniedList.isNotEmpty() -> {
                //Group the rejected all list, and the grouping condition is whether to check and do not ask again
                val map = deniedList.groupBy { permission ->
                    if (shouldShowRequestPermissionRationale(permission)) DENIED else EXPLAINED
                }
                // Rejected and unchecked Do not ask again
                map[DENIED]?.let {
                    denied.invoke(it)
                }
                // Rejected and checked Do not ask again
                map[EXPLAINED]?.let {
                    explained.invoke(it)
                }
            }
            else -> allGranted.invoke()
        }
    }.launch(permissions)
}

inline fun FragmentActivity.requestMultiplePermissions(
    permissions: Array<String>,
    crossinline allGranted: () -> Unit = {},
    crossinline denied: (List<String>) -> Unit = {},
    crossinline explained: (List<String>) -> Unit = {}
) {
    registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result->
        //Filter elements whose value is false and convert them to list
        val deniedList = result.filter { !it.value }.map { it.key }
        when {
            deniedList.isNotEmpty() -> {
                //Group the rejected all list, and the grouping condition is whether to check and do not ask again
                val map = deniedList.groupBy { permission ->
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) DENIED else EXPLAINED
                }
                // Rejected and unchecked Do not ask again
                map[DENIED]?.let { denied.invoke(it) }
                // Rejected and checked Do not ask again
                map[EXPLAINED]?.let { explained.invoke(it) }
            }
            else -> allGranted.invoke()
        }
    }.launch(permissions)
}


