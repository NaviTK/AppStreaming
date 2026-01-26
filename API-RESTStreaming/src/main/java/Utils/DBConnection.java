/*
 * DBConnection.java
 */
package Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBConnection {

    // Constantes de conexión
    private static final String URL = "jdbc:derby://localhost:1527/StreamingAppData";
    private static final String USER = "app";
    private static final String PASSWORD = "app";
    private static final String DRIVER = "org.apache.derby.jdbc.ClientDriver";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            throw new SQLException("No se encontró el driver de Derby.", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    
    // Método para Login
    public static boolean checkLogin(String username, String password) throws SQLException {
        String sql = "SELECT 1 FROM usuarios WHERE id_usuario=? AND password=?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); 
            }
        } 
    }
    
    // Método para comprobar si existe usuario
    public static boolean existeUsuario(String username) throws SQLException {
        String sql = "SELECT 1 FROM usuarios WHERE id_usuario=?";
        
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // Método para registrar usuario
    public static void registrarUsuario(String username, String password) throws SQLException {
        // ⚠️ IMPORTANTE: Asegúrate de que tu tabla usuarios tiene una columna para 'es_admin'
        // Si la columna no acepta nulos, deberías añadirla aquí, por ejemplo:
        // "INSERT INTO usuarios (id_usuario, password, es_admin) VALUES (?, ?, false)"
        String sql = "INSERT INTO usuarios (id_usuario, password) VALUES (?, ?)";
        
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.executeUpdate();
        }
    }
    
    // Método para verificar Rol de Admin (CORREGIDO)
    public static boolean esAdmin(String usuario) throws SQLException {
        // ⚠️ REVISA: ¿Tu columna en Derby se llama 'es_admin', 'is_admin' o 'isAdmin'?
        // Asegúrate de que coincida exactamente con la base de datos.
        String sql = "SELECT es_admin FROM usuarios WHERE id_usuario = ?";
        
        try (Connection con = getConnection(); // ✅ Corregido: Ya no usa 'this'
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, usuario);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Devuelve true si la columna es 1 o true.
                    // Si la columna es nula, devolverá false.
                    return rs.getBoolean("es_admin");
                }
            }
        }
        return false; // Si no encuentra al usuario o falla algo
    }
}
