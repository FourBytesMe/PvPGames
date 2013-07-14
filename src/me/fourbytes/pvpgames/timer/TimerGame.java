package me.fourbytes.pvpgames.timer;

import me.fourbytes.pvpgames.PvPGamesBase;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class TimerGame extends BukkitRunnable {
    public PvPGamesBase plugin;

    public TimerGame(PvPGamesBase p) {
        plugin = p;
    }

    @Override
    public void run() {
        if (plugin.playingplayers.size() < 1) {
            plugin.endGame();
        }
        // Startup timing.
        if (plugin.gameTime <= plugin.maxGameTime) {
            int currentGameTime = plugin.maxGameTime - plugin.gameTime;
            if (currentGameTime == 60) {
                Bukkit.broadcastMessage(ChatColor.BLUE + "Time left: " + (currentGameTime / 60) + " minute.");
            } else if ((currentGameTime % 60) == 0 && currentGameTime != 0 && currentGameTime != 10) {
                Bukkit.broadcastMessage(ChatColor.BLUE + "Time left: " + (currentGameTime / 60) + " minutes.");
            } else if ((currentGameTime % 10) == 0 && currentGameTime != 0 && currentGameTime != 10 && currentGameTime < 60) {
                Bukkit.broadcastMessage(ChatColor.BLUE + "Time left: " + (currentGameTime) + " seconds.");
            } else if (currentGameTime <= 10 && currentGameTime > 0) {
                Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "The game will end in " + (currentGameTime) + " seconds!");
            }
        } else {
            plugin.endGame();
        }

        // Add 1 second to the current game time, as this runs every 1 second.
        plugin.gameTime += 5;
    }
}
