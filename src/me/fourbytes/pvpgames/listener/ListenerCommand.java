package me.fourbytes.pvpgames.listener;

import me.fourbytes.pvpgames.LeaderboardManager;
import me.fourbytes.pvpgames.PvPGamesBase;
import me.fourbytes.pvpgames.util.ChatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/*
 * PvPGamesBase class, provides onEnable and onDisable functions. Other's will be moved if they haven't already.
 *  Fourbytes 2013
 */

public class ListenerCommand implements CommandExecutor {
    private PvPGamesBase plugin; // pointer to your main class, unrequired if you don't need methods from the main class

    public ListenerCommand(PvPGamesBase p) {
        plugin = p;
    }

    /*
     * All commands
     */
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("playing")) {
            if (sender instanceof Player) {
                String players = "";
                for (String p : PvPGamesBase.playingplayers)
                    players = players + ": " + p + " ";
                sender.sendMessage("Currently Playing" + players);
            } else {
                plugin.getLogger().info("/playing can only be run by players.");
            }
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("startgame")) {
            if (sender.isOp()) {
                if (!PvPGamesBase.inProgress) {
                    plugin.readyTime = 0;
                    sender.sendMessage("Game starting...");
                } else {
                    sender.sendMessage("Game is already in progress.");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Sorry, you must be op to use this command.");
            }
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("leaderboard")) {
            if (sender instanceof Player) {

                sender.sendMessage(ChatColor.BOLD + "" + ChatColor.AQUA + "Coming soon!");
            } else {
                plugin.getLogger().info("/playing can only be run by players.");
            }

            return true;
        }
        if (cmd.getName().equalsIgnoreCase("stats")) {
            String player = sender.getName();
            if (args.length > 0)
                player = args[0];
            LeaderboardManager leaderboard = new LeaderboardManager(plugin);
            sender.sendMessage(ChatColor.AQUA + "####################################################");
            sender.sendMessage(ChatColor.AQUA + ChatUtils.centerText(ChatColor.GRAY + "PLAYER STAT'S FOR " + sender.getName(), ChatColor.AQUA + "#"));
            sender.sendMessage(ChatColor.AQUA + ChatUtils.centerText(ChatColor.GRAY + " WINS: " + leaderboard.getWins(player) + " KILLS: " + leaderboard.getKills(player) + " DEATHS: " + leaderboard.getDeaths(player), ChatColor.AQUA + "#"));
            sender.sendMessage(ChatColor.AQUA + "####################################################");

            return true;
        }

        return false;
    }
}
