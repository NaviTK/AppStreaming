package Servlets;

import Utils.GestionCarpeta;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "Modificar", urlPatterns = {"/Modificar"})
public class Modificar extends HttpServlet {

    // ==========================================
    // MÉTODO GET: MUESTRA EL FORMULARIO
    // ==========================================
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Recogemos el parámetro de la URL (admin.jsp -> modificar.jsp?v=dash_matrix)
        String carpeta = request.getParameter("v");

        // 2. Validación: Si no hay carpeta, volvemos al admin
        if (carpeta == null || carpeta.trim().isEmpty()) {
            response.sendRedirect("AdminVideos");
            return;
        }

        // 3. Enviamos el dato al JSP para que rellene el formulario
        request.setAttribute("carpetaParaEditar", carpeta);
        
        // 4. Mostramos la pantalla de edición
        request.getRequestDispatcher("modificar.jsp").forward(request, response);
    }

    // ==========================================
    // MÉTODO POST: PROCESA EL CAMBIO
    // ==========================================
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Recogemos los datos del formulario (modificar.jsp)
        String nombreOriginal = request.getParameter("nombreOriginal");
        String nuevoNombre = request.getParameter("nuevoNombre");

        // 2. Validamos que lleguen datos
        if (nombreOriginal != null && nuevoNombre != null) {
            
            // 3. Llamamos a nuestra clase Utils para hacer el trabajo duro
            boolean exito = GestionCarpeta.renombrarVideo(nombreOriginal, nuevoNombre);

            if (exito) {
                System.out.println("✅ Video renombrado: " + nombreOriginal + " -> " + nuevoNombre);
                // Redirigimos con mensaje de ÉXITO
                response.sendRedirect("AdminVideos?msg=modificado");
            } else {
                System.out.println("❌ Fallo al renombrar: " + nombreOriginal);
                // Redirigimos con mensaje de ERROR
                response.sendRedirect("AdminVideos?msg=errorModificar");
            }
            
        } else {
            // Datos incompletos
            response.sendRedirect("AdminVideos");
        }
    }
}
