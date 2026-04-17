package com.herotraining.update

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.Uri
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File

/** Downloads the APK via system DownloadManager + fires install intent when done. */
class UpdateDownloader(private val context: Context) {

    private val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    fun download(info: UpdateInfo, onProgress: (percent: Int) -> Unit = {}, onReady: (File) -> Unit) {
        val outDir = File(context.getExternalFilesDir(null), "updates").apply { mkdirs() }
        val outFile = File(outDir, "hero-training-${info.versionName}.apk")
        if (outFile.exists()) outFile.delete()

        val request = DownloadManager.Request(Uri.parse(info.apkUrl))
            .setTitle("Hero Training · обновление ${info.versionName}")
            .setDescription("Загрузка APK ${info.apkSizeBytes / 1024 / 1024} МБ")
            .setDestinationUri(Uri.fromFile(outFile))
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(false)

        val id = dm.enqueue(request)

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context, intent: Intent) {
                val finishedId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
                if (finishedId != id) return
                ctx.unregisterReceiver(this)
                if (outFile.exists() && outFile.length() > 0) {
                    onProgress(100)
                    onReady(outFile)
                }
            }
        }
        val filter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.registerReceiver(context, receiver, filter, ContextCompat.RECEIVER_EXPORTED)
        } else {
            context.registerReceiver(receiver, filter)
        }

        // Lightweight polling for progress while DownloadManager is running.
        pollProgress(id, onProgress)
    }

    private fun pollProgress(downloadId: Long, onProgress: (Int) -> Unit) {
        val thread = Thread {
            var running = true
            while (running) {
                val query = DownloadManager.Query().setFilterById(downloadId)
                val cursor: Cursor? = dm.query(query)
                cursor?.use { c ->
                    if (c.moveToFirst()) {
                        val total = c.getLong(c.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                        val so = c.getLong(c.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                        val status = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
                        if (total > 0) onProgress(((so * 100) / total).toInt())
                        running = status == DownloadManager.STATUS_RUNNING ||
                                  status == DownloadManager.STATUS_PENDING ||
                                  status == DownloadManager.STATUS_PAUSED
                    } else running = false
                } ?: run { running = false }
                Thread.sleep(500)
            }
        }
        thread.isDaemon = true
        thread.start()
    }

    /** Opens the standard Android installer with the downloaded APK. */
    fun launchInstaller(apk: File) {
        val authority = "${context.packageName}.fileprovider"
        val uri = FileProvider.getUriForFile(context, authority, apk)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }
}
