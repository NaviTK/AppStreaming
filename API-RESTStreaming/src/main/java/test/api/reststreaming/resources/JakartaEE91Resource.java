package test.api.reststreaming.resources;

import Utils.Conversor;
import Utils.DBConnection; 
import Utils.GestionCarpeta;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import jakarta.ws.rs.*; 
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.sql.SQLException;

/**
 * Controlador API REST Centralizado
 * Base URL: /api/jakartaee9
 */
@Path("jakartaee9")
public class JakartaEE91Resource {
    
    // ⚠️ Asegúrate de que esta ruta coincida con donde guardas los videos en GestionCarpeta
    private static final String VIDEO_ROOT = "/home/alumne/videos_dash"; 

    // -------------------------------------------------------------------------
    // 0. PING (Health Check)
    // -------------------------------------------------------------------------
    @GET
    @Path("ping")
    public Response ping(){
        return Response.ok("API REST Streaming Operativa con DB").build();
    }

    // -------------------------------------------------------------------------
    // 1. LISTAR VIDEOS
    // -------------------------------------------------------------------------
    @GET
    @Path("list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response obtenerVideos() {
        List<String> videos = GestionCarpeta.getListaVideos();
        return Response.ok(videos).build();
    }

    // -------------------------------------------------------------------------
    // 2. SUBIR Y CONVERTIR VIDEO
    // -------------------------------------------------------------------------
    @POST
    @Path("upload")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Produces(MediaType.APPLICATION_JSON)
    public Response subirVideo(InputStream fileInputStream, 
                               @QueryParam("nombreVideo") String nombreVideo) { // Corregido nombre variable
        
        System.out.println("📡 API: Recibiendo video para: " + nombreVideo);
        
        if (nombreVideo == null || nombreVideo.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Falta el parámetro nombreVideo\"}").build();
        }

        try {
            GestionCarpeta.vaciarCarpetaInput();

            File targetFile = new File(GestionCarpeta.RUTA_INPUT + File.separator + "video_temp.mp4");
            targetFile.getParentFile().mkdirs(); 
            
            Files.copy(fileInputStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            
            String nombreLimpio = nombreVideo.trim().toLowerCase().replaceAll("\\s+", "_");
            String nombreCarpetaFinal = "dash_" + nombreLimpio;

            boolean exito = Conversor.ejecutarScriptConversion(targetFile.getAbsolutePath(), nombreCarpetaFinal);

            if (exito) {
                return Response.ok("{\"status\":\"ok\", \"mensaje\":\"Video subido y convertido\"}").build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("{\"status\":\"error\", \"mensaje\":\"Fallo en FFmpeg\"}").build();
            }

        } catch (IOException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"status\":\"error\", \"mensaje\":\"" + e.getMessage() + "\"}").build();
        }
    }

    // -------------------------------------------------------------------------
    // 3. BORRAR VIDEO
    // -------------------------------------------------------------------------
    @DELETE
    @Path("delete/{nombre}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response borrarVideo(@PathParam("nombre") String nombreCarpeta) {
        boolean eliminado = GestionCarpeta.borrarVideo(nombreCarpeta);

        if (eliminado) {
            return Response.ok("{\"status\":\"ok\", \"mensaje\":\"Video eliminado\"}").build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                           .entity("{\"status\":\"error\", \"mensaje\":\"No encontrado\"}").build();
        }
    }

    // -------------------------------------------------------------------------
    // 4. RENOMBRAR VIDEO
    // -------------------------------------------------------------------------
    @PUT
    @Path("rename")
    @Produces(MediaType.APPLICATION_JSON)
    public Response renombrarVideo(@QueryParam("oldName") String nombreActual,
                                   @QueryParam("newName") String nuevoNombre) {

        boolean renombrado = GestionCarpeta.renombrarVideo(nombreActual, nuevoNombre);

        if (renombrado) {
            return Response.ok("{\"status\":\"ok\", \"mensaje\":\"Renombrado con éxito\"}").build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("{\"status\":\"error\", \"mensaje\":\"Fallo al renombrar\"}").build();
        }
    }
    
    // -------------------------------------------------------------------------
    // 5. LOGIN DE USUARIO
    // -------------------------------------------------------------------------
    @POST
    @Path("login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@FormParam("user") String usuario,
                          @FormParam("pass") String password) {

        if (usuario == null || password == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"status\":\"error\", \"mensaje\":\"Faltan datos\"}").build();
        }

        try {
            boolean loginCorrecto = DBConnection.checkLogin(usuario, password);

            if (loginCorrecto) {
                boolean esAdmin = DBConnection.esAdmin(usuario);
                String jsonRespuesta = String.format(
                        "{\"status\":\"ok\", \"mensaje\":\"Login correcto\", \"isAdmin\": %b}", 
                        esAdmin
                );
                return Response.ok(jsonRespuesta).build();
            } else {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"status\":\"error\", \"mensaje\":\"Usuario o contraseña incorrectos\"}").build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"status\":\"error\", \"mensaje\":\"BD Error: " + e.getMessage() + "\"}").build();
        }
    }

    // -------------------------------------------------------------------------
    // 6. REGISTRO DE USUARIO
    // -------------------------------------------------------------------------
    @POST
    @Path("register")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registro(@FormParam("user") String usuario,
                             @FormParam("pass") String password) {

        if (usuario == null || password == null || usuario.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"status\":\"error\", \"mensaje\":\"Datos inválidos\"}").build();
        }

        try {
            if (DBConnection.existeUsuario(usuario)) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("{\"status\":\"error\", \"mensaje\":\"El usuario ya existe\"}").build();
            }

            DBConnection.registrarUsuario(usuario, password);
            System.out.println("✅ API: Nuevo usuario registrado: " + usuario);
            return Response.ok("{\"status\":\"ok\", \"mensaje\":\"Usuario registrado correctamente\"}").build();

        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"status\":\"error\", \"mensaje\":\"Error de Base de Datos: " + e.getMessage() + "\"}").build();
        }
    }

    // =========================================================================
    // 7. STREAMING DE VIDEO (Integrado directamente aquí)
    // URL: GET /api/jakartaee9/stream/{videoName}/{fileName}
    // =========================================================================
    @GET
    @Path("stream/{videoName}/{fileName}")
    public Response streamVideo(@PathParam("videoName") String videoName,
                                @PathParam("fileName") String fileName) {

        // 1. Construir ruta del archivo
        File file = new File(VIDEO_ROOT + File.separator + videoName + File.separator + fileName);

        // 2. Validar existencia
        if (!file.exists()) {
            System.err.println("❌ Video no encontrado: " + file.getAbsolutePath());
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        // 3. Determinar MIME Type
        String mimeType = "application/octet-stream";
        if (fileName.endsWith(".mpd")) {
            mimeType = "application/dash+xml";
        } else if (fileName.endsWith(".m4s")) {
            mimeType = "video/iso.segment";
        } else if (fileName.endsWith(".mp4")) {
            mimeType = "video/mp4";
        }

        // 4. Devolver archivo con CORS
        return Response.ok(file)
                .type(mimeType)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, HEAD")
                .header("Content-Length", file.length())
                .build();
    }
}
