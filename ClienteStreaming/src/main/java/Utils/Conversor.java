/*
 * Conversor.java
 * Esta clase se utiliza DESDE EL SERVLET para convertir UN SOLO video
 * que acaba de subir el usuario.
 */
package Utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class Conversor {

    // 🛑 IMPORTANTE: Asegúrate de que esta ruta es la misma que usa tu servidor para LEER los videos
    // Debe coincidir con la OUTPUT_ROOT de tu otro archivo si quieres que vayan al mismo sitio.
    private static final String OUTPUT_ROOT = "/home/alumne/videos_dash";

    /**
     * Método estático llamado desde el Servlet SubirVideo.
     * @param rutaMp4 La ruta absoluta del archivo .mp4 temporal (ej: /tmp/video_temp.mp4)
     * @param nombreCarpetaSalida El nombre de la carpeta destino (ej: dash_matrix)
     * @return true si la conversión fue exitosa
     */
    public static boolean ejecutarScriptConversion(String rutaMp4, String nombreCarpetaSalida) {
        
        System.out.println("🚀 Iniciando conversión WEB para: " + nombreCarpetaSalida);

        try {
            // 1. Preparar rutas
            Path finalOutputDir = Path.of(OUTPUT_ROOT, nombreCarpetaSalida);

            // Crear carpeta si no existe
            if (!Files.exists(finalOutputDir)) {
                Files.createDirectories(finalOutputDir);
            }

            // 2. Ejecutar FFmpeg
            // Llamamos a la función privada que tiene toda tu configuración "Anti-Desync"
            generateDash(rutaMp4, finalOutputDir.toString());
            
            System.out.println("✅ Conversión finalizada con éxito.");
            return true;

        } catch (Exception e) {
            System.err.println("❌ Error crítico en la conversión: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // --------------------------------------------------------------------------------
    // TU LÓGICA DE FFMPEG EXACTA (COPIADA DE VIDEOTRANSCODER)
    // --------------------------------------------------------------------------------
    private static void generateDash(String inputPath, String outputDir) throws IOException, InterruptedException {
        var command = new ArrayList<String>();
        command.add("ffmpeg");
        command.add("-y");
        command.add("-i"); 
        command.add(inputPath);

        // --- 1. AJUSTES DEL CÓDEC DE VIDEO ---
        command.add("-c:v"); command.add("libx264");
        
        // Preset fast para equilibrio velocidad/calidad
        command.add("-preset"); command.add("fast"); 
        command.add("-profile:v"); command.add("main");
        
        // [CRÍTICO] Sincronización por TIEMPO (Anti-Desync)
        
        // 1. Desactivar detección de escenas
        command.add("-sc_threshold"); command.add("0");
        
        // 2. Obligar a poner un Keyframe EXACTAMENTE cada 4 segundos
        command.add("-force_key_frames"); command.add("expr:gte(t,n_forced*4)");
        
        // 3. Cola de muxing segura
        command.add("-max_muxing_queue_size"); command.add("4096");

        // --- Stream 0: 1080p ---
        command.add("-map"); command.add("0:v");
        command.add("-b:v:0"); command.add("3000k");
        command.add("-s:v:0"); command.add("1920x1080");

        // --- Stream 1: 720p ---
        command.add("-map"); command.add("0:v");
        command.add("-b:v:1"); command.add("1500k");
        command.add("-s:v:1"); command.add("1280x720");

        // --- Stream 2: 480p ---
        command.add("-map"); command.add("0:v");
        command.add("-b:v:2"); command.add("800k");
        command.add("-s:v:2"); command.add("854x480");

        // --- Stream 3: 360p ---
        command.add("-map"); command.add("0:v");
        command.add("-b:v:3"); command.add("400k");
        command.add("-s:v:3"); command.add("640x360");

        // --- Audio ---
        command.add("-map"); command.add("0:a");
        command.add("-c:a"); command.add("aac");
        command.add("-b:a"); command.add("128k");
        command.add("-ac"); command.add("2"); // Forzar estéreo
        command.add("-ar"); command.add("44100"); // Estandarizar frecuencia
        
        // --- Configuración DASH ---
        command.add("-f"); command.add("dash");
        
        // Sincronización de segmentos
        command.add("-seg_duration"); command.add("4");
        command.add("-use_template"); command.add("1");
        command.add("-use_timeline"); command.add("1");
        command.add("-streaming"); command.add("1"); 
        
        command.add("-adaptation_sets"); command.add("id=0,streams=v id=1,streams=a");
        
        // Salida
        command.add(outputDir + "/manifiesto.mpd");

        System.out.println("🎥 Ejecutando FFmpeg...");

        ProcessBuilder pb = new ProcessBuilder(command);
        
        // Esto permite ver el log de FFmpeg en la consola de Tomcat/NetBeans
        pb.inheritIO(); 
        
        Process process = pb.start();
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new IOException("FFmpeg terminó con código de error: " + exitCode);
        }
    }
}