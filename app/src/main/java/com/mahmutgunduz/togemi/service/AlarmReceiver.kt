package com.mahmutgunduz.togemi.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.mahmutgunduz.togemi.R
import com.mahmutgunduz.togemi.view.MainActivity

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        createNotification(context, intent)
    }

    private fun createNotification(context: Context, intent: Intent) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Bildirime tıklandığında açılacak aktivite
        val contentIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Bildirim başlığı ve mesajı
        val title = intent.getStringExtra("title") ?: "Hatırlatıcı"
        val message = intent.getStringExtra("message") ?: "Notunuz için hatırlatma zamanı!"

        // Bildirim kanalı oluştur (Android 8.0 ve üzeri için gerekli)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Hatırlatıcılar",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Not hatırlatıcıları için bildirim kanalı"
                enableLights(true)
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Bildirimi oluştur
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_alarm)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        // Bildirimi göster
        notificationManager.notify(NOTIFICATION_ID, builder.build())

        // Toast mesajı göster
        Toast.makeText(context, title, Toast.LENGTH_LONG).show()
    }

    companion object {
        private const val CHANNEL_ID = "reminder_channel"
        private const val NOTIFICATION_ID = 1
    }
}


