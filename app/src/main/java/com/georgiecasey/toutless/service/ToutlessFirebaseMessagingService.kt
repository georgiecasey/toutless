package com.georgiecasey.toutless.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.georgiecasey.toutless.MainActivity
import com.georgiecasey.toutless.R
import com.georgiecasey.toutless.service.workers.PostFcmTokenWorker
import com.georgiecasey.toutless.utils.extension.fcmToken
import com.georgiecasey.toutless.utils.extension.prefs
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import timber.log.Timber

class ToutlessFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage?.let { fcmMessage ->
            fcmMessage.messageId?.let { messageId ->
                val fcmMessageId = remoteMessage.data.get("toutlessThreadId").toString()
                val msgTitle = remoteMessage.data.get("msgTitle").toString()
                val msgBody = remoteMessage.data.get("msgBody").toString()
                sendNotification(fcmMessageId, msgTitle, msgBody)
            }
        }
    }

    override fun onNewToken(token: String) {
        Timber.d("Refreshed token: $token")
        val currentToken = application.prefs.fcmToken ?: "NONE"
        val postWorkRequest = OneTimeWorkRequestBuilder<PostFcmTokenWorker>()
            .setInputData(workDataOf(PostFcmTokenWorker.ARG_FCM_TOKEN to token, PostFcmTokenWorker.ARG_CURRENT_FCM_TOKEN to currentToken))
            .build()
        WorkManager.getInstance(applicationContext).enqueue(postWorkRequest)
    }

    private fun sendNotification(toutlessThreadId: String, messageTitle: String, messageBody: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT)

        val channelId = "fcm_default_channel"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(messageTitle)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId,
                "Toutless ticket notifications",
                NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }
}