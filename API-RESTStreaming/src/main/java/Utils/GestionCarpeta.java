package Utils;

import java.io.File;
import java.io.IOException;            // 👈 CORRECCIÓN: Faltaba este import
import java.io.InputStream;            // 👈 CORRECCIÓN: Para guardar archivos de forma segura
import java.nio.file.Files;            // 👈 CORRECCIÓN: Para manejo moderno de archivos
import java.nio.file.Path;             // 👈 CORRECCIÓN: Para rutas
import java.nio.file.StandardCopyOption; // 👈 CORRECCIÓN: Para reemplazar si existe
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.http.Part;      // 👈 Asegúrate de tener la librería Jakarta EE

public class GestionCarpeta {

    // ⚠️ Asegúrate de que esta ruta sea la correcta en tu servidor
    // Debe tener permisos de lectura/escritura (chmod 777 o ser dueño el usuario tomcat)
    private static final String RUTA_BASE = "/home/alumne/videos_dash"; 

    // 📂 RUTA DONDE SE GUARDARÁ EL MP4 ANTES DE CONVERTIR
    public static final String RUTA_INPUT = "/home/alumne/videos_mp4"; 

    /**
     * Borra TODOS los archivos de la carpeta de input para evitar mezclas.
     */
    public static void vaciarCarpetaInput() {
        File directorio = new File(RUTA_INPUT);
        
        if (directorio.exists() && directorio.isDirectory()) {
            File[] archivos = directorio.listFiles();
            if (archivos != null) {
                for (File archivo : archivos) {
                    archivo.delete();
                }
            }
            System.out.println("🧹 Carpeta de input vaciada correctamente.");
        } else {
            // Si no existe, la creamos para evitar errores futuros
            directorio.mkdirs();
        }
    }

    /**
     * Guarda el archivo MP4 que viene del formulario en la carpeta de input.
     * @param filePart El objeto Part que viene del Servlet (request.getPart)
     * @param nombreArchivo El nombre con el que queremos guardar el archivo (ej: video.mp4)
     * @return La ruta absoluta del archivo guardado.
     * @throws IOException Si falla la escritura en disco.
     */
    public static String guardarVideoEnInput(Part filePart, String nombreArchivo) throws IOException {
        // Aseguramos que la carpeta exista antes de guardar
        File carpetaInput = new File(RUTA_INPUT);
        if (!carpetaInput.exists()) {
            carpetaInput.mkdirs();
        }

        // Construimos la ruta destino
        // Usamos Path.of para que sea compatible con el sistema operativo (Linux/Windows)
        Path rutaDestino = Path.of(RUTA_INPUT, nombreArchivo);
        
        // CORRECCIÓN IMPORTANTE:
        // Usamos input stream en lugar de filePart.write() porque write() a veces falla
        // con rutas absolutas dependiendo de la configuración de Tomcat.
        try (InputStream input = filePart.getInputStream()) {
            Files.copy(input, rutaDestino, StandardCopyOption.REPLACE_EXISTING);
        }
        
        return rutaDestino.toString();
    }

    /**
     * Obtiene la lista de carpetas de videos disponibles.
     */
    public static List<String> getListaVideos() {
        List<String> lista = new ArrayList<>();
        File carpeta = new File(RUTA_BASE);

        if (carpeta.exists() && carpeta.isDirectory()) {
            File[] archivos = carpeta.listFiles();
            if (archivos != null) {
                for (File archivo : archivos) {
                    // Filtramos solo directorios que empiecen por "dash_"
                    if (archivo.isDirectory() && archivo.getName().startsWith("dash_")) {
                        lista.add(archivo.getName());
                    }
                }
            }
        }
        return lista;
    }

    /**
     * Borra un video (la carpeta y todo su contenido).
     * @param nombreCarpeta El nombre de la carpeta (ej: "dash_matrix")
     * @return true si se borró correctamente, false si falló.
     */
    public static boolean borrarVideo(String nombreCarpeta) {
        if (nombreCarpeta == null || nombreCarpeta.trim().isEmpty()) {
            return false;
        }

        // Construimos la ruta completa
        File carpetaABorrar = new File(RUTA_BASE + File.separator + nombreCarpeta);

        // Por seguridad, verificamos que existe antes de intentar nada
        if (!carpetaABorrar.exists()) {
            return false;
        }

        // Llamamos al método recursivo
        return borrarRecursivamente(carpetaABorrar);
    }
    
    /**
     * Renombra una carpeta de video.
     * @param carpetaActual El nombre actual de la carpeta (ej: "dash_matrix")
     * @param nuevoNombreUsuario El nuevo nombre que escribió el usuario (ej: "Matrix Reloaded")
     * @return true si se cambió con éxito, false si falló o ya existe.
     */
    public static boolean renombrarVideo(String carpetaActual, String nuevoNombreUsuario) {
        
        // 1. Validaciones básicas
        if (carpetaActual == null || nuevoNombreUsuario == null || nuevoNombreUsuario.trim().isEmpty()) {
            return false;
        }

        // 2. Formatear el nuevo nombre para que cumpla el estándar del sistema
        // Ejemplo: "Matrix Reloaded" -> "matrix_reloaded"
        String nombreLimpio = nuevoNombreUsuario.trim().toLowerCase().replaceAll("\\s+", "_");
        
        // Aseguramos que empiece por "dash_"
        String carpetaNuevaNombre;
        if (nombreLimpio.startsWith("dash_")) {
            carpetaNuevaNombre = nombreLimpio;
        } else {
            carpetaNuevaNombre = "dash_" + nombreLimpio;
        }

        // 3. Preparar los objetos File
        File carpetaVieja = new File(RUTA_BASE + File.separator + carpetaActual);
        File carpetaNueva = new File(RUTA_BASE + File.separator + carpetaNuevaNombre);

        // 4. Comprobaciones de seguridad
        if (!carpetaVieja.exists()) {
            System.out.println("❌ Error: La carpeta original no existe.");
            return false;
        }
        
        if (carpetaNueva.exists()) {
            System.out.println("❌ Error: Ya existe un video con ese nombre.");
            return false;
        }

        // 5. Intentar renombrar
        return carpetaVieja.renameTo(carpetaNueva);
    }

    // ==========================================
    // 🛠️ MÉTODO PRIVADO (HELPER)
    // ==========================================
    private static boolean borrarRecursivamente(File archivo) {
        // Si es un directorio, primero vaciamos su contenido
        if (archivo.isDirectory()) {
            File[] contenidos = archivo.listFiles();
            if (contenidos != null) {
                for (File f : contenidos) {
                    // Llamada recursiva: si falla borrar un hijo, fallamos todo
                    if (!borrarRecursivamente(f)) {
                        return false; 
                    }
                }
            }
        }
        // Finalmente borramos el archivo o la carpeta (ya vacía)
        return archivo.delete();
    }
    
    /**
     * 🚀 MÉTODO MAESTRO: Coordina todo el proceso de subida.
     * 1. Limpia la carpeta temporal.
     * 2. Guarda el MP4 subido.
     * 3. Llama al Conversor para generar el DASH.
     * * @param filePart El archivo que llega del formulario.
     * @param nombreUsuario El nombre "bonito" que puso el usuario (ej: Matrix 1).
     * @return true si todo el proceso (subida + conversión) salió bien.
     */
    public static boolean procesarNuevoVideo(jakarta.servlet.http.Part filePart, String nombreUsuario) {
        try {
            // 1. Limpieza inicial
            vaciarCarpetaInput();

            // 2. Preparar nombres
            // Convertimos "Matrix Reloaded" -> "dash_matrix_reloaded"
            String nombreLimpio = nombreUsuario.trim().toLowerCase().replaceAll("\\s+", "_");
            String nombreCarpetaFinal = "dash_" + nombreLimpio;
            
            // Nombre temporal para el archivo físico (da igual cuál sea, se va a borrar luego)
            String nombreArchivoTemp = "video_temp.mp4";

            // 3. Guardar el archivo físico
            String rutaMp4 = guardarVideoEnInput(filePart, nombreArchivoTemp);
            System.out.println("💾 Video guardado temporalmente en: " + rutaMp4);

            // 4. Iniciar la conversión (Esto tardará unos segundos/minutos)
            // Llamamos a la clase Conversor que creamos antes
            boolean exitoConversion = Conversor.ejecutarScriptConversion(rutaMp4, nombreCarpetaFinal);
            
            return exitoConversion;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
