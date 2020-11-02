package me.jumba.overflow.checks.movement.invalid;

import me.jumba.overflow.Overflow;
import me.jumba.overflow.base.check.Check;
import me.jumba.overflow.base.check.CheckType;
import me.jumba.overflow.base.event.Listen;
import me.jumba.overflow.base.event.impl.PacketEvent;
import me.jumba.overflow.base.user.User;
import me.jumba.overflow.util.time.TimeUtils;
import org.bukkit.Statistic;

/**
 * Created on 15/03/2020 Package me.jumba.sparky.checks.movement.invalid
 */
public class InvalidH extends Check {
    public InvalidH(String checkName, String type, CheckType checkType, boolean enabled) {
        super(checkName, type, checkType, enabled);
        setExperimental(true);
    }

    /*
        Flight stat check - checks if the flight stat is not the same as the last fliat
     */

    @Listen
    public void onPacket(PacketEvent e) {
        if (e.isPacketMovement()) {
            User user = e.getUser();
            if (user != null) {
                int flightStat = user.getPlayer().getStatistic(Statistic.FLY_ONE_CM);

                if (Overflow.getInstance().isLagging() || user.isWaitingForMovementVerify() || (System.currentTimeMillis() - user.getMovementData().getLastTeleport()) < 1000L || (user.getBlockData().slime || user.getBlockData().slimeTicks > 0 || user.getMiscData().getJumpPotionTicks() > 0 || user.generalCancel() || user.getBlockData().climbableTicks > 0 || user.getMiscData().getBoatTicks() > 0 || user.getMiscData().isNearBoat() || user.generalCancel() || user.getMovementData().isJumpPad() || TimeUtils.secondsFromLong(user.getMovementData().getLastJunpPadUpdate()) < 5L || user.getMiscData().isHasJumpPotion() || user.getMiscData().getJumpPotionTicks() > 0)) {
                    user.getCheckData().lastInvalidHFlightStat = flightStat;
                    return;
                }

                double yDiff = (user.getMovementData().getTo().getY() - user.getMovementData().getFrom().getY());

                if (yDiff >= 0.0 && flightStat == user.getCheckData().lastInvalidHFlightStat && !user.getMovementData().isOnGround() && !user.getMovementData().isLastOnGround() && user.getMovementData().getAirTicks() > 15) {
                   // flag(user, "stat=FLY_ONE_CM", "currentStat="+flightStat, "lastStat="+user.getCheckData().lastInvalidHFlightStat, "ground="+user.getMovementData().isOnGround());
                }

                user.getCheckData().lastInvalidHFlightStat = flightStat;
            }
        }
    }
}
