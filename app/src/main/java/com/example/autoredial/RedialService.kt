package com.example.autoredial

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.telecom.TelecomManager

class RedialService : Service() {

    @Volatile private var running = false
    private var worker: Thread? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == "STOP") {
            stopAll()
            return START_NOT_STICKY
        }
        if (running) return START_NOT_STICKY

        val number = intent?.getStringExtra("number") ?: return START_NOT_STICKY
        val interval = intent.getIntExtra("interval", 18)
        val max = intent.getIntExtra("max", 20)

        startForeground(1, buildNotification("準備撥打…"))
        running = true

        worker = Thread {
            var i = 1
            while (running && i <= max) {
                updateNotification("第 $i / $max 次撥打中:$number")
                placeCall(number)
                sleepWhileRunning(interval * 1000L)
                if (!running) break
                endCall()
                sleepWhileRunning(3000L)
                i++
            }
            running = false
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }.also { it.start() }

        return START_NOT_STICKY
    }

    @SuppressLint("MissingPermission")
    private fun placeCall(number: String) {
        val tm = getSystemService(Context.TELECOM_SERVICE) as TelecomManager
        tm.placeCall(Uri.fromParts("tel", number, null), Bundle())
    }

    @SuppressLint("MissingPermission")
    private fun endCall() {
        val tm = getSystemService(Context.TELECOM_SERVICE) as TelecomManager
        @Suppress("DEPRECATION")
        tm.endCall()
    }

    private fun sleepWhileRunning(ms: Long) {
        var waited = 0L
        while (running && waited < ms) {
            Thread.sleep(200)
            waited += 200
        }
    }

    private fun stopAll() {
        running = false
        endCall()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun buildNotification(text: String): Notification {
        val channelId = "redial"
        val nm = getSystemService(NotificationManager::class.java)
        nm.createNotificationChannel(
            NotificationChannel(channelId, "自動重撥", NotificationManager.IMPORTANCE_LOW)
        )
        val stopIntent = PendingIntent.getService(
            this, 0,
            Intent(this, RedialService::class.java).setAction("STOP"),
            PendingIntent.FLAG_IMMUTABLE
        )
        return Notification.Builder(this, channelId)
            .setContentTitle("自動重撥")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.sym_action_call)
            .addAction(Notification.Action.Builder(null, "停止", stopIntent).build())
            .setOngoing(true)
            .build()
    }

    private fun updateNotification(text: String) {
        getSystemService(NotificationManager::class.java).notify(1, buildNotification(text))
    }

    override fun onDestroy() {
        running = false
        super.onDestroy()
    }
}
