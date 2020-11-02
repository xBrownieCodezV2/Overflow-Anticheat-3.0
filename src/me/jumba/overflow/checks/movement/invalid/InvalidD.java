package me.jumba.overflow.checks.movement.invalid;

import me.jumba.overflow.Overflow;
import me.jumba.overflow.base.check.Check;
import me.jumba.overflow.base.check.CheckType;
import me.jumba.overflow.base.event.Listen;
import me.jumba.overflow.base.event.impl.PacketEvent;
import me.jumba.overflow.base.user.User;
import me.jumba.overflow.util.location.CustomLocation;
import me.jumba.overflow.util.math.MathUtil;
import me.jumba.overflow.util.time.TimeUtils;

/**
 * Created on 17/01/2020 Package me.jumba.sparky.checks.movement.invalid
 */
public class InvalidD extends Check {
    public InvalidD(String checkName, String type, CheckType checkType, boolean enabled) {
        super(checkName, type, checkType, enabled);
    }

    /*
        Jump value check - Checks how low the player jumps (for yports)
     */

    @Listen
    public void onPacket(PacketEvent e) {

        if (e.isPacketMovement()) {
            User user = e.getUser();
            if (user != null) {

                if (Overflow.getInstance().isLagging() || user.getBlockData().liquidTicks > 0 || user.getBlockData().ice || user.getBlockData().iceTicks > 0 || (System.currentTimeMillis() - user.getBlockData().lastIce) < 1000L || user.getCombatData().hasBowBoosted() || (System.currentTimeMillis() - user.getCombatData().getLastEntityDamageAttack()) < 1000L || user.getBlockData().blockAboveTicks > 0 || user.getMiscData().getBoatTicks() > 0 || user.generalCancel() || TimeUtils.secondsFromLong(user.getMovementData().getLastTeleport()) < 3L || user.getMiscData().getMountedTicks() > 0 || user.getMiscData().isSwitchedGamemodes() || user.getBlockData().stairTicks > 0 || user.getBlockData().slabTicks > 0) {
                    user.getCheckData().speedBVerbose = 0;
                    user.getCheckData().invalidDThreshold = Math.min(user.getCheckData().invalidDThreshold, 0.0);
                    return;
                }

                if (user.getMiscData().getSpeedPotionTicks() > 0 && user.getMiscData().getSpeedPotionEffectLevel() > 4.00) return;

                CustomLocation from = user.getMovementData().getTo(), to = user.getMovementData().getFrom();
                double distX = to.getX() - from.getX();
                double distZ = to.getZ() - from.getZ();
                double dist = (distX * distX) + (distZ * distZ);

                double lastDist = user.getCheckData().invalidDDistance;
                user.getCheckData().invalidDDistance = dist;
                boolean onGround = user.getMovementData().isOnGround();
                double friction = 0.91F;
                double shiftDist = lastDist * friction;
                double equalness = dist - shiftDist;
                double fix = equalness * 138;



               if (user.getMovementData().getMovementSpeed() > 0.2 && (from.getY() - to.getY()) < 0.0) {

                   if (fix < 0.0) {

                       if (user.getCheckData().invalidDThreshold > 5.0) flag(user, "threshold="+user.getCheckData().invalidDThreshold, "friction="+MathUtil.preciseRound(fix, 4));

                       user.getCheckData().invalidDThreshold+= 0.50;

                   } else user.getCheckData().invalidDThreshold = Math.min(user.getCheckData().invalidDThreshold, 0.0);
               }


             //   user.getCheckData().invalidDLastGround = ground;
                user.getCheckData().invalidDGround = onGround;
            }
        }
    }
}
