package com.android.consolelog.view

import android.content.Context
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import com.android.consolelog.R
import com.android.consolelog.model.TabData
import com.android.consolelog.model.TabIndex
import com.android.consolelog.model.TabStates

class LogConsoleView(context: Context, private val attrs: AttributeSet) :
    ConstraintLayout(context, attrs) {

    companion object {
        private const val KEY_TAB_STATES = "TAB_STATES"
        private const val KEY_SUPER_STATE = "SUPER_STATE"
        private const val KEY_BUNDLE = "BUNDLE"
        private const val KEY_TAB_INDEXER = "TAB_INDEXER"
    }

    private var consoleTabLayout: LinearLayout? = null
    private var tabIndexer: Int = 0
    private val tabStates = TabStates(hashMapOf())

    init {
        LayoutInflater.from(context).inflate(R.layout.console_log_view, this, true)
        findViewById<LinearLayout>(R.id.consoleLogTabsLayout).also(::consoleTabLayout::set)

        val clearLogsButton = findViewById<Button>(R.id.clearLogsButton)
        clearLogsButton.setOnClickListener { clearLogsOnEachTab() }
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState: Parcelable? = super.onSaveInstanceState()
        val bundle = Bundle().apply {
            putParcelable(KEY_TAB_STATES, tabStates)
            putInt(KEY_TAB_INDEXER, tabIndexer)
        }
        return bundleOf(KEY_SUPER_STATE to superState, KEY_BUNDLE to bundle)
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            val savedState = state.parcelable<Parcelable>(KEY_SUPER_STATE)
            super.onRestoreInstanceState(savedState)

            val bundle = state.parcelable<Bundle>(KEY_BUNDLE)
            bundle?.let {
                val tabStates = it.parcelable<TabStates>(KEY_TAB_STATES)
                tabStates?.let { states ->
                    restoreTabs(states.tabs)
                }

                it.getInt(KEY_TAB_INDEXER).also(::tabIndexer::set)
            }
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    private fun clearLogsOnEachTab() {
        for (index in 0..tabIndexer) {
            clearLogsOnTab(index)
        }
    }

    private fun clearLogsOnTab(tabIndex: Int) {
        consoleTabLayout?.runCatching {
            findViewWithTag<LogConsoleTabView>(tabIndex).clearLogs()
            tabStates.tabs[tabIndex]?.let {
                tabStates.tabs.put(tabIndex, it.copy(printedLogs = mutableListOf()))
            }
        }?.getOrElse { Log.d(javaClass.simpleName, "Could not find view with tag `$tabIndex`") }
    }

    private fun restoreTabs(tabStates: Map<TabIndex, TabData>) {
        for (tabState in tabStates) {
            addTab(tabState.value.promptTextTag, tabState.key)
            if (!tabState.value.isVisible) {
                hideTab(tabState.key)
            }

            restoreLogsOnTab(tabState.key, tabState.value.printedLogs)
        }
    }

    private fun restoreLogsOnTab(tabIndex: Int, logs: List<String>) {
        for (log in logs) {
            printLog(log, tabIndex)
        }
    }

    fun addTab(promptTextTag: String? = null, tabIndex: TabIndex? = null) {
        consoleTabLayout?.apply {
            val tab = promptTextTag?.let { LogConsoleTabView(context, attrs, it) }
                ?: LogConsoleTabView(context, attrs)
            tab.apply {
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LayoutParams.MATCH_PARENT,
                    1.0f,
                )
                tag = tabIndex ?: tabIndexer++
                (tag as? Int)?.let {
                    tabStates.tabs[it] =
                        TabData(isVisible = true, promptTextTag = promptTextTag)
                }
            }.also(::addView)
        }
    }

    fun printLog(outputText: String, tabIndex: Int = 0) {
        consoleTabLayout?.runCatching {
            findViewWithTag<LogConsoleTabView>(tabIndex).printLog(outputText)
            tabStates.tabs[tabIndex]?.let {
                it.printedLogs.add(outputText)
                // tabStates.tabs.put(tabIndex, it)
            }
        }?.getOrElse { Log.d(javaClass.simpleName, "Could not find view with tag `$tabIndex`") }
    }

    fun hideTab(index: Int) {
        consoleTabLayout?.runCatching {
            findViewWithTag<LogConsoleTabView>(index).visibility = View.GONE
            val tabData = tabStates.tabs[index]
            tabData?.let {
                tabStates.tabs.put(index, tabData.copy(isVisible = false))
            }
        }?.getOrElse { Log.d(javaClass.simpleName, "Could not find view with tag `$index`") }
    }

    fun showTab(index: Int) {
        consoleTabLayout?.runCatching {
            findViewWithTag<LogConsoleTabView>(index).visibility = View.VISIBLE
            val tabData = tabStates.tabs[index]
            tabData?.let {
                tabStates.tabs.put(index, tabData.copy(isVisible = true))
            }
        }?.getOrElse { Log.d(javaClass.simpleName, "Could not find view with tag `$index`") }
    }
}

inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = when {
    SDK_INT >= 33 -> getParcelable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelable(key) as? T
}


