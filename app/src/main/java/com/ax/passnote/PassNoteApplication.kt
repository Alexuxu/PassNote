package com.ax.passnote

import android.app.Application
import com.ax.passnote.data.AppDatabase
import com.ax.passnote.data.PasswordRepository
import com.ax.passnote.data.settings.SettingsRepository

class PassNoteApplication : Application() {
    // Using by lazy so the database and repository are only created when they're needed
    // rather than when the application starts
    val database by lazy { AppDatabase.getDatabase(this) }
    val passwordRepository by lazy { PasswordRepository(database.passwordDao()) }
    val settingsRepository by lazy { SettingsRepository(this) }
}
