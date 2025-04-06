package com.example.qrvigilantsystem.data.repository

import android.content.Context
import com.example.qrvigilantsystem.data.datasource.AppPreferences
import com.example.qrvigilantsystem.domain.model.APIResponse
import com.example.qrvigilantsystem.domain.model.AppConfig
import com.example.qrvigilantsystem.domain.model.AppState
import com.example.qrvigilantsystem.domain.repository.QRScannerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class QRScannerRepositoryImpl(context: Context) : QRScannerRepository {

    private val preferences = AppPreferences(context)
    private val _appConfig = MutableStateFlow(preferences.getConfig())
    override val appConfig: StateFlow<AppConfig> = _appConfig

    private val _appState = MutableStateFlow<AppState>(AppState.CameraOff)
    override val appState: StateFlow<AppState> = _appState

    private val _cameraEnabled = MutableStateFlow(false)
    override val cameraEnabled: StateFlow<Boolean> = _cameraEnabled

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    override fun enableCamera() {
        _cameraEnabled.value = true
        _appState.value = AppState.WaitingForQR
    }

    override fun handleQRDetection(qrData: String) {
        _appState.value = AppState.QRDetected(qrData)

        // Realizar la solicitud POST a la API
        Thread {
            try {
                val response = makeApiRequest(qrData)
                _appState.value = if (response.isSuccessful) {
                    parseSuccessResponse(response)
                } else {
                    AppState.APIError("Error en la API: ${response.code}")
                }
            } catch (e: Exception) {
                _appState.value = AppState.APIError("Error de conexión: ${e.localizedMessage}")
            }
        }.start()
    }

    private fun makeApiRequest(qrData: String): Response {
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = JSONObject().apply {
            put("qr_data", qrData)
            put("device_id", "mobile_device")
        }.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url(_appConfig.value.apiUrl)
            .post(requestBody)
            .addHeader("Content-Type", "application/json")
            .build()

        return client.newCall(request).execute()
    }

    private fun parseSuccessResponse(response: Response): AppState {
        val responseBody = response.body?.string()
        return try {
            val json = JSONObject(responseBody)
            AppState.APISuccess(
                APIResponse(
                    status = json.getString("status"),
                    message = json.getString("message"),
                    securityLevel = json.getInt("security_level")
                )
            )
        } catch (e: Exception) {
            AppState.APIError("Error al procesar respuesta")
        }
    }

    override fun resetState() {
        _appState.value = if (_cameraEnabled.value) {
            AppState.WaitingForQR
        } else {
            AppState.CameraOff
        }
    }

    override suspend fun updateApiUrl(newUrl: String) {
        val newConfig = _appConfig.value.copy(apiUrl = newUrl)
        _appConfig.value = newConfig
        preferences.saveConfig(newConfig)
    }
}