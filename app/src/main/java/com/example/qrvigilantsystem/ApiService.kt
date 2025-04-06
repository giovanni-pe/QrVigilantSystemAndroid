package com.example.qrvigilantsystem

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

interface QRVigilantApiService {
    @POST
    suspend fun sendQRData(
        @Url url: String = "api.php",
        @Body request: QRDataRequest
    ): ApiResponse
}

data class QRDataRequest(val qr_data: String)
data class ApiResponse(val status: String, val message: String)