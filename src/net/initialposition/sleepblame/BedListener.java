package net.initialposition.sleepblame;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

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

        // make sure only players that meet certain criteria are considered
        for (Player currentPlayer : playerList) {

            // remove everyone not in the overworld from the player list
            String worldName = currentPlayer.getWorld().getName();
            if (worldName.endsWith("_nether") || worldName.endsWith("_end")) {
                playerList.remove(currentPlayer);
                continue;
            }

            // remove everyone not in survival or adventure mode
            GameMode playerGameMode = player.getGameMode();
            if (playerGameMode == GameMode.CREATIVE || playerGameMode == GameMode.SPECTATOR) {
                playerList.remove(currentPlayer);
            }
        }

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
            for (Player currentPlayer : playerList) {
                if (!sleepingList.contains(currentPlayer)) {

                    // found him!
                    Logging.consoleLog(MessageFormat.format("{0} is the only player not sleeping in the overworld! Sending notification...", currentPlayer.getDisplayName()));
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
    public void onDisconnectInBed(PlayerQuitEvent event) {
        // player that logged out
        Player player = event.getPlayer();

        // check if player was sleeping (i.e. on the sleeping list) and remove him if he was
        if (sleepingList.contains(player)) {
            sleepingList.remove(player);
            Logging.consoleLog(MessageFormat.format("{0} just logged out while sleeping", player.getDisplayName()));
        }
    }
}
