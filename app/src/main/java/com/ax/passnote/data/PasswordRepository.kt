package com.ax.passnote.data

import kotlinx.coroutines.flow.Flow

class PasswordRepository(private val passwordDao: PasswordDao) {

    val allPasswords: Flow<List<Password>> = passwordDao.getAllPasswords()

    // --- Create --- //
    suspend fun createPassword(password: Password) {
        passwordDao.insert(password)
    }

    suspend fun createPasswords(passwords: List<Password>) {
        passwordDao.insertAll(passwords)
    }

    // --- Update --- //
    suspend fun updatePassword(password: Password) {
        passwordDao.update(password)
    }

    // --- Delete --- //
    suspend fun deletePassword(password: Password) {
        passwordDao.delete(password)
    }

    suspend fun clearAllPasswords() {
        passwordDao.clearAll()
    }

    // --- Query --- //
    suspend fun findPasswordById(id: Int): Password? {
        return passwordDao.getPasswordById(id)
    }

    fun getPasswordStream(id: Int): Flow<Password?> {
        return passwordDao.getPasswordFlowById(id)
    }

    suspend fun getAllPasswordsList(): List<Password> {
        return passwordDao.getAllPasswordsList()
    }

    fun searchPasswords(query: String): Flow<List<Password>> {
        return passwordDao.searchPasswords("%${query}%")
    }

    suspend fun findPasswordsByService(service: String): List<Password> {
        return passwordDao.getPasswordsByService(service)
    }

    suspend fun findPasswordsByUsername(username: String): List<Password> {
        return passwordDao.getPasswordsByUsername(username)
    }

    fun getFrequentUsernames(threshold: Int): Flow<List<UsernameCount>> { // Parameterized threshold
        return passwordDao.getFrequentUsernames(threshold)
    }
}
