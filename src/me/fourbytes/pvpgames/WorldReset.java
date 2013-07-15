package me.fourbytes.pvpgames;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;

public class WorldReset {
    private PvPGamesBase plugin;
    public String world;

    public WorldReset(PvPGamesBase p, String w) {
        world = w;
        plugin = p;
        // Run deleteWorld() and createWorld() in Runnables so that they actually work.
        Bukkit.getServer().getScheduler().runTask(plugin, new BukkitRunnable() {
            @Override
            public void run() {
                if (Bukkit.unloadWorld(world, false))
                    plugin.getLogger().info("Unloaded world: " + plugin.pvpWorld);
                deleteWorld(world);
            }
        });
        Bukkit.getServer().getScheduler().runTask(plugin, new BukkitRunnable() {
            @Override
            public void run() {
                createWorld(world);
            }
        });
    }

    public void deleteWorld(final String file) {
        plugin.getLogger().info("Deleting world: " + plugin.pvpWorld);

        // Delete the world folder.
        deleteWorldFile(new File(file + File.separator));

        plugin.getLogger().info("Deleted world: " + plugin.pvpWorld);
    }

    public boolean deleteWorldFile(File file) {
        if (file.isDirectory()) {
            for (File subfile : file.listFiles()) {
                if (!deleteWorldFile(subfile)) {
                    return false;
                }
            }
        } else if (!file.delete()) {
            System.out.println("Failed to delete " + file);
            return false;
        }
        return true;
    }

    public void createWorld(final String world) {
        plugin.getLogger().info("Creating new world: " + plugin.pvpWorld);

        Bukkit.createWorld(new WorldCreator(world).environment(World.Environment.NORMAL));

        plugin.getLogger().info("Created world: " + plugin.pvpWorld);
    }
}
