package com.ovaku.utils.ext

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.telephony.TelephonyManager
import android.text.format.DateUtils
import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.navOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ovaku.R
import com.ovaku.app.AppData.EMAIL_PATTERN
import com.ovaku.data.models.responseModel.Resource
import com.ovaku.utils.imageUtils.FileUtils
import com.ovaku.utils.imageUtils.FileUtils.getMimeType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern


/**Convert simple object to String with Gson*/
inline fun <reified T : Any> T.toSimpleJson() : String =  Gson().toJson(this, T::class.java)

/**Convert String Json to Object*/
inline fun <reified T : Any> String.fromJsonToObject() : T =  Gson().fromJson(this ,  T::class.java)

/**Convert String List Json to Object*/
inline fun <reified T : Any> String.fromJsonToObjectList() : MutableList <T> =  when( this.isNotEmpty()){
    true -> Gson().fromJson(this, object : TypeToken<MutableList<T>>() {}.type)
    false -> mutableListOf()
}

fun JSONArray.toMutableList(): MutableList<Any> = MutableList(length(), this::get)

inline fun <T> safeCall(action: () -> Resource<T>): Resource<T> {
    return try {
        action()
    } catch (e: Exception) {
        e.printStackTrace()
        Resource.Error(message = e.message ?: "An unknown Error Occurred")
    }
}


fun NavController.navigateWithDefaultAnimation(directionId: Int, bundle: Bundle? = null) {
    navigate(directionId, bundle, navOptions {
        anim {
            enter = R.anim.slide_in_right
            exit = R.anim.slide_out_left
            popEnter = R.anim.slide_in_left
            popExit = R.anim.slide_out_right
        }
    })
}

fun NavController.navigateWithBottomTopAnimation(directionId: Int, bundle: Bundle? = null) {
    navigate(directionId, bundle, navOptions {
        anim {
            enter = R.anim.slide_in_bottom
            exit = R.anim.no_anim   /*R.anim.slide_out_top*/
            popEnter = R.anim.no_anim
            popExit = R.anim.slide_out_bottom
        }
    })
}

fun String.convertToDate(format: String = "yyyy-MM-dd"/*"yyyy-MM-dd HH:mm:ss"*/, localeId: Locale = Locale.getDefault()): Date? {
    try {
        return SimpleDateFormat(format, localeId).parse(this)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}

fun Date?.convertToStringDate(outputFormat: String = "dd-MM-yyyy", localeId: Locale = Locale.getDefault()): String {
    if (this != null) {
        val requiredFormat = SimpleDateFormat(outputFormat, localeId)
        try {
            return requiredFormat.format(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    return ""
}

fun currentDate(): String? {
    return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
}

fun currentDateAsDF(format: String = "yyyy-MM-dd HH:mm:ss"): String? {
    return SimpleDateFormat(format, Locale.getDefault()).format(Date())
}

fun Date.calculateAgoTime(): String = DateUtils.getRelativeTimeSpanString(time,
    Calendar.getInstance().timeInMillis, DateUtils.MINUTE_IN_MILLIS).toString()

fun Context.BitmapToFile(bitmap: Bitmap, filename: String): File? { // File name like "image.png"
    //create a file to write bitmap data
    var file: File? = null
    return try {
        /*file = File(Environment.getExternalStorageDirectory().toString() + File.separator + fileNameToSave)*/
        file = File(cacheDir, filename)
        file.createNewFile()

        //Convert bitmap to byte array
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos) // YOU can also save it in JPEG
        val bitmapdata = bos.toByteArray()

        //write the bytes in file
        val fos = FileOutputStream(file)
        fos.write(bitmapdata)
        fos.flush()
        fos.close()
        file
    } catch (e: Exception) {
        e.printStackTrace()
        file // it will return null
    }
}

fun Date?.convertToString(outputFormat: String = "dd-MM-yyyy", localeId: Locale = Locale.getDefault()): String {
    if (this != null) {
        val requiredFormat = SimpleDateFormat(outputFormat, localeId)
        try {
            return requiredFormat.format(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    return ""
}

/*var dayOfTheWeek = DateFormat.format("EEEE", date) as String // Thursday
var day = DateFormat.format("dd", date) as String // 20
var monthString = DateFormat.format("MMM", date) as String // Jun
var monthNumber = DateFormat.format("MM", date) as String // 06
var year = DateFormat.format("yyyy", date) as String // 2013*/

fun Context.createMultipartImageFile(filename: String?, file: Bitmap?, partName: String,
                                     listener: (MultipartBody.Part) -> Unit) {
    if (filename != null) {
        val f = File(cacheDir, filename)
        try {
            f.createNewFile()
            val bitmap = file!!
            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos)
            val bitmapData = bos.toByteArray()
            val fos = FileOutputStream(f)
            fos.write(bitmapData)
            fos.flush()
            fos.close()
            val imageBody = prepareFilePart(partName, Uri.fromFile(f))
            listener(imageBody)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}

fun Context.prepareFilePart(partName: String?, fileUri: Uri?): MultipartBody.Part {
    val file = FileUtils.getFile(this, fileUri)
    val type: MediaType = getMimeType(file)?.let { it.toMediaTypeOrNull() }!!
    val requestFile: RequestBody = RequestBody.create(type, file)
    return MultipartBody.Part.createFormData(partName!!, file.name, requestFile)
}

/*fun File.createMultipartFile(imgType: String): MultipartBody.Part {
    return MultipartBody.Part.createFormData(
        imgType, this.toString(), this.asRequestBody("multipart/from-data".toMediaTypeOrNull())
    )
}*/

fun String.createMultipartFormData(): RequestBody {
    return this.toRequestBody("multipart/form-data".toMediaTypeOrNull())
}

fun Int.getResizedBitmap(height: Int, width: Int, context: Context): Bitmap? {
    val imageBitmap = BitmapFactory.decodeResource(
        context.resources,
        context.resources.getIdentifier(this.toString(), "drawable", context.packageName)
    )
    return Bitmap.createScaledBitmap(imageBitmap, width, height, false)
}

val emailValid = fun(mail: String): Boolean {
    return Pattern.compile(EMAIL_PATTERN).matcher(mail).matches()
}

suspend fun getUrlDrawable(url: String?): Drawable? = withContext(Dispatchers.IO){
    try {
        Drawable.createFromStream(URL(url).openStream(), "image.jpg")
    } catch (ex: Exception) {
        ex.printStackTrace()
        null
    }
}