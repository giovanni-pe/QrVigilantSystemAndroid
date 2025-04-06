package com.example.qrvigilantsystem.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.qrvigilantsystem.data.repository.QRScannerRepositoryImpl
import com.example.qrvigilantsystem.domain.repository.QRScannerRepository
import kotlinx.coroutines.launch

class ScannerViewModel(
    private val repository: QRScannerRepository
) : ViewModel() {

    val appState = repository.appState
    val cameraEnabled = repository.cameraEnabled
    val appConfig = repository.appConfig

    fun enableCamera() = repository.enableCamera()
    fun handleQRDetection(qrData: String) = repository.handleQRDetection(qrData)
    fun resetState() = repository.resetState()

    fun updateApiUrl(newUrl: String) {
        viewModelScope.launch {
            repository.updateApiUrl(newUrl)
        }
    }
}

class ScannerViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScannerViewModel::class.java)) {
            val repository = QRScannerRepositoryImpl(context)
            @Suppress("UNCHECKED_CAST")
            return ScannerViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}