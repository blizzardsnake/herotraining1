package com.herotraining.crash

import android.content.Context
import android.os.Build
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

/**
 * Writes uncaught exceptions to /external-files/crash/last_crash.log so we can inspect them
 * without ADB. Also keeps the default handler so process still terminates cleanly.
 */
object CrashHandler {
    private var previous: Thread.UncaughtExceptionHandler? = null

    fun install(context: Context) {
        val appContext = context.applicationContext
        previous = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            runCatching {
                val dir = File(appContext.getExternalFilesDir(null), "crash").apply { mkdirs() }
                val file = File(dir, "last_crash.log")
                file.writeText(formatCrash(thread, throwable))
            }
            previous?.uncaughtException(thread, throwable)
        }
    }

    fun readLastCrash(context: Context): String? {
        val file = File(context.getExternalFilesDir(null), "crash/last_crash.log")
        return if (file.exists()) file.readText() else null
    }

    fun clearLastCrash(context: Context) {
        val file = File(context.getExternalFilesDir(null), "crash/last_crash.log")
        if (file.exists()) file.delete()
    }

    private fun formatCrash(thread: Thread, t: Throwable): String {
        val sw = StringWriter()
        PrintWriter(sw).use { pw ->
            pw.println("Hero Training crash report")
            pw.println("Time: ${java.time.Instant.now()}")
            pw.println("Device: ${Build.MANUFACTURER} ${Build.MODEL}, Android ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})")
            pw.println("Thread: ${thread.name}")
            pw.println("---")
            t.printStackTrace(pw)
            var cause = t.cause
            while (cause != null) {
                pw.println("---")
                pw.println("Caused by:")
                cause.printStackTrace(pw)
                cause = cause.cause
            }
        }
        return sw.toString()
    }
}
