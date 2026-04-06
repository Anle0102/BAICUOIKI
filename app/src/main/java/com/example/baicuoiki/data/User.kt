package com.example.baicuoiki.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val username: String,
    val passwordHash: String, // Trong thực tế nên hash mật khẩu
    val fullName: String = ""
)
