package org.jesjack.serverjesjackplugin.controllers

import com.onarandombox.MultiverseCore.api.MultiverseWorld
import com.onarandombox.MultiversePortals.PortalLocation
import com.sk89q.worldedit.math.BlockVector3
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.WorldType
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.jesjack.serverjesjackplugin.MultiWorldParser
import org.jesjack.serverjesjackplugin.MultiWorldParser.Companion.logger
import org.jesjack.serverjesjackplugin.MultiWorldParser.Companion.multiverseCore
import org.jesjack.serverjesjackplugin.entities.Chunk.Companion.loadChunkAtCoordinates
import org.jesjack.serverjesjackplugin.entities.Frame
import org.jesjack.serverjesjackplugin.entities.Portal
import kotlin.math.sign
import kotlin.random.Random

class OnPlayerJoinController(private val event: PlayerJoinEvent, private val multiWorldParser: MultiWorldParser) {

    fun handle() {
        val world = createWorldForPlayer(event.player) ?: return
        createNetherWorldForPlayer(event.player)
        createEndWorldForPlayer(event.player)
        createPortalForWorld(world) {
            it.forEach { p ->
                Frame(p).apply {
                    createFrame()
                    createCenter()
                    addSign("Mundo de ${event.player.name}")
                }
            }
        }
    }

    private fun createEndWorldForPlayer(player: Player) {
        val worldName = "world_${player.name}_end"
        if (worldExists(worldName)) {
            logger.info("End world $worldName already exists")
            return
        }
        if (!multiverseCore.mvWorldManager.addWorld(
                worldName,
                World.Environment.THE_END,
                null,
                WorldType.NORMAL,
                true,
                "THE_END"
            )
        ) {
            logger.severe("Failed to create or get End world $worldName")
            return
        }
        logger.info("Created End world $worldName for player ${player.name}")
        return
    }

    private fun createNetherWorldForPlayer(player: Player) {
        val worldName = "world_${player.name}_nether"
        if (worldExists(worldName)) {
            logger.info("Nether world $worldName already exists")
            return
        }
        if (!multiverseCore.mvWorldManager.addWorld(
                worldName,
                World.Environment.NETHER,
                null,
                WorldType.NORMAL,
                true,
                "NETHER"
            )
        ) {
            logger.severe("Failed to create or get Nether world $worldName")
            return
        }
        logger.info("Created Nether world $worldName for player ${player.name}")
        return
    }


    private fun createPortalForWorld(destWorld: MultiverseWorld, callback: (Array<Portal>) -> Unit) {
        val mainWorld = Bukkit.getWorlds().first()
        val mvMainWorld = multiverseCore.mvWorldManager.getMVWorld(mainWorld)
        val minPt = BlockVector3.at(0, 64, 0)
        val maxPt = BlockVector3.at(3, 68, 0)
        val portalLocation = PortalLocation(minPt, maxPt, mvMainWorld)

        var x = Random.nextInt(20000) - 10000
        var z = Random.nextInt(20000) - 10000
        x += sign(x.toDouble()).toInt() * 1000
        z += sign(z.toDouble()).toInt() * 1000

        loadChunkAtCoordinates(multiWorldParser, destWorld.name, x, z) {
            if (!it) {
                logger.severe("Failed to load chunk at coordinates $x, $z in world ${destWorld.name}")
                return@loadChunkAtCoordinates
            }

            val y = Bukkit.getWorld(destWorld.name)!!.getHighestBlockYAt(x, z)
            val minPt2 = BlockVector3.at(x, y, z)
            val maxPt2 = BlockVector3.at(x + 3, y + 4, z)

            val mainPortal =
                Portal(portalLocation, Location(Bukkit.getWorld(destWorld.name), x.toDouble(), y + 4.0, z.toDouble()))
            val otherPortal = Portal(PortalLocation(minPt2, maxPt2, destWorld), Location(mainWorld, 10.0, 65.5, 10.0))

            val created1 = mainPortal.create()
            val created2 = otherPortal.create()

            if (!created1) {
                logger.warning("Failed to create portal to world ${destWorld.name}")
            }

            if (!created2) {
                logger.warning("Failed to create portal from world ${destWorld.name}")
            }

            if (created1 && created2) {
                logger.info("Created portal to world ${destWorld.name} for player ${event.player.name}")
            }

            callback(arrayOf(mainPortal, otherPortal))
        }
    }

    private fun createWorldForPlayer(player: Player): MultiverseWorld? {
        val worldName = "world_${player.name}"
        if (worldExists(worldName)) {
            logger.info("World $worldName already exists")
            return multiverseCore.mvWorldManager.getMVWorld(worldName)
        }
        if (!multiverseCore.mvWorldManager.addWorld(
                worldName, World.Environment.NORMAL, null, WorldType.NORMAL, true, null
            )
        ) {
            logger.severe("Failed to get or create world $worldName")
            return null
        }
        logger.info("Created world $worldName for player ${player.name}")
        return multiverseCore.mvWorldManager.getMVWorld(worldName)
    }

    private fun worldExists(worldName: String): Boolean {
        return multiverseCore.mvWorldManager.getMVWorld(worldName) != null
    }

}