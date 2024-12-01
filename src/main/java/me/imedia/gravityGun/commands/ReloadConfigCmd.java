package me.imedia.gravityGun.commands;

import me.imedia.gravityGun.GravityGun;
import me.imedia.gravityGun.GravityGunManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReloadConfigCmd implements CommandExecutor {
    private final GravityGun plugin;
    private final GravityGunManager gravityGunManager;

    // Constructor accepts both the plugin and GravityGunManager
    public ReloadConfigCmd(GravityGun plugin, GravityGunManager gravityGunManager) {
        this.plugin = plugin;
        this.gravityGunManager = gravityGunManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("gravityreload")) {
            // Ensure the sender is a player
            if (sender instanceof Player) {
                Player player = (Player) sender;

                // Check if the player has the required permission
                if (player.hasPermission("gravitygun.reload")) {
                    plugin.reloadConfig(); // Reload the plugin configuration
                    gravityGunManager.reloadConfigValues(); // Reload the GravityGunManager-specific values
                    player.sendMessage(ChatColor.AQUA + "[GG] Config Reloaded.");
                } else {
                    // Handle insufficient permissions
                    player.sendMessage(getMessage("messages.invalid_permissions"));
                }
            } else {
                // Allow console to reload the config
                sender.sendMessage(ChatColor.AQUA + "[GG] Config reloaded from console.");
                plugin.reloadConfig();
                gravityGunManager.reloadConfigValues();
            }
            return true;
        }
        return false;
    }

    private String getMessage(String path, String... replacements) {
        String message = plugin.getConfig().getString(path, "Message not found: " + path);
        for (int i = 0; i < replacements.length; i += 2) {
            message = message.replace(replacements[i], replacements[i + 1]);
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
