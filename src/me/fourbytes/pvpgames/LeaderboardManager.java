package me.fourbytes.pvpgames;

/*
 * LeaderboardManager class, provides all functions for getting and setting leaderboard values.
 *  Oscar Rainford 2013
 */

import me.fourbytes.pvpgames.util.ConfigAccessor;

import java.util.Arrays;
import java.util.List;

public class LeaderboardManager {
    private ConfigAccessor config;

    public LeaderboardManager(PvPGamesBase plugin) {
        config = new ConfigAccessor(plugin, "leaderboard.yml");
        config.saveDefaultConfig();
        config.saveConfig();
    }

    /**
     * Adds a certain amount to the current kill count.
     *
     * @param player Player whos stats are to be changed.
     * @param k      Amount to add to current kill count.
     */
    public void addToKills(String player, int k) {
        @SuppressWarnings("unchecked")
        List<Integer> oldscores = (List<Integer>) config.getConfig().getList("leaderboard." + player, Arrays.asList(0, 0, 0));
        config.getConfig().set("leaderboard." + player, Arrays.asList(oldscores.get(0), oldscores.get(1) + k, oldscores.get(2)));
    }

    /**
     * Adds a certain amount to the current death count.
     *
     * @param player Player whos stats are to be changed.
     * @param k      Amount to add to current death count.
     */
    public void addToDeaths(String player, int k) {
        @SuppressWarnings("unchecked")
        List<Integer> oldscores = (List<Integer>) config.getConfig().getList("leaderboard." + player, Arrays.asList(0, 0, 0));
        config.getConfig().set("leaderboard." + player, Arrays.asList(oldscores.get(0), oldscores.get(1), oldscores.get(2) + k));
    }

    /**
     * Adds a certain amount to the current win count.
     *
     * @param player Player whos stats are to be changed.
     * @param k      Amount to add to current win count.
     */
    public void addToWins(String player, int k) {
        @SuppressWarnings("unchecked")
        List<Integer> oldscores = (List<Integer>) config.getConfig().getList("leaderboard." + player, Arrays.asList(0, 0, 0));
        config.getConfig().set("leaderboard." + player, Arrays.asList(oldscores.get(0) + k, oldscores.get(1), oldscores.get(2)));
    }

    public void save() {
        config.saveConfig();
    }
}
