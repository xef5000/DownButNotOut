package me.rainstxrm.downbutnotout.Events;

import me.rainstxrm.downbutnotout.CustomEvents.KOEvent;
import me.rainstxrm.downbutnotout.CustomEvents.ReviveEvent;
import me.rainstxrm.downbutnotout.DownButNotOut;
import me.rainstxrm.downbutnotout.KOHandler;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerMoveEvent;

public class DownedEvents implements Listener {

    private final DownButNotOut plugin;

    public DownedEvents(DownButNotOut plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void KOPlayer(EntityDamageByEntityEvent e){
        if (DownButNotOut.plugin.getConfig().getBoolean("enable-downs")){
            if (!(e.getEntity() instanceof Player)){
                return;
            }
            Player player = (Player) e.getEntity();

            if (KOHandler.getDownedPlayers().contains(player.getUniqueId())){
                return;
            }

            setKO(e, player);
        }
    }

    private void setKO(EntityDamageEvent e, Player player) {
        double healthOnAttack = player.getHealth();
        double attackDamage = e.getDamage();
        if (healthOnAttack - attackDamage <= 0){
            KOEvent event = new KOEvent(player);
            Bukkit.getPluginManager().callEvent(event);
            e.setDamage(0);
            player.setHealth(20);
            KOHandler.KOPlayer(player.getUniqueId());
            KOHandler.spawnStand(player);
            String titleTop = plugin.getConfig().getString("messages.title-top").replace("(p)", player.getDisplayName());
            String titleBottom = plugin.getConfig().getString("messages.title-bottom").replace("(p)", player.getDisplayName());
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

    @EventHandler
    public void onDamageWithNoCause(EntityDamageEvent e){
        if (DownButNotOut.plugin.getConfig().getBoolean("enable-downs")){
            if (!(e.getEntity() instanceof Player)){
                return;
            }
            Player player = (Player) e.getEntity();

            if (e.getCause().equals(EntityDamageEvent.DamageCause.VOID)){
                return;
            }

            if (KOHandler.getDownedPlayers().contains(player.getUniqueId())){
                return;
            }

            setKO(e, player);
        }
    }

    @EventHandler
    public void unKOPlayer(PlayerDeathEvent e){
        Player player = e.getEntity();
        if (KOHandler.getDownedPlayers().contains(e.getEntity().getUniqueId())) {
            String bledOut = plugin.getConfig().getString("messages.bled-out").replace("(p)", player.getDisplayName());
            e.setDeathMessage(ChatColor.translateAlternateColorCodes('&', bledOut));
            for (Entity en : player.getNearbyEntities(2,2,2)){
                if (en.hasMetadata("DownedStand") || en.hasMetadata("ReviveStand")){
                    en.remove();
                }
            }

            KOHandler.removePlayer(player.getUniqueId());
        }
    }

    @EventHandler
    public void noMoveWhenDowned(PlayerMoveEvent e){
        if (KOHandler.getDownedPlayers().contains(e.getPlayer().getUniqueId())){
            Location from = new Location(e.getFrom().getWorld(), e.getFrom().getX(), e.getFrom().getY(), e.getFrom().getZ());
            Location to = new Location(e.getFrom().getWorld(), e.getTo().getX(), e.getTo().getY(), e.getTo().getZ());
            if (from.getX() != to.getX() || from.getZ() != to.getZ()){
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void noDamageWhenDowned(EntityDamageByEntityEvent e){
        if (!(e.getEntity() instanceof Player)){
            return;
        }
        Player player = (Player) e.getEntity();
        if (!DownButNotOut.plugin.getConfig().getBoolean("damage-while-down")){
            if (KOHandler.getDownedPlayers().contains(player.getUniqueId())){
                e.setCancelled(true);
                //e.getDamager().setVelocity(player.getLocation().toVector().normalize().multiply(-4));
            }
        }
    }

    @EventHandler
    public void noSuffocateWhenDowned(EntityDamageEvent e){
        if (e.getEntity() instanceof Player){
            Player player = (Player) e.getEntity();
            if (e.getCause().equals(EntityDamageEvent.DamageCause.SUFFOCATION)){
                if (KOHandler.getDownedPlayers().contains(player.getUniqueId())){
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void stayDown(EntityToggleSwimEvent e) {
        if (!(e.getEntity() instanceof Player))
            return;
        Player player = (Player)e.getEntity();
        if (KOHandler.getDownedPlayers().contains(player.getUniqueId()) &&
                player.isSwimming())
            e.setCancelled(true);
    }
}
