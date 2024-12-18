package org.jesjack.serverjesjackplugin.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.jesjack.serverjesjackplugin.MultiWorldParser
import org.jesjack.serverjesjackplugin.controllers.OnPlayerJoinController

class PlayerJoinListener(private val plugin: MultiWorldParser) : Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        OnPlayerJoinController(event, plugin).handle()
    }
}