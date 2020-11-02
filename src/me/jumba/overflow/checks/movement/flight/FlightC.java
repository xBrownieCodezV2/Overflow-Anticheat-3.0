package me.jumba.overflow.checks.movement.flight;

import me.jumba.overflow.base.check.Check;
import me.jumba.overflow.base.check.CheckType;
import me.jumba.overflow.base.event.Listen;
import me.jumba.overflow.base.event.impl.PacketEvent;
import me.jumba.overflow.base.user.User;
import me.jumba.overflow.util.time.TimeUtils;

/**
 * Created on 07/03/2020 Package me.jumba.sparky.checks.movement.flight
 */
public class FlightC extends Check {
    public FlightC(String checkName, String type, CheckType checkType, boolean enabled) {
        super(checkName, type, checkType, enabled);
    }

    /*
        Ascension check - checks if the current location is over a large amount to the ground location
     */

    @Listen
    public void onPacket(PacketEvent e) {
        if (e.isPacketMovement()) {
            User user = e.getUser();
            if (user != null) {
                boolean gay = (System.currentTimeMillis() - user.getCombatData().getLastRandomDamage()) < 1000L || user.getPlayer().isFlying() || user.getPlayer().getAllowFlight() || user.getMovementData().isExplode() || (System.currentTimeMillis() - user.getMiscData().getLastBlockPlace() < 1000L);

                if (!gay && !user.isWaitingForMovementVerify() && !user.getCombatData().hasBowBoosted() && (System.currentTimeMillis() - user.getMovementData().getLastEnderpearl()) > 1000L && !user.getBlockData().slime && user.getBlockData().slimeTicks < 1 && !user.generalCancel() && user.getMiscData().getMountedTicks() < 1 && user.getMiscData().getBoatTicks() < 1 && !user.getMiscData().isNearBoat() && (System.currentTimeMillis() - user.getCombatData().getLastEntityDamage()) > 1000L && !user.getMovementData().isJumpPad() && TimeUtils.secondsFromLong(user.getMovementData().getLastJunpPadUpdate()) > 2L && user.getBlockData().liquidTicks < 1 && user.getBlockData().climbableTicks < 1 && user.getMovementData().getLastGroundLocation() != null && e.getTo().getY() > e.getFrom().getY()) {
                    double toGroundDiff = (user.getMovementData().getLastGroundLocation().getY() - e.getTo().getY());

                    if (!user.getMovementData().isOnGround()) {

                        //TODO: make better prediction service for this

                        double maxAccesend = (user.getMovementData().isCollidesHorizontally() ? 1.00 : 0.9);

                        if (user.getMiscData().getJumpPotionTicks() > 0) {
                            maxAccesend += user.getMiscData().getJumpPotionTicks();
                        }

                        if (user.getMovementData().getCollidedGroundTicks() > 0 || (System.currentTimeMillis() - user.getMovementData().getLastBlockJump()) < 1000L)
                            maxAccesend += 0.5;

                        if ((System.currentTimeMillis() - user.getMovementData().getLastFallDamage()) < 1000L)
                            maxAccesend += 7;

                        if ((System.currentTimeMillis() - user.getCombatData().getLastBowDamage()) < 1000L)
                            maxAccesend += 1.5;

                        if (user.getBlockData().stairTicks > 0 || user.getBlockData().slabTicks > 0) maxAccesend += 0.5;


                        if (toGroundDiff > maxAccesend && user.getCheckData().flightCVerbose1.flag(2, 999L)) {
                            flag(user, "diff=" + toGroundDiff);
                        }
                    }
                }
            }
        }
    }
}
