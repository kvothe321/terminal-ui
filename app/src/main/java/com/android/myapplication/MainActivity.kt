package com.android.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.android.consolelog.view.LogConsoleView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val consoleLog = findViewById<LogConsoleView>(R.id.consoleLogView) ?: return
        consoleLog.addTab("assetlsit@log:~$")
        consoleLog.printLog("some text creditcard=1234-5678-9012-3456 and other text 9876 5432 1098 7654 end")

        consoleLog.addTab("pinfetcher@log:~$")
        consoleLog.printLog(
            "IN FETCHeyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5cERome text creditcard=1234-5678-9012-3456 and other text 9876 5432 1098 7654",
            1,
        )
    }
}
