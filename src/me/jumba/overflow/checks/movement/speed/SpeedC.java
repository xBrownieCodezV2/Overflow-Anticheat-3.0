package me.jumba.overflow.checks.movement.speed;

import me.jumba.overflow.Overflow;
import me.jumba.overflow.base.check.Check;
import me.jumba.overflow.base.check.CheckType;
import me.jumba.overflow.base.event.Listen;
import me.jumba.overflow.base.event.impl.PacketEvent;
import me.jumba.overflow.base.user.User;
import me.jumba.overflow.util.box.ReflectionUtil;
import me.jumba.overflow.util.location.CustomLocation;
import me.jumba.overflow.util.math.MathUtil;

/**
 * Created on 07/01/2020 Package me.jumba.sparky.checks.movement.speed
 */
public class SpeedC extends Check {
    public SpeedC(String checkName, String type, CheckType checkType, boolean enabled) {
        super(checkName, type, checkType, enabled);
    }
    /*
        OnGround speed check - gets if the player goes faster on ground
     */

    @Listen
    public void onPacket(PacketEvent e) {
        if (e.isPacketMovement()) {
            User user = e.getUser();
            if (user != null) {
                CustomLocation to = user.getMovementData().getTo(), from = user.getMovementData().getFrom();

                if (user.getBlockData().slimeTicks > 0 || Overflow.getInstance().isLagging() || (ReflectionUtil.isHypreionSpigot && user.getBlockData().leaveTicks > 0) || user.getMovementData().getWalkSpeed() > 0.2f || user.getBlockData().wallTicks > 0 || user.getBlockData().fenceTicks > 0 || user.getBlockData().chestTicks > 0 || (System.currentTimeMillis() - user.getMovementData().getLastTeleport()) < 1000L || (System.currentTimeMillis() - user.getCheckData().lastUnknownValidTeleport) < 1000L || user.getPlayer().isFlying() || user.getPlayer().getAllowFlight() || user.getBlockData().halfBlockTicks > 0 || user.getMovementData().isJumpPad() || user.getBlockData().snowTicks > 1 || user.generalCancel() || user.getMiscData().isSwitchedGamemodes()) {
                    return;
                }


                double x = to.getX() - from.getX();
                double z = to.getZ() - from.getZ();
                double maxSpeed = 0.2873D;
                if (user.getBlockData().iceTicks > 0) {
                    maxSpeed += (0.98F * 0.91F);
                }

                if (user.getBlockData().slabTicks > 0 || user.getBlockData().stairTicks > 0 || (System.currentTimeMillis() - user.getMovementData().getLastBlockJump()) < 1000L) {
                    //0.33966671926
                    maxSpeed += 0.33966671926;
                }
                if (user.getBlockData().blockAboveTicks > 0) {
                    return;
                }

                maxSpeed += user.getMiscData().getSpeedPotionEffectLevel() * 0.2;
                if (!user.getMiscData().isHasSpeedPotion() && user.getMiscData().getSpeedPotionTicks() > 0) {
                    return;
                }

                double speed = Math.sqrt(x * x + z * z);

//                Bukkit.broadcastMessage(""+diff + " "+user.getPlayer().isOnGround() + " "+speed);
                if (speed > maxSpeed && user.getMovementData().getGroundTicks() > 19 && user.getMovementData().getAirTicks() < 1 && (to.getY() - from.getY()) == 0 && user.getCheckData().getSpeedCVerbose().flag(2,920L)) {
                    flag(user, "speed="+ MathUtil.preciseRound(speed, 2), "maxSpeed="+MathUtil.preciseRound(maxSpeed, 2), "groundTicks="+user.getMovementData().getGroundTicks(), "airTicks="+user.getMovementData().getAirTicks(), "verbose="+user.getCheckData().getSpeedCVerbose().getVerbose());
                }
            }
        }
    }
}
