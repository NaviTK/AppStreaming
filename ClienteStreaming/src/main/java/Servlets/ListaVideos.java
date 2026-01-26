package Servlets;

import Utils.ClienteAPI; // 👈 Ahora usamos el cliente HTTP
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "ListaVideos", urlPatterns = {"/ListaVideos"})
public class ListaVideos extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // -----------------------------------------------------------
        // 1. OBTENER DATOS VÍA API REST
        // -----------------------------------------------------------
        
        // A. Hacemos la llamada GET a /list
        String jsonRespuesta = ClienteAPI.get("list");
        
        // B. Convertimos el JSON recibido a una Lista de Java
        // (Usando el parser mejorado que hicimos antes)
        List<String> videoFolders = ClienteAPI.jsonArrayToList(jsonRespuesta);

        // -----------------------------------------------------------

        // 2. Pasar la lista a la vista (menu.jsp)
        // El JSP no sabe si los datos vienen del disco o de una API en China,
        // solo le importa recibir su atributo "listaVideos".
        request.setAttribute("listaVideos", videoFolders);
        
        // 3. Redirigir internamente al JSP
        request.getRequestDispatcher("menu.jsp").forward(request, response);
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