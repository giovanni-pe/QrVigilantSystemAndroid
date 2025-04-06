package com.example.qrvigilantsystem.domain.repository

import com.example.qrvigilantsystem.domain.model.AppConfig
import com.example.qrvigilantsystem.domain.model.AppState
import kotlinx.coroutines.flow.StateFlow

interface QRScannerRepository {
    val appState: StateFlow<AppState>
    val cameraEnabled: StateFlow<Boolean>
    val appConfig: StateFlow<AppConfig>

    fun enableCamera()
    fun handleQRDetection(qrData: String)
    fun resetState()
    suspend fun updateApiUrl(newUrl: String)
}