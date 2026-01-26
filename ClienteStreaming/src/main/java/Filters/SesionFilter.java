/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Filter.java to edit this template
 */
package Filters;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession; // ← faltaba este import
import java.io.IOException;

@WebFilter("/*") // Aplica a todas las rutas
public class SesionFilter implements Filter { // ← usa mayúscula inicial (convención Java)

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // Obtener la sesión si existe, sin crear una nueva
        HttpSession session = req.getSession(false);
        String usuario = null;
        if (session != null) usuario = (String) session.getAttribute("usuario");
        String path = req.getRequestURI();

        // Permitir libre acceso a login, index y recursos estáticos
        boolean esLogin = path.endsWith("login.jsp") || path.endsWith("index.jsp") || 
                path.endsWith("Login") || path.endsWith("ClienteStreaming/") ||
                path.endsWith("registrar.jsp") || path.endsWith("Registrar");
        boolean esRecursos = path.contains("/css/") || path.contains("/js/");

        if (usuario == null && !esLogin && !esRecursos) {
            // No hay sesión activa, redirigir al error.jsp
            req.setAttribute("mensaje", "Sesion No Iniciada");
            req.setAttribute("destino", "login");
            req.getRequestDispatcher("error.jsp").forward(req, res);
            return;
        }

        // Si la sesión es válida, continuar normalmente
        chain.doFilter(request, response);
    }
}