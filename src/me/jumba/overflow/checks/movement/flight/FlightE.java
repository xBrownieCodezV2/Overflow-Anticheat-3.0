package me.jumba.overflow.checks.movement.flight;

import me.jumba.overflow.base.check.Check;
import me.jumba.overflow.base.check.CheckType;
import me.jumba.overflow.base.event.Listen;
import me.jumba.overflow.base.event.impl.PacketEvent;
import me.jumba.overflow.base.user.User;
import me.jumba.overflow.util.box.ReflectionUtil;
import me.jumba.overflow.util.math.MathUtil;
import me.jumba.overflow.util.time.TimeUtils;

/**
 * Created on 15/04/2020 Package me.jumba.sparky.checks.movement.flight
 */
public class FlightE extends Check {
    public FlightE(String checkName, String type, CheckType checkType, boolean enabled) {
        super(checkName, type, checkType, enabled);
    }


    @Listen
    public void onPacket(PacketEvent e) {
        if (e.isPacketMovement()) {
            User user = e.getUser();
            if (user != null) {

                if ((ReflectionUtil.isHyperion() && (System.currentTimeMillis() - user.getMovementData().getLastFallDamage()) < 1000L) || !user.isSafe() || user.getMovementData().isDidUnknownTeleport() || user.getBlockData().anvilTicks > 0 || user.getBlockData().climbableTicks > 0 || user.getBlockData().slime || user.getBlockData().slimeTicks > 0 || (System.currentTimeMillis() - user.getMovementData().getLastTeleport()) < 1000L  || user.generalCancel() || user.getBlockData().liquidTicks > 0 || user.getBlockData().webTicks > 0 || user.getBlockData().climbableTicks > 0 || TimeUtils.secondsFromLong(user.getMovementData().getLastJunpPadUpdate()) < 5L || user.getMovementData().isJumpPad()) {
                    return;
                }


                double diff = (user.getMovementData().getTo().getY() - user.getMovementData().getFrom().getY());

                double max = 0.55f;

                if ((System.currentTimeMillis() - user.getCombatData().getLastBowDamage()) < 1000L) max += 0.42f;

                if ((System.currentTimeMillis() - user.getMiscData().getLastBlockPlace()) < 1000L) max += 0.42f;

                if (user.getMiscData().getJumpPotionTicks() > 0) max += user.getMiscData().getJumpPotionMultiplyer();

                if (user.getBlockData().halfBlockTicks > 0 || user.getBlockData().slabTicks > 0 || user.getBlockData().stairTicks > 0 || user.getBlockData().fenceTicks > 0 || user.getBlockData().wallTicks > 0) max += 0.8f;


                if (user.getBlockData().trapDoorTicks > 0) max += 0.42f;

                if (Math.abs(diff) > max && user.getMovementData().isOnGround()) {
                    if (user.getCheckData().flightETotalFlags > 3 && user.getCheckData().flightEVerbose.flag(3, 999L)) {
                        flag(user, "diff=" + MathUtil.trim(7, diff), "t=1");
                    }
                    user.getCheckData().flightETotalFlags++;
                }

                if ((diff > max) && user.getMovementData().isClientGround()) {
                    if (user.getCheckData().flightETotalFlags > 3 && user.getCheckData().flightEVerbose.flag(3, 999L)) {
                        flag(user, "diff=" + MathUtil.trim(7, diff), "cg=" + user.getMovementData().isClientGround(), "sg=" + user.getMovementData().isOnGround(), "t=2");
                    }

                    user.getCheckData().flightETotalFlags++;
                }
            }
        }
    }
}
