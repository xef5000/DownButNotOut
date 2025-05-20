package me.rainstxrm.downbutnotout.CustomEvents;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class ReviveEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final UUID player;

    public ReviveEvent(UUID player){
        this.player = player;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public UUID getPlayer() {
        return player;
    }
}
