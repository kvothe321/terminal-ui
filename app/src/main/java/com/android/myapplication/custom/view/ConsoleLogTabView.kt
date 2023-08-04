package com.android.myapplication.custom.view

import android.content.Context
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.android.myapplication.R

internal class ConsoleLogTabView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet,
    private val promptTextTag: String = "$>",
    defStyle: Int = 0
) : LinearLayout(context, attrs, defStyle) {

    companion object {
        private const val START_INDEX = 0
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.console_log_tab_view, this, true)
    }

    fun clearLogs() = kotlin.runCatching {
        findViewById<LinearLayout>(R.id.log_tab_view_root).removeAllViews()
    }

    fun printLog(outputText: String) {
        val terminalOutputLine = "$promptTextTag $outputText"
        val root = findViewById<LinearLayout>(R.id.log_tab_view_root)

        val spannableString = SpannableStringBuilder(terminalOutputLine)
        spannableString.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(context, R.color.prompt_tag)),
            START_INDEX,
            promptTextTag.length,
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )

        spannableString.setSpan(
            ForegroundColorSpan(Color.WHITE),
            promptTextTag.length,
            terminalOutputLine.length,
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )

        // ClickableSpan allows to perform actions like copying text to the clipboard
        /*spannableString.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("text", outputText)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, "Text copied to clipboard", Toast.LENGTH_SHORT).show()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }, promptTextTag.length, terminalOutputLine.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)*/

        TextView(context).apply {
            layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = resources.getDimensionPixelSize(R.dimen.kv_unit_x2) }
            text = spannableString
            textSize =
                resources.getDimension(R.dimen.kv_sp_unit_x3) / resources.displayMetrics.density
            setTextIsSelectable(true)
        }.also(root::addView)
    }
}
