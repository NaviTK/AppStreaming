package Servlets;

import Utils.GestionCarpeta;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// ⚠️ NOTA: Si has cambiado el nombre de la clase a "Eliminar", 
// asegúrate de que en eliminar.jsp el form diga action="Eliminar"
@WebServlet(name = "Eliminar", urlPatterns = {"/Eliminar"})
public class Eliminar extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Recibimos el nombre (ej: dash_matrix)
        String nombreCarpeta = request.getParameter("carpeta");
        
        // 2. Lógica de borrado (SOLO UNA VEZ)
        if (nombreCarpeta != null && !nombreCarpeta.trim().isEmpty()) {
            
            // Ejecutamos el borrado y guardamos el resultado
            boolean exito = GestionCarpeta.borrarVideo(nombreCarpeta);
            
            if (exito) {
                System.out.println("✅ Video eliminado correctamente: " + nombreCarpeta);
                // Redirigimos con mensaje de ÉXITO
                response.sendRedirect("AdminVideos?msg=borrado");
            } else {
                System.out.println("❌ Error: No se pudo eliminar (o ya no existe): " + nombreCarpeta);
                // Redirigimos con mensaje de ERROR
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
