/* JDashPlayer.js - Versión Corregida v3 */
class JDashPlayer {
    
    constructor(videoElementId, url) {
        this.videoElementId = videoElementId;
        this.url = url;
        this.player = null; // Empezamos en null
        this.videoElement = document.querySelector("#" + videoElementId);
    }

    init() {
        console.log("🔗 Inicializando J-DASH Class para: " + this.url);
        
        // 1. Crear instancia
        this.player = dashjs.MediaPlayer().create();
        
        // 2. Inicializar
        this.player.initialize(this.videoElement, this.url, true);

        // 3. Configuración
        this.player.updateSettings({
            'streaming': {
                'buffer': {
                    'stableBufferTime': 12,
                    'bufferTimeAtTopQuality': 30,
                    'fastSwitchEnabled': true
                },
                'abr': {
                    'autoSwitchBitrate': { 'video': true }
                }
            }
        });

        // Eventos
        this.player.on(dashjs.MediaPlayer.events.QUALITY_CHANGE_RENDERED, this.updateStats.bind(this));
        this.player.on(dashjs.MediaPlayer.events.STREAM_INITIALIZED, this.updateStats.bind(this));
        
        setInterval(() => this.updateStats(), 1000);
    }

    updateStats() {
        if (!this.player) return;

        try {
            var qIndex = this.player.getQualityFor("video");
            
            // Método seguro para obtener bitrates
            var bitrates = this.getBitratesSeguro();

            if (bitrates && bitrates[qIndex]) {
                var currentInfo = bitrates[qIndex];
                
                var elIndex = document.getElementById("qualityIndex");
                var elRes = document.getElementById("resolution");
                var elBit = document.getElementById("bitrate");

                if(elIndex) elIndex.innerText = qIndex + " (Max: " + (bitrates.length - 1) + ")";
                if(elRes) elRes.innerText = currentInfo.width + "x" + currentInfo.height;
                if(elBit) elBit.innerText = (currentInfo.bitrate / 1000).toFixed(0);
            }
        } catch (e) { 
            // Ignoramos errores visuales menores
        }
    }

setQuality(targetIndex) {
        if (!this.player) return;

        // 1. Validaciones (Igual que antes)
        var bitrates = this.getBitratesSeguro();
        if (!bitrates || bitrates.length === 0) return;
        if (targetIndex >= bitrates.length) targetIndex = bitrates.length - 1;

        console.log("⚡ Forzando cambio AGRESIVO a calidad: " + targetIndex);

        // 2. Desactivar Auto y Asignar Calidad
        this.player.updateSettings({
            'streaming': { 'abr': { 'autoSwitchBitrate': { 'video': false } } }
        });
        this.player.setQualityFor("video", targetIndex);

        var tiempoActual = this.player.time();

        // -------------------------------------------------------------
        // 3. EL TRUCO "ESTRANGULAMIENTO" (BUFFER FLUSH)
        // -------------------------------------------------------------
        // Le decimos al player que el buffer permitido es de 0 segundos.
        // Esto obliga al recolector de basura interno a borrar los 12s guardados.
        this.player.updateSettings({
            'streaming': {
                'buffer': {
                    'stableBufferTime': 0.1, 
                    'bufferTimeAtTopQuality': 0.1 
                }
            }
        });

        // 4. Hacemos el seek para que pida datos nuevos inmediatamente
        this.player.seek(tiempoActual);

        // 5. Restauramos el buffer normal después de medio segundo
        // (Damos tiempo a que se borre lo viejo antes de permitir guardar lo nuevo)
        setTimeout(() => {
            this.player.updateSettings({
                'streaming': {
                    'buffer': {
                        'stableBufferTime': 12, // Volvemos a tu buffer de 12s
                        'bufferTimeAtTopQuality': 30
                    }
                }
            });
            console.log("✅ Buffer restaurado a capacidad normal (12s).");
        }, 500); 
    }

    setAuto() {
        console.log("🤖 Modo Automático Activado");
        this.player.updateSettings({
            'streaming': { 'abr': { 'autoSwitchBitrate': { 'video': true } } }
        });
    }

    // --- HELPER PARA EVITAR EL ERROR "NOT A FUNCTION" ---
    getBitratesSeguro() {
        // Intento 1: API Standard
        if (typeof this.player.getBitrateInfoListFor === 'function') {
            return this.player.getBitrateInfoListFor("video");
        }
        // Intento 2: API Antigua/Alternativa
        if (typeof this.player.getVariantTracks === 'function') {
            return this.player.getVariantTracks().video;
        }
        // Fallo total
        console.error("❌ FATAL: No encuentro la función para leer bitrates en esta versión de Dash.js");
        return [];
    }
}

