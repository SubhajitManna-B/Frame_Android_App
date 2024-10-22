package com.ovaku.utils.ext

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.SystemClock
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.ovaku.R
import com.ovaku.databinding.CustomDialogLayoutBinding
import java.util.*

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Context.toast(message: String, gravity: Int) {
    val toast = Toast.makeText(this, message, Toast.LENGTH_LONG)
    toast.setGravity(gravity, 0, 0)
    toast.show()
}

fun Fragment.toast(msg: String, duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this.requireContext(), msg, duration).show()
}

fun ProgressBar.show() {
    visibility = View.VISIBLE
}

fun ProgressBar.hide() {
    visibility = View.GONE
}

fun View.snackbar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG).also { snackbar ->
        snackbar.setAction("Ok") {
            snackbar.dismiss()
        }
    }.show()
}

/**View Visibility Ext*/
fun View.gone() {
    if (visibility != View.GONE) visibility = View.GONE
}

fun View.visible() {
    if (visibility != View.VISIBLE) visibility = View.VISIBLE
}

fun View.invisible() {
    if (visibility != View.INVISIBLE) visibility = View.INVISIBLE
}

/**View Get Value Ext*/
val Button.value
    get() = text?.toString() ?: ""

val EditText.value
    get() = text?.toString() ?: ""

val TextView.value
    get() = text?.toString() ?: ""

/**View Empty validation Ext*/
fun EditText.isEmpty() = value.isEmpty()

fun TextView.isEmpty() = value.isEmpty()

/**Image SetUp Using Glide*/
fun ImageView.loadImage(imageId: Int) {
    Glide.with(this)
        .asBitmap()
        .load(imageId)
        .placeholder(R.color.colorPrimary)
        .into(this)
}

fun ImageView.loadImageWithCircle(image: Bitmap) {
    Glide.with(this)
        .asBitmap()
        .load(image)
        .circleCrop()
        .placeholder(R.drawable.profile)
        .into(this)
}

/**View Enable/Disable Ext*/
fun View.enable() {
    isEnabled = true
    alpha = 1f
}

fun View.disable() {
    isEnabled = false
    alpha = 0.5f
}

fun Window.setLightStatusBars(bool: Boolean) {
    WindowCompat.getInsetsController(this, decorView).isAppearanceLightStatusBars = bool
}

fun View.clickWithDebounce(debounceTime: Long = 600L, action: () -> Unit) {
    this.setOnClickListener(object : View.OnClickListener {
        private var lastClickTime: Long = 0

        override fun onClick(v: View) {
            if (SystemClock.elapsedRealtime() - lastClickTime < debounceTime) return
            else action()

            lastClickTime = SystemClock.elapsedRealtime()
        }
    })
}

fun Context.showCustomOkAlertFunction(background:Int = R.color.red ,title: String = getString(R.string.error), titleBackground: Int = R.color.dark_red, message: String) {

    val dialogBuilder2 = AlertDialog.Builder(this)
    val binding = CustomDialogLayoutBinding.inflate(LayoutInflater.from(this))
    binding.root.scaleX = 0f
    binding.root.scaleY = 0f
    binding.root.alpha = 0f

    binding.root.animate().scaleX(1f).duration = 1000
    binding.root.animate().scaleY(1f).duration = 1000
    binding.root.animate().alpha(1f).duration = 1000

    dialogBuilder2.setView(binding.root)
    dialogBuilder2.setCancelable(false)
    val alertDialog2 = dialogBuilder2.create()
    alertDialog2.window?.setBackgroundDrawableResource(android.R.color.transparent);
    alertDialog2.show()
    binding.clMainLayout.setBackgroundColor(ContextCompat.getColor(this, background))
    binding.tvTitle.text = title
    binding.tvTitle.setBackgroundColor(ContextCompat.getColor(this, titleBackground))
    binding.tvMessage.text = message
    binding.btnOk.setOnClickListener {
        binding.root.animate().scaleX(0f).duration = 1000
        binding.root.animate().scaleY(0f).duration = 1000
        binding.root.animate().alpha(0f).setDuration(1000).withEndAction {
            alertDialog2.dismiss()
        }
    }
    val window: Window = alertDialog2.window!!
    val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
    window.setLayout(
        width,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
}

fun Toast.showSuccessCustomToast(message: String, activity: Activity)
{
    val layout = activity.layoutInflater.inflate (
        R.layout.custom_toast_layout,
        activity.findViewById(R.id.mcvCustomToast)
    )
    val mcvCustomToast = layout.findViewById<MaterialCardView>(R.id.mcvCustomToast)
    val ivDone = layout.findViewById<ImageView>(R.id.ivDone)
    val tvMessage = layout.findViewById<TextView>(R.id.tvMessage)
    mcvCustomToast.strokeColor = ContextCompat.getColor(activity,R.color.green)
    mcvCustomToast.strokeWidth = 2
    ivDone.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_tick))
    tvMessage.text = message
    tvMessage.setTextColor(ContextCompat.getColor(activity,R.color.green))

    this.apply {
        setGravity(Gravity.BOTTOM, 0, 40)
        duration = Toast.LENGTH_SHORT
        view = layout
        show()
    }
}

fun Toast.showErrorCustomToast(message: String, activity: Activity)
{
    val layout = activity.layoutInflater.inflate (
        R.layout.custom_toast_layout,
        activity.findViewById(R.id.mcvCustomToast)
    )

    val mcvCustomToast = layout.findViewById<MaterialCardView>(R.id.mcvCustomToast)
    val ivDone = layout.findViewById<ImageView>(R.id.ivDone)
    val tvMessage = layout.findViewById<TextView>(R.id.tvMessage)
    mcvCustomToast.strokeColor = ContextCompat.getColor(activity,R.color.red)
    mcvCustomToast.strokeWidth = 2
    ivDone.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_cross))
    tvMessage.text = message
    tvMessage.setTextColor(ContextCompat.getColor(activity,R.color.red))

    this.apply {
        setGravity(Gravity.BOTTOM, 0, 40)
        duration = Toast.LENGTH_LONG
        view = layout
        show()
    }
}

fun decodeToken(jwt: String): String {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return "Requires SDK 26"
    val parts = jwt.split(".")
    return try {
        val charset = charset("UTF-8")
        val header = String(Base64.getUrlDecoder().decode(parts[0].toByteArray(charset)), charset)
        val payload = String(Base64.getUrlDecoder().decode(parts[1].toByteArray(charset)), charset)
        "$header"
        "$payload"
    } catch (e: Exception) {
        "Error parsing JWT: $e"
    }
}

/**Extension method for Material Alert Dialog Using Higher order function.*/
inline fun Context.showOkayAlertFunction(title: String, message: String, positiveBtnTxt: String = "Okay", crossinline positiveBtnClick: () -> Unit) {
    MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
        .setCancelable(false)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(positiveBtnTxt) { _, _ ->
            positiveBtnClick()
        }
        .show()
}
