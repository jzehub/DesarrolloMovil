package com.example.sensors

import android.Manifest
import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.graphics.YuvImage
import android.graphics.Rect
import android.graphics.BitmapFactory
import androidx.camera.core.ImageProxy
import java.io.ByteArrayOutputStream
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.sensors.ui.theme.SensorsTheme
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import androidx.core.graphics.get

fun imageProxyToBitmap(image: ImageProxy): Bitmap? {
    val yBuffer = image.planes[0].buffer
    val uBuffer = image.planes[1].buffer
    val vBuffer = image.planes[2].buffer

    val ySize = yBuffer.remaining()
    val uSize = uBuffer.remaining()
    val vSize = vBuffer.remaining()

    val nv21 = ByteArray(ySize + uSize + vSize)

    yBuffer.get(nv21, 0, ySize)
    vBuffer.get(nv21, ySize, vSize)
    uBuffer.get(nv21, ySize + vSize, uSize)

    val yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)
    val out = ByteArrayOutputStream()
    yuvImage.compressToJpeg(Rect(0, 0, image.width, image.height), 100, out)
    val imageBytes = out.toByteArray()

    return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
}

class MainActivity : ComponentActivity(), SensorEventListener {

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var proximitySensor: Sensor
    private lateinit var sensorManager: SensorManager
    val isNearState = mutableStateOf(false)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraExecutor = Executors.newSingleThreadExecutor()

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)!!

        setContent {
            SensorsTheme {
                val detectedColor = remember { mutableStateOf(Color.Black) }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    // Camera view
                    CameraPreview(isNear = isNearState) { color ->
                        detectedColor.value = color
                    }

                    // Proximity text
                    if (isNearState.value) {
                        Text(
                            text = "¡Objeto detectado cerca!",
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 24.dp)
                                .background(Color.Black)
                                .padding(8.dp),
                            color = Color.White,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    // Small box showing the detected color
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                            .background(detectedColor.value)
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_PROXIMITY) {
            isNearState.value = event.values[0] < proximitySensor.maximumRange
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
}

@Composable
fun CameraPreview(isNear: State<Boolean>, onColorDetected: (Color) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
    }

    LaunchedEffect(Unit) {
        if (!hasPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    if (hasPermission) {
        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    val previewView = PreviewView(ctx)

                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()

                        val preview = Preview.Builder().build().apply {
                            surfaceProvider = previewView.surfaceProvider
                        }

                        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                        val imageAnalyzer = ImageAnalysis.Builder().build().apply {
                            setAnalyzer(ContextCompat.getMainExecutor(ctx)) { imageProxy ->
                                if (isNear.value) {
                                    val bitmap = imageProxyToBitmap(imageProxy)
                                    bitmap?.let {
                                        val centerX = it.width / 2
                                        val centerY = it.height / 2
                                        val pixel = it.getPixel(centerX, centerY) // <- Usa getPixel, no it[]

                                        val color = Color(
                                            android.graphics.Color.red(pixel) / 255f,
                                            android.graphics.Color.green(pixel) / 255f,
                                            android.graphics.Color.blue(pixel) / 255f
                                        )

                                        onColorDetected(color)
                                    }
                                }

                                imageProxy.close()
                            }
                        }

                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview,
                                imageAnalyzer
                            )
                        } catch (exc: Exception) {
                            exc.printStackTrace()
                        }
                    }, ContextCompat.getMainExecutor(ctx))

                    previewView
                }
            )
        }
    }
}
