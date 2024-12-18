package org.jesjack.serverjesjackplugin.entities

import org.bukkit.util.Vector

class Cube(private var vectors: List<Vector>) {

    // Calculamos los límites del cubo en cada eje a partir de sus vértices
    private var minX = vectors.minOf { it.x }
    private var maxX = vectors.maxOf { it.x }
    private var minY = vectors.minOf { it.y }
    private var maxY = vectors.maxOf { it.y }
    private var minZ = vectors.minOf { it.z }
    private var maxZ = vectors.maxOf { it.z }

    fun updateVectors(newVectors: List<Vector>) {
        vectors = newVectors
        minX = vectors.minOf { it.x }
        maxX = vectors.maxOf { it.x }
        minY = vectors.minOf { it.y }
        maxY = vectors.maxOf { it.y }
        minZ = vectors.minOf { it.z }
        maxZ = vectors.maxOf { it.z }
    }

    companion object {
        /**
         * Verifica si dos cubos se tocan.
         */
        fun areTouched(cube1: Cube, cube2: Cube): Boolean {
            // Verificamos si los cubos NO se tocan en algún eje.
            val noOverlapX = cube1.maxX < cube2.minX || cube1.minX > cube2.maxX
            val noOverlapY = cube1.maxY < cube2.minY || cube1.minY > cube2.maxY
            val noOverlapZ = cube1.maxZ < cube2.minZ || cube1.minZ > cube2.maxZ

            // Si no se solapan en algún eje, no se tocan.
            return !(noOverlapX || noOverlapY || noOverlapZ)
        }
    }

    override fun toString(): String {
        return "Cube(minX=$minX, maxX=$maxX, minY=$minY, maxY=$maxY, minZ=$minZ, maxZ=$maxZ)"
    }
}
