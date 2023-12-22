package com.innawaylabs.calendarplus

import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.AlarmClock
import android.provider.CalendarContract
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.innawaylabs.calendarplus.data.CalendarEvent
import com.innawaylabs.calendarplus.ui.theme.CalendarPlusTheme
import java.util.Calendar


class MainActivity : ComponentActivity() {


    companion object {
        private const val PERMISSIONS_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalendarPlusTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    var message by remember {
                        mutableStateOf("")
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = message)
                    }
                }
            }
        }

        checkAndRequestPermissions()
    }

    private fun checkAndRequestPermissions() {
        val readCalendarPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CALENDAR)

        if (readCalendarPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_CALENDAR),
                PERMISSIONS_REQUEST_CODE
            )
        } else {
            // Permission already granted, proceed with accessing calendar data
            accessCalendarData()
        }
        val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
            putExtra(AlarmClock.EXTRA_MESSAGE, "Test Alarm")
            putExtra(AlarmClock.EXTRA_HOUR, 10)
            putExtra(AlarmClock.EXTRA_MINUTES, 30)
        }

        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(this, "Unable to invoke alarm app to set alarm(s)", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission granted, proceed with accessing calendar data
                    accessCalendarData()
                } else {
                    // Permission denied, handle the feature limitation
                }
                return
            }
            else -> {
                // Ignore all other requests.
            }
        }
    }

    private fun accessCalendarData() {
        val eventsList = fetchCalendarEvents()

        // Logic to access calendar data
        Log.d("Ravi: ", "Fetched ${eventsList.size} calendar events.")
        setSystemAlarms(eventsList)
    }

    private fun fetchCalendarEvents(): List<CalendarEvent> {
        val eventsList = mutableListOf<CalendarEvent>()

        val projection = arrayOf(
            CalendarContract.Events._ID,
            CalendarContract.Events.TITLE,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.DTEND
        )

        val uri: Uri = CalendarContract.Events.CONTENT_URI
        val selection = "((${CalendarContract.Events.DTSTART} >= ?) AND (${CalendarContract.Events.DTEND} <= ?))"
        val selectionArgs: Array<String> = arrayOf(
            // Start time in milliseconds
            System.currentTimeMillis().toString(),
            // End time in milliseconds, for example, a day ahead
            (System.currentTimeMillis() + 86400000).toString()
        )

        val cursor: Cursor? = contentResolver.query(uri, projection, selection, selectionArgs, null)

        cursor?.use {
            while (it.moveToNext()) {
                val eventId = it.getLong(0)
                val title = it.getString(1)
                val startTime = it.getLong(2)
                val endTime = it.getLong(3)

                eventsList.add(CalendarEvent(eventId, title, startTime, endTime))
            }
        }

        return eventsList
    }

    private fun setSystemAlarms(events: List<CalendarEvent>) {
        events.forEach { event ->
            val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
                putExtra(AlarmClock.EXTRA_MESSAGE, "CalendarPlus: ${event.title}")
                putExtra(AlarmClock.EXTRA_HOUR, getHourFromMillis(event.startTime))
                putExtra(AlarmClock.EXTRA_MINUTES, getMinuteFromMillis(event.startTime))
            }

            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            }
        }
    }

    private fun getHourFromMillis(millis: Long): Int {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = millis
        }
        return calendar.get(Calendar.HOUR_OF_DAY)
    }

    private fun getMinuteFromMillis(millis: Long): Int {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = millis
        }
        return calendar.get(Calendar.MINUTE)
    }

}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Column (
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Welcome to $name!",
            modifier = modifier
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CalendarPlusTheme {
        Greeting("Android")
    }
}