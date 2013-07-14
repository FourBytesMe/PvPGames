package me.fourbytes.pvpgames.timer;

import me.fourbytes.pvpgames.PvPGamesBase;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;

public class TimerPreGame extends BukkitRunnable {
    public PvPGamesBase plugin;

    public TimerPreGame(PvPGamesBase p) {
        plugin = p;
    }

    @Override
    public void run() {
        // Keep all worlds daytime while a game isn't running.
        // TODO: Move this to a seperate function and keep lobby world daytime even when world is running.
        Iterator<World> worlds = plugin.getServer().getWorlds().iterator();
        while (worlds.hasNext()) {
            worlds.next().setTime(6000);
        }

        // Startup timing.
        if (plugin.readyTime == 60) {
            Bukkit.broadcastMessage(ChatColor.GRAY + "The game will begin in " + plugin.readyTime / 60 + " minute! Make sure to select a kit with /kit.");
        } else if ((plugin.readyTime % 60) == 0 && plugin.readyTime != 0 && plugin.readyTime != 10) {
            Bukkit.broadcastMessage(ChatColor.GRAY + "The game will begin in " + plugin.readyTime / 60 + " minutes! Make sure to select a kit with /kit.");
        } else if ((plugin.readyTime % 10) == 0 && plugin.readyTime != 0 && plugin.readyTime != 10 && plugin.readyTime < 60) {
            Bukkit.broadcastMessage(ChatColor.GRAY + "The game will begin in " + plugin.readyTime + " seconds! Make sure to select a kit with /kit.");
        } else if (plugin.readyTime <= 10 && plugin.readyTime > 0) {
            Bukkit.broadcastMessage(ChatColor.AQUA + "The game will begin in " + plugin.readyTime + " seconds!");
        } else if (plugin.readyTime <= 0) {
            if (Bukkit.getOnlinePlayers().length >= plugin.getConfig().getInt("General.Startup.PlayerThreshold")) {
                plugin.readyTime = plugin.getConfig().getInt("General.Startup.GameStartCooldown");
                Bukkit.broadcastMessage(String.valueOf(plugin.readyTime));
                PvPGamesBase.readyTimer.cancel();
                PvPGamesBase.inProgress = true;
                Bukkit.broadcastMessage(ChatColor.RED + "The games have begun!");
                plugin.startGame();
            } else {
                Bukkit.broadcastMessage(ChatColor.RED + "Not enough players online, starting countdown again.");
                plugin.readyTime = plugin.getConfig().getInt("General.Startup.GameStartCooldown");
            }
        }
        plugin.readyTime--;
    }
}
