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
                plugin.getLogger().info("Deleting world: " + plugin.pvpWorld);

                // Delete the world folder.
                deleteWorld(plugin.pvpWorld);

                plugin.getLogger().info("Deleted world: " + plugin.pvpWorld);
            }
        });
        Bukkit.getServer().getScheduler().runTask(plugin, new BukkitRunnable() {
            @Override
            public void run() {
                createWorld(world);
            }
        });
    }

    /**
     * Delete a world.
     *
     * @param world The name of the world to be deleted
     * @return Returns true if deleting was succesful
     */
    public boolean deleteWorld(String world) {
        File file = new File(plugin.pvpWorld + File.separator);
        if (deleteFile(file))
            return true;
        else
            return false;
    }

    /**
     * Create a new world.
     * @param world The name of the world to be created.
     */
    public void createWorld(final String world) {
        plugin.getLogger().info("Creating new world: " + plugin.pvpWorld);

        Bukkit.createWorld(new WorldCreator(world).environment(World.Environment.NORMAL));

        plugin.getLogger().info("Created world: " + plugin.pvpWorld);
    }

    /**
     * Delete a specified file, if the specified file is a folder, it will loop again through the folder.
     * @param file The file to delete
     * @return Returns true if succesful.
     */
    public boolean deleteFile(File file) {
        if (file.isDirectory()) {
            for (File subfile : file.listFiles()) {
                if (!deleteFile(subfile)) {
                    return false;
                }
            }
        } else if (!file.delete()) {
            System.out.println("Failed to delete " + file);
            return false;
        }
        return true;
    }
}
