package com.rtccaller.utils

import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat

class NotificationFactory {
    companion object {

        fun getStandardNotification(
            context: Context,
            notificationTitle: String,
            notificationMessage: String,
            pendingIntent: PendingIntent,
            channelId: String,
            iconRes: Int
        ) =
            NotificationCompat.Builder(context, channelId)
                .setAutoCancel(true)
                .setContentTitle(notificationTitle)
                .setContentText(notificationMessage)
                .setSmallIcon(iconRes)
                .setContentIntent(pendingIntent)
                .build()
    }
}