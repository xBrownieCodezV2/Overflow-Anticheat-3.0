package me.jumba.overflow.checks.movement.invalid;

import me.jumba.overflow.Overflow;
import me.jumba.overflow.base.check.Check;
import me.jumba.overflow.base.check.CheckType;
import me.jumba.overflow.base.event.Listen;
import me.jumba.overflow.base.event.impl.PacketEvent;
import me.jumba.overflow.base.user.User;
import me.jumba.overflow.util.time.TimeUtils;

/**
 * Created on 26/03/2020 Package me.jumba.sparky.checks.movement.invalid
 */
public class InvalidK extends Check {
    public InvalidK(String checkName, String type, CheckType checkType, boolean enabled) {
        super(checkName, type, checkType, enabled);
    }

    /*
        High motion - checks if the player is collided and there Y difference is high
     */

    @Listen
    public void onPacket(PacketEvent e) {
        if (e.isPacketMovement()) {
            User user = e.getUser();
            if (Overflow.getInstance().isLagging()) return;

            if (user.getMovementData().isCollidesVertically()) {
                double yDiff = (user.getMovementData().getTo().getY() - user.getMovementData().getFrom().getY());

                if (yDiff > 1.00 && !user.generalCancel() && !user.getBlockData().slime && user.getBlockData().slimeTicks < 1 && (System.currentTimeMillis() - user.getMovementData().getLastTeleport()) < 1000L && (System.currentTimeMillis() - user.getMovementData().getLastUnknownTeleport()) < 1000L && !user.getMovementData().isJumpPad() && TimeUtils.secondsFromLong(user.getMovementData().getLastJunpPadUpdate()) > 5L) {
                    flag(user, "yDiff="+yDiff);
                }
            }
        }
    }
}
