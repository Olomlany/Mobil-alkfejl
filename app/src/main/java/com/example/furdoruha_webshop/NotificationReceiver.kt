package com.example.furdoruha_webshop

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val permission = android.Manifest.permission.POST_NOTIFICATIONS
                if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    return
                }
            }

            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Időzített értesítés")
                .setContentText("Ez egy 1 perccel késleltetett értesítés.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            NotificationManagerCompat.from(context).notify(ALARM_NOTIFICATION_ID, builder.build())
        } catch (e: SecurityException) {
        } catch (e: Exception) {
        }
    }
}
