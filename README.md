# Plataforma de Video Streaming Adaptativo (MPEG-DASH) 🎬

Este proyecto implementa una solución completa de streaming de video utilizando el estándar **ISO/IEC 23009-1 (MPEG-DASH)**. A través de una arquitectura desacoplada, el sistema permite la transmisión de contenido multimedia que se adapta dinámicamente a las condiciones de red del cliente, garantizando una reproducción sin interrupciones.

## 🚀 Características Técnicas Destacadas

- **Streaming Adaptativo (ABR):** Implementación de lógica para conmutar entre diferentes calidades de video en tiempo real según el ancho de banda disponible.
- **Procesamiento de Video:** Uso de `ffmpeg` para la segmentación de video en fragmentos (.m4s) y generación de archivos de descripción de presentación multimedia (.mpd).
- **Backend Robusto:** API REST desarrollada con **Jakarta EE y Servlets**, encargada de la gestión de recursos y despacho de segmentos multimedia.
- **Persistencia:** Gestión de metadatos mediante **Apache Derby** integrada en el servidor de aplicaciones.

## 📁 Estructura del Proyecto

- **`API-RESTStreaming/`**: Backend en Java que expone los endpoints necesarios para el descubrimiento y consumo de recursos multimedia.
- **`ClienteStreaming/`**: Cliente web que integra el reproductor para interpretar manifiestos DASH y gestionar el buffer de reproducción.
- **`informe MPEG-DASH.pdf`**: Documentación exhaustiva que cubre desde la codificación hasta la arquitectura de red y protocolos utilizados.

## 🛠️ Stack Tecnológico

* **Lenguaje:** Java 17+ (Jakarta EE)
* **Servidor de Aplicaciones:** GlassFish / Payara Server
* **Base de Datos:** Apache Derby (Java DB)
* **Procesamiento:** FFmpeg (Segmentación de video)
* **Protocolo:** HTTP/1.1 con soporte para rangos de bytes (Byte-range requests)

## 🔧 Configuración y Despliegue

### Requisitos Previos
* Java JDK 17 o superior.
* Servidor GlassFish configurado en el puerto 8080.
* Recursos de video segmentados siguiendo la estructura detallada en el informe técnico.

### Pasos de Instalación
1. **Clonación:** `git clone https://github.com/tu-usuario/streaming-dash-jakarta.git`
2. **Backend:** Importar `API-RESTStreaming` en el IDE y desplegar el artefacto `.war` en el servidor de aplicaciones.
3. **Frontend:** Servir la carpeta `ClienteStreaming` (se recomienda usar un servidor web ligero para evitar problemas de rutas relativas).
4. **Base de Datos:** El sistema inicializa automáticamente el esquema en Derby al primer despliegue si el pool de conexiones está correctamente referenciado.

## 📚 Resumen de Investigación (Extracto del Informe)
El proyecto aborda la problemática de la variabilidad del ancho de banda en redes IP. Mediante el uso de **MPEG-DASH**, el contenido se divide en segmentos cortos descargables por HTTP. El cliente decide qué calidad descargar basándose en su capacidad actual, eliminando el buffering tradicional de las descargas progresivas.

---
*Este proyecto es el resultado de una investigación técnica profunda sobre protocolos de capa de aplicación y sistemas de distribución de contenido (CDN).*
