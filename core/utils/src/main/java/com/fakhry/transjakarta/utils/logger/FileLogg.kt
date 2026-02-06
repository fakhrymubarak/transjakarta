package com.fakhry.transjakarta.utils.logger

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FileLoggingTree(private val context: Context) : Timber.DebugTree() {

    private val logDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // Create a specific file in the app's internal cache or files dir
    // Path: /data/user/0/com.your.package/files/logs/app_logs.txt
    private val logFile: File by lazy {
        val logDir = File(context.filesDir, "logs")
        if (!logDir.exists()) {
            logDir.mkdirs()
        }
        File(logDir, "app_logs.txt")
    }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        // Optional: Only log Warnings and Errors to file to save I/O
        // if (priority < Log.WARN) return

        val timestamp = logDateFormat.format(Date())
        val priorityString = priorityToString(priority)

        // Format: 2026-02-05 10:00:00.123 | D | Tag | Message
        val logMessage = "$timestamp | $priorityString | $tag | $message\n"

        // Write asynchronously to avoid blocking the Main Thread
        scope.launch {
            try {
                // 'true' enables append mode
                FileWriter(logFile, true).use { writer ->
                    writer.append(logMessage)
                    // If there's an exception, append the stack trace
                    t?.let {
                        writer.append(Log.getStackTraceString(it))
                    }
                }
            } catch (e: IOException) {
                Log.e("FileLoggingTree", "Error writing log to file", e)
            }
        }
    }

    private fun priorityToString(priority: Int): String {
        return when (priority) {
            Log.VERBOSE -> "V"
            Log.DEBUG -> "D"
            Log.INFO -> "I"
            Log.WARN -> "W"
            Log.ERROR -> "E"
            Log.ASSERT -> "A"
            else -> "?"
        }
    }
}
