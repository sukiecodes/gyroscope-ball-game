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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cs501gyroscopeballgame.MainActivity.Rect
import com.example.cs501gyroscopeballgame.ui.theme.CS501GyroscopeBallGameTheme
import kotlin.math.max
import kotlin.math.min

class MainActivity : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var gyroscope: Sensor? = null

    // gyroscope values xy for side to side tilt
    private var _x by mutableStateOf(0f)
    private var _y by mutableStateOf(0f)

    // ball position and speed
    private var ballX by mutableStateOf(300f)
    private var ballY by mutableStateOf(300f)
    private val ballRadius = 20f
    private val speed = 3f

    data class Rect(val left: Float, val top: Float, val width: Float, val height: Float)

    // wall obstacles
    private val walls = listOf(
        Rect(50f, 50f, 200f, 50f),
        Rect(200f, 150f, 50f, 300f),
        Rect(400f, 200f, 50f, 100f),
        Rect(150f, 300f, 150f, 50f),
        Rect(350f, 400f, 50f, 200f),
        Rect(50f, 60f, 200f, 50f),
        Rect(100f, 70f, 50f, 150f),
        Rect(250f, 500f, 100f, 50f),
        Rect(700f, 200f, 50f, 100f),
        Rect(900f, 800f, 50f, 100f),
        Rect(680f, 450f, 100f, 300f),
        Rect(800f, 800f, 100f, 300f),
        Rect(1000f, 1000f, 25f, 100f)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        setContent {
            CS501GyroscopeBallGameTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GyroscopeGame(ballX = ballX, ballY = ballY, walls = walls)
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

                ballX += _x * speed
                ballY += _y * speed

                // boundary check
                ballX = max(ballRadius, min(ballX, 600f - ballRadius))
                ballY = max(ballRadius, min(ballY, 400f - ballRadius))
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not needed for this example
    }
}

@Composable
fun GyroscopeGame(ballX: Float, ballY: Float, walls: List<Rect>) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // drawing the walls and the ball for game
        Canvas(modifier = Modifier.fillMaxSize()) {
            walls.forEach {
                drawRect(
                    color = Color.Gray,
                    topLeft = Offset(x = it.left, y = it.top),
                    size = Size(height = it.height, width = it.width)
                )
            }
            drawCircle(
                color = Color.Blue,
                radius = 20f,
                center = Offset(ballX, ballY) // change the position of the ball on screen coordinates
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GyroscopeGamePreview() {
    CS501GyroscopeBallGameTheme {
        GyroscopeGame(ballX = 300f, ballY = 300f, listOf(
            Rect(50f, 50f, 200f, 50f),
            Rect(200f, 150f, 50f, 300f),
            Rect(400f, 200f, 50f, 100f),
            Rect(150f, 300f, 150f, 50f),
            Rect(350f, 400f, 50f, 200f),
            Rect(50f, 60f, 200f, 50f),
            Rect(100f, 70f, 50f, 150f),
            Rect(250f, 50f, 100f, 50f),
            Rect(100f, 200f, 50f, 100f)
        ))
    }
}
