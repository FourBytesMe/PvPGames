/*
 * This file is the event listener file, it is always active.
 */
package me.fourbytes.pvpgames;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PvPGamesGlobalListener implements Listener {
	
	public String lobbyworld;
	
    public PvPGamesGlobalListener(PvPGamesBase plugin, String lobbyworldo) {
    	lobbyworld = lobbyworldo;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerLoginEvent event){
		Bukkit.getLogger().info("Teleporting "+event.getPlayer().getName()+" to lobby.");
		event.getPlayer().teleport(new Location(Bukkit.getWorld(lobbyworld), Bukkit.getWorld(lobbyworld).getSpawnLocation().getBlockX(), 128, Bukkit.getWorld(lobbyworld).getSpawnLocation().getBlockZ()));
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDeath(PlayerDeathEvent event) {
		if(PvPGamesBase.inProgress)
			PvPGamesBase.playingplayers.remove((Player)event.getEntity());
		event.getEntity().teleport(new Location(Bukkit.getWorld(lobbyworld), Bukkit.getWorld(lobbyworld).getSpawnLocation().getBlockX(), 128, Bukkit.getWorld(lobbyworld).getSpawnLocation().getBlockZ()));
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDamage(EntityDamageEvent event){
		Entity e = event.getEntity();
		if(e instanceof Player) {
			Player p = (Player) e;
			if(PvPGamesBase.ignoreFallDamage.contains(p) && event.getCause() == DamageCause.FALL) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		World w = Bukkit.getWorld(lobbyworld);
		event.setRespawnLocation(new Location(w, w.getSpawnLocation().getBlockX(), 128, w.getSpawnLocation().getBlockZ()));
	}
}
