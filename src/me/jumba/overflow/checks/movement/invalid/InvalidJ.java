package me.jumba.overflow.checks.movement.invalid;

import me.jumba.overflow.Overflow;
import me.jumba.overflow.base.check.Check;
import me.jumba.overflow.base.check.CheckType;
import me.jumba.overflow.base.event.Listen;
import me.jumba.overflow.base.event.impl.PacketEvent;
import me.jumba.overflow.base.user.User;
import me.jumba.overflow.util.math.MathUtil;
import me.jumba.overflow.util.time.TimeUtils;

/**
 * Created on 20/03/2020 Package me.jumba.sparky.checks.movement.invalid
 */
public class InvalidJ extends Check {
    public InvalidJ(String checkName, String type, CheckType checkType, boolean enabled) {
        super(checkName, type, checkType, enabled);
        setExperimental(true);
    }

    /*
        Illegal fall - checks if the falls at an none legit fall difference, more of a sister check to InvalidI
     */

    @Listen
    public void onPacket(PacketEvent e) {
        if (e.isPacketMovement()) {
            User user = e.getUser();
            if (user != null) {

                if (Overflow.getInstance().isLagging() || (System.currentTimeMillis() - user.getMovementData().getLastTeleport()) < 1000L || (System.currentTimeMillis() - user.getMovementData().getLastUnknownTeleport()) < 1000L || (System.currentTimeMillis() - user.getMovementData().getLastUnknownTeleport()) < 1000L || user.getBlockData().blockAboveTicks > 0 || (System.currentTimeMillis() - user.getCombatData().getLastRandomDamage() < 1000L) || TimeUtils.secondsFromLong(user.getMovementData().getLastCollidedGround()) < 5L || user.getMovementData().isCollidedGround() || user.getMovementData().getCollidedGroundTicks() > 0 || user.generalCancel() || user.getMovementData().isCollidesHorizontally() || user.getBlockData().fenceTicks > 0 || user.getBlockData().wallTicks > 0 || user.getBlockData().climbableTicks > 0 || user.getBlockData().slime || user.getBlockData().slimeTicks > 0 || (System.currentTimeMillis() - user.getBlockData().lastSline) < 1000L || user.getMovementData().isJumpPad() || TimeUtils.secondsFromLong(user.getMovementData().getLastJunpPadUpdate()) < 4L || user.getBlockData().stairTicks > 0 || user.getMovementData().isExplode() || (System.currentTimeMillis() - user.getCombatData().getLastEntityDamageAttack()) < 1000L || (System.currentTimeMillis() - user.getCombatData().getLastBowDamage()) < 1000L || user.getBlockData().blockAboveTicks > 0 || user.getBlockData().halfBlockTicks > 0 || user.getBlockData().slimeTicks > 0) {
                    return;
                }

                if (!user.getMovementData().isCollidesVertically() && user.getMovementData().isLastCollidedVrtically() && user.getMovementData().isOnGround() && user.getMovementData().isLastOnGround()) {
                    double motionY = (user.getMovementData().getTo().getY() - user.getMovementData().getFrom().getY());
                    double trim = MathUtil.trim(8, motionY);

                    if ((user.getMovementData().getWalkSpeed() > 0.2f || user.getBlockData().chestTicks > 0) && trim == -0.0784) return;

                    if (motionY < 0.0 && user.getCheckData().invalidJVerbose.flag(2, 950L)) {
                        flag(user, "motionY="+ MathUtil.trim(8, motionY));
                    }
                }
            }
        }
    }
}
