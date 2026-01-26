/*
 * Servlet encargado de validar el usuario y asignar ROLES (Admin/User).
 */
package Servlets;

import Utils.DBConnection; 
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
        
        System.out.println("🔍 Intentando login con usuario: " + usuario);

        try {
            // 1. VALIDACIÓN DE CREDENCIALES
            if (DBConnection.checkLogin(usuario, pass)) {

                // -----------------------------------------------------------
                // 2. ÉXITO: CREAR LA SESIÓN Y DEFINIR ROL
                // -----------------------------------------------------------
                HttpSession session = request.getSession();
                session.setAttribute("usuario", usuario); 
                
                // --- NUEVO BLOQUE: VERIFICAR SI ES ADMIN ---
                DBConnection db = new DBConnection(); // Instanciamos para usar la función esAdmin
                boolean esJefe = db.esAdmin(usuario); // Llamamos a la función que creamos antes
                
                if (esJefe) {
                    session.setAttribute("rol", "ADMIN"); // <--- IMPORTANTE
                    System.out.println("👑 El usuario " + usuario + " ha entrado como ADMINISTRADOR");
                } else {
                    session.setAttribute("rol", "USER");
                    System.out.println("👤 El usuario " + usuario + " ha entrado como USUARIO NORMAL");
                }
                // -------------------------------------------

                session.setMaxInactiveInterval(30 * 60); // 30 mins
                
                // 3. REDIRECCIÓN
                response.sendRedirect("ListaVideos");

            } else {
                // FALLO
                System.out.println("❌ Login fallido: Contraseña incorrecta.");
                request.setAttribute("error", "❌ Usuario o contraseña incorrectos");
                request.getRequestDispatcher("login.jsp").forward(request, response);
            }

        } catch (SQLException e) {
            // ERROR BD
            e.printStackTrace();
            request.setAttribute("error", "⚠️ Error técnico: No se pudo conectar a la base de datos.");
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
