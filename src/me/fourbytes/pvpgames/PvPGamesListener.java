package me.fourbytes.pvpgames;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PvPGamesListener implements Listener {
	
    public PvPGamesListener(PvPGamesBase plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
	@EventHandler
	public void onPickup(PlayerPickupItemEvent event){
		if(!PvPGamesBase.inProgress)
			if(!event.getPlayer().isOp())
				event.setCancelled(true);
	}
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		if(!PvPGamesBase.inProgress)
			if(!event.getPlayer().isOp())
				event.setCancelled(true);
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if(!PvPGamesBase.inProgress)
			if(!event.getPlayer().isOp())
				event.setCancelled(true);
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if(!PvPGamesBase.inProgress)
			if(!event.getPlayer().isOp())
				event.setCancelled(true);
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		Entity e = event.getEntity();
		if(e instanceof Player) {
			//Player player = (Player)e;
			//if(!player.isOp())
			if(!PvPGamesBase.inProgress)
				event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerLogout(PlayerQuitEvent event) {
		PvPGamesBase.playingplayers.remove(event.getPlayer());
	}
}
