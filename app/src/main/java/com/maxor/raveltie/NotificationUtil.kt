package com.maxor.raveltie

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.maxor.raveltie.location.LocationService
import com.maxor.raveltie.score.ScoreActivity

object NotificationUtil {

    fun setupForegroundNotification(service: Service) {
        val channelId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(
                "raveltie_service",
                "Raveltie Service",
                service)
        } else {""}
        val pendingIntent: PendingIntent =  Intent(service, ScoreActivity::class.java).let {
                notificationIntent ->
            PendingIntent.getActivity(service, 0, notificationIntent, 0)
        }
        val notification: Notification = NotificationCompat.Builder(service,channelId)
            .setContentTitle("Raveltie Active")
            .setContentText("Collecting raveltie reputation score. Tap for more info.")
            .setSmallIcon(R.drawable.notification_icon_background)
            .setContentIntent(pendingIntent)
            .setTicker("")
            .build()
        service.startForeground(LocationService.ONGOING_NOTIFICATION_ID, notification)
    }

    fun stopForegroundNOtificatioin(service: Service) {
        service.stopForeground(true)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String,service: Service): String {
        val chan = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_NONE)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = service.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }
}