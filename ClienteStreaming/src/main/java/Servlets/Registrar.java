package Servlets;

import Utils.ClienteAPI; // Importamos tu cliente HTTP
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "Registrar", urlPatterns = {"/Registrar"})
public class Registrar extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Recogemos datos del formulario
        String usuario = request.getParameter("usuario");
        String pass1 = request.getParameter("pass1");
        String pass2 = request.getParameter("pass2");
        
        String error = null;

        // 2. VALIDACIONES LOCALES (Lo que podemos comprobar sin llamar al servidor)
        // Es mejor validar esto antes para ahorrar una petición HTTP si los datos están mal.
        
        if (pass1 == null || !pass1.equals(pass2)) {
            error = "❌ Las contraseñas no coinciden.";
        } 
        else if (usuario == null || usuario.trim().isEmpty() || pass1.isEmpty()) {
            error = "❌ Todos los campos son obligatorios.";
        } 
        else {
            // 3. LLAMADA A LA API (Sustituye a DBConnection)
            // Intentamos registrar directamente. La API devolverá false si el usuario ya existe (409) 
            // o si hay error (500).
            
            boolean registroExitoso = ClienteAPI.register(usuario, pass1);

            if (registroExitoso) {
                // --- ÉXITO ---
                // Redirigimos al login
                response.sendRedirect("login.jsp?registro=exito");
                return; // Importante hacer return para que no siga ejecutando código abajo
            } else {
                // --- FALLO ---
                // Si false, es muy probable que el usuario ya exista (según tu API devuelve 409)
                error = "⚠️ No se pudo registrar. Es posible que el usuario '" + usuario + "' ya exista o el servidor no responda.";
            }
        }

        // --- MANEJO DE ERRORES ---
        // Si llegamos aquí, es que hubo un error (error != null)
        request.setAttribute("error", error);
        request.setAttribute("usuarioPrevio", usuario); // Para no borrar lo que escribió
        request.getRequestDispatcher("registrar.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Si intentan entrar por GET, mostramos el formulario
        response.sendRedirect("registrar.jsp");
    }
}