package com.newlifepartner.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.newlifepartner.MainActivity
import com.newlifepartner.R
import com.newlifepartner.activity.ChatActivity
import com.newlifepartner.notification.NotificationUtils.isAppIsInBackground
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val TAG = "MyFirebaseMsgService"
    private val str_type = ""
    private lateinit var notificationUtils:NotificationUtils

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages
        // are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data
        // messages are the type
        // traditionally used with GCM. Notification messages are only received here in
        // onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated
        // notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages
        // containing both notification
        // and data payloads are treated as notification messages. The Firebase console always
        // sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]
        if (remoteMessage == null) {
            return
        }
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.e("TAG", "From: " + remoteMessage.getFrom())

        // Check if message contains a data payload.

        // Check if message contains a data payload.
            if (remoteMessage.getNotification() != null) {
                val notification: RemoteMessage.Notification = remoteMessage.getNotification()!!
                try {
                    val params: Map<String, String> = remoteMessage.getData()
                    val intent: Intent = remoteMessage.toIntent()
                    val bundle = intent.extras
                    val value = bundle!!.getString("google.c.a.c_l")!!
                    Log.e("Notification ", value)
                } catch (e: Exception) {
                    Log.e("Notification:", e.toString())
                }
                val getImageUrl = notification.imageUrl
                sendNotification(notification.title!!, notification.body!!, getImageUrl)
                val intent = Intent("Message_received_chat")
                sendBroadcast(intent)
            }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    private fun handleDataMessage(json: JSONObject) {
        Log.e("TAG", "push json: $json")
        try {
            val data = json.getJSONObject("data")
            val title = data.getString("title")
            val message = data.getString("message")
            val isBackground = data.getBoolean("is_background")
            val imageUrl = data.getString("image")
            val timestamp = data.getString("timestamp")
            val payload = data.getJSONObject("payload")
            Log.e("TAG", "title: $title")
            Log.e("TAG", "message: $message")
            Log.e("TAG", "isBackground: $isBackground")
            Log.e("TAG", "payload: $payload")
            Log.e("TAG", "imageUrl: $imageUrl")
            Log.e("TAG", "timestamp: $timestamp")
            if (isAppIsInBackground(applicationContext)) {
                // app is in foreground, broadcast the push message
                val pushNotification = Intent("pushNotification")
                pushNotification.putExtra("message", message)
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification)

                // play notification sound
                //   NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                //  notificationUtils.playNotificationSound();
            } else {
                // app is in background, show the notification in notification tray
                val resultIntent = Intent(applicationContext, MainActivity::class.java)
                resultIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                resultIntent.putExtra("message", message)

                // check for image attachment
                if (TextUtils.isEmpty(imageUrl)) {
                    showNotificationMessage(
                        applicationContext,
                        title,
                        message,
                        timestamp,
                        resultIntent
                    )
                } else {
                    // image is present, show notification with image
                    showNotificationMessageWithBigImage(
                        applicationContext,
                        title,
                        message,
                        timestamp,
                        resultIntent,
                        imageUrl
                    )
                }
            }
        } catch (e: JSONException) {
            Log.e("TAG", "Json Exception: " + e.message)
        } catch (e: java.lang.Exception) {
            Log.e("TAG", "Exception: " + e.message)
        }
    }

    /**
     * Showing notification with text only
     */
    private fun showNotificationMessage(
        context: Context,
        title: String,
        message: String,
        timeStamp: String,
        intent: Intent
    ) {
        notificationUtils = NotificationUtils(context)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent)
    }

    /**
     * Showing notification with text and image
     */
    private fun showNotificationMessageWithBigImage(
        context: Context,
        title: String,
        message: String,
        timeStamp: String,
        intent: Intent,
        imageUrl: String
    ) {
        notificationUtils = NotificationUtils(context)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl)
    }

    private fun sendNotification(messageTitle: String, messageBody: String, getImageUrl: Uri?) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val bundle_data = Bundle()
        bundle_data.putString("type", str_type)
        val intent = Intent(this, MainActivity::class.java)
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtras(bundle_data)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0 /* Request code */,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val channelId = getString(R.string.default_notification_channel_id)
        val str_app_name = getString(R.string.app_name)
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                str_app_name,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }
        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.mipmap.ic_launcher))
        try {
            if (getImageUrl != null) {
                val bitmap = getBitmapFromURL(getImageUrl.toString())
                if (bitmap != null) {
                    notificationBuilder.setStyle(
                        NotificationCompat.BigPictureStyle().bigPicture(bitmap)
                    )
                }
            }
        } catch (e: java.lang.Exception) {
        }
        notificationManager.notify(1234 /* ID of notification */, notificationBuilder.build())
    }

    fun getBitmapFromURL(strURL: String?): Bitmap? {
        try {
            val url = URL(strURL)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            return BitmapFactory.decodeStream(input)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
    }


}