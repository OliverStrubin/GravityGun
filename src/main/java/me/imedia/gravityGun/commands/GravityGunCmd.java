package me.imedia.gravityGun.commands;

import me.imedia.gravityGun.GravityGun;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

import static org.bukkit.Bukkit.getLogger;

public class GravityGunCmd implements CommandExecutor {
    private final GravityGun plugin;

    public GravityGunCmd(GravityGun plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            // Check if the player has permission
            if (player.hasPermission("gravitygun.use")) {
                // Check if the player already has a Gravity Gun in their inventory
                Inventory inventory = player.getInventory();
                ItemStack existingGun = getGravityGunFromInventory(inventory);

                if (existingGun != null) {
                    // Remove the existing Gravity Gun if found
                    inventory.remove(existingGun);
                    player.sendMessage(getMessage("messages.toggle_gone"));
                    return true;
                }

                // Retrieve the name and lore from the config
                String gravityGunName = plugin.getConfig().getString("gravitygun.name", "&bGravity Gun");
                List<String> lore = plugin.getConfig().getStringList("gravitygun.lore");

                // Create the new Gravity Gun item
                ItemStack gravityGun = new ItemStack(Material.GOLDEN_HOE);
                ItemMeta meta = gravityGun.getItemMeta();
                if (meta != null) {
                    // Set the display name from the config
                    meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', gravityGunName));

                    // Set the lore from the config
                    for (int i = 0; i < lore.size(); i++) {
                        lore.set(i, ChatColor.translateAlternateColorCodes('&', lore.get(i)));
                    }
                    meta.setLore(lore);

                    gravityGun.setItemMeta(meta);
                }

                // Give the Gravity Gun to the player
                inventory.addItem(gravityGun);

                player.sendMessage(getMessage("messages.gained_gun"));
                return true;
            } else {
                player.sendMessage(getMessage("messages.invalid_permissions"));
                return true;
            }
        } else {
            getLogger().warning("Only players can use this command.");
        }
        return true;
    }

    // Helper method to find the Gravity Gun in the player's inventory
    private ItemStack getGravityGunFromInventory(Inventory inventory) {
        // Get the Gravity Gun name from config
        String gravityGunName = plugin.getConfig().getString("gravitygun.name", "&bGravity Gun");

        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.getType() == Material.GOLDEN_HOE && item.hasItemMeta()) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null && ChatColor.translateAlternateColorCodes('&', gravityGunName).equals(meta.getDisplayName())) {
                    return item; // Return the existing Gravity Gun
                }
            }
        }
        return null; // Return null if no Gravity Gun found
    }

    private String getMessage(String path, String... replacements) {
        String message = plugin.getConfig().getString(path, "Message not found: " + path);
        for (int i = 0; i < replacements.length; i += 2) {
            message = message.replace(replacements[i], replacements[i + 1]);
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
