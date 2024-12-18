package org.jesjack.serverjesjackplugin.entities

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.jesjack.serverjesjackplugin.MultiWorldParser.Companion.logger

class Chunk {
    companion object {
        fun loadChunkAtCoordinates(plugin: JavaPlugin, worldName: String, x: Int, z: Int, onChunkLoaded: (Boolean) -> Unit) {
            val world = Bukkit.getWorld(worldName)
            if (world == null) {
                logger.severe("No se encontró el mundo con el nombre: $worldName")
                onChunkLoaded(false)
                return
            }

            val chunkX = x shr 4
            val chunkZ = z shr 4

            // Primero, verificamos si el chunk ya está cargado
            Bukkit.getScheduler().runTask(plugin, Runnable {
                if (world.isChunkLoaded(chunkX, chunkZ)) {
                    logger.info("Chunk $chunkX, $chunkZ ya está cargado en el mundo $worldName.")
                    onChunkLoaded(true)
                    return@Runnable
                }

                // Si no está cargado, iniciamos el proceso de carga asíncrona
                world.getChunkAtAsync(chunkX, chunkZ, true).thenAccept { chunk ->
                    Bukkit.getScheduler().runTask(plugin, Runnable {
                        if (chunk != null && chunk.isLoaded) {
                            logger.info("Chunk $chunkX, $chunkZ cargado correctamente en el mundo $worldName.")
                            onChunkLoaded(true)
                        } else {
                            logger.severe("Error al cargar el chunk $chunkX, $chunkZ en el mundo $worldName.")
                            onChunkLoaded(false)
                        }
                    })
                }
            })
        }
    }
}