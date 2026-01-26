/*
 * Servlet Login Refactorizado
 * Delega la validación HTTP a la clase Utils.ClienteAPI
 */
package Servlets;

import Utils.ClienteAPI;   // ✅ Importamos nuestra nueva clase cliente
//import Utils.DBConnection; // ✅ Seguimos usando esto para el ROL (temporalmente)
import java.io.IOException;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "Login", urlPatterns = {"/Login"})
public class Login extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String usuario = request.getParameter("usuario");
        String pass = request.getParameter("pass");

        // Calculamos la URL base dinámicamente para pasársela al cliente
        // Ejemplo resultado: http://localhost:8080/TuProyecto
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();

        System.out.println("🔍 Servlet: Solicitando validación a ClienteAPI...");

        // -----------------------------------------------------------
        // 1. LLAMADA LIMPIA A LA CLASE HELPER
        // -----------------------------------------------------------
        int resultado = ClienteAPI.checkLogin(usuario, pass);

        if (resultado != -1) {
        // LOGIN CORRECTO
        HttpSession session = request.getSession();
        session.setAttribute("usuario", usuario);
        
        if (resultado == 1) {
            session.setAttribute("rol", "admin"); // Guardamos el rol
            response.sendRedirect("ListaVideos"); // Redirigir a panel admin
        } else {
            session.setAttribute("rol", "user");
            response.sendRedirect("ListaVideos"); // Redirigir a web normal
        }
        } else {
            // -------------------------------------------------------
            // 3. MANEJO DE ERROR
            // -------------------------------------------------------
            request.setAttribute("error", "❌ Usuario o contraseña incorrectos");
            request.getRequestDispatcher("login.jsp").forward(request, response);
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
        response.sendRedirect("login.jsp");
    }
}