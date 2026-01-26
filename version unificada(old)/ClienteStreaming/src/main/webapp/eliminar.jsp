<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String carpetaVideo = request.getParameter("v");
    if (carpetaVideo == null || carpetaVideo.trim().isEmpty()) {
        response.sendRedirect("AdminVideos");
        return;
    }
    String nombreVisual = carpetaVideo.replace("dash_", "").toUpperCase();
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Confirmar Eliminación</title>
        <link rel="stylesheet" type="text/css" href="css/estilo.css?v=8">
        <style>
            /* CAJA MÁS PEQUEÑA Y COMPACTA */
            .danger-zone {
                border: 1px solid #ff3860;
                background-color: rgba(43, 29, 29, 0.95); /* Fondo más oscuro */
                padding: 25px; /* Menos padding */
                border-radius: 8px;
                margin: 40px auto; /* Centrado vertical */
                max-width: 500px; /* Ancho limitado (mitad que antes) */
                box-shadow: 0 5px 20px rgba(0,0,0,0.5);
            }
            .warning-text {
                color: #ff8095;
                font-size: 0.95em;
                margin-top: 10px;
            }
        </style>
    </head>
    <body>

        <div class="navbar">
            <div class="user-info">🛡️ MODO ADMIN</div>
            <a href="AdminVideos" class="btn-logout" style="background: #444; border-color: #444;">Cancelar</a>
        </div>

        <div class="admin-main-container" style="background: transparent; box-shadow: none; padding-top: 0;">
            
            <div class="danger-zone">
                <div style="font-size: 3em; margin-bottom: 10px;">🗑️</div>
                
                <h3 style="margin: 0; color: #ff3860;">Eliminar Video</h3>
                
                <h2 style="color: white; font-size: 1.8em; margin: 15px 0;">
                    <%= nombreVisual %>
                </h2>
                
                <p class="warning-text">¿Estás seguro? Esta acción es irreversible.</p>

                <form action="Eliminar" method="POST" style="margin-top: 25px;">
                    <input type="hidden" name="carpeta" value="<%= carpetaVideo %>">
                    
                    <div style="display: flex; gap: 10px; justify-content: center;">
                        <a href="AdminVideos" style="padding: 10px 20px; background: #444; color: white; border-radius: 4px; font-weight: bold; font-size: 0.9em; text-decoration: none;">
                            Cancelar
                        </a>
                        <button type="submit" style="padding: 10px 20px; background: #ff3860; color: white; border: none; border-radius: 4px; font-weight: bold; font-size: 0.9em; cursor: pointer;">
                            SÍ, BORRAR
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </body>
</html>
