package me.imedia.gravityGun.Utils;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class CarryTask extends BukkitRunnable {
    private final Player player;
    private final Entity carriedEntity;

    public CarryTask(Player player, Entity entity) {
        this.player = player;
        this.carriedEntity = entity;
    }

    public Entity getCarriedEntity() {
        return carriedEntity;
    }

    @Override
    public void run() {
        if (carriedEntity != null && player != null && player.isOnline()) {
            Vector direction = player.getLocation().getDirection();
            Vector targetPosition = player.getLocation().toVector().add(direction.multiply(3));
            targetPosition.setY(player.getLocation().getY() + 1.5);

            Vector currentPosition = carriedEntity.getLocation().toVector();
            Vector velocity = targetPosition.subtract(currentPosition).multiply(0.3); // Smooth movement
            carriedEntity.setVelocity(velocity);
        } else {
            cancel();
        }
    }
}
