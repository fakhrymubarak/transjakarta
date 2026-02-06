package com.fakhry.transjakarta

import android.app.Application
import com.fakhry.transjakarta.core.networking.BuildConfig
import com.fakhry.transjakarta.utils.logger.FileLoggingTree
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class TransjakartaApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Set the timber only for debugger.
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Timber.plant(FileLoggingTree(this))
        }
    }
}
