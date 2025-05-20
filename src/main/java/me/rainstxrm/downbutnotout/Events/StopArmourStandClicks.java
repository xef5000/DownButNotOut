package me.rainstxrm.downbutnotout.Events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public class StopArmourStandClicks implements Listener {
    @EventHandler
    public void onStandClick(PlayerInteractAtEntityEvent e) {
        if (!(e.getRightClicked() instanceof org.bukkit.entity.ArmorStand))
            return;
        if (e.getRightClicked().hasMetadata("DownedStand") || e.getRightClicked().hasMetadata("ReviveStand"))
            e.setCancelled(true);
    }
}
