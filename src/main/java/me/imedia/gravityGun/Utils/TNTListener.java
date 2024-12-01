package me.imedia.gravityGun.Utils;

import me.imedia.gravityGun.GravityGun;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class TNTListener implements Listener {
    private final GravityGun plugin;

    public TNTListener(GravityGun plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (!plugin.getConfig().getBoolean("settings.tnt_exploding", true)) {
            return;
        }

        if (event.getEntity() instanceof FallingBlock fallingBlock &&
                fallingBlock.getBlockData().getMaterial() == Material.TNT) {

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!fallingBlock.isValid() || fallingBlock.isOnGround()) {
                        return;
                    }

                    Location loc = fallingBlock.getLocation();
                    World world = loc.getWorld();
                    if (world != null) {
                        float explosionPower = (float) plugin.getConfig().getDouble("tnt.explosion_power", 4.0);
                        boolean fire = plugin.getConfig().getBoolean("tnt.fire", false);
                        boolean blockDamage = plugin.getConfig().getBoolean("tnt.block_damage", false);

                        world.createExplosion(loc, explosionPower, fire, blockDamage);
                        fallingBlock.remove();
                    }
                    cancel();
                }
            }.runTaskLater(plugin, 20L);
        }
    }
}
