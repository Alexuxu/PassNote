package com.ax.passnote.ui.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ax.passnote.data.Password
import com.ax.passnote.data.PasswordRepository
import com.ax.passnote.data.UsernameCount
import com.ax.passnote.data.settings.SettingsRepository
import com.ax.passnote.data.settings.UserSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.security.SecureRandom

class AddPasswordViewModel(private val passwordRepository: PasswordRepository, private val settingsRepository: SettingsRepository) : ViewModel() {

    private val _validationError = MutableStateFlow<String?>(null)
    val validationError: StateFlow<String?> = _validationError.asStateFlow()

    private val settings: StateFlow<UserSettings> = settingsRepository.userSettingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserSettings(6, 12, true, ".!@#$%*&-")
        )

    val frequentUsernames: StateFlow<List<UsernameCount>> = settings.flatMapLatest { userSettings ->
        passwordRepository.getFrequentUsernames(userSettings.usernameThreshold)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun validateAndSave(service: String, username: String?, password: String, notes: String?) {
        viewModelScope.launch {
            if (service.isBlank()) {
                _validationError.value = "公司/服务为空"
                return@launch
            }

            if (password.isBlank()) {
                _validationError.value = "密码为空"
                return@launch
            }

            val existingPasswords = passwordRepository.findPasswordsByService(service)
            if (existingPasswords.any { it.username.equals(username, ignoreCase = false) }) {
                _validationError.value = "账号已经存在"
                return@launch
            }

            val newPassword = Password(service = service, username = username ?: "", password = password, notes = notes)
            passwordRepository.createPassword(newPassword)
            _validationError.value = "Success"
        }
    }

    fun generateRandomPassword(): String {
        val currentSettings = settings.value
        val length = currentSettings.passwordLength
        val baseChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        val specialChars = if (currentSettings.useCustomChars) currentSettings.customChars else ""
        val allChars = baseChars + specialChars

        val random = SecureRandom()
        return (1..length)
            .map { random.nextInt(allChars.length) }
            .map(allChars::get)
            .joinToString("")
    }

    fun resetValidationError() {
        _validationError.value = null
    }
}

class AddPasswordViewModelFactory(private val passwordRepository: PasswordRepository, private val settingsRepository: SettingsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddPasswordViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddPasswordViewModel(passwordRepository, settingsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}