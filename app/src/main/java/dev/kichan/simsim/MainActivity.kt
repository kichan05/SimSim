package dev.kichan.simsim

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView

class MainActivity : AppCompatActivity(), SensorEventListener {
    lateinit var sensorManager : SensorManager
    var accelerometerSenser : Sensor? = null

    val textView : TextView by lazy { findViewById(R.id.txt_main_text) }
    val fox : ImageView by lazy { findViewById(R.id.img_main_fox) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initSensorManager()
        fox.apply {
            x = getRealRootViewWidth() / 2.0f
//            y = getRealRootViewHeight() / 2.0f
        }
    }

    fun initSensorManager() {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometerSenser = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        Log.d("[sensor]", "바뀜")
        if(event?.sensor != accelerometerSenser || event == null) {
            textView.text = "널"
            return
        }

        val x = Math.round(event.values[0] * 10) / 10.0F
        val y = Math.round(event.values[1] * 10) / 10.0F
        val z = Math.round(event.values[2] * 10) / 10.0F

        textView.text = "X: $x, Y: $y, Z: $z"

        fox.x = ((9.5F * 2) - getRealRootViewWidth() / (9.5F * 2) * (x - 9.5F))
        fox.y = ((9.5F * 2) - getRealRootViewHeight() / (9.5F * 2) * (y - 9.5F))
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        return Unit
    }
    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, accelerometerSenser, SensorManager.SENSOR_DELAY_UI)
    }

    override fun onPause() {
        sensorManager.unregisterListener(this)
        super.onPause()
    }

    private fun getRealRootViewWidth(): Int {
        return window.decorView.width - 100
    }

    private fun getRealRootViewHeight(): Int {
        return window.decorView.height
//        return if (Build.VERSION.SDK_INT < 30) {
//            window.decorView.height - 100 - window.decorView.rootWindowInsets.run {
//                systemWindowInsetTop + systemWindowInsetBottom
//            }
//        } else {
//            val insets = window.decorView.rootWindowInsets.displayCutout?.run {
//                safeInsetBottom + safeInsetTop
//            } ?: 0
//            window.decorView.height - 100 - insets
//        }
    }
}