package com.example.qrvigilantsystem.presentation.screen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.qrvigilantsystem.data.datasource.QRScannerDataSource
import com.example.qrvigilantsystem.domain.model.AppState
import com.example.qrvigilantsystem.presentation.component.*
import com.example.qrvigilantsystem.presentation.theme.MilitaryScannerTheme
import com.example.qrvigilantsystem.presentation.viewmodel.ScannerViewModel
import com.example.qrvigilantsystem.presentation.viewmodel.ScannerViewModelFactory
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import java.util.concurrent.Executors

@Composable
fun ScannerScreen(
    viewModel: ScannerViewModel = viewModel(
        factory = ScannerViewModelFactory(LocalContext.current)
    )
) {
    val appState by viewModel.appState.collectAsState()
    val cameraEnabled by viewModel.cameraEnabled.collectAsState()
    val appConfig by viewModel.appConfig.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var showConfigDialog by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.enableCamera()
        }
    }

    MilitaryScannerTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                MilitaryPatternBackground()

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    MilitaryHeader()

                    when {
                        !cameraEnabled -> MilitaryCameraDisabledView {
                            if (ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.CAMERA
                                ) == PackageManager.PERMISSION_GRANTED
                            ) {
                                viewModel.enableCamera()
                            } else {
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        }
                        appState is AppState.CameraOff -> MilitaryCameraDisabledView { viewModel.enableCamera() }
                        else -> MilitaryCameraView(appState, viewModel)
                    }

                    MilitaryFooter()
                }

                // Botón de configuración flotante
                ConfigButton(
                    onClick = { showConfigDialog = true },
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.BottomEnd)
                )

                // Diálogo de configuración
                if (showConfigDialog) {
                    ConfigDialog(
                        currentUrl = appConfig.apiUrl,
                        onDismiss = { showConfigDialog = false },
                        onSave = { newUrl ->
                            viewModel.updateApiUrl(newUrl)
                            showConfigDialog = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MilitaryCameraView(appState: AppState, viewModel: ScannerViewModel) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    val barcodeScanner = remember {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
        BarcodeScanning.getClient(options)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
                    implementationMode = PreviewView.ImplementationMode.PERFORMANCE
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                }
            },
            modifier = Modifier.fillMaxSize(),
            update = { previewView ->
                val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    val imageAnalysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                        .also {
                            it.setAnalyzer(
                                cameraExecutor,
                                QRScannerDataSource(barcodeScanner) { qrData ->
                                    viewModel.handleQRDetection(qrData)
                                }
                            )
                        }

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            preview,
                            imageAnalysis
                        )
                    } catch (exc: Exception) {
                        // Handle error
                    }
                }, ContextCompat.getMainExecutor(context))
            }
        )

        MilitaryScanningFrameWithAnimation()

        when (appState) {
            AppState.WaitingForQR -> MilitaryScanningView()
            is AppState.QRDetected -> MilitaryQRDetectedView(appState.data)
            is AppState.APISuccess -> MilitaryVerificationSuccessView(appState.response) {
                viewModel.resetState()
            }
            is AppState.APIError -> MilitaryErrorView(appState.message) {
                viewModel.resetState()
            }
            else -> {}
        }
    }
}