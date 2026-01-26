package Filters;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

// Este filtro vigila TODAS las rutas para proteger las sensibles
@WebFilter(filterName = "AdminFilter", urlPatterns = {"/*"}) 
public class AdminFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        
        // 1. Obtener la ruta que se intenta visitar
        String path = req.getRequestURI();
        
        // -----------------------------------------------------------
        // 2. LISTA NEGRA: ¿Qué archivos son SOLO para Admins?
        // -----------------------------------------------------------
        // Añade aquí cualquier JSP o Servlet que sea exclusivo del admin
        boolean esRutaProtegida = path.endsWith("admin.jsp") || 
                                  path.endsWith("SubirVideoServlet") || 
                                  path.endsWith("BorrarVideoServlet");

        if (esRutaProtegida) {
            
            // 3. Verificamos la sesión y el rol
            HttpSession session = req.getSession(false);
            String rol = (session != null) ? (String) session.getAttribute("rol") : null;
            
            // Si el rol NO es ADMIN (es nulo, o es "USER")...
            if (rol == null || !rol.equals("ADMIN")) {
                
                System.out.println("⛔ ACCESO DENEGADO: Intento de entrar a " + path);
                
                // Opción A: Mandarlo al menú (más amable)
                // res.sendRedirect("menu.jsp");
                
                // Opción B: Mandarlo al Logout (castigo, como pediste)
                res.sendRedirect("Logout"); 
                
                return; // 🛑 IMPORTANTE: Detenemos la ejecución aquí
            }
        }

        // 4. Si no es ruta protegida, o si es ADMIN, dejamos pasar
        chain.doFilter(request, response);
    }
}