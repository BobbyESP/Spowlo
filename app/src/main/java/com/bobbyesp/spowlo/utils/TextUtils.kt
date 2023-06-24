package com.bobbyesp.spowlo.utils

import android.content.ClipboardManager
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.core.text.isDigitsOnly
import com.bobbyesp.spowlo.App.Companion.context
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.utils.notifications.ToastUtil
import java.util.regex.Pattern



object GeneralTextUtils {

    fun convertDuration(durationOfSong: Double): String {
        //First of all the duration comes with this format "146052" but it has to be "146.052"
        val duration: Double = if (durationOfSong > 100000.0){
            durationOfSong / 1000
        } else {
            durationOfSong
        }
        val hours = (duration / 3600).toInt()
        val minutes = ((duration % 3600) / 60).toInt()
        val seconds = (duration % 60).toInt()
        return if (hours > 0) {
            String.format("%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }

    fun copyToClipboardAndNotify( string: String){
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = android.content.ClipData.newPlainText(context.getString(R.string.copied_to_clipboard), string)
        clipboard.setPrimaryClip(clip)
        ToastUtil.makeToast(R.string.copied_to_clipboard)
    }
}

private const val GIGA_BYTES = 1024f * 1024f * 1024f
private const val MEGA_BYTES = 1024f * 1024f
@Composable
fun Long.toFileSizeText() = this.toFloat().run {
    if (this > GIGA_BYTES)
        stringResource(R.string.filesize_gb).format(this / GIGA_BYTES)
    else stringResource(R.string.filesize_mb).format(this / MEGA_BYTES)
}

fun String.isNumberInRange(start: Int, end: Int): Boolean {
    return this.isNotEmpty() && this.isDigitsOnly() && this.length < 10 && this.toInt() >= start && this.toInt() <= end
}

fun String?.toHttpsUrl(): String =
    this?.run {
        if (matches(Regex("^(http:).*"))) replaceFirst("http", "https") else this
    } ?: ""


fun matchUrlFromClipboard(string: String, isMatchingMultiLink: Boolean = false): String {
    matchUrlFromString(string, isMatchingMultiLink).run {
        if (isEmpty())
            ToastUtil.makeToast(R.string.paste_fail_msg)
        else
            ToastUtil.makeToast(R.string.paste_msg)
        return this
    }
}

private fun matchUrlFromString(s: String, isMatchingMultiLink: Boolean = false): String {
    val builder = StringBuilder()
    val pattern =
        Pattern.compile("(http|https)://[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-.,@?^=%&:/~+#]*[\\w\\-@?^=%&/~+#])?")
    with(pattern.matcher(s)) {
        if (isMatchingMultiLink)
            while (find()) {
                if (builder.isNotEmpty())
                    builder.append("\n")
                builder.append(group())
            }
        else if (find())
            builder.append(group())
    }
    return builder.toString()
}


fun connectWithDelimiter(vararg strings: String, delimiter: String = " · "): String {
    val builder = StringBuilder(strings.first())
    for (s in strings.asList().subList(1, strings.size)) {
        builder.append(delimiter)
        builder.append(s)
    }
    return builder.toString()
}

fun connectWithBlank(s1: String, s2: String): String {
    val f1 = s1.toEmpty()
    val f2 = s2.toEmpty()
    val blank = if (f1.isEmpty() || f2.isEmpty()) "" else " "
    return f1 + blank + f2
}

fun String.toEmpty() = if (equals("none") || equals("null")) "" else this