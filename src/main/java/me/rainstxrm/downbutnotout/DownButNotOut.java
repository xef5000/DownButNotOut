package me.rainstxrm.downbutnotout;

import me.rainstxrm.downbutnotout.Commands.*;
import me.rainstxrm.downbutnotout.Events.DownedEvents;
import me.rainstxrm.downbutnotout.Events.PlayerConnectionEvents;
import me.rainstxrm.downbutnotout.Events.ReviveEvents;
import me.rainstxrm.downbutnotout.Events.StopArmourStandClicks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.haoshoku.nick.api.NickAPI;

import java.util.UUID;
import java.util.logging.Level;

public final class DownButNotOut extends JavaPlugin {

    public static DownButNotOut plugin;
    public static boolean nickAPIEnabled = false;


    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        getServer().getPluginManager().registerEvents(new DownedEvents(this), this);
        getServer().getPluginManager().registerEvents(new ReviveEvents(), this);
        getServer().getPluginManager().registerEvents(new PlayerConnectionEvents(), this);
        getServer().getPluginManager().registerEvents(new StopArmourStandClicks(), this);

        if (getConfig().getInt("bleed-out-time") <= 0){
            getLogger().log(Level.WARNING, "The bleed out time has been set to 0 in the config! PLayers will not be downed and instead instantly die.");
        }

        getLogger().log(Level.INFO, "DBNO is online!");

        saveDefaultConfig();
        Metrics metrics = new Metrics(this, 14605);

        getCommand("setreviveclicks").setExecutor(new SetReviveClicks());
        getCommand("dbnoreloadconfig").setExecutor(new ReloadConfig());
        getCommand("setbleedouttime").setExecutor(new SetBleedOutTime());
        getCommand("reviveplayer").setExecutor(new RevivePlayer());
        getCommand("removedbnostands").setExecutor(new RemoveStands());

        if (getServer().getPluginManager().getPlugin("NickAPI") != null) {
            nickAPIEnabled = true;
            getLogger().log(Level.INFO, "NickAPI has been detected! Enabling nick support!");
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().log(Level.INFO, "DBNO is shutting down, cleaning up armor stands...");

        // Clean up all armor stands to prevent them from being "lost" on server restart
        if (!KOHandler.getPlayerArmorStands().isEmpty()) {
            int count = 0;
            for (UUID playerID : KOHandler.getPlayerArmorStands().keySet()) {
                KOHandler.removeArmorStands(playerID);
                count++;
            }
            getLogger().log(Level.INFO, "Cleaned up armor stands for " + count + " players.");
        }

        getLogger().log(Level.INFO, "DBNO has been disabled!");
    }

    public UUID getPlayerUUID(Player player) {
        if (nickAPIEnabled) {
            return NickAPI.getUniqueId(player);
        } else {
            return player.getUniqueId();
        }
    }
}
