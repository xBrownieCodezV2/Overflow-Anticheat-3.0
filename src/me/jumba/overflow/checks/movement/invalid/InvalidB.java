package me.jumba.overflow.checks.movement.invalid;

import me.jumba.overflow.Overflow;
import me.jumba.overflow.base.check.Check;
import me.jumba.overflow.base.check.CheckType;
import me.jumba.overflow.base.event.Listen;
import me.jumba.overflow.base.event.impl.PacketEvent;
import me.jumba.overflow.base.user.User;
import me.jumba.overflow.util.time.TimeUtils;

/**
 * Created on 17/01/2020 Package me.jumba.sparky.checks.movement.invalid
 */
public class InvalidB extends Check {
    public InvalidB(String checkName, String type, CheckType checkType, boolean enabled) {
        super(checkName, type, checkType, enabled);
    }

    /*
        Ground motion check - checks for motion whule the player is on the ground (made to detect ncp on-ground speeds)
     */

    @Listen
    public void onPacket(PacketEvent e) {
        if (e.isPacketMovement()) {
            User user = e.getUser();
            if (user != null) {

                if (Overflow.getInstance().isLagging() || user.getMiscData().getBoatTicks() < 1 && !user.getMovementData().isJumpPad() && TimeUtils.secondsFromLong(user.getMovementData().getLastJunpPadUpdate()) > 4 && user.getBlockData().blockAboveTicks < 1 && !user.getBlockData().slime && user.getBlockData().slimeTicks < 1 && user.getMovementData().isOnGround() && user.getMovementData().isLastOnGround()) {
                    double processDiff = Math.abs(user.getMovementData().getTo().getY() - user.getMovementData().getFrom().getY());
                    double packetDiff = Math.abs(e.getTo().getY() - e.getFrom().getY());

                    if (Overflow.getInstance().isLagging()) return;

                    if (processDiff > 0.0 && packetDiff == 0.0 && user.getCheckData().invalidBVerbose.flag(2, 999L)) {
                        flag(user, "process=" + processDiff, "packet=" + packetDiff, "verbose=" + user.getCheckData().getInvalidBVerbose().getVerbose());
                    }
                }
            }
        }
    }
}
