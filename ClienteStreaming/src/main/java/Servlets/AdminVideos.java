package Servlets;

import Utils.ClienteAPI; // Importamos nuestra nueva herramienta
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "AdminVideos", urlPatterns = {"/AdminVideos"})
public class AdminVideos extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // -----------------------------------------------------------
        // 1. OBTENER DATOS (AHORA ES SÚPER SENCILLO)
        // -----------------------------------------------------------
        
        // A. Hacemos la llamada HTTP
        String jsonRespuesta = ClienteAPI.get("list"); 
        
        // B. Convertimos el JSON a Lista Java
        List<String> listaVideos = ClienteAPI.jsonArrayToList(jsonRespuesta);
        
        // -----------------------------------------------------------
        
        // 2. Pasar datos al JSP
        request.setAttribute("listaVideosAdmin", listaVideos);

        // 3. Gestionar mensajes de alerta (URL ?msg=...)
        String mensajeURL = request.getParameter("msg");
        if (mensajeURL != null) {
            request.setAttribute("mensajeAlerta", mensajeURL);
        }

        // 4. Renderizar
        request.getRequestDispatcher("admin.jsp").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}