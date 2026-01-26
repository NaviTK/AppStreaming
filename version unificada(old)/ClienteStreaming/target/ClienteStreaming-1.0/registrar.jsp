<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <link rel="stylesheet" type="text/css" href="css/estilo.css">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Registro - J-DASH Player</title>
    </head>
    <body>
        
        <div class="login-wrapper">
            
            <div class="login-card">
                <h1>📝 Registro</h1>
                <p>Crea tu cuenta nueva</p>

                <% 
                    String error = (String) request.getAttribute("error");
                    String usuarioPrevio = (String) request.getAttribute("usuarioPrevio");
                    if(usuarioPrevio == null) usuarioPrevio = ""; 
                    
                    if (error != null) { 
                %>
                    <div class="error-message">
                        <%= error %>
                    </div>
                <% } %>

                <form action="Registrar" method="POST" autocomplete="off">
    
                    <label>Nombre de Usuario</label>
                    <input type="text" name="usuario" value="<%= usuarioPrevio %>" required placeholder="Elige tu usuario" autocomplete="off">

                    <label>Contraseña</label>
                    <input type="password" name="pass1" required placeholder="Mínimo 4 caracteres" autocomplete="new-password">

                    <label>Repetir Contraseña</label>
                    <input type="password" name="pass2" required placeholder="Confirma tu contraseña" autocomplete="new-password">

                    <button type="submit" class="btn-login">Registrarse</button>
                </form>

                <a href="login.jsp" class="login-link">
                    ¿Ya tienes cuenta? <b>Inicia sesión</b>
                </a>

            </div>
        </div>

    </body>
</html>
