package com.innawaylabs.calendarplus

import com.innawaylabs.calendarplus.data.AlarmItem

interface AlarmScheduler {
    fun schedule(alarmItem: AlarmItem)
    fun cancel(alarmItem: AlarmItem)
}