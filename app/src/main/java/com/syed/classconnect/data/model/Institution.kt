package com.syed.classconnect.data.model

import com.google.firebase.Timestamp

data class Institution(
    val id: String = "",
    val name: String = "",
    val adminId: String = "",
    val joinCode: String = "",
    val createdAt: Timestamp = Timestamp.now()
)

