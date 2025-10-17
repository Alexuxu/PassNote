package com.ax.passnote.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "passwords")
data class Password(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val service: String,
    val username: String,
    val password: String,
    val notes: String? = null
)
