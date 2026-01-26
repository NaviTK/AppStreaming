package Utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration; // ✅ Importante para el timeout
import java.util.ArrayList;
import java.util.List;

public class ClienteAPI {

    // ⚠️ AJUSTA ESTO: Asegúrate de que coincida con tu URL base real
    private static final String BASE_URL = "http://localhost:8080/API-RESTStreaming/resources/jakartaee9/";
    
    // Instancia única del cliente (es mejor reutilizarla para gestionar conexiones)
    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10)) // Timeout de conexión inicial
            .build();

    /**
     * Realiza una petición GET y devuelve el cuerpo como String (JSON)
     */
    public static String get(String endpoint) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + endpoint))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                return response.body();
            } else {
                System.out.println("❌ Error GET " + endpoint + ": Código " + response.statusCode());
                return "[]"; // Retornar array vacío en caso de error
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "[]";
        }
    }

    /**
     * Realiza una petición DELETE
     */
    public static boolean delete(String endpoint) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + endpoint))
                    .DELETE()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Realiza una petición POST con archivo (Generic Stream)
     */
    public static boolean postStream(String endpoint, InputStream fileStream) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + endpoint))
                    .header("Content-Type", "application/octet-stream")
                    .POST(BodyPublishers.ofInputStream(() -> fileStream))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Utilidad para convertir JSON String ["a","b"] a List<String>
     * VERSIÓN MEJORADA: Soporta comas dentro de los nombres.
     */
    public static List<String> jsonArrayToList(String json) {
        List<String> lista = new ArrayList<>();
        if (json == null || json.length() <= 2) return lista;

        // 1. Quitar corchetes [ y ]
        String contenido = json.substring(1, json.length() - 1); 

        // 2. Separar por comas (PERO IGNORANDO LAS QUE ESTÁN ENTRE COMILLAS)
        String[] partes = contenido.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

        for (String p : partes) {
            // 3. Limpieza: Quitamos comillas y espacios de los extremos
            String limpio = p.trim();
            if (limpio.startsWith("\"") && limpio.endsWith("\"")) {
                limpio = limpio.substring(1, limpio.length() - 1);
            }
            
            if (!limpio.isEmpty()) {
                lista.add(limpio);
            }
        }
        return lista;
    }
    
    /**
     * Verifica credenciales enviando datos como FORMULARIO (x-www-form-urlencoded)
     * Compatible con: @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
     */
    /**
     * Verifica credenciales y rol.
     * Retorna:
     * -1 = Error / Login Incorrecto
     * 0 = Usuario Normal
     * 1 = Administrador
     */
    public static int checkLogin(String username, String password) {
        try {
            String endpoint = "login"; 
            String formData = "user=" + username + "&pass=" + password;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + endpoint))
                    .header("Content-Type", "application/x-www-form-urlencoded") 
                    .POST(BodyPublishers.ofString(formData))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String json = response.body();
                System.out.println("✅ Login respuesta: " + json);

                // BÚSQUEDA MANUAL EN EL JSON (Sin librerías externas)
                // Buscamos si el texto contiene "isAdmin": true
                // Nota: Esto es un parche rápido. Lo ideal sería usar Jackson o GSON.
                if (json.contains("\"isAdmin\": true") || json.contains("\"isAdmin\":true")) {
                    return 1; // ES ADMIN
                } else {
                    return 0; // ES USUARIO NORMAL
                }
            } else {
                return -1; // FALLO
            }

        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
    
    /**
     * Registra un nuevo usuario.
     * Endpoint: POST /register
     */
    public static boolean register(String username, String password) {
        try {
            String endpoint = "register"; 
            
            String formData = "user=" + username + "&pass=" + password;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + endpoint))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(BodyPublishers.ofString(formData))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println("✅ Registro exitoso: " + response.body());
                return true;
            } else if (response.statusCode() == 409) {
                System.out.println("⚠️ El usuario ya existe (409): " + response.body());
                return false;
            } else {
                System.out.println("❌ Error en registro (Código " + response.statusCode() + "): " + response.body());
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Renombra un video existente.
     * Endpoint: PUT /rename?oldName=...&newName=...
     */
    public static boolean renombrarVideo(String nombreActual, String nuevoNombre) {
        try {
            String oldEncoded = URLEncoder.encode(nombreActual, StandardCharsets.UTF_8);
            String newEncoded = URLEncoder.encode(nuevoNombre, StandardCharsets.UTF_8);

            String endpoint = "rename?oldName=" + oldEncoded + "&newName=" + newEncoded;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + endpoint))
                    .header("Content-Type", "application/json")
                    .PUT(BodyPublishers.noBody()) 
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println("✅ Video renombrado correctamente: " + response.body());
                return true;
            } else {
                System.out.println("❌ Fallo al renombrar (Código " + response.statusCode() + "): " + response.body());
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Sube un video con TIMEOUT ADAPTABLE.
     * Calcula el tiempo de espera según el tamaño del archivo.
     */
    public static boolean uploadVideo(File archivoVideo, String nombreVideo) {
        try {
            // 1. Validaciones previas
            if (!archivoVideo.exists()) {
                System.out.println("❌ Error: Archivo no existe.");
                return false;
            }

            // 2. CÁLCULO DEL TIMEOUT DINÁMICO
            long sizeBytes = archivoVideo.length();
            long sizeMB = sizeBytes / (1024 * 1024); // Convertir bytes a MB
            
            // Regla: 1 minuto base + 1 minuto por cada 10 MB de peso
            long minutosTimeout = 1 + (sizeMB / 10);
            
            // Tope de seguridad mínimo de 2 minutos
            if (minutosTimeout < 2) minutosTimeout = 2;

            System.out.println("⚖️ Tamaño del video: " + sizeMB + " MB");
            System.out.println("⏱️ Timeout calculado: " + minutosTimeout + " minutos.");

            // 3. Preparar URL
            String nombreCodificado = URLEncoder.encode(nombreVideo, StandardCharsets.UTF_8);
            String endpoint = "upload?nombreVideo=" + nombreCodificado;

            // 4. Construir petición con el timeout calculado
            System.out.println("⏳ Enviando y procesando...");
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + endpoint))
                    .header("Content-Type", "application/octet-stream")
                    .timeout(Duration.ofMinutes(minutosTimeout)) // ✅ TIMEOUT
                    .POST(BodyPublishers.ofFile(archivoVideo.toPath()))
                    .build();

            // 5. Enviar
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println("✅ Éxito: " + response.body());
                return true;
            } else {
                System.out.println("❌ Error (" + response.statusCode() + "): " + response.body());
                return false;
            }

        } catch (java.net.http.HttpTimeoutException e) {
            System.out.println("⏰ TARDÓ DEMASIADO: El servidor sigue trabajando, pero el cliente se cansó de esperar.");
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}