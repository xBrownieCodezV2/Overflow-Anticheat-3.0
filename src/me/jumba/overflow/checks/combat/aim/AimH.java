package me.jumba.overflow.checks.combat.aim;

import me.jumba.overflow.base.check.Check;
import me.jumba.overflow.base.check.CheckType;
import me.jumba.overflow.base.event.Listen;
import me.jumba.overflow.base.event.impl.PacketEvent;
import me.jumba.overflow.base.user.User;
import me.jumba.overflow.util.math.MathUtil;

/**
 * Created on 16/03/2020 Package me.jumba.sparky.checks.combat.aim
 */
public class AimH extends Check {
    public AimH(String checkName, String type, CheckType checkType, boolean enabled) {
        super(checkName, type, checkType, enabled);
    }

    /*
        Pitch round check - checks if the players pitch is rounded to 1 to bypass some checks
     */

    @Listen
    public void onPacket(PacketEvent e) {
        if (e.isPacketMovement()) {
            User user = e.getUser();
            if (user != null) {

                if ((System.currentTimeMillis() - user.getCombatData().getLastUseEntityPacket()) < 1000L) {

                    double pitchDiff = Math.abs(user.getMovementData().getTo().getPitch() - user.getMovementData().getFrom().getPitch());

                    if (pitchDiff > 0.0) {

                    }
                }
            }
        }
    }

    private boolean isLegitPredict(double pitchDiff, double predict) {
        //1.5 comes up the most
        //some others are:
        // 3.0 & 4.5 but theses cane be fixed via a verbose
        return (pitchDiff == 1.5 && predict == 1.5);
    }
}
