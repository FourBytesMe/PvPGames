/*
 * This file is the event listener file, it is only active while a game is in progress
 */
package me.fourbytes.pvpgames.listener;

import me.fourbytes.pvpgames.PvPGamesBase;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ListenerInGameEvent implements Listener {

    public ListenerInGameEvent(PvPGamesBase plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        if (!PvPGamesBase.inProgress)
            if (!event.getPlayer().isOp())
                event.setCancelled(true);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (!PvPGamesBase.inProgress)
            if (!event.getPlayer().isOp())
                event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!PvPGamesBase.inProgress)
            if (!event.getPlayer().isOp())
                event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!PvPGamesBase.inProgress)
            if (!event.getPlayer().isOp())
                event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerLogout(PlayerQuitEvent event) {
        PvPGamesBase.playingplayers.remove(event.getPlayer().getName());
    }
}
