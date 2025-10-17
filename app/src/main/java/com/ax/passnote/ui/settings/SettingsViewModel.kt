package com.ax.passnote.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ax.passnote.data.settings.SettingsRepository
import com.ax.passnote.data.settings.UserSettings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val settingsRepository: SettingsRepository) : ViewModel() {

    val settings: StateFlow<UserSettings> = settingsRepository.userSettingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserSettings(6, 12, false, ".!@#$%*&-")
        )

    fun onUsernameThresholdChange(value: Float) {
        viewModelScope.launch {
            settingsRepository.updateUsernameThreshold(value.toInt())
        }
    }

    fun onPasswordLengthChange(value: Float) {
        viewModelScope.launch {
            settingsRepository.updatePasswordLength(value.toInt())
        }
    }

    fun onUseCustomCharsChange(use: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateUseCustomChars(use)
        }
    }

    fun onCustomCharsChange(chars: String) {
        viewModelScope.launch {
            settingsRepository.updateCustomChars(chars)
        }
    }
}

class SettingsViewModelFactory(private val settingsRepository: SettingsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(settingsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}