package Servlets;

import Utils.ClienteAPI; // ✅ Ahora usamos el Cliente HTTP
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

        // 2. Validación: Si no hay carpeta seleccionada, volvemos al listado
        if (carpeta == null || carpeta.trim().isEmpty()) {
            response.sendRedirect("AdminVideos");
            return;
        }

        // 3. Enviamos el dato al JSP para que rellene el campo "Nombre Original"
        request.setAttribute("carpetaParaEditar", carpeta);
        
        // 4. Mostramos la pantalla de edición
        request.getRequestDispatcher("modificar.jsp").forward(request, response);
    }

    // ==========================================
    // MÉTODO POST: PROCESA EL CAMBIO MEDIANTE API
    // ==========================================
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Recogemos los datos del formulario (modificar.jsp)
        String nombreOriginal = request.getParameter("nombreOriginal");
        String nuevoNombre = request.getParameter("nuevoNombre");

        // 2. Validamos que lleguen datos y no estén vacíos
        if (nombreOriginal != null && nuevoNombre != null && !nuevoNombre.trim().isEmpty()) {
            
            // 3. ⚠️ CAMBIO CLAVE: Llamamos al ClienteAPI
            // Esto envía una petición PUT a tu servidor Jakarta EE
            boolean exito = ClienteAPI.renombrarVideo(nombreOriginal, nuevoNombre);

            if (exito) {
                System.out.println("✅ Servlet: Video renombrado vía API correctamente.");
                // Redirigimos al admin con mensaje de ÉXITO
                response.sendRedirect("AdminVideos?msg=modificado");
            } else {
                System.out.println("❌ Servlet: La API devolvió error al renombrar.");
                // Redirigimos al admin con mensaje de ERROR
                response.sendRedirect("AdminVideos?msg=errorModificar");
            }
            
        } else {
            // Datos incompletos o vacíos
            response.sendRedirect("AdminVideos?msg=camposVacios");
        }
    }
}
