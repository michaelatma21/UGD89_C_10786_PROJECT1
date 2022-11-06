package com.example.ugd89_c_10786_project1

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.Toast
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private var mCamera: Camera? = null
    private var mCameraView: CameraView? = null

    lateinit var proximitySensor: Sensor
    lateinit var sensorManager: SensorManager

    private var currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)

        try {
            mCamera = Camera.open(currentCameraId)
        }catch (e: IOException){
            Log.d("Error", "Failed to get Camera" + e.message)
        }

        if (mCamera != null){
            mCameraView = CameraView(this@MainActivity, mCamera!!)
            val camera_view = findViewById<View>(R.id.FLCamera) as FrameLayout
            camera_view.addView(mCameraView)
        }
        @SuppressLint("MissingInflatedId", "LocalSuppress")
        val imageClose = findViewById<View>(R.id.imgClose) as ImageButton
        imageClose.setOnClickListener{view: View? -> System.exit(0)}


        if (proximitySensor == null) {

            Toast.makeText(this, "No proximity sensor found in device..", Toast.LENGTH_SHORT).show()
            finish()
        } else {

            sensorManager.registerListener(
                proximitySensorEventListener,
                proximitySensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    var proximitySensorEventListener: SensorEventListener? = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

        }

        override fun onSensorChanged(event: SensorEvent) {

            if (event.sensor.type == Sensor.TYPE_PROXIMITY) {
                if (event.values[0] == 0f) {

                    if (currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK ) {
                        mCamera?.stopPreview();
                    }
                    mCamera?.release();


                    if (currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
                        currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
                    } else {
                        currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
                    }
                    try {
                        mCamera = Camera.open(currentCameraId)
                    } catch (e: IOException) {
                        Log.d("Error", "Failed to get Camera" + e.message)
                    }

                    if (mCamera != null) {
                        mCameraView = CameraView(this@MainActivity, mCamera!!)
                        val camera_view = findViewById<View>(R.id.FLCamera) as FrameLayout
                        camera_view.addView(mCameraView)
                    }
                    @SuppressLint("MissingInflatedId", "LocalSuppress")
                    val imageClose = findViewById<View>(R.id.imgClose) as ImageButton
                    imageClose.setOnClickListener { view: View? -> System.exit(0) }
                }
            }
        }
    }
}