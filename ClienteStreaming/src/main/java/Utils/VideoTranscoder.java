/*
 * License header...
 */
package Utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class VideoTranscoder {

    // -------------------------------------------------------------------------
    // RUTAS ABSOLUTAS (CONFIGURACIÓN)
    // -------------------------------------------------------------------------
    private static final String INPUT_ROOT = "/home/alumne/videos_mp4";
    private static final String OUTPUT_ROOT = "/home/alumne/videos_dash";

    public static void main(String[] args) {
        System.out.println("🚀 Iniciando Transcodificador Anti-Desync (Batch Mode)...");

        try {
            Path inputDir = Path.of(INPUT_ROOT);
            Path outputDir = Path.of(OUTPUT_ROOT);

            // 1. Validaciones iniciales
            if (!Files.exists(inputDir)) {
                System.err.println("❌ La carpeta de entrada no existe: " + INPUT_ROOT);
                return;
            }
            
            if (!Files.exists(outputDir)) {
                Files.createDirectories(outputDir);
            }

            // 2. Obtener lista de videos .mp4
            List<Path> videoList;
            try (Stream<Path> walk = Files.walk(inputDir, 1)) {
                videoList = walk
                        .filter(p -> !Files.isDirectory(p))
                        .filter(p -> p.toString().toLowerCase().endsWith(".mp4"))
                        .collect(Collectors.toList());
            }

            if (videoList.isEmpty()) {
                System.out.println("⚠️ No encontré videos .mp4 en " + INPUT_ROOT);
                return;
            }

            System.out.println("📦 Se han encontrado " + videoList.size() + " videos.");
            System.out.println("--------------------------------------------------");

            // 3. BUCLE PRINCIPAL
            for (Path inputPath : videoList) {
                processSingleVideo(inputPath, outputDir);
                System.out.println("--------------------------------------------------");
            }
            
            System.out.println("🎉 ¡TODO TERMINADO! Prueba el reproductor ahora.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void processSingleVideo(Path inputPath, Path outputRoot) {
        try {
            String fileName = inputPath.getFileName().toString();
            String baseName = fileName;
            int dotIndex = fileName.lastIndexOf('.');
            if (dotIndex > 0) {
                baseName = fileName.substring(0, dotIndex);
            }

            String folderName = "dash_" + baseName;
            Path finalOutputDir = outputRoot.resolve(folderName);

            if (!Files.exists(finalOutputDir)) {
                Files.createDirectories(finalOutputDir);
            }

            System.out.println("🎬 Procesando (Strict Mode): " + fileName);
            generateDash(inputPath.toString(), finalOutputDir.toString());

        } catch (Exception e) {
            System.err.println("❌ Error procesando " + inputPath.getFileName());
            e.printStackTrace();
        }
    }

// --------------------------------------------------------------------------------
    // MÉTODO CORREGIDO: SINCRONIZACIÓN POR TIEMPO (RESPECTING SOURCE FPS)
    // --------------------------------------------------------------------------------
    private static void generateDash(String inputPath, String outputDir) throws IOException, InterruptedException {
        var command = new ArrayList<String>();
        command.add("ffmpeg");
        command.add("-y");
        command.add("-i"); 
        command.add(inputPath);

        // --- 1. AJUSTES DEL CÓDEC DE VIDEO ---
        command.add("-c:v"); command.add("libx264");
        
        // Usamos 'fast' para asegurar calidad sin tardar una eternidad, 
        // pero NO 'veryfast' que a veces salta cálculos de frames.
        command.add("-preset"); command.add("fast"); 
        command.add("-profile:v"); command.add("main");
        
        // [CRÍTICO] Eliminamos "-r 30" y "-g 120" porque rompían la sync si el video no era nativo 30fps.
        // En su lugar, usamos "Force Key Frames" basado en TIEMPO (4 segundos).
        
        // 1. Desactivar detección de escenas (para que no ponga keyframes aleatorios)
        command.add("-sc_threshold"); command.add("0");
        
        // 2. Obligar a poner un Keyframe EXACTAMENTE cada 4 segundos (n_forced * 4)
        command.add("-force_key_frames"); command.add("expr:gte(t,n_forced*4)");
        
        // 3. Asegurar que el audio y video se procesan en sintonía
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
        
        // Estas dos opciones aseguran que todos los segmentos (video y audio) duren lo mismo
        command.add("-seg_duration"); command.add("4");
        command.add("-use_template"); command.add("1");
        command.add("-use_timeline"); command.add("1");
        
        // IMPORTANTE: Corrección de alineación de segmentos
        command.add("-streaming"); command.add("1"); 
        
        command.add("-adaptation_sets"); command.add("id=0,streams=v id=1,streams=a");
        
        command.add(outputDir + "/manifiesto.mpd");

        // Imprimimos el comando para depuración si falla
        System.out.println("Ejecutando FFmpeg...");

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.inheritIO(); 
        Process process = pb.start();
        process.waitFor();
    }
}