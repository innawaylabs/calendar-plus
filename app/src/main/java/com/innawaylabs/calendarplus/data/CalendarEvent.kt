package com.innawaylabs.calendarplus.data

data class CalendarEvent(
    val id: Long,
    val title: String,
    val startTime: Long,
    val endTime: Long
)