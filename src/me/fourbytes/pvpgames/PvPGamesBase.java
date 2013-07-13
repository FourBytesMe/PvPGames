/*
 * This file is the main file where most of the timing is called,
 * if anyone wants to move timing to another file to clean it up, then they can, just
 * submit a pull request and I'll accept it.
 */
package me.fourbytes.pvpgames;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class PvPGamesBase extends JavaPlugin {

	// Set some variables
	public static Boolean inProgress = false;
	public int readyTime;
	public int gameTime;
	public int maxGameTime;
	public String pvpworld;
	public String lobbyworld;
	public static ArrayList<Player> playingplayers = new ArrayList<Player>();
	public static ArrayList<Player> ignoreFallDamage = new ArrayList<Player>();
	public static BukkitTask readyTimer;
	private BukkitTask gameTimer;
	private Integer cooldownOnStartup;

	@Override
	public void onEnable(){	
		// Get some config options.
		cooldownOnStartup = getConfig().getInt("General.Startup.StartupCooldown");
		lobbyworld = getConfig().getString("Worlds.LobbyWorld");
		pvpworld = getConfig().getString("Worlds.PvPWorld");

		// Reset world.
		resetWorld(pvpworld);

		// Save Default Configuration
		getConfig().options().copyDefaults(true);
		saveConfig();

		// Setup listeners.
		new PvPGamesGameListener(this);
		new PvPGamesGlobalListener(this, lobbyworld);

		// And finally, start the game.
		readyGame(cooldownOnStartup);
	}


	@Override
	public void onDisable() {
		// Unload the world so we don't get world saving errors on shutdown
		Bukkit.unloadWorld(pvpworld, false);
	}

	public void readyGame(Integer cooldown) {
		readyTime = getConfig().getInt("General.Startup.GameStartCooldown");

		readyTimer = Bukkit.getScheduler().runTaskTimer(this, new BukkitRunnable() {
			@Override
			public void run() {
				// Keep all worlds daytime while a game isn't running.
				// TODO: Move this to a seperate function and keep lobby world daytime even when world is running.
				Iterator<World> worlds = getServer().getWorlds().iterator();
				while(worlds.hasNext()) {
					worlds.next().setTime(6000);
				}
				
				// Startup timing.
				if( (readyTime % 10) == 0 && readyTime != 0 && readyTime != 10) {
					Bukkit.broadcastMessage(ChatColor.GRAY + "The game will begin in " + readyTime + " seconds! Make sure to select a kit with /kit.");
				} else if((readyTime % 60) == 0 && readyTime != 0 && readyTime != 10) {
					Bukkit.broadcastMessage(ChatColor.GRAY + "The game will begin in " + readyTime/60 + " minutes! Make sure to select a kit with /kit.");
				} else if(readyTime <= 10 && readyTime > 0) {
					Bukkit.broadcastMessage(ChatColor.AQUA + "The game will begin in " + readyTime + " seconds!");
				} else if( readyTime <= 0 ) {
					if(Bukkit.getOnlinePlayers().length >= getConfig().getInt("General.Startup.PlayerThreshold")) {
						readyTimer.cancel();
						inProgress = true;
						Bukkit.broadcastMessage(ChatColor.RED + "The games have begun!");
						startGame();
					} else {
						Bukkit.broadcastMessage(ChatColor.RED + "Not enough players online, starting countdown again.");
						readyTime = getConfig().getInt("General.Startup.GameStartCooldown");
					}
				}
				readyTime--;
			}
		}, cooldown*20, 20);
	}

	public void startGame() {
		// Reset max game length to the config setting
		maxGameTime = getConfig().getInt("General.Game.MaxGameLength");

		// Reset current game length to 0
		gameTime = 0;
		
		// Add playing players to arrays, clear inventories and teleport the player to the pvpworld
		for(Player p : Bukkit.getOnlinePlayers()) {
			playingplayers.add(p);
			ignoreFallDamage.add(p);
			p.getInventory().clear();
			p.getInventory().setHelmet(null);
			p.getInventory().setChestplate(null);
			p.getInventory().setLeggings(null);
			p.getInventory().setBoots(null);
			p.teleport(new Location(Bukkit.getWorld(pvpworld), Bukkit.getWorld(pvpworld).getSpawnLocation().getBlockX(), 128, Bukkit.getWorld(pvpworld).getSpawnLocation().getBlockZ()));
		}

		// TODO: Replace this with a Bukkit scheduler.
		Timer fallTimer = new Timer();
		fallTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				ignoreFallDamage.clear();
			}
		}, 10*1000);

		gameTimer = Bukkit.getScheduler().runTaskTimer(this, new BukkitRunnable() {
			@Override
			public void run() {
				if(gameTime <=  maxGameTime)
					Bukkit.broadcastMessage(ChatColor.BLUE + "Time left: " + (maxGameTime-gameTime) + " seconds.");
					getLogger().info("1");
				if(gameTime >=  maxGameTime) {
					gameTimer.cancel();
					Bukkit.broadcastMessage(gameTime + " is above or equal to " + maxGameTime);
					int players = playingplayers.size();
					if(players > 0) {
						Bukkit.broadcastMessage(Integer.toString(players));
						endGame(playingplayers.get(0));
					}
					resetWorld(pvpworld);
					readyGame(cooldownOnStartup);
				}
				gameTime += 10;
			}
		}, 0, 10*20);
	}
	
	public void deleteWorld(final String file) {
		getLogger().info("Deleting world: " + pvpworld);

		// Delete the world folder.
		deleteWorldFile(new File(file + File.separator));

		// Delete the player files, probably not necessary, will remove later, I don't want to break anything right now.
		for(File f: new File("world/players").listFiles()) {
			f.delete();
		}
		getLogger().info("Deleted world: " + pvpworld);
	}

	public boolean deleteWorldFile(File file) {
		if (file.isDirectory()) {
			for (File subfile : file.listFiles()) {
				if (!deleteWorldFile(subfile)) {
					return false;
				}
			}
		} else if (!file.delete()) {
			System.out.println("Failed to delete " +file);
			return false;
		}
		return true;
	}

	public void createWorld(final String world) {
		getLogger().info("Creating new world: " + pvpworld);

		Bukkit.createWorld(new WorldCreator(world).environment(Environment.NORMAL));

		getLogger().info("Created world: " + pvpworld);
	}

	public void resetWorld(final String world) {
		// Run deleteWorld() and createWorld() in Runnables so that they actually work.
		Bukkit.getServer().getScheduler().runTask(this, new BukkitRunnable() {
			@Override
			public void run() {
				if(Bukkit.unloadWorld(world, false))
					getLogger().info("Unloaded world: " + pvpworld);
				deleteWorld(world);
			}
		});
		Bukkit.getServer().getScheduler().runTask(this, new BukkitRunnable() {
			@Override
			public void run() {
				createWorld(world);
			}
		});
	}
	
	// Do some game finishing stuff, will eventually add winner to leaderboard and record scores for the game in a db.
	// TODO: Do the leaderboard stuff.
	public boolean endGame(Player winner) {
		// TODO: Announce the winner
		inProgress = false;
		
		winner.getInventory().clear();
		winner.getInventory().setHelmet(null);
		winner.getInventory().setChestplate(null);
		winner.getInventory().setLeggings(null);
		winner.getInventory().setBoots(null);
		
		World w = Bukkit.getWorld(lobbyworld);
		winner.teleport(new Location(w, w.getSpawnLocation().getBlockX(), 128, w.getSpawnLocation().getBlockZ()));
		
		playingplayers.clear();
		return true;
	}
	
	// All the command stuff
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		// Get names of players who are still playing, haven't actually tested this.
		if(cmd.getName().equalsIgnoreCase("playing")){
			if(sender instanceof Player) {
				String players = "";
				for(Player p: playingplayers)
					players = players + ": " + p.getName() + " ";
				sender.sendMessage("Currently Playing" + players);
			} else {
				getLogger().info("/playing can only be run by players.");
			}
			return true;
		}
		return false; 
	}

}
