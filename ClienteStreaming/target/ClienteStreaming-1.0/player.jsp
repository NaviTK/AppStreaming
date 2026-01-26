<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>J-DASH Player</title>
    
    <link rel="stylesheet" type="text/css" href="css/estilo.css">
    
    <script src="https://cdn.dashjs.org/v4.7.4/dash.all.min.js"></script>
    <script src="js/JDashPlayer.js"></script>
</head>
<body>

    <div style="text-align: left; padding: 20px;">
        <a href="ListaVideos" class="back-btn" style="text-decoration: none; font-size: 1.2em;">⬅ Volver al Menú</a>
    </div>

    <h1>📺 Reproduciendo: <%= request.getParameter("v") != null ? request.getParameter("v").replace("dash_", "").toUpperCase() : "VIDEO" %></h1>
    
    <div id="videoContainer">
        <video id="videoPlayer" controls autoplay muted></video>
    </div>

    <div class="dashboard">
        <div class="stats">
            <div>📊 Calidad: <span id="qualityIndex">Cargando...</span></div>
            <div>📐 Resolución: <span id="resolution">-</span></div>
            <div>⚡ Bitrate: <span id="bitrate">-</span> kbps</div>
        </div>

        <div class="controls">
            <button class="btn-360" onclick="myDashApp.setQuality(0)">360p</button>
            <button class="btn-480" onclick="myDashApp.setQuality(1)">480p</button>
            <button class="btn-720" onclick="myDashApp.setQuality(2)">720p</button>
            <button class="btn-1080" onclick="myDashApp.setQuality(3)">1080p</button>
            <button class="btn-auto" onclick="myDashApp.setAuto()">Auto 🤖</button>
        </div>
    </div>

    <script>
        // ---------------------------------------------------------
        // VARIABLE GLOBAL
        // ---------------------------------------------------------
        var myDashApp;

        document.addEventListener("DOMContentLoaded", function () {
            
            // 1. Obtener el nombre de la carpeta del video desde el parámetro URL
            var carpetaVideo = "<%= request.getParameter("v") %>"; 
            
            if (!carpetaVideo || carpetaVideo === "null") {
                alert("Error: No se ha seleccionado ningún video.");
                return;
            }

            // ------------------------------------------------------------------
            // ⚠️ CORRECCIÓN DE LA RUTA API
            // ------------------------------------------------------------------
            
            // NO usamos request.getContextPath() aquí porque estamos en ClienteStreaming
            // y queremos apuntar a API-RESTStreaming.
            var backendProjectName = "/API-RESTStreaming";

            // Nombre fijo del archivo manifiesto
            var nombreArchivoMpd = "manifiesto.mpd"; 

            // Codificamos la carpeta por si tiene espacios o caracteres raros
            var carpetaEncoded = encodeURIComponent(carpetaVideo);

            // Construcción exacta de la URL apuntando al OTRO proyecto:
            // http://localhost:8080 + /API-RESTStreaming + /resources/jakartaee9/stream/ + carpeta + /manifiesto.mpd
            var finalUrl = window.location.origin + 
                           backendProjectName + 
                           "/resources/jakartaee9/stream/" + 
                           carpetaEncoded + "/" + 
                           nombreArchivoMpd;
            
            console.log("🚀 URL API Generada: " + finalUrl);

            // 3. Instanciar TU Clase JDashPlayer
            if (typeof JDashPlayer !== 'undefined') {
                myDashApp = new JDashPlayer("videoPlayer", finalUrl);
                myDashApp.init();
            } else {
                console.error("Error: La clase JDashPlayer no se ha cargado correctamente.");
            }
        });
    </script>
</body>
</html>
