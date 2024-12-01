package me.imedia.gravityGun.settings;

import me.imedia.gravityGun.GravityGun;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class Dropping implements Listener {
    private final GravityGun plugin;

    public Dropping(GravityGun plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDrop(PlayerDropItemEvent event) {
        ItemStack droppedItem = event.getItemDrop().getItemStack();

        String gravityGunName = plugin.getConfig().getString("gravitygun.name", "&bGravity Gun");

        if (droppedItem.hasItemMeta() && droppedItem.getItemMeta().hasDisplayName()) {
            String itemName = droppedItem.getItemMeta().getDisplayName();

            // Compare the item's name with the config value
            if (itemName.equals(ChatColor.translateAlternateColorCodes('&', gravityGunName))) {
                boolean isEnabled = plugin.getConfig().getBoolean("settings.can_be_dropped", true);

                if (isEnabled) {
                    event.setCancelled(false);
                } else {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(ChatColor.RED + "You cannot drop the Gravity Gun.");
                }
            }
        }
    }
}
