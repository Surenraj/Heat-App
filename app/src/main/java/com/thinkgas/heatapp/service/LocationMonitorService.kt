package com.thinkgas.heatapp.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.thinkgas.heatapp.MainActivity
import com.thinkgas.heatapp.R
import com.thinkgas.heatapp.supportActivities.EnableLocationActivity
import com.thinkgas.heatapp.supportActivities.NoInternetActivity

class LocationMonitorService: Service() {

    companion object {
        private const val CHANNEL_ID = "1002"
        private const val ONGOING_NOTIFICATION_ID = 102

        fun startService(context: Context) {
            val intent = Intent(context, LocationMonitorService::class.java)
            if (Build.VERSION.SDK_INT < 26) {
                context.startService(intent)
            } else {
                context.startForegroundService(intent)
            }
        }

        fun stopService(context: Context) {
            val intent = Intent(context, LocationMonitorService::class.java)
            context.stopService(intent)
        }
    }

    private var isLocationEnabled: Boolean = false
    private lateinit var notificationManager: NotificationManager

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createServiceNotificationChannel()
    }

    override fun onBind(intent: Intent?): IBinder? {
        throw UnsupportedOperationException()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        makeForeground()
        Thread { monitorLocation(this) }.start()
        return START_STICKY
    }
    private fun createServiceNotificationChannel() {
        if (Build.VERSION.SDK_INT < 26) {
            return
        }

        val channel = NotificationChannel(
            CHANNEL_ID,
            "Foreground Service channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        notificationManager.createNotificationChannel(channel)
    }

    private fun makeForeground() {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification: Notification = NotificationCompat.Builder(this,
            CHANNEL_ID
        )
            .setContentTitle("Heat App")
            .setContentText("Service is Active")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(ONGOING_NOTIFICATION_ID, notification)
    }

    private fun monitorLocation(context: Context) {
        while (true) {
            isLocationEnabled = isLocationEnabled()

            if (!isLocationEnabled) {
                Log.d(
                    "LocationMonitor",
                    "Location Enabled: ${isLocationEnabled()}"
                )
                val intent = Intent(context, EnableLocationActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                break
            }
            Thread.sleep(1000)
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }
}