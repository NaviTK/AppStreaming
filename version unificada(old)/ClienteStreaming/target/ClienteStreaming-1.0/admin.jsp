<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Panel de Administración - Gestión de Videos</title>
        <link rel="stylesheet" type="text/css" href="css/estilo.css?v=8">
    </head>
    <body>

        <div class="navbar">
            <div class="user-info">
                🛡️ MODO ADMIN: <b><%= session.getAttribute("usuario") %></b>
            </div>
            <a href="Logout" class="btn-logout">Cerrar Sesión</a>
        </div>

        <div class="admin-main-container">
            
            <h1>⚙️ Gestión de Contenido</h1>
            
            <%-- BLOQUE DE MENSAJES DE ALERTA --%>
            <% 
                // AHORA LEEMOS EL ATRIBUTO QUE NOS ENVÍA EL SERVLET
                String mensaje = (String) request.getAttribute("mensajeAlerta");
                
                if ("borrado".equals(mensaje)) {
            %>
                <div style="background-color: rgba(0, 209, 178, 0.2); border: 1px solid #00d1b2; color: #00d1b2; padding: 15px; border-radius: 8px; margin-bottom: 30px; text-align: center; font-weight: bold;">
                    ✅ Video eliminado correctamente.
                </div>
            <% 
                } else if ("modificado".equals(mensaje)) {
            %>
                <div style="background-color: rgba(50, 152, 220, 0.2); border: 1px solid #3298dc; color: #3298dc; padding: 15px; border-radius: 8px; margin-bottom: 30px; text-align: center; font-weight: bold;">
                    ✏️ Nombre del video actualizado correctamente.
                </div>
            <% 
                } else if ("errorModificar".equals(mensaje)) {
            %>
                <div style="background-color: rgba(255, 56, 96, 0.2); border: 1px solid #ff3860; color: #ff3860; padding: 15px; border-radius: 8px; margin-bottom: 30px; text-align: center; font-weight: bold;">
                    ❌ Error: El nombre ya existe o no es válido.
                </div>
            <% 
                } else if ("error".equals(mensaje)) {
            %>
                <div style="background-color: rgba(255, 56, 96, 0.2); border: 1px solid #ff3860; color: #ff3860; padding: 15px; border-radius: 8px; margin-bottom: 30px; text-align: center; font-weight: bold;">
                    ❌ Hubo un error inesperado.
                </div>
            <% } %>
            <%-- FIN BLOQUE MENSAJES --%>

            <a href="subirVideo.jsp" class="btn-subir-grande">
                ☁️ Subir Nuevo Video
            </a>

            <div class="tabla-responsive-container">
                <table class="tabla-admin">
                    <thead>
                        <tr>
                            <th style="width: 40%;">Nombre del Video</th>
                            <th style="width: 30%;">ID Sistema (Carpeta)</th>
                            <th style="text-align: center; width: 30%;">Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% 
                            List<String> videos = (List<String>) request.getAttribute("listaVideosAdmin");

                            if (videos != null && !videos.isEmpty()) {
                                for (String carpeta : videos) {
                                    String nombreVisual = carpeta.replace("dash_", "").toUpperCase();
                        %>
                        <tr>
                            <td>
                                <span style="font-size: 1.1em; font-weight: bold;">🎬 <%= nombreVisual %></span>
                            </td>
                            <td style="color: #888; font-family: monospace; font-size: 0.9em;">
                                <%= carpeta %>
                            </td>
                            <td>
                                <div class="acciones" style="justify-content: center;">
                                    <a href="Modificar?v=<%= carpeta %>" class="btn-accion btn-modificar">✏️ Modificar</a>
                                    <a href="eliminar.jsp?v=<%= carpeta %>" class="btn-accion btn-eliminar">🗑️ Eliminar</a>
                                </div>
                            </td>
                        </tr>
                        <% 
                                }
                            } else {
                        %>
                        <tr>
                            <td colspan="3" style="text-align: center; padding: 40px; color: #888;">
                                <h3>⚠️ No hay videos subidos todavía.</h3>
                                <p>Usa el botón de arriba para añadir el primero.</p>
                            </td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>

            <div style="text-align: center; margin-top: 40px; padding-top: 20px; border-top: 1px solid #333;">
                <a href="ListaVideos" style="color: #888; text-decoration: none; font-size: 0.9em; transition: color 0.3s;">
                    ⬅️ Volver al Menú Principal
                </a>
                <style>a[href="ListaVideos"]:hover { color: #00d1b2 !important; }</style>
            </div>

        </div> 
    </body>
</html>
