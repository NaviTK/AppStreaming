package Servlets;

import Utils.GestionCarpeta;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

@WebServlet(name = "SubirVideo", urlPatterns = {"/SubirVideo"})
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,  // 2MB (Memoria antes de disco)
    maxFileSize = 1024 * 1024 * 500,      // 500MB Máximo por archivo
    maxRequestSize = 1024 * 1024 * 600    // 600MB Máximo por petición total
)
public class SubirVideo extends HttpServlet {

    // GET: Simplemente redirige al JSP del formulario
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("subirVideo.jsp").forward(request, response);
    }

    // POST: Recibe el archivo y procesa
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8"); // Para tildes en el nombre

        try {
            // 1. Recoger datos
            String nombreUsuario = request.getParameter("nombreVideo");
            Part filePart = request.getPart("videoFile");

            // Validar que hay datos
            if (nombreUsuario == null || nombreUsuario.trim().isEmpty() || filePart == null || filePart.getSize() <= 0) {
                response.sendRedirect("subirVideo.jsp?error=datos");
                return;
            }

            // 2. LLAMADA MÁGICA A GESTIONCARPETA
            // Aquí el servidor se quedará "pausado" esperando a que FFmpeg termine.
            // Mientras tanto, el usuario ve el spinner en el navegador.
            boolean exito = GestionCarpeta.procesarNuevoVideo(filePart, nombreUsuario);

            if (exito) {
                // Todo perfecto -> Vamos al panel de admin con mensaje verde
                response.sendRedirect("AdminVideos?msg=subido"); // Nota: Si no usas Servlet de Admin, pon "admin.jsp?msg=..."
            } else {
                // Falló algo (conversión o escritura)
                response.sendRedirect("subirVideo.jsp?error=conversion");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("subirVideo.jsp?error=interno");
        }
    }
}
