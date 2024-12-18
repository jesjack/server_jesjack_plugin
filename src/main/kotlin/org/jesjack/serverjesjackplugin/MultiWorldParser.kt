package org.jesjack.serverjesjackplugin

import com.onarandombox.MultiverseCore.MultiverseCore
import com.onarandombox.MultiversePortals.MultiversePortals
import com.onarandombox.multiverseinventories.MultiverseInventories
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import org.jesjack.serverjesjackplugin.listeners.PlayerJoinListener
import java.io.InputStream
import java.io.InputStreamReader
import java.util.logging.Logger


class MultiWorldParser : JavaPlugin() {
    companion object {
        lateinit var multiverseCore: MultiverseCore
        lateinit var multiversePortals: MultiversePortals
        lateinit var multiverseInventories: MultiverseInventories
        lateinit var logger: Logger
    }

    override fun onEnable() {
        MultiWorldParser.logger = logger

        // Get the Multiverse-Core plugin
        val plugin1 = Bukkit.getPluginManager().getPlugin("Multiverse-Core")
        val plugin2 = Bukkit.getPluginManager().getPlugin("Multiverse-Portals")
        val plugin3 = Bukkit.getPluginManager().getPlugin("WorldEdit")
        val plugin4 = Bukkit.getPluginManager().getPlugin("Multiverse-Inventories")

        if (plugin1 == null || plugin2 == null || plugin3 == null || plugin4 == null) {
            logger.severe("Multiverse-Core or Multiverse-Portals or WorldEdit or Multiverse-Inventories not found! Disabling plugin.")
            Bukkit.getPluginManager().disablePlugin(this)
            return
        }

        multiverseCore = plugin1 as MultiverseCore
        multiversePortals = plugin2 as MultiversePortals
        multiverseInventories = plugin4 as MultiverseInventories



        logger.info("Multiverse-Core found: " + multiverseCore.name + " " + getPluginVersion(multiverseCore))
        logger.info(
            "Multiverse-Portals found: " + multiversePortals.name + " " + getPluginVersion(
                multiversePortals
            )
        )
        logger.info("WorldEdit found: " + plugin3.name + " " + getPluginVersion(plugin3))

        Bukkit.getPluginManager().registerEvents(PlayerJoinListener(this), this)

        // Initialize your plugin's functionality here
        initializePlugin()
    }

    private fun getPluginVersion(plugin: Plugin): String {
        val stream: InputStream? = plugin.getResource("plugin.yml")
        if (stream != null) {
            val config = YamlConfiguration.loadConfiguration(InputStreamReader(stream))
            return config.getString("version", "Unknown")!!
        }
        return "Unknown"
    }

    private fun initializePlugin() {
        // get worlds with world* prefix
        val worlds = Bukkit.getWorlds().filter { it.name.startsWith("world") }
        val group = multiverseInventories.groupManager.getGroup("player_worlds")
        worlds.forEach { group.addWorld(it) }
        logger.info("MultiWorldParser initialized successfully!")
    }

    override fun onDisable() {
        // Clean up resources, save data, etc.
        logger.info("Disabling MultiWorldParser plugin...")
    }
}