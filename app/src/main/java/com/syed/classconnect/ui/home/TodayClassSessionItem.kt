package com.syed.classconnect.ui.home

import com.syed.classconnect.data.model.ClassRoom

data class TodayClassSessionItem(
    val classRoom: ClassRoom,
    val slot: String,
    val startMinutes: Int
)

