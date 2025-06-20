package me.rainstxrm.downbutnotout.Commands;

import me.rainstxrm.downbutnotout.DownButNotOut;
import me.rainstxrm.downbutnotout.KOHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class RemoveStands implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)){
            DownButNotOut.plugin.getLogger().log(Level.WARNING, "Only players can execute this command!");
            return true;
        }

        String prefix = ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + "DBNO" + ChatColor.DARK_GREEN + "]";

        Player player = (Player) sender;

        // Remove stands - Removes plugin related armour stands
        // Permission - dbno.admin.removestands

        if (player.hasPermission("dbno.admin.removestands")){
            int enCount = 0;

            // First, get access to the playerArmorStands map in KOHandler
            HashMap<UUID, List<UUID>> playerArmorStands = KOHandler.getPlayerArmorStands();

            // Remove all tracked armor stands
            if (playerArmorStands != null && !playerArmorStands.isEmpty()) {
                for (UUID playerID : playerArmorStands.keySet()) {
                    List<UUID> armorStandUUIDs = playerArmorStands.get(playerID);
                    for (UUID armorStandUUID : armorStandUUIDs) {
                        Entity entity = Bukkit.getEntity(armorStandUUID);
                        if (entity != null) {
                            entity.getWorld().spawnParticle(Particle.SPELL_WITCH, entity.getLocation(), 10);
                            entity.remove();
                            enCount++;
                        }
                    }
                }
                // Clear the map after removing all armor stands
                playerArmorStands.clear();
            }

            // As a fallback, also check for any untracked armor stands nearby
            for (Entity e : player.getNearbyEntities(10,10,10)){
                if (e.hasMetadata("DownedStand") || e.hasMetadata("ReviveStand")){
                    e.getWorld().spawnParticle(Particle.SPELL_WITCH, e.getLocation(), 10);
                    e.remove();
                    enCount++;
                }
            }

            player.sendMessage(prefix + ChatColor.GREEN + " " + enCount + " Entities removed!");
        }

        return true;
    }
}
