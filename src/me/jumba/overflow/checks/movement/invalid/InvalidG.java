package me.jumba.overflow.checks.movement.invalid;

import me.jumba.overflow.Overflow;
import me.jumba.overflow.base.check.Check;
import me.jumba.overflow.base.check.CheckType;
import me.jumba.overflow.base.event.Listen;
import me.jumba.overflow.base.event.impl.PacketEvent;
import me.jumba.overflow.base.user.User;
import me.jumba.overflow.util.math.MathUtil;
import me.jumba.overflow.util.time.TimeUtils;
import org.bukkit.Statistic;

/**
 * Created on 15/03/2020 Package me.jumba.sparky.checks.movement.invalid
 */
public class InvalidG extends Check {
    public InvalidG(String checkName, String type, CheckType checkType, boolean enabled) {
        super(checkName, type, checkType, enabled);
        setExperimental(true);
    }

    /*
        Flight jump stat check - checks to see if the 'FLY_ONE_CM' stat is being updated while the players yDifference >= 0.0
     */

    @Listen
    public void onPacket(PacketEvent e) {
        if (e.isPacketMovement()) {
            User user = e.getUser();

            if (user != null) {
                int flightStat = user.getPlayer().getStatistic(Statistic.FLY_ONE_CM);


//                user.debug(""+flightStat);

                double diff = (user.getMovementData().getTo().getY() - user.getMovementData().getFrom().getY());

                if (Overflow.getInstance().isLagging() || user.getCombatData().hasBowBoosted() || !user.isHasVerify() || user.generalCancel() || user.getMovementData().isJumpPad() || TimeUtils.secondsFromLong(user.getMovementData().getLastJunpPadUpdate()) < 5L || user.getBlockData().slabTicks > 0 || user.getBlockData().slime) {
                    user.getCheckData().lastInvalidGFlightStat = flightStat;
                    return;
                }

                if (user.getMovementData().isOnGround() && user.getMovementData().isLastOnGround()) {
                    user.getCheckData().lastInvalidGFlightStat = flightStat;
                } else {
                    if (diff >= 0.0) {
                        int difference = Math.abs(flightStat - user.getCheckData().lastInvalidGFlightStat);

                        if (difference > 1000) {
                         //   flag(user, "difference="+difference, "yDiff="+ MathUtil.trim(4, diff));
                        }
                    }
                }
            }
        }
    }
}
