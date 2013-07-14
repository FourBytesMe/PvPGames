package me.fourbytes.pvpgames.listener;

import me.fourbytes.pvpgames.PvPGamesBase;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ListenerCommand implements CommandExecutor {
    private PvPGamesBase plugin; // pointer to your main class, unrequired if you don't need methods from the main class

    public ListenerCommand(PvPGamesBase p) {
        plugin = p;
    }

    // All the command stuff
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // Get names of players who are still playing, haven't actually tested this.
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
        return false;
    }
}
