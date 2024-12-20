package org.jesjack.serverjesjackplugin.entities

import com.onarandombox.MultiversePortals.MVPortal
import com.onarandombox.MultiversePortals.PortalLocation
import com.sk89q.worldedit.math.BlockVector3
import org.bukkit.Bukkit
import org.bukkit.Location
import org.jesjack.serverjesjackplugin.MultiWorldParser.Companion.multiversePortals

class Portal(
    var location: PortalLocation,
    private var _destination: Location,
) {
    private val portals = multiversePortals.portalManager.getPortals(Bukkit.getConsoleSender())
    private val portalName = "${location.mvWorld.name}_to_${_destination.world?.name}".replace('.', '_')

    init {
        if (exists()) {
            getPortalLocation()
        } else {
            adjustPortalLocation()
        }
    }

    private fun getPortalLocation() {
        val portal = portals.firstOrNull { it.name == portalName } ?: return

        location = portal.location
        _destination = portal.destination.getLocation(null)
    }

    private fun exists(): Boolean {
        return portals.any { it.name == portalName }
    }

    private fun adjustPortalLocation() {
        val vectors = location.vectors
        val cube2 = Cube(vectors)

        for (attempt in 1..100) { // Limit attempts to prevent infinite loop
            var overlap = false
            for (portal in portals) {
                if (location.mvWorld.name != portal.location.mvWorld.name) continue

                val cube1 = Cube(portal.location.vectors)
                if (Cube.areTouched(cube1, cube2)) {
//                    // log cubes for debugging
//                    logger.info("Adjusting portal, cube1: $cube1, cube2: $cube2")
                    overlap = true
                    break
                }
            }

            if (!overlap) break

            // Adjust location
            val x = location.vectors.minOf { it.x } + 1
            val y = location.vectors.minOf { it.y }
            val z = location.vectors.minOf { it.z }
            val minPt = BlockVector3.at(x, y, z)
            val maxPt = BlockVector3.at(x + 3, y + 4, z)
            location = PortalLocation(minPt, maxPt, location.mvWorld)
            cube2.updateVectors(location.vectors)
        }
    }

    fun create(): Boolean {
        return multiversePortals.portalManager.addPortal(
            MVPortal(
                location.mvWorld,
                multiversePortals,
                portalName,
                "admin",
                location.toString()
            ).apply {
                setExactDestination(_destination)
            })
    }
}