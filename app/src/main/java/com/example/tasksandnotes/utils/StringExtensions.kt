package com.example.tasksandnotes.utils


import android.text.SpannableString
import android.text.Spanned
import android.text.style.StrikethroughSpan

fun String.addStrikethrough() : SpannableString {
    val spannable = SpannableString(this)
    spannable.setSpan(StrikethroughSpan(), 0, this.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    return spannable
}