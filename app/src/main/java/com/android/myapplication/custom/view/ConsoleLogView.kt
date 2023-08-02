package com.android.myapplication.custom.view

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import com.android.myapplication.R

class ConsoleLogView(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {

    init {
        LayoutInflater.from(context).inflate(R.layout.console_log_tab_view, this, true)
    }
}
