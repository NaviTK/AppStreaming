package Servlets;

import Utils.DBConnection;
import java.io.IOException;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "Registrar", urlPatterns = {"/Registrar"})
public class Registrar extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Recogemos datos
        String usuario = request.getParameter("usuario");
        String pass1 = request.getParameter("pass1");
        String pass2 = request.getParameter("pass2");
        
        String error = null;

        try {
            // 2. VALIDACIÓN 1: ¿Coinciden las contraseñas?
            if (pass1 == null || !pass1.equals(pass2)) {
                error = "❌ Las contraseñas no coinciden.";
            } 
            // 3. VALIDACIÓN 2: ¿Campos vacíos?
            else if (usuario == null || usuario.trim().isEmpty() || pass1.isEmpty()) {
                error = "❌ Todos los campos son obligatorios.";
            }
            // 4. VALIDACIÓN 3: ¿El usuario ya existe en la BD?
            else if (DBConnection.existeUsuario(usuario)) {
                error = "⚠️ El usuario '" + usuario + "' ya está registrado. Elige otro.";
            }

            // --- TOMA DE DECISIONES ---
            
            if (error != null) {
                // SI HAY ERROR: Volvemos al formulario y mostramos el error
                request.setAttribute("error", error);
                // Truco: Devolvemos el nombre de usuario para que no tenga que escribirlo otra vez
                request.setAttribute("usuarioPrevio", usuario); 
                request.getRequestDispatcher("registrar.jsp").forward(request, response);
            } else {
                // SI TODO ESTÁ BIEN: Guardamos en la base de datos
                DBConnection.registrarUsuario(usuario, pass1);
                
                // Redirigimos al login con un mensaje de éxito (opcional pasarlo por URL)
                response.sendRedirect("login.jsp?registro=exito");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "🔥 Error de Base de Datos: " + e.getMessage());
            request.getRequestDispatcher("registrar.jsp").forward(request, response);
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
        // Si intentan entrar por URL directa, los mandamos al formulario
        response.sendRedirect("registrar.jsp");
    }
}