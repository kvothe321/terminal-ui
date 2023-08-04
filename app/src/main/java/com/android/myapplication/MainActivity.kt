package com.android.myapplication

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.android.myapplication.custom.view.ConsoleLogView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val consoleLog = findViewById<ConsoleLogView>(R.id.consoleLogView) ?: return
        consoleLog.addTab("assetlsit@log:~$")
        consoleLog.printLog("WTF coaie")
        consoleLog.printLog("WTF coaie")
        consoleLog.printLog("WTF coaie")
        consoleLog.printLog("WTF coaie")
        consoleLog.printLog("WTF coaie")

        consoleLog.addTab("pinfetcher@log:~$")
        consoleLog.printLog("WTF coaie PIN FETCHERWTF coaie PIN FETCHERWTF coaie PIN FETCHERWTF coaie PIN FETCHERWTF coaie PIN FETCHERWTF coaie PIN FETCHERWTF coaie PIN FETCHER", 1)
        consoleLog.printLog("WTF coaie PIN FETCHER", 1)
        consoleLog.printLog("WTF coaie PIN FETCHER", 1)
        consoleLog.printLog("WTF coaie PINWTF coaie PIN FETCHERWTF coaie PIN FETCHERWTF coaie PIN FETCHER FETCHER", 1)
        consoleLog.printLog("WTF coaie PIN FETCHER", 1)
    }
}
