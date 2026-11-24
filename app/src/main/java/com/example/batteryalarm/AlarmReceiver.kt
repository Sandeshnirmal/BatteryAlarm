package com.example.batteryalarm
import android.content.*
class AlarmReceiver:BroadcastReceiver(){override fun onReceive(c:Context,i:Intent){c.startService(Intent(c,BatteryMonitorService::class.java).setAction("dismiss"))}}
