<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>ErrorPage</title>
    <link rel="stylesheet" href="css/estilo.css">
</head>
<body>
    <% // Tienes que poner Siempre:
      //req.setAttribute("mensaje", "Sesion No Iniciada");
      //req.setAttribute("destino", "login");
      //req.getRequestDispatcher("error.jsp").forward(req, res);
    %>
    <h1>Ups...</h1>

    <p>
        <%= "Error: " + request.getAttribute("mensaje") %>
    </p>

    <%
        // Obtener parámetro que controla hacia dónde volver
        String destino = (String) request.getAttribute("destino");
        String textoBoton;
        String link;

        // Decidir texto y enlace según parámetro
        if ("menu".equals(destino)) {
            textoBoton = "Volver al menú ➣";
            link = "menu.jsp";
        } else {
            // Valor por defecto
            textoBoton = "Volver al login ➣";
            link = "login.jsp";
        }
    %>

    <li><a href="<%= link %>"><%= textoBoton %></a></li>
</body>
</html>
