package com.ax.passnote.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

data class UsernameCount(val username: String, val count: Int)

@Dao
interface PasswordDao {

    // --- Create --- //
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(password: Password)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(passwords: List<Password>)

    // --- Update --- //
    @Update
    suspend fun update(password: Password)

    // --- Delete --- //
    @Delete
    suspend fun delete(password: Password)

    @Query("DELETE FROM passwords")
    suspend fun clearAll()

    // --- Query --- //
    @Query("SELECT * FROM passwords WHERE id = :id")
    suspend fun getPasswordById(id: Int): Password?

    @Query("SELECT * FROM passwords WHERE id = :id")
    fun getPasswordFlowById(id: Int): Flow<Password?>

    @Query("SELECT * FROM passwords ORDER BY service ASC")
    suspend fun getAllPasswordsList(): List<Password>

    @Query("SELECT * FROM passwords WHERE service LIKE :query")
    fun searchPasswords(query: String): Flow<List<Password>>

    @Query("SELECT * FROM passwords WHERE service LIKE :serviceName")
    suspend fun getPasswordsByService(serviceName: String): List<Password>

    @Query("SELECT * FROM passwords WHERE username LIKE :username")
    suspend fun getPasswordsByUsername(username: String): List<Password>

    @Query("SELECT * FROM passwords ORDER BY service ASC")
    fun getAllPasswords(): Flow<List<Password>>

    @Query("SELECT username, COUNT(username) as count FROM passwords WHERE username != '' GROUP BY username HAVING count >= :threshold ORDER BY count DESC")
    fun getFrequentUsernames(threshold: Int): Flow<List<UsernameCount>> // Parameterized threshold
}
