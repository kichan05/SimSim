package dev.kichan.simsim

import android.content.Context
import android.graphics.Point
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.text.isDigitsOnly
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import dev.kichan.simsim.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), SensorEventListener {
    lateinit var binding : ActivityMainBinding
    lateinit var sensorManager : SensorManager
    var accelerometerSenser : Sensor? = null

    val screenWidth : Int by lazy {
        val display = windowManager.defaultDisplay // in case of Activity
        val size = Point()
        display.getRealSize(size) // or getSize(size)
        size.x
    }
    val screenHeight : Int by lazy {
        val display = windowManager.defaultDisplay // in case of Activity
        val size = Point()
        display.getRealSize(size) // or getSize(size)
        size.y
    }

    var speedWeight : Int = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        initSensorManager()

        with(binding) {
//            txtMainState.text = "너비 : $screenWidth / 높이 : $screenHeight"
            sbMainSpeed.setOnSeekBarChangeListener(object :  SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    speedWeight = p1
                    txtMainImgSpeed.text = "속도 : $speedWeight"
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {}

                override fun onStopTrackingTouch(p0: SeekBar?) {}
            })
        }
    }

    fun initSensorManager() {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometerSenser = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if(event?.sensor != accelerometerSenser || event == null) {
            binding.txtMainText.text = "널"
            return
        }

        val acceleX = Math.round(event.values[0] * 10) / 10.0F
        val acceleY = Math.round(event.values[1] * 10) / 10.0F
        val acceleZ = Math.round(event.values[2] * 10) / 10.0F

        binding.txtMainText.text = "기울기 X: $acceleX, Y: $acceleY, Z: $acceleZ"

        with(binding.imgMainFox) {
            x -= acceleX * speedWeight
            y += acceleY * speedWeight

            if(x <= 0)
                x = 0f
            if(x >= screenWidth - width)
                x = (screenWidth - width).toFloat()

            if(y <= 0)
                y = 0f

            if(y >= screenHeight - height)
                y = (screenHeight - height).toFloat()

//            binding.txtMainImgState.text = "$x, $y"
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, accelerometerSenser, SensorManager.SENSOR_DELAY_UI)
    }

    override fun onPause() {
        sensorManager.unregisterListener(this)
        super.onPause()
    }

    private fun getRealRootViewWidth(): Int {
        return window.decorView.width
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