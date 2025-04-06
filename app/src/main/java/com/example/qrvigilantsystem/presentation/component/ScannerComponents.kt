package com.example.qrvigilantsystem.presentation.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.qrvigilantsystem.domain.model.APIResponse
import com.example.qrvigilantsystem.domain.model.AppState
import com.example.qrvigilantsystem.presentation.theme.*
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun MilitaryPatternBackground() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(color = Color.Black)
        val gridColor = MilitaryDarkGreen.copy(alpha = 0.15f)
        val gridStep = 50.dp.toPx()

        for (x in 0..(size.width / gridStep).toInt()) {
            drawLine(
                color = gridColor,
                start = Offset(x * gridStep, 0f),
                end = Offset(x * gridStep, size.height),
                strokeWidth = 1f
            )
        }

        for (y in 0..(size.height / gridStep).toInt()) {
            drawLine(
                color = gridColor,
                start = Offset(0f, y * gridStep),
                end = Offset(size.width, y * gridStep),
                strokeWidth = 1f
            )
        }
    }
}

@Composable
fun MilitaryHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color.Black, MilitaryDarkGray)
                )
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "SISTEMA DE VERIFICACIÓN QR",
            color = MilitaryYellow,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Text(
            text = "NIVEL DE SEGURIDAD 3",
            color = MilitaryRed,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun MilitaryFooter() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MilitaryDarkGray)
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "STATUS: OPERATIVO",
            color = MilitaryGreen,
            fontSize = 12.sp
        )
        Text(
            text = "v2.4.1",
            color = MilitaryLightGray,
            fontSize = 10.sp
        )
    }
}

@Composable
fun MilitaryScanningFrameWithAnimation() {
    var animationProgress by remember { mutableFloatStateOf(0f) }
    val infiniteTransition = rememberInfiniteTransition()

    val animatedProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    animationProgress = animatedProgress

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.Center)
        ) {
            val cornerLength = 30f
            val strokeWidth = 3f
            val frameWidth = size.width
            val frameHeight = size.height

            // Esquinas
            drawLine(color = MilitaryGreen, start = Offset(0f, 0f), end = Offset(cornerLength, 0f), strokeWidth = strokeWidth)
            drawLine(color = MilitaryGreen, start = Offset(0f, 0f), end = Offset(0f, cornerLength), strokeWidth = strokeWidth)
            drawLine(color = MilitaryGreen, start = Offset(frameWidth, 0f), end = Offset(frameWidth - cornerLength, 0f), strokeWidth = strokeWidth)
            drawLine(color = MilitaryGreen, start = Offset(frameWidth, 0f), end = Offset(frameWidth, cornerLength), strokeWidth = strokeWidth)
            drawLine(color = MilitaryGreen, start = Offset(0f, frameHeight), end = Offset(cornerLength, frameHeight), strokeWidth = strokeWidth)
            drawLine(color = MilitaryGreen, start = Offset(0f, frameHeight), end = Offset(0f, frameHeight - cornerLength), strokeWidth = strokeWidth)
            drawLine(color = MilitaryGreen, start = Offset(frameWidth, frameHeight), end = Offset(frameWidth - cornerLength, frameHeight), strokeWidth = strokeWidth)
            drawLine(color = MilitaryGreen, start = Offset(frameWidth, frameHeight), end = Offset(frameWidth, frameHeight - cornerLength), strokeWidth = strokeWidth)

            // Línea de escaneo
            val scanLineY = frameHeight * animationProgress
            drawLine(
                color = MilitaryGreen.copy(alpha = 0.7f),
                start = Offset(0f, scanLineY),
                end = Offset(frameWidth, scanLineY),
                strokeWidth = 4f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 10f), 0f)
            )

            // Punto de escaneo
            val dotX = frameWidth * (0.5f + 0.4f * sin(animationProgress * 2 * PI.toFloat()))
            drawCircle(
                color = MilitaryYellow,
                center = Offset(dotX, scanLineY),
                radius = 8f
            )
        }
    }
}

@Composable
fun MilitaryCameraDisabledView(onEnableCamera: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Cámara desactivada",
                tint = MilitaryRed,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "SISTEMA DE CÁMARA DESACTIVADO",
                color = MilitaryYellow,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Para iniciar el escaneo de seguridad, active el sistema de cámara",
                color = MilitaryLightGray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            Button(
                onClick = onEnableCamera,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MilitaryGreen,
                    contentColor = Color.Black
                ),
                modifier = Modifier.width(200.dp)
            ) {
                Text("ACTIVAR SISTEMA", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun MilitaryScanningView() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 32.dp)
        ) {
            CircularProgressIndicator(
                color = MilitaryGreen,
                strokeWidth = 3.dp,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "ESCANEO ACTIVO",
                color = MilitaryYellow,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Buscando códigos QR...",
                color = MilitaryLightGray,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun MilitaryQRDetectedView(data: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "QR detectado",
                tint = MilitaryYellow,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "CREDENCIAL DETECTADA",
                color = MilitaryGreen,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = data.take(30) + if (data.length > 30) "..." else "",
                color = MilitaryLightGray,
                fontSize = 14.sp,
                modifier = Modifier.padding(16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            LinearProgressIndicator(
                color = MilitaryGreen,
                trackColor = MilitaryDarkGray,
                modifier = Modifier.fillMaxWidth(0.6f)
            )
            Text(
                text = "VERIFICANDO...",
                color = MilitaryYellow,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}


@Composable
fun MilitaryVerificationSuccessView(response: APIResponse, onContinue: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.9f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Verificado",
                tint = MilitaryGreen,
                modifier = Modifier.size(72.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "VERIFICACIÓN EXITOSA",
                color = MilitaryGreen,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = response.message,
                color = MilitaryYellow,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Nivel de seguridad: ${"★".repeat(response.securityLevel)}",
                color = MilitaryLightGray,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = onContinue,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MilitaryGreen,
                    contentColor = Color.Black
                ),
                modifier = Modifier.width(200.dp)
            ) {
                Text("CONTINUAR ESCANEO", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun MilitaryErrorView(message: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.9f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Error",
                tint = MilitaryRed,
                modifier = Modifier.size(72.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "ALERTA DE SEGURIDAD",
                color = MilitaryRed,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                color = MilitaryYellow,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MilitaryRed,
                    contentColor = Color.White
                ),
                modifier = Modifier.width(200.dp)
            ) {
                Text("REINTENTAR", fontWeight = FontWeight.Bold)
            }
        }
    }
}