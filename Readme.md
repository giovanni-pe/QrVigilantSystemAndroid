# Arquitectura del Sistema de Verificación QR

## Diagrama de Arquitectura

```
[Dispositivo Android] │ (HTTP POST - JSON) ▼ [API PHP (api.php)] │ (Escribe en archivo) ▼ [Archivo de Logs (qr_logs.txt)]
```

## Componentes Principales

### 1. Capa Móvil (Android)
- **Aplicación**: QrVigilant.apk
- **Funcionalidades**:
  - Escaneo de códigos QR
  - Configuración de endpoint
  - Visualización de resultados
- **Tecnologías**:
  - Kotlin/Java
  - CameraX API
  - ML Kit para QR
  - Retrofit para HTTP

### 2. Capa de Servicio (Backend)
- **Endpoint**: api.php
- **Procesamiento**:
  ```php
  1. Recibe POST JSON
  2. Valida estructura
  3. Escribe en log
  4. Devuelve respuesta JSON
  ```
- **Características**:
  - Stateless
  - Sin base de datos
  - Rotación automática de logs

### 3. Capa de Persistencia
- **Almacenamiento**:
  - Archivo plano (qr_logs.txt)
- **Formato**:
  ```
  [TIMESTAMP] QR: [DATA] | Device: [ID]
  ```
- **Mecanismo**:
  - Append-only
  - Rotación por tamaño (5MB)

## Flujo de Datos
1. **Escaneo**:
   - App detecta QR → Extrae texto
2. **Envío**:
   ```json
   POST /api.php
   {
     "qr_data": "texto_del_qr",
     "device_id": "ID_DEVICE"
   }
   ```
3. **Procesamiento**:
   - Validación de campos
   - Sanitización de input
   - Escritura asíncrona
4. **Respuesta**:
   ```json
   HTTP 200 OK
   {
     "status": "PROCESADO",
     "securityLevel": 1
   }
   ```

## Pasos para Probar la Implementación

### 1. Configuración del Backend
```bash
# Clonar repositorio
git clone https://github.com/giovanni-pe/VigilantVerifyApi.git
cd VigilantVerifyApi

# Iniciar servidor PHP
php -S 0.0.0.0:8000
```

### 2. Instalación de la Aplicación
1. Transferir `QrVigilant.apk` al dispositivo móvil
2. Habilitar instalación de fuentes desconocidas
3. Instalar la aplicación

### 3. Configuración de la Conexión
1. Identificar IP local del servidor:
   ```bash
   # Windows
   ipconfig
   
   # Linux/Mac
   ifconfig | grep "inet "
   ```
2. En la aplicación QrVigilant:
   - Ir a Ajustes → Configuración API
   - Establecer URL: `http://<IP-LOCAL>:8000/api.php`

### 4. Prueba del Sistema
1. Escanear un código QR con la aplicación
2. Verificar respuesta "Verificación exitosa"
3. Comprobar registro en servidor:
   ```bash
   cat qr_logs.txt
   ```

## Decisiones de Arquitectura
### ✅ Ventajas
- **Simplicidad**: PHP + Archivo plano
- **Portabilidad**: Solo requiere PHP
- **Bajo acoplamiento**: API REST estándar


## Esquema de Respuestas HTTP
| Código | Situación | Body Ejemplo |
|--------|-----------|-------------|
| 200 | QR procesado | `{"status":"PROCESADO"...}` |
| 400 | Falta qr_data | `{"error":"Campo requerido"}` |
| 405 | Método no permitido | `{"error":"Usar POST"}` |
| 500 | Error al escribir log | `{"error":"Fallo en servidor"}` |

## Seguridad
**Medidas actuales**:
- Sanitización básica de inputs
- Limitación de tamaño de log
- CORS restringido (en producción)

**Recomendaciones para producción**:
1. Añadir HTTPS
2. Implementar API Key
3. Validar formato de QR
4. Limitar tasa de requests

## Evolución Futura
1. **Migración a Base de Datos**:
   ```mermaid
   graph LR
   API --> MySQL
   API --> Redis(Cache)
   ```

2. **Microservicios**:
   - Servicio de autenticación
   - Servicio de logging separado
   - Cola de mensajes (RabbitMQ)

3. **Monitorización**:
   - Dashboard con métricas
   - Alertas por anomalías

## Resumen

El Sistema de Verificación QR es una solución ligera de tres capas diseñada para registrar lecturas de códigos QR desde dispositivos Android. Utiliza una arquitectura cliente-servidor simple con comunicación vía API REST, sin dependencias de bases de datos, priorizando la portabilidad y facilidad de despliegue. El sistema actual es ideal para entornos con volumen bajo-medio de escaneos, mientras que su evolución futura contempla la migración a una arquitectura más robusta con bases de datos, microservicios y capacidades de monitorización avanzadas.

**Última actualización**: 25/Nov/2023 **Versión arquitectura**: 1.0