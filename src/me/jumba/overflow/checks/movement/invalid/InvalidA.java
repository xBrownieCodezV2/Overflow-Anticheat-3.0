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
 * Created on 09/01/2020 Package me.jumba.sparky.checks.movement.invalid
 */
public class InvalidA extends Check {
    public InvalidA(String checkName, String type, CheckType checkType, boolean enabled) {
        super(checkName, type, checkType, enabled);
    }
    /*
        Fall Motion Check - Checks if the player modifies their motion Y while falling
     */

    @Listen
    public void onPacket(PacketEvent e) {
        if (e.isPacketMovement()) {
            User user = e.getUser();
            if (user != null) {
                if (e.getFrom() != null && e.getTo() != null) {
                    CustomLocation from = e.getFrom(), to = e.getTo();
                    double yDiff = (to.getY() - from.getY());

                    if (!user.getMovementData().isChunkLoaded() || Overflow.getInstance().isLagging() || user.getMiscData().isAfkMovement() || user.getBlockData().slime || user.getBlockData().slabTicks > 0 || user.getMovementData().isExplode() || user.getPlayer().isFlying() || user.getPlayer().getAllowFlight() || (System.currentTimeMillis() - user.getMiscData().getLastBlockCancel()) < 1000L || user.isWaitingForMovementVerify() || user.getBlockData().climbableTicks > 0) {
                        user.getCheckData().lastBadLandLocationInvalidA = System.currentTimeMillis();
                        user.getCheckData().invalidAVerbose = 0;
                        return;
                    }

                    if (yDiff < -1.22) {
                        if (user.getCheckData().invalidAFallTicks < 20) user.getCheckData().invalidAFallTicks++;
                    } else {
                        if (user.getCheckData().invalidAFallTicks > 0) user.getCheckData().invalidAFallTicks--;
                    }
                    if (user.getCheckData().invalidAFallTicks > 7) {
                        if (MathUtil.isBlockBelow(user.getPlayer())) {
                            user.getCheckData().lastBadLandLocationInvalidA = System.currentTimeMillis();
                            user.getCheckData().invalidAVerbose = 0;
                            return;
                        }
                    }
                    boolean expections = user.getBlockData().webTicks > 0 || (System.currentTimeMillis() - user.getCheckData().lastBadLandLocationInvalidA) < 1000L || TimeUtils.secondsFromLong(user.getMovementData().getLastTeleport()) < 5L || TimeUtils.secondsFromLong(user.getTimestamp()) < 3L || user.getBlockData().liquidTicks > 0 || user.getBlockData().slime || user.getBlockData().slimeTicks > 0 || user.generalCancel() || (System.currentTimeMillis() - user.getCombatData().getLastEntityDamage()) < 640L;
                    if (user.getMovementData().isOnGround() || expections) {
                        user.getCheckData().invalidAVerbose = 0;
                    } else {
                        if (user.getMovementData().getAirTicks() > 18 && user.getMovementData().getGroundTicks() < 1) {

                            if (!MathUtil.isBlockBelow(user.getPlayer())) {
                                if (yDiff >= 0) {
                                    if (user.getCheckData().invalidAVerbose < 50) user.getCheckData().invalidAVerbose += 5;
                                } else {
                                    if (user.getCheckData().invalidAVerbose > 0) user.getCheckData().invalidAVerbose--;
                                }
                                if (user.getCheckData().invalidAVerbose > 5)
                                    flag(user, "verbose=" + user.getCheckData().invalidAVerbose, "yDiff=" + MathUtil.preciseRound(yDiff, 2), "airTicks=" + user.getMovementData().getAirTicks(), "groundTicks=" + user.getMovementData().getGroundTicks(), "ground=" + user.getMovementData().isOnGround());
                            } else {
                                if (user.getCheckData().invalidAVerbose > 0) user.getCheckData().invalidAVerbose--;
                            }
                        }
                    }
                }
            }
        }
    }
}
