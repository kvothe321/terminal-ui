package com.android.consolelog.view

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.ReplacementSpan
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.android.consolelog.R

internal class LogConsoleTabView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet,
    private val promptTextTag: String = "$>",
) : LinearLayout(context, attrs) {

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
            0,
            promptTextTag.length,
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE,
        )

        spannableString.setSpan(
            ForegroundColorSpan(Color.WHITE),
            promptTextTag.length,
            terminalOutputLine.length,
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE,
        )

        val creditCardRegex =
            Regex("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}")

        formatText(spannableString, outputText, creditCardRegex)

        TextView(context).apply {
            layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT,
            ).apply { bottomMargin = resources.getDimensionPixelSize(R.dimen.kv_unit_x2) }
            text = spannableString
            textSize =
                resources.getDimension(R.dimen.kv_sp_unit_x3) / resources.displayMetrics.density
            setTextIsSelectable(true)
            movementMethod = LinkMovementMethod.getInstance()
        }.also(root::addView)

        val scrollView = root.parent as? ScrollView
        scrollView?.post {
            scrollView.fullScroll(ScrollView.FOCUS_DOWN)
        }
    }

    private fun formatText(spannableString: SpannableStringBuilder, text: String, regex: Regex) {
        val indices = regex.findAll(text).map { it.range }.toList()
        for (range in indices) {
            val offset = promptTextTag.length + 1
            val startIndexOfWord = range.first + offset
            val endIndexOfWord = range.last + offset + 1

            spannableString.setSpan(
                object : ReplacementSpan() {
                    override fun getSize(
                        paint: Paint,
                        text: CharSequence?,
                        start: Int,
                        end: Int,
                        fm: Paint.FontMetricsInt?,
                    ): Int {
                        return paint.measureText(text, start, end).toInt()
                    }

                    override fun draw(
                        canvas: Canvas,
                        text: CharSequence?,
                        start: Int,
                        end: Int,
                        x: Float,
                        top: Int,
                        y: Int,
                        bottom: Int,
                        paint: Paint,
                    ) {
                        val rect = RectF(
                            x,
                            top.toFloat(),
                            x + paint.measureText(text, start, end),
                            bottom.toFloat(),
                        )
                        paint.color = Color.parseColor("#50a8e9ff")
                        canvas.drawRoundRect(rect, 10f, 10f, paint)
                        paint.color = Color.WHITE // Change text color as needed
                        if (text != null) {
                            canvas.drawText(text, start, end, x, y.toFloat(), paint)
                        }
                    }
                },
                startIndexOfWord,
                endIndexOfWord,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE,
            )

            spannableString.setSpan(
                object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        val clipboard =
                            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText(
                            "text",
                            text.subSequence(startIndexOfWord - offset, endIndexOfWord - offset)
                        )
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Text copied to clipboard", Toast.LENGTH_SHORT)
                            .show()
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        super.updateDrawState(ds)
                        ds.isUnderlineText = false
                    }
                },
                startIndexOfWord,
                endIndexOfWord,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE,
            )
        }
    }
}
