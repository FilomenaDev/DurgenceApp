package com.filomenadeveloper.durgente_app

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.filomenadeveloper.durgente_app.Model.CustomerInfo
import java.lang.StringBuilder

object Common {
    fun biuldwelcameMessage(): String{
        return  StringBuilder("")
            .append(correntUser!!.firstName)
            .append(" ")
            .append(correntUser!!.lastName).toString()
    }

    fun showNotification(context: Context, id: Int, title: String?, body: String?, intent: Intent?) {
        var padingIntent : PendingIntent? = null
        if (intent != null){
            padingIntent = PendingIntent.getActivity(context,id,intent!!,PendingIntent.FLAG_UPDATE_CURRENT)
            val NOTIFICATION_CHANNEL_ID = "Filomena dev"
            val notificatinManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                val notification = NotificationChannel(NOTIFICATION_CHANNEL_ID,"Durgence",
                NotificationManager.IMPORTANCE_HIGH)
                notification.description = "Chamada de Ajuda"
                notification.enableLights(true)
                notification.lightColor = Color.RED
                notification.vibrationPattern = longArrayOf(0,1000,500,1000)
                notification.enableVibration(true)

                notificatinManager.createNotificationChannel(notification)
            }

            val builder = NotificationCompat.Builder(context,NOTIFICATION_CHANNEL_ID)
                builder.setContentText(title)
                    .setAutoCancel(false)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setDefaults(Notification.DEFAULT_VIBRATE)
                    .setSmallIcon(R.drawable.ic_baseline_local_car_wash)
                    .setLargeIcon(BitmapFactory.decodeResource(context.resources,R.drawable.ic_baseline_local_car_wash))
            if (padingIntent != null){
                builder.setContentIntent(padingIntent!!)
                val notifica = builder.build()
                notificatinManager.notify(id,notifica)
            }
        }
    }

    val NOTI_BODY: String = "body"
    val NOTI_TITLE: String ="title"
    val TOKEN_REFERENCE: String = "Token"
    val CUSTOMER_LOCATION_REF: String = "CustomerLocation"
    var correntUser : CustomerInfo?=null
    val CUSTOMER_INFO_REFERENCE: String = "RequesterInfo"
    val USUARIO: String = "Users"
}