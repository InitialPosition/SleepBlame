package de.SYRAPT0R.sleepblame;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.world.TimeSkipEvent;

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
            // we got a blame victim! lets find out who it is...
            for (Player currentPlayer: playerList) {
                if (!sleepingList.contains(currentPlayer)) {
                    // GOTCHA
                    Logging.consoleLog(MessageFormat.format("{0} is the only player not sleeping! Sending notification...", player.getDisplayName()));
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

    @EventHandler
    public void onTimeSkip(TimeSkipEvent timeSkipEvent) {
        if (timeSkipEvent.getSkipReason() == TimeSkipEvent.SkipReason.NIGHT_SKIP) {
            Logging.consoleLog("Everyone slept successfully! Resetting sleeping list...");
            sleepingList.clear();
        }
    }
}
