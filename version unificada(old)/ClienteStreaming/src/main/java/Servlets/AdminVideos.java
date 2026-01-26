package Servlets;

import Utils.GestionCarpeta;
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
        
        // 1. Obtener la lista de videos (Lógica principal)
        List<String> listaVideos = GestionCarpeta.getListaVideos();
        request.setAttribute("listaVideosAdmin", listaVideos);

        // 2. CAPTURAR MENSAJES DE LA URL (NUEVO CÓDIGO)
        // Si venimos de borrar, la URL traerá ?msg=borrado
        String mensajeURL = request.getParameter("msg");
        
        if (mensajeURL != null) {
            // Se lo pasamos al JSP como un atributo limpio
            request.setAttribute("mensajeAlerta", mensajeURL);
        }

        // 3. Enviar al JSP de administración
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