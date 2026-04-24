package com.curve.delivery.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.curve.delivery.R
import com.curve.delivery.ui.home.HomeScreen
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        SharedPreference.get(this).deviceToken = token
    }

    override fun onMessageReceived(message: RemoteMessage) {
        // Broadcast data to Activity
        val rideMode = message.data["status"]
        val intent = Intent("com.example.UPDATE_ACTIVITY").apply {
            putExtra("RIDE_MODE", rideMode)
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)

        // Handle notification payload
        val title = message.notification?.title ?: "Notification"
        val body = message.notification?.body ?: ""

        sendNotification(title, body)
    }
    private fun sendNotification(title: String, body: String) {

        // Create an empty intent or launch your activity
        val clickIntent = Intent(this, HomeScreen::class.java)

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            clickIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val channelId = "Driver Notification"
        val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setSound(defaultSound)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create channel for Android O+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                getString(R.string.app_name),
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.enableLights(true)
            channel.lightColor = Color.RED
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(100, 200, 300)
            manager.createNotificationChannel(channel)
        }

        manager.notify(1, builder.build())
    }
}
