package me.imedia.gravityGun.Utils;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Utils {
    public static boolean isFacing(Player player, Location target) {
        Vector toTarget = target.toVector().subtract(player.getEyeLocation().toVector()).normalize();
        return toTarget.dot(player.getEyeLocation().getDirection()) > 0.95;
    }
}
