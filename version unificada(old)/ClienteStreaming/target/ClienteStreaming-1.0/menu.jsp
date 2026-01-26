<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>

<!DOCTYPE html>
<html>
    <head>
        <link rel="stylesheet" type="text/css" href="css/estilo.css?v=4">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Catálogo de Videos DASH</title>
    </head>
    <body>
        
        <div class="navbar">
            <div class="user-info">
                👤 Hola, <b><%= session.getAttribute("usuario") %></b>
            </div>

            <div style="display: flex; gap: 15px; align-items: center;">
                
                <% 
                    // RECUPERAMOS EL ROL QUE GUARDAMOS EN EL LOGIN
                    String rol = (String) session.getAttribute("rol");
                    
                    // Solo pintamos este botón si es ADMIN
                    if (rol != null && rol.equals("ADMIN")) { 
                %>
                    <a href="AdminVideos" class="btn-admin">⚙️ Gestionar Videos</a>
                <% } %>

                <a href="Logout" class="btn-logout">Cerrar Sesión</a>
            </div>
        </div>

        <h1>🎬 Mis Videos Disponibles</h1>
        <p>Selecciona un video para empezar el streaming</p>

        <div class="search-container">
            <input type="text" id="buscador" onkeyup="filtrarVideos()" placeholder="🔍 Buscar video...">
        </div>

        <div class="container" id="gridVideos">
            <% 
                List<String> videos = (List<String>) request.getAttribute("listaVideos");
                
                if (videos != null && !videos.isEmpty()) {
                    for (String carpeta : videos) {
                        String nombreBonito = carpeta.replace("dash_", "").toUpperCase();
            %>
            
            <a href="player.jsp?v=<%= carpeta %>" class="video-card" data-title="<%= nombreBonito %>">
                <span class="icon">▶️</span>
                <span class="title"><%= nombreBonito %></span>
            </a>

            <% 
                    }
                } else {
            %>
                <div class="error-message" style="grid-column: 1 / -1;">
                    <h3>⚠️ No se encontraron videos.</h3>
                </div>
            <% } %>
        </div>

        <script>
            function filtrarVideos() {
                var input = document.getElementById('buscador');
                var filtro = input.value.toUpperCase();
                var contenedor = document.getElementById("gridVideos");
                var tarjetas = contenedor.getElementsByClassName('video-card');

                for (var i = 0; i < tarjetas.length; i++) {
                    var titulo = tarjetas[i].getElementsByClassName("title")[0];
                    if (titulo) {
                        var textoTitulo = titulo.textContent || titulo.innerText;
                        if (textoTitulo.toUpperCase().indexOf(filtro) > -1) {
                            tarjetas[i].style.display = "";
                        } else {
                            tarjetas[i].style.display = "none";
                        }
                    }       
                }
            }
        </script>

    </body>
</html>