package me.rainstxrm.downbutnotout.Events;

import me.rainstxrm.downbutnotout.DownButNotOut;
import me.rainstxrm.downbutnotout.KOHandler;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.UUID;

/**
 * Handles events related to player connections (join/quit)
 * to ensure armor stands are properly tracked
 */
public class PlayerConnectionEvents implements Listener {

    // Store UUIDs of players who were downed when they disconnected
    private static HashMap<UUID, Boolean> disconnectedDownedPlayers = new HashMap<>();

    /**
     * When a player quits, if they are downed, we need to remove their armor stands
     * but remember their state so they can be KO'd again when they reconnect
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID playerID = player.getUniqueId();

        // If the player is downed when they disconnect
        if (KOHandler.getDownedPlayers().contains(playerID)) {
            // Store that this player was downed when they disconnected
            disconnectedDownedPlayers.put(playerID, true);

            // Remove the armor stands to prevent them from being "lost"
            KOHandler.removeArmorStands(playerID);

            // Remove the player from the downed players list temporarily
            KOHandler.removePlayer(playerID);
        } else {
            // If they weren't downed, make sure they're not in our tracking map
            disconnectedDownedPlayers.remove(playerID);
        }
    }

    /**
     * When a player joins, check if they were downed before disconnecting
     * If they were, put them back in the KO state
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerID = player.getUniqueId();

        // Check if this player was downed when they disconnected
        if (disconnectedDownedPlayers.containsKey(playerID) && disconnectedDownedPlayers.get(playerID)) {
            // Remove them from our tracking map
            disconnectedDownedPlayers.remove(playerID);

            // Put them back in the KO state
            KOHandler.KOPlayer(playerID);
            KOHandler.spawnStand(player);

            // Send them the KO message and start the countdown
            String titleTop = DownButNotOut.plugin.getConfig().getString("messages.title-top").replace("(p)", player.getDisplayName());
            String titleBottom = DownButNotOut.plugin.getConfig().getString("messages.title-bottom").replace("(p)", player.getDisplayName());
            player.sendTitle(
                    ChatColor.translateAlternateColorCodes('&', titleTop),
                    ChatColor.translateAlternateColorCodes('&', titleBottom),
                    0, 40, 0)
            ;
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.5f, 0.5f);
            player.setSwimming(true);
            KOHandler.playerCountDown(player);
        }
    }
}
