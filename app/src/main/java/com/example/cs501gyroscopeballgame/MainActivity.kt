package com.example.cs501gyroscopeballgame

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cs501gyroscopeballgame.ui.theme.CS501GyroscopeBallGameTheme
import kotlin.math.atan2

class MainActivity : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var gyroscope: Sensor? = null
    private var accelerometer: Sensor? = null

    // gyroscope values xyz
    private var _x by mutableStateOf(0f)
    private var _y by mutableStateOf(0f)
    private var _z by mutableStateOf(0f)

    // accelerometer data ab (only dealing with x axis and y axis)
    private var _a by mutableStateOf(0f)
    private var _b by mutableStateOf(0f)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        setContent {
            CS501GyroscopeBallGameTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GyroscopeGame(x = _x, y = _y, z = _z)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        gyroscope?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            // Adjust rotation values based on gyroscope data
            if (it.sensor.type == Sensor.TYPE_GYROSCOPE) {
                _x = it.values[0]
                _y = it.values[1]
                _z = it.values[2]
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not needed for this example
    }
}

@Composable
fun GyroscopeGame(x: Float, y: Float, z: Float) {
    // pitch, roll, yaw values needed to know orientation angles calculated using math!
    val pitch = atan2(z, -y) * 180 / Math.PI
    val roll = atan2(x, -y) * 180 / Math.PI
    val yaw = atan2(-x, -z) * 180 / Math.PI

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Canvas(
            // circle that rotates 3D based on the pitch, roll, yaw values that were calculated using gyroscope
            modifier = Modifier.graphicsLayer {
                this.transformOrigin = TransformOrigin(0f, 0f)
                this.rotationX = pitch.toFloat()
                this.rotationY = roll.toFloat()
                this.rotationZ = yaw.toFloat()
            }
        ) {
            drawCircle(color = Color.Blue, radius = 200f)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CS501GyroscopeBallGameTheme {
        GyroscopeGame(x = 0f, y = 0f, z = 0f)
    }
}