package Servlets;

import Utils.ClienteAPI; // ✅ Usamos el Cliente HTTP
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

@WebServlet(name = "SubirVideo", urlPatterns = {"/SubirVideo"})
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,  // 2MB
    maxFileSize = 1024 * 1024 * 500,      // 500MB
    maxRequestSize = 1024 * 1024 * 600    // 600MB
)
public class SubirVideo extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("subirVideo.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        
        File tempFile = null; // Referencia para poder borrarlo luego

        try {
            // 1. Recoger datos del formulario
            String nombreUsuario = request.getParameter("nombreVideo");
            Part filePart = request.getPart("videoFile");

            // Validar
            if (nombreUsuario == null || nombreUsuario.trim().isEmpty() || filePart == null || filePart.getSize() <= 0) {
                response.sendRedirect("subirVideo.jsp?error=datos");
                return;
            }

            // 2. CREAR UN ARCHIVO TEMPORAL (Puente)
            // Necesitamos convertir el 'Part' en un 'File' real para pasarlo al ClienteAPI.
            // createTempFile crea un archivo único en la carpeta temp del sistema operativo.
            tempFile = File.createTempFile("upload-bridge-", ".mp4");
            
            try (InputStream input = filePart.getInputStream()) {
                Files.copy(input, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }

            System.out.println("servlet: Archivo temporal creado en: " + tempFile.getAbsolutePath());

            // 3. LLAMADA A LA API (Con Timeout Dinámico)
            // Ahora el Servlet espera a que el ClienteAPI suba el archivo y el servidor lo procese.
            boolean exito = ClienteAPI.uploadVideo(tempFile, nombreUsuario);

            if (exito) {
                // Éxito: Redirigimos al admin
                response.sendRedirect("AdminVideos?msg=subido"); 
            } else {
                // Fallo: API devolvió error o Timeout
                response.sendRedirect("subirVideo.jsp?error=api_fail");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("subirVideo.jsp?error=interno");
        } finally {
            // 4. LIMPIEZA
            // Muy importante: Borrar el archivo temporal del servidor web para no llenar el disco
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
                System.out.println("servlet: Archivo temporal eliminado.");
            }
        }
    }
}
