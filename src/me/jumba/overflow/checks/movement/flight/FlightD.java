package me.jumba.overflow.checks.movement.flight;

import me.jumba.overflow.base.check.Check;
import me.jumba.overflow.base.check.CheckType;
import me.jumba.overflow.base.event.Listen;
import me.jumba.overflow.base.event.impl.PacketEvent;
import me.jumba.overflow.base.user.User;
import me.jumba.overflow.util.time.TimeUtils;

/**
 * Created on 23/03/2020 Package me.jumba.sparky.checks.movement.flight
 */
public class FlightD extends Check {
    public FlightD(String checkName, String type, CheckType checkType, boolean enabled) {
        super(checkName, type, checkType, enabled);
        setExperimental(true);
    }

    /*
        Disvion check - checks if the devivded motionY value between 1 and 64 is 0.0 or the same as the last tick
     */

    private int min = 1;
    private int max = 64;

    @Listen
    public void onPacket(PacketEvent e) {
        if (e.isPacketMovement()) {
            User user = e.getUser();

            if (user.getMovementData().isCollidesHorizontally() || user.generalCancel() || user.getMovementData().isExplode() || user.getBlockData().webTicks > 0 || user.getBlockData().soulSandTicks > 0 || user.getBlockData().liquidTicks > 0 || user.getBlockData().slime || user.getBlockData().slimeTicks > 0 || user.getMovementData().isJumpPad() || TimeUtils.secondsFromLong(user.getMovementData().getLastJunpPadUpdate()) < 5L) {
                user.getCheckData().flightDVerbose.setVerbose(0);
                return;
            }

            boolean ground = user.getMovementData().isOnGround() && user.getMovementData().isLastOnGround();

            double devidedY = (user.getMovementData().getTo().getY() % min/max);

            boolean badPrediction = ((devidedY == 0.0) || (devidedY > 0.0 && devidedY == user.getCheckData().flightDLastDevide)) && !ground && user.getMovementData().getAirTicks() > 5;


            if (badPrediction && user.getCheckData().flightDVerbose.flag(3, 999L)) {
                flag(user, "devide="+devidedY);
            }

            user.getCheckData().flightDLastDevide = devidedY;
        }
    }
}
