package com.android.consolelog.view

import android.content.Context
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.android.consolelog.R

class LogConsoleView(context: Context, private val attrs: AttributeSet) :
    ConstraintLayout(context, attrs) {

    private var consoleTabLayout: LinearLayout? = null
    private var tabIndexer: Int = 0
    private val tabStates = mutableMapOf<Int, Boolean>()
    private val logStates = mutableMapOf<Int, MutableList<String>>()

    init {
        LayoutInflater.from(context).inflate(R.layout.console_log_view, this, true)
        findViewById<LinearLayout>(R.id.consoleLogTabsLayout).also(::consoleTabLayout::set)

        val clearLogsButton = findViewById<Button>(R.id.clearLogsButton)
        clearLogsButton.setOnClickListener { clearLogsOnEachTab() }
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        val savedState = SavedState(superState)
        savedState.tabStates = tabStates
        savedState.tabLogs = logStates
        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        val savedState = state as? SavedState
        super.onRestoreInstanceState(savedState?.superState)

        savedState?.tabStates?.let {
            tabStates.clear()
            tabStates.putAll(it)
            for ((tabIndex, isVisible) in tabStates) {
                if (isVisible) {
                    showTab(tabIndex)
                } else {
                    hideTab(tabIndex)
                }
            }
        }

        savedState?.tabLogs?.let {
            logStates.clear()
            logStates.putAll(it)
            for ((tabIndex, logs) in logStates) {
                val logConsoleTabView = findViewWithTag<LogConsoleTabView>(tabIndex)
                logConsoleTabView.clearLogs()
                for (log in logs) {
                    logConsoleTabView.printLog(log)
                }
            }
        }
    }

    private fun clearLogsOnEachTab() {
        for (index in 0..tabIndexer) {
            clearLogsOnEachTab(index)
        }
    }

    private fun clearLogsOnEachTab(tabIndex: Int) {
        consoleTabLayout?.runCatching {
            findViewWithTag<LogConsoleTabView>(tabIndex).clearLogs()
        }?.getOrElse { Log.d(javaClass.simpleName, "Could not find view with tag `$tabIndex`") }
    }

    fun addTab(promptTextTag: String? = null) {
        consoleTabLayout?.apply {
            val tab = promptTextTag?.let { LogConsoleTabView(context, attrs, it) }
                ?: LogConsoleTabView(context, attrs)
            tab.apply {
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LayoutParams.MATCH_PARENT,
                    1.0f,
                )
                tag = tabIndexer++
            }.also(::addView)
        }
    }

    fun printLog(outputText: String, tabIndex: Int = 0) {
        consoleTabLayout?.runCatching {
            findViewWithTag<LogConsoleTabView>(tabIndex).printLog(outputText)
        }?.getOrElse { Log.d(javaClass.simpleName, "Could not find view with tag `$tabIndex`") }
    }

    fun hideTab(index: Int) {
        consoleTabLayout?.runCatching {
            findViewWithTag<LogConsoleTabView>(index).visibility = View.GONE
        }?.getOrElse { Log.d(javaClass.simpleName, "Could not find view with tag `$index`") }
    }

    fun showTab(index: Int) {
        consoleTabLayout?.runCatching {
            findViewWithTag<LogConsoleTabView>(index).visibility = View.VISIBLE
        }?.getOrElse { Log.d(javaClass.simpleName, "Could not find view with tag `$index`") }
    }

    private class SavedState : BaseSavedState {
        var tabStates: Map<Int, Boolean>? = null
        var tabLogs: Map<Int, MutableList<String>>? = null

        constructor(superState: Parcelable?) : super(superState)

        constructor(source: Parcel) : super(source) {
            val classLoader = this.javaClass.classLoader
            val bundle = source.readBundle(classLoader)
            tabStates = bundle?.let { bundleToMap(it) }
            tabLogs = bundle?.let { bundleToMapOfLists(it) }
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            super.writeToParcel(parcel, flags)
            val bundle = mapToBundle(tabStates)
            val logsBundle = mapOfListsToBundle(tabLogs)
            parcel.writeBundle(bundle)
            parcel.writeBundle(logsBundle)
        }

        private fun bundleToMap(bundle: Bundle): Map<Int, Boolean> {
            val map = mutableMapOf<Int, Boolean>()
            for (key in bundle.keySet()) {
                map[key.toInt()] = bundle.getBoolean(key)
            }
            return map
        }

        private fun bundleToMapOfLists(bundle: Bundle): Map<Int, MutableList<String>> {
            val map = mutableMapOf<Int, MutableList<String>>()
            for (key in bundle.keySet()) {
                map[key.toInt()] = bundle.getStringArrayList(key) ?: mutableListOf()
            }
            return map
        }

        private fun mapToBundle(map: Map<Int, Boolean>?): Bundle {
            val bundle = Bundle()
            map?.forEach { (key, value) ->
                bundle.putBoolean(key.toString(), value)
            }
            return bundle
        }

        private fun mapOfListsToBundle(map: Map<Int, MutableList<String>>?): Bundle {
            val bundle = Bundle()
            map?.forEach { (key, value) ->
                bundle.putStringArrayList(key.toString(), ArrayList(value))
            }
            return bundle
        }

        companion object CREATOR : Parcelable.Creator<SavedState> {
            override fun createFromParcel(parcel: Parcel): SavedState {
                return SavedState(parcel)
            }

            override fun newArray(size: Int): Array<SavedState?> {
                return arrayOfNulls(size)
            }
        }
    }
}
