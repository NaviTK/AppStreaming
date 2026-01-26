/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Servlets;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author alumne
 */
// IMPORTANTE: He añadido "/*" al final del urlPattern. 
// Esto permite capturar rutas como: /ReproductorVideo/dash_matrix/manifiesto.mpd
@WebServlet(name = "ReproductorVideo", urlPatterns = {"/ReproductorVideo/*"})
public class ReproductorVideo extends HttpServlet {

    // -------------------------------------------------------------------------
    // ¡IMPORTANTE! Esta ruta debe ser EXACTAMENTE la misma que en VideoTranscoder.java
    // -------------------------------------------------------------------------
    // Si estás en Linux (PC escuela):
    private static final String VIDEO_ROOT = "/home/alumne/videos_dash";
    
    // Si estás en Windows (Descomenta esta y comenta la de arriba si cambias de PC):
    // private static final String VIDEO_ROOT = "C:/temp/videos_dash";

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Obtener la ruta del archivo solicitado (lo que va después de /ReproductorVideo/)
        String requestedFile = request.getPathInfo();

        // Si entran sin pedir archivo, error
        if (requestedFile == null || requestedFile.equals("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Falta el nombre del archivo");
            return;
        }

        // 2. Localizar el archivo en el disco duro (Usando la ruta absoluta compartida)
        File file = new File(VIDEO_ROOT, requestedFile);

        // Logs para depuración en la consola de Output de Tomcat (Apache Tomcat Log)
        System.out.println("📩 Petición Servlet: " + requestedFile);
        System.out.println("🔍 Ruta física: " + file.getAbsolutePath());

        if (file.exists() && !file.isDirectory()) {
            
            // 3. Configurar cabeceras CORS (Obligatorio para que funcione DASH en navegadores)
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Methods", "GET, HEAD, OPTIONS");

            // 4. Configurar el tipo de archivo (MIME Type) correcto
            String fileName = file.getName();
            if (fileName.endsWith(".mpd")) {
                response.setContentType("application/dash+xml");
            } else if (fileName.endsWith(".m4s")) {
                response.setContentType("video/iso.segment");
            } else if (fileName.endsWith(".mp4")) {
                response.setContentType("video/mp4");
            } else {
                response.setContentType("application/octet-stream");
            }

            // 5. Enviar el archivo al navegador
            response.setContentLengthLong(file.length());
            
            try (OutputStream out = response.getOutputStream()) {
                Files.copy(file.toPath(), out);
            }
            
        } else {
            // Si el archivo no existe
            System.err.println("❌ Archivo NO encontrado: " + file.getAbsolutePath());
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     */
    @Override
    public String getServletInfo() {
        return "Servlet para Streaming de Video DASH";
    }// </editor-fold>

}
