package com.ax.passnote.ui.more

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ax.passnote.data.Password
import com.ax.passnote.data.PasswordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class MoreViewModel(private val repository: PasswordRepository) : ViewModel() {

    private val _showClearConfirmDialog = MutableStateFlow(false)
    val showClearConfirmDialog: StateFlow<Boolean> = _showClearConfirmDialog.asStateFlow()

    private val _showCsvErrorDialog = MutableStateFlow(false)
    val showCsvErrorDialog: StateFlow<Boolean> = _showCsvErrorDialog.asStateFlow()

    private val _importSuccess = MutableStateFlow(false)
    val importSuccess: StateFlow<Boolean> = _importSuccess.asStateFlow()

    private val _exportSuccess = MutableStateFlow(false)
    val exportSuccess: StateFlow<Boolean> = _exportSuccess.asStateFlow()

    private val _clearSuccess = MutableStateFlow(false)
    val clearSuccess: StateFlow<Boolean> = _clearSuccess.asStateFlow()

    fun onShowClearConfirmDialog() {
        _showClearConfirmDialog.value = true
    }

    fun onDismissClearConfirmDialog() {
        _showClearConfirmDialog.value = false
    }

    fun onDismissCsvErrorDialog() {
        _showCsvErrorDialog.value = false
    }

    fun onImportSuccessToastShown() {
        _importSuccess.value = false
    }

    fun onExportSuccessToastShown() {
        _exportSuccess.value = false
    }

    fun onClearSuccessToastShown() {
        _clearSuccess.value = false
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllPasswords()
            _clearSuccess.value = true
        }
    }

    private suspend fun getPasswordsAsCsvString(): String {
        val passwords = repository.getAllPasswordsList()
        val csvBuilder = StringBuilder()
        // Header updated: Id is removed
        csvBuilder.append("Company,Username,Password,Notes\n")
        passwords.forEach { pw ->
            // Data row updated: pw.id is removed
            csvBuilder.append("${pw.service},${pw.username},${pw.password},${pw.notes ?: ""}\n")
        }
        return csvBuilder.toString()
    }

    fun exportPasswordsToCsv(uri: Uri, contentResolver: ContentResolver) {
        viewModelScope.launch {
            try {
                val csvContent = getPasswordsAsCsvString()
                contentResolver.openOutputStream(uri)?.use {
                    OutputStreamWriter(it).use {
                        it.write(csvContent)
                    }
                }
                _exportSuccess.value = true
            } catch (e: Exception) {
                // Handle potential exceptions during file write
            }
        }
    }

    fun importPasswordsFromCsv(uri: Uri, contentResolver: ContentResolver) {
        viewModelScope.launch {
            try {
                contentResolver.openInputStream(uri)?.use { inputStream ->
                    BufferedReader(InputStreamReader(inputStream)).use { reader ->
                        val header = reader.readLine()?.split(",")?.map { it.trim() }
                        // Required columns are still Company, Username, Password
                        if (header == null || !header.containsAll(listOf("Company", "Username", "Password"))) {
                            _showCsvErrorDialog.value = true
                            return@launch
                        }

                        val companyIndex = header.indexOf("Company")
                        val usernameIndex = header.indexOf("Username")
                        val passwordIndex = header.indexOf("Password")
                        // Notes column is now optional
                        val notesIndex = header.indexOf("Notes")

                        val passwordsToInsert = mutableListOf<Password>()
                        var line: String?
                        while (reader.readLine().also { line = it } != null) {
                            val values = line!!.split(",")
                            if (values.size > companyIndex && values.size > usernameIndex && values.size > passwordIndex) {
                                val company = values[companyIndex].trim()
                                val username = values[usernameIndex].trim()
                                val password = values[passwordIndex].trim()
                                // Read notes if the column exists and the data is available
                                val notes = if (notesIndex != -1 && values.size > notesIndex) values[notesIndex].trim() else null

                                if (company.isNotEmpty() && password.isNotEmpty()) {
                                    passwordsToInsert.add(Password(service = company, username = username, password = password, notes = notes))
                                }
                            }
                        }
                        if (passwordsToInsert.isNotEmpty()) {
                            repository.createPasswords(passwordsToInsert)
                            _importSuccess.value = true
                        }
                    }
                }
            } catch (e: Exception) {
                _showCsvErrorDialog.value = true
            }
        }
    }
}

class MoreViewModelFactory(private val repository: PasswordRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MoreViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MoreViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}