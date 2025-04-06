package com.example.qrvigilantsystem.data.datasource

import android.content.Context
import androidx.core.content.edit
import com.example.qrvigilantsystem.domain.model.AppConfig
import com.google.gson.Gson

class AppPreferences(context: Context) {
    private val sharedPref = context.getSharedPreferences("qr_vigilant_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveConfig(config: AppConfig) {
        sharedPref.edit {
            putString("app_config", gson.toJson(config))
        }
    }

    fun getConfig(): AppConfig {
        val configJson = sharedPref.getString("app_config", null)
        return if (configJson != null) {
            gson.fromJson(configJson, AppConfig::class.java)
        } else {
            AppConfig() // Configuración por defecto
        }
    }
}