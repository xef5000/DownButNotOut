package me.rainstxrm.downbutnotout.Events;

import com.google.common.eventbus.AllowConcurrentEvents;
import me.rainstxrm.downbutnotout.DownButNotOut;
import me.rainstxrm.downbutnotout.KOHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ReviveEvents implements Listener {

    HashMap<UUID, Integer> reviveTimer = new HashMap<>(); //The time the reviver has before the clicks reset
    HashMap<UUID, Integer> reviveClicks = new HashMap<>(); //The amount of clicks the downed player has
    HashMap<UUID, UUID> reviving = new HashMap<>(); //A list of players currently reviving and the downed player getting revived. Used to stop players from reviving multiple players

    Configuration config = DownButNotOut.plugin.getConfig();

    @EventHandler
    public void StartRevivalProcess(PlayerInteractAtEntityEvent e){
        if (!(e.getRightClicked() instanceof Player)){
            return;
        }
        Player player = e.getPlayer();
        Player downed = (Player) e.getRightClicked();

        if (!KOHandler.getDownedPlayers().contains(downed.getUniqueId()) || KOHandler.getDownedPlayers().contains(player.getUniqueId())){
            return;
        }
        if (reviving.containsKey(player.getUniqueId()) || reviving.containsValue(downed.getUniqueId())){
            return;
        }

        String revivingBy = DownButNotOut.plugin.getConfig().getString("messages.reviving-by").replace("(p)", player.getDisplayName());
        String revivingOther = DownButNotOut.plugin.getConfig().getString("messages.reviving-other").replace("(p)", downed.getDisplayName());

        downed.sendTitle(ChatColor.translateAlternateColorCodes('&', revivingBy), null, 0, 40, 0);
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', revivingOther));

        reviving.put(player.getUniqueId(), downed.getUniqueId());
        reviveTimer.put(player.getUniqueId(), 20);
        reviveClicks.put(downed.getUniqueId(), 0);
        revival(player.getUniqueId(), downed.getUniqueId(), DownButNotOut.plugin.getConfig().getInt("clicks-to-revive"));
    }

    @EventHandler
    public void onDownedRightClick(PlayerInteractEntityEvent e){
        if (!(e.getRightClicked() instanceof Player)){
            return;
        }
        Player player = e.getPlayer();
        Player downed = (Player) e.getRightClicked();
        int reqClicks = DownButNotOut.plugin.getConfig().getInt("clicks-to-revive");
        if (!e.getHand().equals(EquipmentSlot.HAND)){
            return;
        }
        if (reviving.containsKey(player.getUniqueId()) || reviving.containsValue(downed.getUniqueId())){
           reviveTimer.replace(player.getUniqueId(), 20);
           int clicks = reviveClicks.get(downed.getUniqueId()) + 1;
           reviveClicks.replace(downed.getUniqueId(), clicks);

            if (DownButNotOut.plugin.getConfig().getBoolean("play-sounds")){
                float percent = (reviveClicks.get(downed.getUniqueId()).floatValue() / reqClicks) * 2;
                downed.playSound(downed.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, percent);
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, percent);
            }
            String clicksLeft = DownButNotOut.plugin.getConfig().getString("messages.clicks-left").replace("(c)", Integer.toString(reqClicks - clicks));
            String clicksLeftText = DownButNotOut.plugin.getConfig().getString("messages.clicks-left-text");
            player.sendTitle(
                    ChatColor.translateAlternateColorCodes('&', clicksLeft),
                    ChatColor.translateAlternateColorCodes('&', clicksLeftText),
                    0, 20, 0
            );
        }
    }

    public void revival(UUID player, UUID downed, int clicks){
        new BukkitRunnable(){
            int time;
            @Override
            public void run() {
                int clicksSoFar = -1;
                try {
                    clicksSoFar = reviveClicks.get(downed);
                } catch (Exception e){
                    cancel();
                }
                if (clicksSoFar >= clicks){
                    reviveTimer.remove(player);
                    reviveClicks.remove(downed);
                    reviving.remove(player, downed);

                    String reviveBy = DownButNotOut.plugin.getConfig().getString("messages.revive-by").replace("(p)", Bukkit.getPlayer(player).getDisplayName());
                    Bukkit.getPlayer(downed).sendMessage(ChatColor.translateAlternateColorCodes('&', reviveBy));
                    Bukkit.getPlayer(downed).playSound(Bukkit.getPlayer(downed).getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.8f);

                    String reviveOther = DownButNotOut.plugin.getConfig().getString("messages.revive-other").replace("(p)", Bukkit.getPlayer(downed).getDisplayName());
                    Bukkit.getPlayer(player).sendMessage(ChatColor.translateAlternateColorCodes('&', reviveOther));
                    KOHandler.revivePlayer(downed);
                    cancel();
                }
                if (Bukkit.getPlayer(downed).isDead()){
                    reviveTimer.remove(player);
                    reviveClicks.remove(downed);
                    reviving.remove(player, downed);
                    cancel();
                }
                if (!KOHandler.getDownedPlayers().contains(downed)){
                    cancel();
                }
                if (Bukkit.getPlayer(downed).isDead()){
                    cancel();
                }
                try {
                    time = reviveTimer.get(player) - 1;
                } catch (Exception e){
                    cancel();
                }
                reviveTimer.replace(player, time);
                try{
                    if (reviveTimer.get(player) <= 0){
                        Bukkit.getPlayer(downed).playSound(Bukkit.getPlayer(downed).getLocation(), Sound.ENTITY_WOLF_WHINE, 1.0f, 1.8f);
                        Bukkit.getPlayer(player).playSound(Bukkit.getPlayer(downed).getLocation(), Sound.ENTITY_WOLF_WHINE, 1.0f, 1.8f);
                        reviveTimer.remove(player);
                        reviveClicks.remove(downed);
                        reviving.remove(player, downed);
                        cancel();
                    }
                } catch (Exception e){
                    cancel();
                }
            }
        }.runTaskTimer(DownButNotOut.plugin, 0, 1);
    }
}
