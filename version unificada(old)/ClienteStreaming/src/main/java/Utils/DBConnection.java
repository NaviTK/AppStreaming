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

    // 1. Definimos las constantes arriba para que sea fácil cambiarlas si mueves la BD
    private static final String URL = "jdbc:derby://localhost:1527/StreamingAppData";
    private static final String USER = "app";
    private static final String PASSWORD = "app";
    private static final String DRIVER = "org.apache.derby.jdbc.ClientDriver";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            throw new SQLException("No se encontró el driver de Derby. ¿Has añadido el JAR a librerías?", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    
    // HE AÑADIDO 'static' AQUÍ
    public static boolean checkLogin(String username, String password) throws SQLException {
        
        String sql = "SELECT 1 FROM usuarios WHERE id_usuario=? AND password=?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                // Si rs.next() es true, el usuario y contraseña coinciden
                return rs.next(); 
            }
        } 
        // No hace falta capturar SQLException aquí si solo la vas a relanzar,
        // el 'throws SQLException' del método ya se encarga.
    }
    
    // 1. Método para comprobar si un usuario YA existe (sin comprobar contraseña)
    public static boolean existeUsuario(String username) throws SQLException {
        String sql = "SELECT 1 FROM usuarios WHERE id_usuario=?";
        
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // True si ya existe
            }
        }
    }

    // 2. Método para INSERTAR un nuevo usuario
    public static void registrarUsuario(String username, String password) throws SQLException {
        String sql = "INSERT INTO usuarios (id_usuario, password) VALUES (?, ?)";
        
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.executeUpdate(); // Ejecutar la inserción
        }
    }
    
    public boolean esAdmin(String usuario) {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        boolean esAdmin = false; // Por defecto: NO es admin (Seguridad ante todo)

        try {
            // Asumo que tienes un método conectar() o getConnection() en esta clase
            // Si tu método se llama distinto, cámbialo aquí.
            con = this.getConnection(); 

            String sql = "SELECT * FROM usuarios WHERE id_usuario = ?";
            pst = con.prepareStatement(sql);
            pst.setString(1, usuario);

            rs = pst.executeQuery();

            if (rs.next()) {
                // Recuperamos el valor booleano de la columna (1=true, 0=false)
                esAdmin = rs.getBoolean("es_admin");
            }

        } catch (Exception e) {
            System.out.println("Error verificando admin: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Cerramos recursos para no saturar la BD
            try {
                if (rs != null) rs.close();
                if (pst != null) pst.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return esAdmin;
    }
}
