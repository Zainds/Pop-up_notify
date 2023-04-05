package com.example.pop_up_notify


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.text.Layout
import android.widget.Button
import android.widget.RemoteViews
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import org.w3c.dom.Text
import java.lang.reflect.Modifier
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder: Notification.Builder
    private val channelId = "i.apps.notifications"
    private val description = "Test notification"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            Thread.sleep(1000)
        }

        setContentView(R.layout.activity_main)



        val audioManager: AudioManager =
            getSystemService(AUDIO_SERVICE) as AudioManager

        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        var mybutt: Button = findViewById(R.id.mybutton)
        mybutt.setOnClickListener {
            println("Butt is worikun")
            Thread.sleep(3_000)
            println("Time ended")
            // pendingIntent is an intent for future use i.e after
            // the notification is clicked, this intent will come into action
            val intent = Intent(this, afterNotification::class.java)

            // FLAG_UPDATE_CURRENT specifies that if a previous
            // PendingIntent already exists, then the current one
            // will update it with the latest intent
            // 0 is the request code, using it later with the
            // same method again will get back the same pending
            // intent for future reference
            // intent passed here is to our afterNotification class
            val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

            // RemoteViews are used to use the content of
            // some different layout apart from the current activity layout
            val contentView = RemoteViews(packageName, R.layout.activity_after_notification)

            // checking if android version is greater than oreo(API 26) or not
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationChannel = NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
                notificationChannel.enableLights(true)
                notificationChannel.lightColor = Color.GREEN
                notificationChannel.enableVibration(false)
                notificationManager.createNotificationChannel(notificationChannel)

                builder = Notification.Builder(this, channelId)
                    .setContent(contentView)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.ic_launcher_background))
                    .setContentIntent(pendingIntent)
                    .setSubText("Ну ка нахуй")
            } else {

                builder = Notification.Builder(this)
                    .setContent(contentView)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.ic_launcher_background))
                    .setContentIntent(pendingIntent)
            }
            val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            if (!(hour in 1..6)){
                if(User.pushNotify) notificationManager.notify(1234, builder.build())
                if(audioManager.mediaCurrentVolume >=4 && User.volumeDecrease){



                    toast(hour.toString())
                    val previousVolume: Int = audioManager.mediaCurrentVolume
                    audioManager.setMediaVolume(3)
                    Thread.sleep(3000)
                    audioManager.setMediaVolume(previousVolume)
                }
            }


        }

    }
    // Extension function to change media volume programmatically
    fun AudioManager.setMediaVolume(volumeIndex:Int) {
        // Set media volume level
        this.setStreamVolume(
            AudioManager.STREAM_MUSIC, // Stream type
            volumeIndex, // Volume index
            AudioManager.FLAG_SHOW_UI// Flags
        )
    }

    // Extension property to get media maximum volume index
    val AudioManager.mediaMaxVolume:Int
        get() = this.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

    // Extension property to get media/music current volume index
    val AudioManager.mediaCurrentVolume:Int
        get() = this.getStreamVolume(AudioManager.STREAM_MUSIC)

    // Extension function to show toast message
    fun Context.toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }


}