package com.innawaylabs.calendarplus

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        val message = intent.getStringExtra("EXTRA_MESSAGE") ?: return

        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        // Here, you can start an activity, service, or perform any actions as needed
    }
}
