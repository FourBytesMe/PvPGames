package me.fourbytes.pvpgames.util;

import org.bukkit.entity.Player;

/*
 * PlayerUtils class, provides all functions for resetting and modifying players, will be expanded upon.
 *  Fourbytes 2013
 */

public class PlayerUtils {
    /**
     * Resets a players health and food to 20, and empties their inventory
     *
     * @param player the player to be reset.
     */
    public static void resetPlayer(Player player) {
        player.setHealth(20);
        player.setFoodLevel(20);
        clearInv(player);
    }

    /**
     * Clears a players inventory.
     *
     * @param player the player to be cleared
     */
    public static void clearInv(Player player) {
        player.getInventory().clear();
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);
    }
}
