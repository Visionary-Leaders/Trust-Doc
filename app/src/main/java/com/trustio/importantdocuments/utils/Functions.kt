package com.trustio.importantdocuments.utils

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.OvershootInterpolator
import android.view.animation.ScaleAnimation
import android.view.animation.TranslateAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.Zbekz.tashkentmetro.utils.enums.CurrentScreenEnum
import com.trustio.importantdocuments.R
import com.trustio.importantdocuments.data.local.room.entity.Bookmark
import com.trustio.importantdocuments.data.remote.response.file.FileItem
import java.io.File

fun initActivity(a: Activity) {
    val window = a.window

    WindowCompat.setDecorFitsSystemWindows(window, false)

}
fun List<Long>.convertBytesToMb(): Double {
    // Sum all values in the list, assumed to be in bytes
    val totalBytes = this.sum()

    // Convert the total bytes to megabytes (1 MB = 1048576 bytes)
    return totalBytes / 1048576.0
}

fun Int.convertBytesToMb(): Double {
    // Convert bytes to megabytes (1 MB = 1048576 bytes)
    return this / 1048576.0
}
fun List<Bookmark>.containsFileItem(fileItem: FileItem): Boolean {
    return this.any { it.id == fileItem.id }
}
//Mapper

fun openFile(context: Context, file: File) {
    val uri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, if (file.extension == "pdf") "application/pdf" else "image/*")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    } else {
        Toast.makeText(context, "Faylni ochish uchun dastur mavjud emas", Toast.LENGTH_SHORT).show()
    }
}

fun Bookmark.toFileItem(): FileItem {
    return FileItem(
        id = this.id,
        section = this.section,
        file = this.file,
        file_name = this.fileName,
        file_type = this.fileType,
        file_size = this.fileSize,
        user = this.user
    )
}
fun stringToCustomInt(input: String): Int {
    val prefix = "2456"
    val encoded = input.map { it.code }.joinToString("")
    return (prefix + encoded).toInt()
}

fun customIntToString(encoded: Int): String {
    val encodedString = encoded.toString()
    val prefix = "2456"
    if (!encodedString.startsWith(prefix)) {
        throw IllegalArgumentException("Invalid encoded value")
    }
    val asciiValues = encodedString.removePrefix(prefix)
    return asciiValues.chunked(2) { it.toString().toInt().toChar() }.joinToString("")
}

fun Bookmark.toFileItemWithSection(sectionName: String): FileItem {
    return FileItem(
        id = this.id,
        section = stringToCustomInt(sectionName),
        file = this.file,
        file_name = this.fileName,
        file_type = this.sectionName,
        file_size = this.fileSize,
        user = this.user
    )
}
fun FileItem.toBookmark(sectionName: String): Bookmark {
    return Bookmark(
        id = this.id,
        section = this.section,
        file = this.file,
        fileName = this.file_name,
        fileType = this.file_type,
        fileSize = this.file_size,
        user = this.user,
        sectionName = sectionName
    )
}

fun hideKeyboard(view: View) {
    val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}
class ZoomOutPageTransformer :
    ViewPager2.PageTransformer {
    override fun transformPage(view: View, position: Float) {
        if (position == 0.0f ) {
            setAnimation(
                view.context,
                view,
                300,
                floatArrayOf(1.3f, 1f, 1.3f, 1f),
                0.5f to 0f
            )
            ObjectAnimator.ofFloat(view, "alpha", 0f, 1.0f)
                .setDuration((200 * 1.5).toLong())
                .start()
        }
    }
}



fun String.screenCurrentEnum(): CurrentScreenEnum {
    return when (this) {
        "HOME" -> CurrentScreenEnum.HOME
        else -> CurrentScreenEnum.INTRO
    }
}
@Suppress("DEPRECATION")
fun Activity.hideSystemBars() {
    window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            )
}
@Suppress("DEPRECATION")
fun Fragment.hideSystemBars() {
    requireActivity().window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            )
}


fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun convertToPlainPhoneNumber(maskedPhoneNumber: String): String {
    // Remove all characters that are not digits or '+'
    return maskedPhoneNumber.filter { it.isDigit() || it == '+' }
}
fun sanitizePhoneNumber(phoneNumber: String): String {
    return phoneNumber.replace("[()\\s ]".toRegex(), "")
}

fun isValidPhoneNumber(phoneNumber: String): Boolean {
    val regex = Regex("""\+998 \(\d{2}\) \d{3} \d{2} \d{2}""")
    return regex.matches(phoneNumber)
}


fun setAnimation(
    context: Context,
    viewToAnimate: View,
    duration: Long = 150,
    list: FloatArray = floatArrayOf(0.0f, 1.0f, 0.0f, 1.0f),
    pivot: Pair<Float, Float> = 0.5f to 0.5f
) {
    val anim = ScaleAnimation(
        list[0],
        list[1],
        list[2],
        list[3],
        Animation.RELATIVE_TO_SELF,
        pivot.first,
        Animation.RELATIVE_TO_SELF,
        pivot.second
    )
    anim.duration = (duration * 1f).toLong()
    anim.setInterpolator(context, R.anim.over_shoot)
    viewToAnimate.startAnimation(anim)
}

fun Int?.or1() = this ?: 1

fun setSlideIn() = AnimationSet(false).apply {
    var animation: Animation = AlphaAnimation(0.0f, 1.0f)
    animation.duration = (500 * 1f).toLong()
    animation.interpolator = AccelerateDecelerateInterpolator()
    addAnimation(animation)

    animation = TranslateAnimation(
        Animation.RELATIVE_TO_SELF, 1.0f,
        Animation.RELATIVE_TO_SELF, 0f,
        Animation.RELATIVE_TO_SELF, 0.0f,
        Animation.RELATIVE_TO_SELF, 0f
    )

    animation.duration = (750 * 1f).toLong()
    animation.interpolator = OvershootInterpolator(1.1f)
    addAnimation(animation)
}

fun setSlideUp() = AnimationSet(false).apply {
    var animation: Animation = AlphaAnimation(0.0f, 1.0f)
    animation.duration = (500 * 1f).toLong()
    animation.interpolator = AccelerateDecelerateInterpolator()
    addAnimation(animation)

    animation = TranslateAnimation(
        Animation.RELATIVE_TO_SELF, 0.0f,
        Animation.RELATIVE_TO_SELF, 0f,
        Animation.RELATIVE_TO_SELF, 1.0f,
        Animation.RELATIVE_TO_SELF, 0f
    )

    animation.duration = (750 * 1f).toLong()
    animation.interpolator = OvershootInterpolator(1.1f)
    addAnimation(animation)
}
