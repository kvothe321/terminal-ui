package com.android.consolelog.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TabStates(
    val tabs: HashMap<TabIndex, TabData>
) : Parcelable

@Parcelize
data class TabData(
    var isVisible: Boolean,
    val promptTextTag: String?,
    val printedLogs: MutableList<String> = mutableListOf()
) : Parcelable

internal typealias TabIndex = Int
