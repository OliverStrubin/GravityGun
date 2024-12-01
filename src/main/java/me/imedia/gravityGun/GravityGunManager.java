package me.imedia.gravityGun;

import me.imedia.gravityGun.Utils.CarryTask;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.Comparator;

public class GravityGunManager implements Listener {
    private final GravityGun plugin;
    private CarryTask carryTask;
    private boolean showMessages;
    private boolean throwingEnabled;

    public GravityGunManager(GravityGun plugin) {
        this.plugin = plugin;
        reloadConfigValues();
    }

    public void reloadConfigValues() {
        this.showMessages = plugin.getConfig().getBoolean("settings.messages", true);
        this.throwingEnabled = plugin.getConfig().getBoolean("settings.enabled_throwing", true);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        String gravityGunName = plugin.getConfig().getString("gravitygun.name", "&bGravity Gun");

        if (!isGravityGun(item, gravityGunName)) return;

        event.setCancelled(true);

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (carryTask != null) {
                return;
            } else if (!pickupEntity(player) && !pickupBlock(player)) {
                if (showMessages) {
                    player.sendMessage(getMessage("messages.no_valid_target"));
                }
            }
        } else if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (throwingEnabled) {
                if (carryTask != null && carryTask.getCarriedEntity() != null) {
                    if (carryTask.getCarriedEntity() instanceof FallingBlock) {
                        throwCarriedBlock(player);
                    } else {
                        throwCarriedEntity(player);
                    }
                }
            } else {
                return;
            }
        }
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();

        if (carryTask != null) {
            dropCarriedEntity(player);
        }
    }

    private boolean isGravityGun(ItemStack item, String gravityGunName) {
        if (item != null && item.getType() == Material.GOLDEN_HOE && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                String translatedName = ChatColor.translateAlternateColorCodes('&', gravityGunName);
                return translatedName.equals(meta.getDisplayName());
            }
        }
        return false;
    }

    private boolean pickupEntity(Player player) {
        if (carryTask != null) {
            return false;
        }

        double range = 10.0;

        Entity target = player.getNearbyEntities(range, range, range).stream()
                .filter(e -> e instanceof LivingEntity && !(e instanceof Player))
                .filter(e -> isInLineOfSight(player, e))
                .min(Comparator.comparingDouble(e -> e.getLocation().distance(player.getEyeLocation())))
                .orElse(null);

        if (target != null) {
            target.setGravity(false);
            target.setInvulnerable(true); // Prevent damage while carried
            startCarryTask(player, target);

            if (showMessages) {
                player.sendMessage(getMessage("messages.carrying_entity", "%entity%", target.getName()));
            }
            return true;
        }
        return false;
    }

    private boolean pickupBlock(Player player) {
        if (carryTask != null) {
            return false;
        }

        double range = 10.0;
        Block targetBlock = player.getTargetBlockExact((int) range);

        if (targetBlock != null && targetBlock.getType() != Material.AIR) {
            FallingBlock fallingBlock = player.getWorld().spawnFallingBlock(targetBlock.getLocation(), targetBlock.getBlockData());
            fallingBlock.setGravity(false);
            targetBlock.setType(Material.AIR);

            startCarryTask(player, fallingBlock);
            if (showMessages) {
                player.sendMessage(getMessage("messages.carrying_block"));
            }
            return true;
        }
        return false;
    }

    private void throwCarriedEntity(Player player) {
        Entity carriedEntity = carryTask.getCarriedEntity();
        if (carriedEntity != null) {
            stopCarryTask();

            Vector throwDirection = player.getLocation().getDirection().multiply(2);
            throwDirection.setY(Math.max(0.2, throwDirection.getY()));
            carriedEntity.setGravity(true);
            carriedEntity.setVelocity(throwDirection);
            carriedEntity.setInvulnerable(false);

            if (showMessages) {
                player.sendMessage(getMessage("messages.threw_item"));
            }
        }
    }

    private void throwCarriedBlock(Player player) {
        if (carryTask != null && carryTask.getCarriedEntity() instanceof FallingBlock) {
            Entity carriedEntity = carryTask.getCarriedEntity();
            stopCarryTask();

            Vector throwDirection = player.getLocation().getDirection().multiply(2);
            throwDirection.setY(Math.max(0.2, throwDirection.getY()));
            carriedEntity.setGravity(true);
            carriedEntity.setVelocity(throwDirection);

            if (showMessages) {
                player.sendMessage(getMessage("messages.threw_item"));
            }
        }
    }

    private void dropCarriedEntity(Player player) {
        Entity carriedEntity = carryTask.getCarriedEntity();
        if (carriedEntity != null) {
            stopCarryTask();

            carriedEntity.setGravity(true);
            carriedEntity.setInvulnerable(false);

            if (showMessages) {
                player.sendMessage(getMessage("messages.dropped_item"));
            }
        }
    }

    private void startCarryTask(Player player, Entity entity) {
        stopCarryTask();
        carryTask = new CarryTask(player, entity);
        carryTask.runTaskTimer(plugin, 0L, 1L);
    }

    public void stopCarryTask() {
        if (carryTask != null) {
            carryTask.cancel();
            carryTask = null;
        }
    }

    private boolean isInLineOfSight(Player player, Entity entity) {
        Vector direction = player.getLocation().getDirection();
        Vector toEntity = entity.getLocation().toVector().subtract(player.getEyeLocation().toVector());
        return direction.normalize().dot(toEntity.normalize()) > 0.95;
    }

    private String getMessage(String path, String... replacements) {
        if (!showMessages) return "";
        String message = plugin.getConfig().getString(path, "");
        for (int i = 0; i < replacements.length; i += 2) {
            message = message.replace(replacements[i], replacements[i + 1]);
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
