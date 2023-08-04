package com.android.myapplication.custom.view

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import com.android.myapplication.R

class ConsoleLogView(context: Context, private val attrs: AttributeSet) :
    ConstraintLayout(context, attrs) {

    private var consoleTabLayout: LinearLayout? = null
    private var tabIndexer: Int = 0

    init {
        LayoutInflater.from(context).inflate(R.layout.console_log_view, this, true)
        findViewById<LinearLayout>(R.id.consoleLogTabsLayout).also(::consoleTabLayout::set)

        val clearLogsButton = findViewById<Button>(R.id.clearLogsButton)
        clearLogsButton.setOnClickListener { clearLogsOnEachTab() }
    }

    private fun clearLogsOnEachTab() {
        for (index in 0..tabIndexer) {
            clearLogsOnEachTab(index)
        }
    }

    private fun clearLogsOnEachTab(tabIndex: Int) {
        consoleTabLayout?.runCatching {
            findViewWithTag<ConsoleLogTabView>(tabIndex).clearLogs()
        }?.getOrElse { Log.d(javaClass.simpleName, "Could not find view with tag `$tabIndex`") }
    }

    fun addTab(promptTextTag: String? = null) {
        consoleTabLayout?.apply {
            val tab = promptTextTag?.let { ConsoleLogTabView(context, attrs, it) }
                ?: ConsoleLogTabView(context, attrs)
            tab.apply {
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LayoutParams.MATCH_PARENT,
                    1.0f
                )
                tag = tabIndexer++
            }.also(::addView)
        }
    }

    fun printLog(outputText: String, tabIndex: Int = 0) {
        consoleTabLayout?.runCatching {
            findViewWithTag<ConsoleLogTabView>(tabIndex).printLog(outputText)
        }?.getOrElse { Log.d(javaClass.simpleName, "Could not find view with tag `$tabIndex`") }
    }

    fun hideTab(index: Int) {
        consoleTabLayout?.runCatching {
            findViewWithTag<ConsoleLogTabView>(index).visibility = View.GONE
        }?.getOrElse { Log.d(javaClass.simpleName, "Could not find view with tag `$index`") }
    }

    fun showTab(index: Int) {
        consoleTabLayout?.runCatching {
            findViewWithTag<ConsoleLogTabView>(index).visibility = View.VISIBLE
        }?.getOrElse { Log.d(javaClass.simpleName, "Could not find view with tag `$index`") }
    }
}
