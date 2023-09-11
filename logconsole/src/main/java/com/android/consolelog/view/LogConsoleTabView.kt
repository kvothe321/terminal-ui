package com.android.consolelog.view

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.webkit.*
import android.widget.Toast
import com.android.consolelog.R
import java.util.*

internal class LogConsoleTabView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet,
    private val promptTextTag: String = "$>",
) : WebView(context, attrs) {

    private var webView: WebView? = null
    private val javascriptCallQueue = LinkedList<String>()
    private var pageLoaded = false

    init {
        LayoutInflater.from(context).inflate(R.layout.console_log_tab_view, this, true)

        initWebView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        findViewById<WebView>(R.id.web_view).apply {
            settings.javaScriptEnabled = true
            webView = this
            addJavascriptInterface(WebViewJavascriptInterface(context), "app")
            loadUrl("file:///android_asset/web.html")

            webViewClient = object : WebViewClient() {
                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?,
                ) {
                    super.onReceivedError(view, request, error)
                    Log.e(
                        this@LogConsoleTabView.javaClass.simpleName,
                        error?.description.toString(),
                    )
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    pageLoaded = true
                    executePendingJavascriptCalls()
                }
            }
        }
    }

    fun clearLogs() {
        webView?.loadUrl("file:///android_asset/web.html")
    }

    fun printLog(outputText: String) {
        queueJavascriptCall("javascript:printLog('$promptTextTag', '$outputText');")
    }

    private fun queueJavascriptCall(jsFunction: String) {
        javascriptCallQueue.add(jsFunction)
        executePendingJavascriptCalls()
    }

    private fun executePendingJavascriptCalls() {
        if (pageLoaded) {
            while (javascriptCallQueue.isNotEmpty()) {
                val jsFunction = javascriptCallQueue.poll().orEmpty()
                webView?.evaluateJavascript(jsFunction, null)
            }
        }
    }

    internal inner class WebViewJavascriptInterface(private val context: Context) {
        @JavascriptInterface
        fun copyToClipboard(message: String) {
            val clipboardManager =
                context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("text", message)
            clipboardManager.setPrimaryClip(clipData)
            Log.d(this@LogConsoleTabView.javaClass.simpleName, "Copied to clipboard: $message")
            Toast.makeText(context, "Text copied to clipboard", Toast.LENGTH_SHORT).show()
        }
    }
}
