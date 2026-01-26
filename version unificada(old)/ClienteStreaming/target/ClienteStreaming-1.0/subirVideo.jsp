<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Subir Nuevo Video</title>
        <link rel="stylesheet" type="text/css" href="css/estilo.css?v=9">
        <style>
            /* ESTILOS ESPECÍFICOS PARA LA CARGA */
            .loading-overlay {
                display: none; /* Oculto por defecto */
                position: fixed;
                top: 0; left: 0; width: 100%; height: 100%;
                background: rgba(10, 10, 20, 0.95);
                z-index: 9999;
                flex-direction: column;
                justify-content: center;
                align-items: center;
                color: white;
            }

            .spinner {
                width: 60px;
                height: 60px;
                border: 5px solid rgba(255,255,255,0.1);
                border-top: 5px solid #00d1b2; /* Color turquesa */
                border-radius: 50%;
                animation: spin 1s linear infinite;
                margin-bottom: 20px;
            }

            @keyframes spin {
                0% { transform: rotate(0deg); }
                100% { transform: rotate(360deg); }
            }

            .upload-box {
                background-color: #2b2d3e;
                border: 2px dashed #444;
                padding: 40px;
                border-radius: 10px;
                max-width: 500px;
                margin: 40px auto;
                text-align: center;
                transition: border-color 0.3s;
            }
            .upload-box:hover { border-color: #00d1b2; }

            input[type="text"] {
                width: 100%; padding: 12px; margin-bottom: 20px;
                background: #1a1c29; border: 1px solid #555;
                color: white; border-radius: 5px;
            }
            input[type="file"] { display: none; }
            
            .custom-file-upload {
                border: 1px solid #00d1b2; display: inline-block;
                padding: 10px 20px; cursor: pointer; color: #00d1b2;
                border-radius: 5px; margin-bottom: 20px; font-weight: bold;
            }
            .custom-file-upload:hover { background: #00d1b2; color: white; }
        </style>
        
        <script>
            // Muestra el nombre del archivo seleccionado
            function mostrarNombreArchivo() {
                var input = document.getElementById('videoFile');
                var label = document.getElementById('nombreArchivoLabel');
                if(input.files && input.files.length > 0) {
                    label.textContent = "📄 " + input.files[0].name;
                    label.style.color = "#fff";
                }
            }

            // Activa la pantalla de carga al enviar el formulario
            function activarCarga() {
                // Verificamos que los campos no estén vacíos antes de mostrar la carga
                var nombre = document.getElementsByName("nombreVideo")[0].value;
                var archivo = document.getElementById("videoFile").value;

                if (nombre.trim() !== "" && archivo !== "") {
                    document.getElementById("pantallaCarga").style.display = "flex";
                }
            }
        </script>
    </head>
    <body>

        <div id="pantallaCarga" class="loading-overlay">
            <div class="spinner"></div>
            <h2 style="margin:0;">Procesando Video...</h2>
            <p style="color: #888; margin-top: 10px;">Subiendo archivo y generando DASH.<br>Por favor, no cierres esta ventana.</p>
        </div>

        <div class="navbar">
            <div class="user-info">🛡️ MODO ADMIN</div>
            <a href="AdminVideos" class="btn-logout" style="background: #444; border-color: #444;">Cancelar</a>
        </div>

        <div class="admin-main-container">
            <h1>☁️ Subir Nuevo Video</h1>
            <p style="text-align: center; color: #888; margin-bottom: 30px;">
                El sistema convertirá automáticamente el MP4 para streaming adaptativo.
            </p>

            <%-- MENSAJES DE ERROR --%>
            <% 
                String error = request.getParameter("error");
                if("datos".equals(error)) { 
            %>
                <div style="color: #ff3860; text-align: center; margin-bottom: 20px;">⚠️ Debes rellenar todos los campos.</div>
            <% } else if("conversion".equals(error)) { %>
                <div style="color: #ff3860; text-align: center; margin-bottom: 20px;">❌ Error técnico durante la conversión. Revisa el archivo.</div>
            <% } %>

            <div class="upload-box">
                <form action="SubirVideo" method="POST" enctype="multipart/form-data" onsubmit="activarCarga()">
                    
                    <label style="display:block; text-align:left; color:#aaa; margin-bottom:5px;">Nombre del Video:</label>
                    <input type="text" name="nombreVideo" placeholder="Ej: Matrix Reloaded" required autocomplete="off">

                    <label for="videoFile" class="custom-file-upload">
                        📂 Seleccionar MP4
                    </label>
                    <input type="file" id="videoFile" name="videoFile" accept=".mp4" required onchange="mostrarNombreArchivo()">
                    
                    <div id="nombreArchivoLabel" style="margin-bottom: 25px; color: #666; font-size: 0.9em;">
                        Ningún archivo seleccionado
                    </div>

                    <button type="submit" class="btn-subir-grande" style="border:none; width:100%; cursor: pointer;">
                        🚀 Subir y Convertir
                    </button>
                </form>
            </div>
        </div>
    </body>
</html>