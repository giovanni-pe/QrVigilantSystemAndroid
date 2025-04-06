package com.example.qrvigilantsystem.domain.model

sealed class AppState {
    object CameraOff : AppState()
    object WaitingForQR : AppState()
    data class QRDetected(val data: String) : AppState()
    data class APISuccess(val response: APIResponse) : AppState()
    data class APIError(val message: String) : AppState()
}

data class APIResponse(
    val status: String,
    val message: String,
    val securityLevel: Int
)