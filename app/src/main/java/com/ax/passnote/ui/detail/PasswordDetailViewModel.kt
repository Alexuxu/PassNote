package com.ax.passnote.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ax.passnote.data.Password
import com.ax.passnote.data.PasswordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PasswordDetailViewModel(private val repository: PasswordRepository, passwordId: Int) : ViewModel() {

    // Switched to a reactive stream from the repository
    val password: StateFlow<Password?> = repository.getPasswordStream(passwordId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    private val _showDeleteConfirmDialog = MutableStateFlow(false)
    val showDeleteConfirmDialog: StateFlow<Boolean> = _showDeleteConfirmDialog.asStateFlow()

    fun deletePassword() {
        viewModelScope.launch {
            password.value?.let {
                repository.deletePassword(it)
            }
        }
    }

    fun onShowDeleteDialog() {
        _showDeleteConfirmDialog.value = true
    }

    fun onDismissDeleteDialog() {
        _showDeleteConfirmDialog.value = false
    }
}

class PasswordDetailViewModelFactory(private val repository: PasswordRepository, private val passwordId: Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PasswordDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PasswordDetailViewModel(repository, passwordId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}