package de.SYRAPT0R.sleepblame;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;

public class BedListener implements Listener {

    private ArrayList<Player> sleepingList = new ArrayList<>();

    @EventHandler
    public void onPlayerBedEnter(PlayerBedEnterEvent event) {

        // we can immediately skip if the event was cancelled (i.e. it was day or monsters were nearby)
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();
        Logging.consoleLog(MessageFormat.format("{0} just went to bed", player.getDisplayName()));

        // get a full list of current players
        Collection<? extends Player> playerCollection = Bukkit.getServer().getOnlinePlayers();
        ArrayList<Player> playerList = new ArrayList<>(playerCollection);

        // if only one player is online, we abort
        if (playerList.size() == 1) {
            return;
        }

        // if this is the first person to sleep, send out a global broadcast
        if (sleepingList.isEmpty()) {
            Bukkit.getServer().broadcastMessage(MessageFormat.format(ChatColor.YELLOW + "{0} just went to sleep! Consider getting to a bed soon!", player.getDisplayName()));
        }

        // add the player to the sleeping list
        sleepingList.add(player);

        // check if only one person remains not sleeping
        if (playerList.size() - sleepingList.size() == 1) {
            // yep, only one person left! lets find out who it is...
            for (Player currentPlayer: playerList) {
                if (!sleepingList.contains(currentPlayer)) {
                    // GOTCHA
                    // we make sure that the player is not in the nether or the end.
                    String playerWorldName = currentPlayer.getWorld().getName();
                    if (playerWorldName.endsWith("_nether") || playerWorldName.endsWith("_end")) {
                        Logging.consoleLog("Last person awake is not in the overworld, skipping message...");
                        return;
                    }

                    // the last player is alive in the overworld, send him a reminder!
                    Logging.consoleLog(MessageFormat.format("{0} is the only player not sleeping in the overworld! Sending notification...", player.getDisplayName()));
                    currentPlayer.sendMessage(ChatColor.RED + "You are the only person not sleeping! Please consider getting to a bed or logging off for a second!");
                    break;
                }
            }
        }
    }

    @EventHandler
    public void onPlayerBedLeave(PlayerBedLeaveEvent event) {
        // the player that just got up
        Player player = event.getPlayer();

        Logging.consoleLog(MessageFormat.format("{0} just got up", player.getDisplayName()));

        // we just remove the person from the sleeping list
        sleepingList.remove(player);
    }
}
