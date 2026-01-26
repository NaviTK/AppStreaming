package Servlets;

// 1. Cambiamos GestionCarpeta por ClienteAPI
import Utils.ClienteAPI;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "Eliminar", urlPatterns = {"/Eliminar"})
public class Eliminar extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Recibimos el nombre (ej: dash_matrix)
        String nombreCarpeta = request.getParameter("carpeta");
        
        // 2. Lógica de borrado (AHORA VÍA API REST)
        if (nombreCarpeta != null && !nombreCarpeta.trim().isEmpty()) {
            
            // Llamamos a la API: DELETE /api/jakartaee9/delete/dash_matrix
            // ClienteAPI se encarga de montar la URL base
            boolean exito = ClienteAPI.delete("delete/" + nombreCarpeta);
            
            if (exito) {
                System.out.println("✅ API: Video eliminado correctamente: " + nombreCarpeta);
                response.sendRedirect("AdminVideos?msg=borrado");
            } else {
                System.out.println("❌ API: Error al eliminar (404 o 500): " + nombreCarpeta);
                response.sendRedirect("AdminVideos?msg=error");
            }
            
        } else {
            // Si no hay parámetro, volvemos sin mensaje
            response.sendRedirect("AdminVideos");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Por seguridad, si entran por GET los mandamos al admin sin hacer nada
        response.sendRedirect("AdminVideos");
    }
}
