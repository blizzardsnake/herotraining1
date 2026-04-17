package com.herotraining.update

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.herotraining.ui.components.UpdateState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

/**
 * Application-scope update checker. Lives at MainActivity level so the banner renders
 * above any screen (BootSplash / SignIn / Anketa / Dashboard / etc).
 */
class UpdateCheckViewModel(app: Application) : AndroidViewModel(app) {
    private val downloader = UpdateDownloader(app)

    private val _state = MutableStateFlow<UpdateState>(UpdateState.Hidden)
    val state: StateFlow<UpdateState> = _state.asStateFlow()

    private var lastCheckAt = 0L

    init {
        check()
    }

    /** Safe to call multiple times — rate-limited to one call per 5 minutes. */
    fun check(force: Boolean = false) {
        val now = System.currentTimeMillis()
        if (!force && now - lastCheckAt < 5 * 60 * 1000) return
        lastCheckAt = now
        viewModelScope.launch {
            val info = UpdateChecker.check(getApplication()) ?: return@launch
            // don't override an in-progress download with a fresh 'Available'
            if (_state.value is UpdateState.Downloading || _state.value is UpdateState.ReadyToInstall) return@launch
            // Auto-start download so user just needs to tap 'УСТАНОВИТЬ'
            download(info)
        }
    }

    fun download(info: UpdateInfo) {
        _state.value = UpdateState.Downloading(info, 0)
        downloader.download(
            info = info,
            onProgress = { pct ->
                val cur = _state.value
                if (cur is UpdateState.Downloading) _state.value = cur.copy(percent = pct)
            },
            onReady = { file ->
                _state.value = UpdateState.ReadyToInstall(info, file.absolutePath)
            }
        )
    }

    fun install() {
        val cur = _state.value
        if (cur is UpdateState.ReadyToInstall) downloader.launchInstaller(File(cur.apkPath))
    }

    fun dismiss() {
        // allow user to hide banner — will reappear on next launch anyway
        _state.value = UpdateState.Hidden
    }
}
