package me.jumba.overflow.util.entity;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

/**
 * Created on 01/02/2020 Package me.jumba.sparky.util.entity
 */
public class BotUtils {
    public static Location getBehind(Player player, double multi) {
        Location location;
        location = player.getLocation().add(player.getEyeLocation().getDirection().multiply(multi));
        BlockFace facing = getCardinalFace(player);
        if (facing == BlockFace.WEST
                || facing == BlockFace.EAST) {
        }
        return location;
    }

    private static BlockFace getCardinalFace(Player player) {
        String direction = getCardinalDirection(player);
        if (direction.equalsIgnoreCase("North"))
            return BlockFace.NORTH;
        if (direction.equalsIgnoreCase("Northeast"))
            return BlockFace.NORTH_EAST;
        if (direction.equalsIgnoreCase("East"))
            return BlockFace.EAST;
        if (direction.equalsIgnoreCase("Southeast"))
            return BlockFace.SOUTH_EAST;
        if (direction.equalsIgnoreCase("South"))
            return BlockFace.SOUTH;
        if (direction.equalsIgnoreCase("Southwest"))
            return BlockFace.SOUTH_WEST;
        if (direction.equalsIgnoreCase("West"))
            return BlockFace.WEST;
        if (direction.equalsIgnoreCase("Northwest"))
            return BlockFace.NORTH_WEST;
        return null;
    }

    private static String getCardinalDirection(Player player) {
        double rot = (player.getLocation().getYaw() - 180) % 360;
        if (rot < 0) {
            rot += 360.0;
        }
        return getDirection(rot);
    }

    private static String getDirection(double rot) {
        if (0 <= rot && rot < 22.5) {
            return "North";
        } else if (22.5 <= rot && rot < 67.5) {
            return "Northeast";
        } else if (67.5 <= rot && rot < 112.5) {
            return "East";
        } else if (112.5 <= rot && rot < 157.5) {
            return "Southeast";
        } else if (157.5 <= rot && rot < 202.5) {
            return "South";
        } else if (202.5 <= rot && rot < 247.5) {
            return "Southwest";
        } else if (247.5 <= rot && rot < 292.5) {
            return "West";
        } else if (292.5 <= rot && rot < 0) {
            return "Northwest";
        } else if (310.5 <= rot && rot < 360) {
            return "North";
        } else {
            return "North";
        }
    }
}
