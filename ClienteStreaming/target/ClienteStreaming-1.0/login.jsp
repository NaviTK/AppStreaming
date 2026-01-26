<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <link rel="stylesheet" type="text/css" href="css/estilo.css?v=2">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Login - J-DASH Player</title>
    </head>
    <body>
        
        <div class="login-wrapper">
            
            <div class="login-card">
                <h1>🔐 Acceso</h1>
                <p>Bienvenido al sistema de Streaming</p>

                <% 
                    String error = (String) request.getAttribute("error");
                    String registro = request.getParameter("registro");

                    // Mensaje de error
                    if (error != null) { 
                %>
                    <div class="error-message">
                        <%= error %>
                    </div>
                <% } 
                   // Mensaje de éxito si viene de registrarse
                   else if ("exito".equals(registro)) {
                %>
                     <div class="error-message" style="color: #00d1b2; background-color: rgba(0, 209, 178, 0.1); border-color: #00d1b2;">
                        ✅ ¡Cuenta creada! Inicia sesión.
                    </div>
                <% } %>

                <form action="Login" method="POST" autocomplete="off">
                    <input type="text" name="usuario" placeholder="Usuario" required autocomplete="off">
    
                    <input type="password" name="pass" placeholder="Contraseña" required autocomplete="new-password">
    
                    <button type="submit" class="btn-login">Entrar</button>
                </form>

                <a href="registrar.jsp" class="login-link">
                    ¿No tienes cuenta? <b>Regístrate aquí</b>
                </a>

            </div>
            
        </div>

    </body>
</html>
