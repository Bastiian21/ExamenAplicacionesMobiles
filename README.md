# Diario de Viaje App 

Aplicación Android desarrollada en Kotlin utilizando **Jetpack Compose** para la gestión de viajes y actividades turísticas con geolocalización.

##  Características Principales
* **Gestión de Usuarios:** Registro e inicio de sesión seguro con persistencia de sesión.
* **Privacidad de Datos:** Separación estricta de actividades por usuario (cada usuario ve solo sus propios registros).
* **Geolocalización:** Integración con API de OpenStreetMap (Nominatim) para obtener direcciones automáticas basadas en coordenadas GPS.
* **Persistencia Híbrida:**
    * **Local:** Base de datos **Room** (SQLite) para guardar actividades offline.
    * **Remota:** Sincronización con API REST mediante **Retrofit**.
* **Búsqueda Inteligente:** Filtrado de actividades por título en tiempo real.

##  Tecnologías Utilizadas
* **Lenguaje:** Kotlin
* **UI:** Jetpack Compose (Material Design 3)
* **Arquitectura:** MVVM (Model-View-ViewModel) con Repository Pattern.
* **Asincronía:** Coroutines & Flow.
* **Red:** Retrofit 2 & GSON.
* **Inyección de Dependencias:** Manual (ViewModelFactory).

##  Instalación
1. Clonar el repositorio.
2. Abrir en Android Studio Ladybug o superior.
3. Sincronizar Gradle.
4. Ejecutar en Emulador (API 30+) o Dispositivo físico.

##  Integrante
* Bastián Troncoso
