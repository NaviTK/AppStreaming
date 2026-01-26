<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    // Recuperamos el dato que nos pasó el Servlet por GET
    String carpetaActual = (String) request.getAttribute("carpetaParaEditar");

    // Si alguien intenta entrar directo sin pasar por el Servlet, lo echamos
    if (carpetaActual == null) {
        response.sendRedirect("AdminVideos");
        return;
    }
    
    // Nombre bonito para mostrar (sin dash_)
    String nombreVisual = carpetaActual.replace("dash_", "").toUpperCase();
%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Modificar Video</title>
        <link rel="stylesheet" type="text/css" href="css/estilo.css?v=8">
        <style>
            .edit-box {
                background-color: #2b2d3e; /* Azul oscuro */
                border: 1px solid #3e415b;
                padding: 40px;
                border-radius: 10px;
                max-width: 500px;
                margin: 40px auto;
                box-shadow: 0 10px 30px rgba(0,0,0,0.5);
            }
            .input-group {
                margin-bottom: 20px;
                text-align: left;
            }
            label {
                display: block;
                margin-bottom: 8px;
                color: #aaa;
            }
            input[type="text"] {
                width: 100%;
                padding: 12px;
                background: #1a1c29;
                border: 1px solid #444;
                color: white;
                border-radius: 5px;
                font-size: 1.1em;
                box-sizing: border-box; /* Para que el padding no rompa el ancho */
            }
            input[type="text"]:focus {
                border-color: #00d1b2;
                outline: none;
            }
        </style>
    </head>
    <body>

        <div class="navbar">
            <div class="user-info">🛡️ MODO ADMIN</div>
            <a href="AdminVideos" class="btn-logout" style="background: #444; border-color: #444;">Cancelar</a>
        </div>

        <div class="admin-main-container">
            
            <h1 style="text-align: center; color: #00d1b2;">✏️ Editar Video</h1>

            <div class="edit-box">
                <form action="Modificar" method="POST">
                    
                    <input type="hidden" name="nombreOriginal" value="<%= carpetaActual %>">

                    <div class="input-group">
                        <label>Nombre Actual (ID Sistema):</label>
                        <input type="text" value="<%= carpetaActual %>" disabled style="background: #333; color: #777; cursor: not-allowed;">
                    </div>

                    <div class="input-group">
                        <label for="nuevoNombre">Nuevo Nombre:</label>
                        <input type="text" name="nuevoNombre" id="nuevoNombre" placeholder="Ej: Matrix Reloaded" required autocomplete="off">
                        <small style="color: #666; display: block; margin-top: 5px;">El sistema añadirá 'dash_' y guiones bajos automáticamente.</small>
                    </div>

                    <div style="text-align: center; margin-top: 30px; display: flex; gap: 15px; justify-content: center;">
                        <a href="AdminVideos" class="btn-subir-grande" style="background: #555; font-size: 1em; padding: 10px 20px; min-width: auto;">Cancelar</a>
                        <button type="submit" class="btn-subir-grande" style="font-size: 1em; padding: 10px 20px; border: none; cursor: pointer; min-width: auto;">
                            💾 Guardar Cambios
                        </button>
                    </div>

                </form>
            </div>

        </div>

    </body>
</html>
