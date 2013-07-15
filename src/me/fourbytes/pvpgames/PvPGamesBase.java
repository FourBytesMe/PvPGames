/*
 * This file is the main file where most of the timing is called,
 * if anyone wants to move timing to another file to clean it up, then they can, just
 * submit a pull request and I'll accept it.
 */
package me.fourbytes.pvpgames;

import me.fourbytes.pvpgames.listener.ListenerCommand;
import me.fourbytes.pvpgames.listener.ListenerGlobalEvent;
import me.fourbytes.pvpgames.listener.ListenerInGameEvent;
import me.fourbytes.pvpgames.timer.TimerGame;
import me.fourbytes.pvpgames.timer.TimerPreGame;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;

public class PvPGamesBase extends JavaPlugin {

    // Set some variables
    public static Boolean inProgress = false;
    public int readyTime;
    public int gameTime;
    public int maxGameTime;
    public String pvpWorld;
    public String lobbyWorld;
    public static ArrayList<String> playingplayers = new ArrayList<String>();
    public static ArrayList<String> ignoreFallDamage = new ArrayList<String>();
    public static BukkitTask readyTimer;
    public static BukkitTask gameTimer;
    public Location spawn;

    @Override
    public void onEnable() {
        // Get some config options.
        lobbyWorld = getConfig().getString("Worlds.LobbyWorld");
        pvpWorld = getConfig().getString("Worlds.PvPWorld");
        spawn = new Location(Bukkit.getWorld(lobbyWorld), getConfig().getInt("Worlds.LobbySpawnCoordinates.x"), getConfig().getInt("Worlds.LobbySpawnCoordinates.y"), getConfig().getInt("Worlds.LobbySpawnCoordinates.z"));
        // Reset world.
        new WorldReset(this, pvpWorld);

        // Save Default Configuration
        getConfig().options().copyDefaults(true);
        saveConfig();

        // Setup listeners.
        new ListenerInGameEvent(this);
        new ListenerGlobalEvent(this, lobbyWorld);

        getCommand("playing").setExecutor(new ListenerCommand(this));
        getCommand("startgame").setExecutor(new ListenerCommand(this));

        // And finally, start the game.
        readyGame();
    }


    @Override
    public void onDisable() {
        // Unload the world so we don't get world saving errors on shutdown
        Bukkit.unloadWorld(pvpWorld, false);
    }

    public void readyGame() {
        // Reset the time, incase it is being run again.
        readyTime = getConfig().getInt("General.Startup.GameStartCooldown");

        readyTimer = Bukkit.getScheduler().runTaskTimer(this, new TimerPreGame(this), 0, 20);
    }

    public void startGame() {
        // Reset max game length to the config setting and current game length to 0
        maxGameTime = getConfig().getInt("General.Game.MaxGameLength");
        gameTime = 0;

        // Add playing players to arrays, clear inventories and teleport the player to the pvpWorld
        for (Player p : Bukkit.getOnlinePlayers()) {
            playingplayers.add(p.getName());
            ignoreFallDamage.add(p.getName());
            p.getInventory().clear();
            p.getInventory().setHelmet(null);
            p.getInventory().setChestplate(null);
            p.getInventory().setLeggings(null);
            p.getInventory().setBoots(null);
            p.teleport(new Location(Bukkit.getWorld(pvpWorld), Bukkit.getWorld(pvpWorld).getSpawnLocation().getBlockX(), 128, Bukkit.getWorld(pvpWorld).getSpawnLocation().getBlockZ()));
            p.setHealth(20);
            p.setFoodLevel(20);
        }

        // Run this after five seconds, makes sure that players don't get fall damage when a game is started, given that
        // we spawn them at y 128 to prevent suffocating.
        this.getServer().getScheduler().scheduleSyncDelayedTask(this, new BukkitRunnable() {
            public void run() {
                ignoreFallDamage.clear();
            }
        }, 20 * 5);

        // Start the game timer, this does all of the checking to find when a game finishes, and counts down to the time limit.
        gameTimer = Bukkit.getScheduler().runTaskTimer(this, new TimerGame(this), 0, 10 * 20);
    }

    // Do some game finishing stuff, will eventually add winner to leaderboard and record scores for the game in a db.
    // TODO: Do the leaderboard stuff.
    public void endGame(Player winner) {
        // Cancel the game.
        gameTimer.cancel();

        winner.teleport(spawn);

        Bukkit.broadcastMessage(ChatColor.GOLD + winner.getName() + " has won this match! Next match starting soon!");

        inProgress = false;

        winner.getInventory().clear();
        winner.getInventory().setHelmet(null);
        winner.getInventory().setChestplate(null);
        winner.getInventory().setLeggings(null);
        winner.getInventory().setBoots(null);
        winner.setHealth(20);
        winner.setFoodLevel(20);

        playingplayers.clear();
        new WorldReset(this, pvpWorld);
        readyGame();
    }

    // End the game, only called if there is no winner/time ran out/everyone logged out.
    public void endGame() {
        gameTimer.cancel();
        Bukkit.broadcastMessage(ChatColor.GOLD + "Time ran out and nobody won. Next match starting soon!");

        for (String p : playingplayers) {
            Bukkit.getPlayer(p).teleport(spawn);
            Bukkit.getPlayer(p).setHealth(20);
            Bukkit.getPlayer(p).setFoodLevel(20);
        }

        inProgress = false;
        playingplayers.clear();

        new WorldReset(this, pvpWorld);
        readyGame();
    }
}
