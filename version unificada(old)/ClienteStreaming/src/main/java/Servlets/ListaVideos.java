/*
 * Servlet que lista las carpetas de videos disponibles
 * AHORA USA Utils.GestionCarpeta
 */
package Servlets;

import Utils.GestionCarpeta; // 👈 Importamos nuestra herramienta centralizada
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "ListaVideos", urlPatterns = {"/ListaVideos"})
public class ListaVideos extends HttpServlet {

    // YA NO NECESITAMOS DEFINIR LA RUTA AQUÍ (¡Adiós código duplicado!)
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Pedimos la lista a nuestra clase de utilidad
        // Si cambias la carpeta en GestionCarpeta, este Servlet se actualiza solo.
        List<String> videoFolders = GestionCarpeta.getListaVideos();

        // 2. Pasar la lista a la vista (menu.jsp)
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