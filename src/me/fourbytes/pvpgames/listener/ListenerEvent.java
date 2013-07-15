/*
 * This file is the event listener file, it is always active.
 */
package me.fourbytes.pvpgames.listener;

import me.fourbytes.pvpgames.LeaderboardManager;
import me.fourbytes.pvpgames.PvPGamesBase;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.*;

/*
 * ListenerEvent class, The main event listener.
 *  Fourbytes 2013
 */

public class ListenerEvent implements Listener {
    public String lobbyWorld;
    public PvPGamesBase plugin;

    public ListenerEvent(PvPGamesBase p, String lb) {
        // Set some variables
        lobbyWorld = lb;
        plugin = p;
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
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().teleport(plugin.spawn);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        Entity e = event.getEntity();
        if (e instanceof Player) {
            Player p = (Player) event.getEntity();
            if (!PvPGamesBase.playingplayers.contains(p.getName())) {
                event.setCancelled(true);
            }
            if (PvPGamesBase.ignoreFallDamage.contains(p.getName()) && event.getCause() == DamageCause.FALL) {
                event.setCancelled(true);
            } else if (p.getHealth() - event.getDamage() < 1 && PvPGamesBase.playingplayers.contains(p.getName())) {
                p.setHealth(20);
                p.teleport(plugin.spawn);
                if (PvPGamesBase.inProgress && PvPGamesBase.playingplayers.contains(p.getName())) {
                    new LeaderboardManager(plugin).addToDeaths(p.getName(), 1);

                    PvPGamesBase.playingplayers.remove(((Player) event.getEntity()).getName());
                    int c = PvPGamesBase.playingplayers.size();
                    if (c == 1) {
                        plugin.endGame(Bukkit.getPlayer(PvPGamesBase.playingplayers.get(0)));
                    }
                    if (c < 1) {
                        plugin.endGame();
                    }
                }
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        event.setRespawnLocation(plugin.spawn);
    }

    @EventHandler
    public void onPlayerLogout(PlayerQuitEvent event) {
        if (PvPGamesBase.playingplayers.contains(event.getPlayer().getName()) && PvPGamesBase.inProgress) {
            PvPGamesBase.playingplayers.remove(event.getPlayer().getName());
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player && PvPGamesBase.inProgress) {
            if (((Player) event.getEntity()).getHealth() - event.getDamage() < 1 && PvPGamesBase.playingplayers.contains(((Player) event.getEntity()).getName())) {
                new LeaderboardManager(plugin).addToDeaths(((Player) event.getEntity()).getName(), 1);
            }
        }
    }
}
