/*
 * This file is the event listener file, it is always active.
 */
package me.fourbytes.pvpgames.listener;

import me.fourbytes.pvpgames.PvPGamesBase;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class ListenerGlobalEvent implements Listener {
    public String lobbyWorld;
    public PvPGamesBase plugin;


    public ListenerGlobalEvent(PvPGamesBase p, String lb) {
        // Set some variables
        lobbyWorld = lb;
        plugin = p;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().teleport(plugin.spawn);
    }

    // TOUCHOSC RULES

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
}
