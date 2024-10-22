package com.ovaku.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import com.ovaku.utils.imageUtils.RealPathUtil
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

fun getBitmapFromURL(src: String?): Bitmap? {
    return try {
        val url = URL(src)
        val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
        connection.doInput = true
        connection.connect()
        val input: InputStream = connection.inputStream
        BitmapFactory.decodeStream(input)
    } catch (e: IOException) {
        null
    }
}

@Throws(FileNotFoundException::class)
fun Context.decodeBitmapUri(uri: Uri): Bitmap? {
    val targetW = 600
    val targetH = 600
    val bmOptions = BitmapFactory.Options()
    bmOptions.inJustDecodeBounds = true
    BitmapFactory.decodeStream(this.contentResolver.openInputStream(uri), null, bmOptions)
    val photoW = bmOptions.outWidth
    val photoH = bmOptions.outHeight
    val scaleFactor = (photoW / targetW).coerceAtMost(photoH / targetH)
    bmOptions.inJustDecodeBounds = false
    bmOptions.inSampleSize = scaleFactor
    return BitmapFactory.decodeStream(this.contentResolver.openInputStream(uri), null, bmOptions)
}

@Throws(IOException::class)
fun fixBitmapOrientation(context: Context, uri: Uri, bmp: Bitmap?, imageChoose: Int?): Bitmap? {
    val ei = if(imageChoose == 0){
        ExifInterface(uri.path!!)
    }else{
        ExifInterface(RealPathUtil.getRealPath(context, uri))
    }

    val orientation = ei.getAttributeInt(
        ExifInterface.TAG_ORIENTATION,
        ExifInterface.ORIENTATION_NORMAL
    )
    when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> return rotateBitmap(bmp!!, 90F)
        ExifInterface.ORIENTATION_ROTATE_180 -> return rotateBitmap(bmp!!, 180F)
        ExifInterface.ORIENTATION_ROTATE_270 -> return rotateBitmap(bmp!!, 270F)
    }
    return bmp
}

fun rotateBitmap(source: Bitmap, angle: Float): Bitmap? {
    val matrix = Matrix()
    matrix.postRotate(angle)
    return Bitmap.createBitmap(
        source, 0, 0, source.width,
        source.height, matrix, true
    )
}