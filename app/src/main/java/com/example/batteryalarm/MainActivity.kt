package com.example.batteryalarm
import android.app.Activity
import android.os.Bundle
import android.os.Build
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.widget.*
import android.view.ViewGroup

class MainActivity : Activity() {
 override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
  val p = getSharedPreferences("settings", MODE_PRIVATE)
  val layout = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(32,64,32,32) }
  val scroll = ScrollView(this).apply { addView(layout) }; setContentView(scroll)
  fun label(s:String) = TextView(this).apply { text=s; textSize=18f; layout.addView(this) }
  label("Battery Alarm").textSize=30f
  val battery=registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
  val level=battery?.getIntExtra(BatteryManager.EXTRA_LEVEL,0)?:0
  val scale=battery?.getIntExtra(BatteryManager.EXTRA_SCALE,100)?:100
  label("Current battery: ${level*100/scale.coerceAtLeast(1)}%")
  fun threshold(title:String,key:String,default:Int,min:Int,max:Int): SeekBar {
   val t=label(""); return SeekBar(this).apply { this.max=max-min; progress=p.getInt(key,default)-min; t.text="$title: ${progress+min}%"; layout.addView(this); setOnSeekBarChangeListener(object:SeekBar.OnSeekBarChangeListener {
    override fun onProgressChanged(s:SeekBar?,v:Int,user:Boolean){t.text="$title: ${v+min}%"; if(user)p.edit().putInt(key,v+min).apply()}
    override fun onStartTrackingTouch(s:SeekBar?){}; override fun onStopTrackingTouch(s:SeekBar?){}
   }) }
  }
  threshold("Low battery alarm","low",15,5,40); threshold("Charging alarm","high",85,50,100)
  fun toggle(title:String,key:String){ layout.addView(Switch(this).apply { text=title; isChecked=p.getBoolean(key,true); setOnCheckedChangeListener { _,v -> p.edit().putBoolean(key,v).apply() } }) }
  toggle("Alarm sound","sound"); toggle("Alert notifications","notify"); toggle("Repeat until dismissed","repeat")
  val status=label(if(p.getBoolean("active",false)) "Monitoring requested" else "Monitoring stopped")
  fun button(title:String, action:()->Unit){layout.addView(Button(this).apply {text=title;setOnClickListener{action()}})}
  button("Start monitoring") { p.edit().putBoolean("active",true).apply(); startForegroundService(Intent(this,BatteryMonitorService::class.java)); status.text="Monitoring requested"; if(Build.VERSION.SDK_INT>=33 && checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)!=android.content.pm.PackageManager.PERMISSION_GRANTED) requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),1) }
  button("Dismiss alarm") { startService(Intent(this,BatteryMonitorService::class.java).setAction("dismiss")) }
  button("Stop monitoring") {p.edit().putBoolean("active",false).apply();stopService(Intent(this,BatteryMonitorService::class.java));status.text="Monitoring stopped"}
  label("Realme: allow notifications, background activity and auto-launch in system settings.")
 }
}
