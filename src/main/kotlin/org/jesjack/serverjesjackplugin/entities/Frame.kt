package org.jesjack.serverjesjackplugin.entities

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.block.Sign
import org.bukkit.block.data.Directional
import org.bukkit.block.data.Rotatable
import org.bukkit.block.sign.Side
import org.jesjack.serverjesjackplugin.MultiWorldParser.Companion.logger

class Frame(portal: Portal) {
    private var frameMaterial: Material = Material.OBSIDIAN
    private var centerMaterial: Material = Material.NETHER_PORTAL

    private val minX = portal.location.vectors.minOf { it.x }.toInt()
    private val minY = portal.location.vectors.minOf { it.y }.toInt()
    private val minZ = portal.location.vectors.minOf { it.z }.toInt()
    private val maxX = portal.location.vectors.maxOf { it.x }.toInt()
    private val maxY = portal.location.vectors.maxOf { it.y }.toInt()
    private val maxZ = portal.location.vectors.maxOf { it.z }.toInt()

    private val world = portal.location.mvWorld.cbWorld

    fun createFrame() {
        for (x in minX..maxX) {
            for (y in minY..maxY) {
                for (z in minZ..maxZ) {
                    val isEdgeX = x == minX || x == maxX
                    val isEdgeY = y == minY || y == maxY
                    val isEdgeZ = z == minZ || z == maxZ

                    if ((isEdgeX && isEdgeY) || (isEdgeX && isEdgeZ) || (isEdgeY && isEdgeZ)) {
                        world.getBlockAt(x, y, z).type = frameMaterial
                    }
                }
            }
        }

        logger.info("Created frame at $minX, $minY, $minZ to $maxX, $maxY, $maxZ in world ${world.name}")
    }

    fun createCenter() {
        for (x in minX..maxX) {
            for (y in minY..maxY) {
                for (z in minZ..maxZ) {
                    val isEdgeX = x == minX || x == maxX
                    val isEdgeY = y == minY || y == maxY
                    val isEdgeZ = z == minZ || z == maxZ

                    if ((!isEdgeX || !isEdgeY) && (!isEdgeX || !isEdgeZ) && (!isEdgeY || !isEdgeZ)) {
                        world.getBlockAt(x, y, z).type = centerMaterial
                    }
                }
            }
        }

        logger.info("Created center at $minX, $minY, $minZ to $maxX, $maxY, $maxZ in world ${world.name}")
    }

    fun addSign(text: String) {
        // Determinar la cara del portal donde se colocará el letrero
        val signFace = when {
            maxX - minX > maxZ - minZ -> BlockFace.NORTH // Portal orientado este-oeste
            else -> BlockFace.EAST // Portal orientado norte-sur
        }

        // Determinar la posición del letrero
        val signX = if (signFace == BlockFace.NORTH) minX else maxX
        val signY = maxY + 1
        val signZ = if (signFace == BlockFace.NORTH) minZ else maxZ

        // Colocar el letrero
        val signBlock = world.getBlockAt(signX, signY, signZ)
        signBlock.type = Material.OAK_SIGN
        val sign = signBlock.state as Sign

        // Orientar el letrero
//        (signBlock.blockData as Directional).facing = signFace
        if (signBlock.blockData is Rotatable) {
            (signBlock.blockData as Rotatable).rotation = signFace
        } else if (signBlock.blockData is Directional) {
            (signBlock.blockData as Directional).facing = signFace
        }

        // Establecer el texto del letrero
        sign.getSide(Side.FRONT).line(0, Component.text(text))
        sign.getSide(Side.BACK).line(0, Component.text(text))

        val words = text.split(" ")
        for (i in words.indices) {
            when (i) {
                0 -> {
                    sign.getSide(Side.FRONT).line(0, Component.text(words[i], Style.style(TextDecoration.BOLD)))
                    sign.getSide(Side.BACK).line(0, Component.text(words[i], Style.style(TextDecoration.BOLD)))
                }

                words.size - 1 -> {
                    sign.getSide(Side.FRONT)
                        .line(i, Component.text(words[i], Style.style(TextDecoration.BOLD, TextColor.color(255, 0, 0))))
                    sign.getSide(Side.BACK)
                        .line(i, Component.text(words[i], Style.style(TextDecoration.BOLD, TextColor.color(255, 0, 0))))
                }

                else -> {
                    sign.getSide(Side.FRONT).line(i, Component.text(words[i]))
                    sign.getSide(Side.BACK).line(i, Component.text(words[i]))
                }
            }
        }

        // Actualizar el estado del letrero
        sign.update(true)

        logger.info("Added sign with text '$text' at $signX, $signY, $signZ in world ${world.name}")
    }
}
