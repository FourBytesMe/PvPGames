package me.fourbytes.pvpgames.timer;

import me.fourbytes.pvpgames.PvPGamesBase;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class TimerGame extends BukkitRunnable {
    private PvPGamesBase plugin;
    private int countdown;
    private int c = 10;
    private int currentGameTime;

    public TimerGame(PvPGamesBase p) {
        plugin = p;
    }

    @Override
    public void run() {
        if (PvPGamesBase.playingplayers.size() < 1) {
            plugin.endGame();
        }
        // Startup timing.
        currentGameTime = plugin.maxGameTime - plugin.gameTime;
        if (plugin.gameTime <= plugin.maxGameTime && currentGameTime > 0) {
            if (currentGameTime == 60) {
                Bukkit.broadcastMessage(ChatColor.BLUE + "Time left: " + (currentGameTime / 60) + " minute.");
            } else if ((currentGameTime % 60) == 0 && currentGameTime != 0 && currentGameTime != 10) {
                Bukkit.broadcastMessage(ChatColor.BLUE + "Time left: " + (currentGameTime / 60) + " minutes.");
            } else if ((currentGameTime % 10) == 0 && currentGameTime != 0 && currentGameTime != 10 && currentGameTime < 60) {
                Bukkit.broadcastMessage(ChatColor.BLUE + "Time left: " + currentGameTime + " seconds.");
            } else if (currentGameTime <= 10 && currentGameTime > 0) {
                countdown = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (c <= 1)
                            Bukkit.getServer().getScheduler().cancelTask(countdown);
                        Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "The game will end in " + c + " seconds!");
                        c--;
                    }
                }, 0L, 20L);
            }
        } else {
            plugin.endGame();
        }

        // Add 10 seconds to the current game time, as this runs every 10 seconds.
        plugin.gameTime += 10;
    }
}
