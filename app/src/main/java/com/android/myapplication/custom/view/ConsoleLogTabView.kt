package com.android.myapplication.custom.view

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.android.myapplication.R

internal class ConsoleLogTabView(context: Context, attrs: AttributeSet) :
    LinearLayout(context, attrs) {

    companion object {
        private const val START_INDEX = 0
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.console_log_tab_view, this, true)

        val promptTagText = resources.getString(R.string.prompt_tag)

        printLog(promptTagText, "this is the first linethis is the first linethis is the first linethis is the first linethis is the first linethis is the first linethis is the first linethis is the first linethis is the first linethis is the first line")
        printLog(promptTagText, "this is the second line")
        printLog(promptTagText, "this is the third line")
        printLog("assetlist@log:~$", "this is the third line")
        printLog("assetlist@log:~$", "this is the third line")
        printLog("assetlist@log:~$", "this is the third line")
        printLog("assetlist@log:~$", "this is the third line")
        printLog(promptTagText, "this is the first linethis is the first linethis is the first linethis is the first linethis is the first linethis is the first linethis is the first linethis is the first linethis is the first linethis is the first line")
        printLog(promptTagText, "this is the second line")
        printLog(promptTagText, "this is the third line")
        printLog("assetlist@log:~$", "this is the third line")
        printLog("assetlist@log:~$", "this is the third line")
        printLog("assetlist@log:~$", "this is the third line")
        printLog("assetlist@log:~$", "this is the third line")
    }

    fun printLog(promptText: String, outputText: String) {
        val terminalOutputLine = promptText + outputText
        val root = findViewById<LinearLayout>(R.id.log_tab_view_root)

        val spannableString = SpannableStringBuilder(terminalOutputLine)
        spannableString.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(context, R.color.prompt_tag)),
            START_INDEX,
            promptText.length,
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                // Triggered when the clickable span is clicked
                val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clipData = ClipData.newPlainText("text", promptText)
                clipboardManager.setPrimaryClip(clipData)
                Toast.makeText(context, "Text copied to clipboard", Toast.LENGTH_SHORT).show()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = ContextCompat.getColor(context, R.color.white) // Set the color you want for the clickable text
                ds.isUnderlineText = false // To remove underline
            }
        }

        spannableString.setSpan(
            clickableSpan,
            promptText.length + 1,
            terminalOutputLine.length,
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )

        TextView(context).apply {
            layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = resources.getDimensionPixelSize(R.dimen.kv_unit_x2) }
            text = spannableString
            textSize = resources.getDimension(R.dimen.kv_sp_unit_x3) / resources.displayMetrics.density
        }.also(root::addView)
    }
}
