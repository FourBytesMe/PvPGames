/*
 * This file is the main file where most of the timing is called,
 * if anyone wants to move timing to another file to clean it up, then they can, just
 * submit a pull request and I'll accept it.
 */
package me.fourbytes.pvpgames;

import me.fourbytes.pvpgames.listener.ListenerCommand;
import me.fourbytes.pvpgames.listener.ListenerEvent;
import me.fourbytes.pvpgames.timer.TimerGame;
import me.fourbytes.pvpgames.timer.TimerPreGame;
import me.fourbytes.pvpgames.util.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;

/*
 * PvPGamesBase class, provides onEnable and onDisable functions. Other's will be moved if they haven't already.
 *  Fourbytes 2013
 */

public class PvPGamesBase extends JavaPlugin {

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
    private LeaderboardManager leaderboard;

    @Override
    public void onEnable() {
        lobbyWorld = getConfig().getString("Worlds.LobbyWorld");
        pvpWorld = getConfig().getString("Worlds.PvPWorld");
        spawn = new Location(Bukkit.getWorld(lobbyWorld), getConfig().getInt("Worlds.LobbySpawnCoordinates.x"), getConfig().getInt("Worlds.LobbySpawnCoordinates.y"), getConfig().getInt("Worlds.LobbySpawnCoordinates.z"));

        new WorldReset(this, pvpWorld);

        getConfig().options().copyDefaults(true);
        saveConfig();
        leaderboard = new LeaderboardManager(this);

        new ListenerEvent(this, lobbyWorld);

        getCommand("playing").setExecutor(new ListenerCommand(this));
        getCommand("startgame").setExecutor(new ListenerCommand(this));
        getCommand("leaderboard").setExecutor(new ListenerCommand(this));
        getCommand("stats").setExecutor(new ListenerCommand(this));

        readyGame();
    }

    @Override
    public void onDisable() {
        Bukkit.unloadWorld(pvpWorld, false);
        leaderboard.save();
    }

    public void readyGame() {
        readyTime = getConfig().getInt("General.Startup.GameStartCooldown");
        readyTimer = Bukkit.getScheduler().runTaskTimer(this, new TimerPreGame(this), 0, 20);
    }

    public void startGame() {
        maxGameTime = getConfig().getInt("General.Game.MaxGameLength");
        gameTime = 0;

        for (Player p : Bukkit.getOnlinePlayers()) {
            playingplayers.add(p.getName());
            ignoreFallDamage.add(p.getName());

            PlayerUtils.resetPlayer(p);
            p.teleport(new Location(Bukkit.getWorld(pvpWorld), Bukkit.getWorld(pvpWorld).getSpawnLocation().getBlockX(), 128, Bukkit.getWorld(pvpWorld).getSpawnLocation().getBlockZ()));
        }

        this.getServer().getScheduler().scheduleSyncDelayedTask(this, new BukkitRunnable() {
            public void run() {
                ignoreFallDamage.clear();
            }
        }, 20 * 5);

        gameTimer = Bukkit.getScheduler().runTaskTimer(this, new TimerGame(this), 0, 10 * 20);
    }

    public void endGame(Player winner) {
        gameTimer.cancel();
        inProgress = false;

        Bukkit.broadcastMessage(ChatColor.GOLD + "####################################################");
        Bukkit.broadcastMessage(ChatColor.GOLD + "# " + winner.getName() + " HAS WON THIS MATCH.");
        Bukkit.broadcastMessage(ChatColor.GOLD + "# NEXT MATCH STARTING SOON.");
        Bukkit.broadcastMessage(ChatColor.GOLD + "####################################################");

        leaderboard.addToWins(winner.getName(), 1);
        PlayerUtils.resetPlayer(winner);
        winner.teleport(spawn);

        playingplayers.clear();
        new WorldReset(this, pvpWorld);
        readyGame();
    }

    public void endGame() {
        gameTimer.cancel();
        inProgress = false;

        Bukkit.broadcastMessage(ChatColor.GOLD + "####################################################");
        Bukkit.broadcastMessage(ChatColor.GOLD + "# TIME RAN OUT AND NOBODY WON. NEXT MATCH STARTING SOON!  ");
        Bukkit.broadcastMessage(ChatColor.GOLD + "# NEXT MATCH STARTING SOON!  ");
        Bukkit.broadcastMessage(ChatColor.GOLD + "####################################################");

        for (String p : playingplayers) {
            Player player = Bukkit.getPlayer(p);
            PlayerUtils.resetPlayer(player);
            player.teleport(spawn);
        }

        playingplayers.clear();
        new WorldReset(this, pvpWorld);
        readyGame();
    }
}
