package com.example.batteryalarm
import android.app.*
import android.content.*
import android.os.*
import android.media.*

class BatteryMonitorService: Service() {
 private val p by lazy { getSharedPreferences("settings",MODE_PRIVATE) }
 private val nm by lazy { getSystemService(NotificationManager::class.java) }
 private var player:MediaPlayer?=null
 private var last=""
 private var registered=false
 private fun open()=PendingIntent.getActivity(this,0,Intent(this,MainActivity::class.java),PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
 private fun dismiss()=PendingIntent.getBroadcast(this,1,Intent(this,AlarmReceiver::class.java),PendingIntent.FLAG_IMMUTABLE)
 private fun notification(channel:String,title:String,body:String)=Notification.Builder(this,channel).setSmallIcon(com.example.batteryalarm.R.drawable.battery).setContentTitle(title).setContentText(body).setContentIntent(open())
 override fun onCreate(){super.onCreate();nm.createNotificationChannel(NotificationChannel("monitor","Battery monitoring",NotificationManager.IMPORTANCE_LOW));nm.createNotificationChannel(NotificationChannel("alerts","Battery alerts",NotificationManager.IMPORTANCE_HIGH).apply{setSound(null,null)})}
 override fun onStartCommand(intent:Intent?,flags:Int,startId:Int):Int {
  if(intent?.action=="dismiss"){silence(); if(!registered)stopSelf();return START_NOT_STICKY}
  if(!p.getBoolean("active",false)){stopSelf();return START_NOT_STICKY}
  startForeground(1,notification("monitor","Battery Alarm","Monitoring battery thresholds").setOngoing(true).addAction(Notification.Action.Builder(null,"Dismiss alarm",dismiss()).build()).build())
  if(!registered){registered=true;val f=IntentFilter(Intent.ACTION_BATTERY_CHANGED);if(Build.VERSION.SDK_INT>=33)registerReceiver(receiver,f,Context.RECEIVER_EXPORTED) else registerReceiver(receiver,f)}
  return START_STICKY
 }
 private val receiver=object:BroadcastReceiver(){override fun onReceive(c:Context,i:Intent){
  val level=i.getIntExtra(BatteryManager.EXTRA_LEVEL,-1);val scale=i.getIntExtra(BatteryManager.EXTRA_SCALE,100);if(level<0||scale<=0)return
  val pct=level*100/scale;val plugged=i.getIntExtra(BatteryManager.EXTRA_PLUGGED,0)!=0
  val kind=when{plugged&&pct>=p.getInt("high",85)->"Charging limit reached";!plugged&&pct<=p.getInt("low",15)->"Low battery";else->""}
  nm.notify(1,notification("monitor","Battery Alarm","Battery: $pct% · ${if(plugged) "Plugged in" else "Unplugged"}").setOngoing(true).addAction(Notification.Action.Builder(null,"Dismiss alarm",dismiss()).build()).build())
  if(kind==last)return;silence();last=kind;if(kind.isEmpty())return
  if(p.getBoolean("notify",true))nm.notify(2,notification("alerts",kind,"Battery: $pct%").addAction(Notification.Action.Builder(null,"Dismiss",dismiss()).build()).setDeleteIntent(dismiss()).build())
  if(p.getBoolean("sound",true))try{val uri=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)?:RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);player=MediaPlayer().apply{setAudioAttributes(AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build());setDataSource(this@BatteryMonitorService,uri);isLooping=p.getBoolean("repeat",true);prepare();start()}}catch(e:Exception){player?.release();player=null}
 }}
 private fun silence(){player?.release();player=null;nm.cancel(2)}
 override fun onDestroy(){if(registered)unregisterReceiver(receiver);silence();super.onDestroy()}
 override fun onBind(i:Intent?) : IBinder?=null
}
