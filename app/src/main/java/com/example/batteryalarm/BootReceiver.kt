package com.example.batteryalarm
import android.content.*
class BootReceiver:BroadcastReceiver(){override fun onReceive(c:Context,i:Intent){if(i.action==Intent.ACTION_BOOT_COMPLETED && c.getSharedPreferences("settings",Context.MODE_PRIVATE).getBoolean("active",false))try{c.startForegroundService(Intent(c,BatteryMonitorService::class.java))}catch(e:IllegalStateException){}}}
