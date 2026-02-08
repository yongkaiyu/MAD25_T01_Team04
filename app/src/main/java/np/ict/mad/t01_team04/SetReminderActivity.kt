
package np.ict.mad.t01_team04

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.content.pm.PackageManager
import androidx.compose.ui.platform.LocalContext
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.ui.text.TextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import java.util.*


data class Reminder(
    val id: Long,
    val title: String,
    val reminderTime: Long
)

class SetReminderActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel(this)
        requestNotificationPermission()

        setContent {
            SetReminderScreen(onBack = { finish() })
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }
    }
}

fun scheduleReminder(context: Context, reminder: Reminder) {
    val intent = Intent(context, ReminderReceiver::class.java).apply {
        putExtra("reminder_title", reminder.title)
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        reminder.id.toInt(),
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    reminder.reminderTime,
                    pendingIntent
                )
            } else {
                val intentSettings = Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                intentSettings.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intentSettings)

                alarmManager.set(AlarmManager.RTC_WAKEUP, reminder.reminderTime, pendingIntent)
            }
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, reminder.reminderTime, pendingIntent)
        }
    } catch (e: SecurityException) {
        alarmManager.set(AlarmManager.RTC_WAKEUP, reminder.reminderTime, pendingIntent)
    }
}

fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            "reminder_channel",
            "Reminder Notifications",
            NotificationManager.IMPORTANCE_HIGH
        ).apply { description = "Notifications for reminders" }

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }
}

@Composable
fun SetReminderScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var reminderTitle by remember { mutableStateOf("") }
    val reminders = remember { mutableStateListOf<Reminder>() }
    var reminderTime by remember { mutableStateOf(System.currentTimeMillis() + 10_000L) } // default 10s demo

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(20.dp)
    ) {

        Text(
            text = "Set Watch Movie Reminder",
            fontSize = 24.sp,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = reminderTitle,
            onValueChange = { reminderTitle = it },
            label = { Text("Movie Title", color = Color.LightGray) },
            textStyle = TextStyle(color = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.DarkGray.copy(alpha = 0.2f))
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                val calendar = Calendar.getInstance()
                val datePicker = DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        calendar.set(year, month, dayOfMonth)
                        val timePicker = TimePickerDialog(
                            context,
                            { _, hour, minute ->
                                calendar.set(Calendar.HOUR_OF_DAY, hour)
                                calendar.set(Calendar.MINUTE, minute)
                                calendar.set(Calendar.SECOND, 0)
                                calendar.set(Calendar.MILLISECOND, 0)
                                reminderTime = calendar.timeInMillis
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true
                        )
                        timePicker.show()
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
                datePicker.show()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Set Reminder Date & Time (Optional)")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                if (reminderTitle.isBlank()) {
                    Toast.makeText(context, "Please enter a title", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                val now = System.currentTimeMillis()
                if (reminderTime <= now) reminderTime = now + 10_000L

                val reminder = Reminder(
                    id = (reminderTime + System.currentTimeMillis() % 1000).toInt().toLong(),
                    title = reminderTitle,
                    reminderTime = reminderTime
                )
                reminders.add(reminder)
                scheduleReminder(context, reminder)
                reminderTitle = ""
                reminderTime = System.currentTimeMillis() + 10_000L
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Reminder")
        }

        Spacer(modifier = Modifier.height(24.dp))

        reminders.forEach { item ->
            Text(
                text = "${item.title} at ${Date(item.reminderTime)}",
                color = Color.White,
                fontSize = 16.sp,
                modifier = Modifier.padding(vertical = 6.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back")
        }
    }
}




