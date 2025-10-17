package com.ax.passnote.ui.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ax.passnote.data.Password
import com.ax.passnote.data.PasswordRepository
import com.ax.passnote.data.settings.SettingsRepository
import com.ax.passnote.data.settings.UserSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.security.SecureRandom

class EditPasswordViewModel(private val passwordRepository: PasswordRepository, private val settingsRepository: SettingsRepository, private val passwordId: Int) : ViewModel() {

    private val _password = MutableStateFlow<Password?>(null)
    val password: StateFlow<Password?> = _password.asStateFlow()

    private val _showConfirmDialog = MutableStateFlow(false)
    val showConfirmDialog: StateFlow<Boolean> = _showConfirmDialog.asStateFlow()
    
    private val settings: StateFlow<UserSettings> = settingsRepository.userSettingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserSettings(6, 12, true, ".!@#$%*&-")
        )

    init {
        viewModelScope.launch {
            _password.value = passwordRepository.findPasswordById(passwordId)
        }
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = _password.value?.copy(password = newPassword)
    }

    fun onNotesChange(newNotes: String) {
        _password.value = _password.value?.copy(notes = newNotes)
    }

    fun updatePassword() {
        viewModelScope.launch {
            password.value?.let { passwordRepository.updatePassword(it) }
        }
    }

    fun generateRandomPassword() {
        val currentSettings = settings.value
        val length = currentSettings.passwordLength
        val baseChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        val specialChars = if (currentSettings.useCustomChars) currentSettings.customChars else ""
        val allChars = baseChars + specialChars

        val random = SecureRandom()
        val newPassword = (1..length)
            .map { random.nextInt(allChars.length) }
            .map(allChars::get)
            .joinToString("")
        onPasswordChange(newPassword)
    }

    fun onShowConfirmDialog() {
        _showConfirmDialog.value = true
    }

    fun onDismissConfirmDialog() {
        _showConfirmDialog.value = false
    }
}

class EditPasswordViewModelFactory(private val passwordRepository: PasswordRepository, private val settingsRepository: SettingsRepository, private val passwordId: Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditPasswordViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditPasswordViewModel(passwordRepository, settingsRepository, passwordId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}