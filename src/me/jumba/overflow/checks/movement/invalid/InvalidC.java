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
 * Created on 17/01/2020 Package me.jumba.sparky.checks.movement.invalid
 */
public class InvalidC extends Check {
    public InvalidC(String checkName, String type, CheckType checkType, boolean enabled) {
        super(checkName, type, checkType, enabled);
    }

    /*
        Jump value check - Checks the players jump value, alot like Hypixel's jump value check but works for flys etc...
     */

    @Listen
    public void onPacket(PacketEvent e) {

        if (e.isPacketMovement()) {
            User user = e.getUser();
            if (user != null) {



                if (user.getBlockData().chestTicks > 0 || user.getBlockData().slabTicks > 0 || user.getBlockData().halfBlockTicks > 0|| Overflow.getInstance().isLagging() || user.getMiscData().isAfkMovement() || ((user.getCheckData().lastInvalidCBlockY - user.getMovementData().getTo().toLocation(user.getPlayer().getWorld()).getBlockY())) > 0) {
                    user.getCheckData().lastInvalidCFallBlock = System.currentTimeMillis();
                    return;
                }

                if (user.getMovementData().isOnGround()) {
                    user.getCheckData().lastInvalidCBlockY = user.getMovementData().getTo().toLocation(user.getPlayer().getWorld()).getBlockY();
                }

                int t = user.getCheckData().invalidCThreshold;
                if (user.getBlockData().anvilTicks > 0  || TimeUtils.secondsFromLong(user.getCheckData().lastUnknownValidTeleport) < 5L || (System.currentTimeMillis() - user.getCheckData().lastUnknownValidTeleport) < 1000L || user.getBlockData().webTicks > 0 || user.getMovementData().isExplode() || (System.currentTimeMillis() - user.getCheckData().lastInvalidCFallBlock) < 1000L || user.getMiscData().isSwitchedGamemodes() || user.getBlockData().wallTicks > 0 || user.getBlockData().fenceTicks > 0 || TimeUtils.secondsFromLong(user.getMovementData().getLastEnderpearl()) < 4L || (System.currentTimeMillis() - user.getCombatData().getLastRandomDamage()) < 1000L || user.getMiscData().getBoatTicks() > 0 || (System.currentTimeMillis() - user.getCombatData().getLastEntityDamageAttack()) < 1000L || user.getMiscData().getJumpPotionTicks() > 0) {
                    t = 0;
                    user.getCheckData().invalidCThreshold = t;
                    return;
                }

                double diff = (user.getMovementData().getTo().getY() - user.getMovementData().getFrom().getY());
                double trim = MathUtil.trim(4, diff);

                if (user.isSafe() && user.totalBlocksCheck > 5) {
                    if (!user.getMovementData().isOnGround() && !user.getMovementData().isLastOnGround()) {
                        if (trim == 0.0831) {
                            t = 0;
                        } else {
                            t++;
                        }
                    }
                } else t = 0;

                if (user.getMovementData().getGroundTicks() > 18 || user.generalCancel() || user.getBlockData().stairTicks > 0 || user.getBlockData().slabTicks > 0 || user.getBlockData().liquidTicks > 0 || user.getBlockData().slimeTicks > 0 || user.getBlockData().slime || user.getBlockData().blockAboveTicks > 0 || diff <= -0.42 || user.getBlockData().climbableTicks > 0) {
                    t = 0;
                }

                if (t > 15) {
                    flag(user, "threshold="+t, "diff="+diff, "trim=4%"+trim);
                }

                user.getCheckData().invalidCThreshold = t;
            }
        }
    }
}
