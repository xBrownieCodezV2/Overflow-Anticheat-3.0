package me.jumba.overflow.checks.combat.aim;

import me.jumba.overflow.base.check.Check;
import me.jumba.overflow.base.check.CheckType;
import me.jumba.overflow.base.event.Listen;
import me.jumba.overflow.base.event.impl.PacketEvent;
import me.jumba.overflow.base.user.User;
import me.jumba.overflow.util.math.MathUtil;

/**
 * Created on 25/03/2020 Package me.jumba.sparky.checks.combat.aim
 */
public class AimN extends Check {
    public AimN(String checkName, String type, CheckType checkType, boolean enabled) {
        super(checkName, type, checkType, enabled);
        setExperimental(true);
    }

    private double offset = Math.pow(2.0, 24.0);

    /*
        GCD Correction - Checks if the player is trying to avoid GCD

        Credits:
            - Lucky for helping me
     */

    @Listen
    public void onPacket(PacketEvent e) {

        User user = e.getUser();

        if (e.isPacketMovement()) {
            if (!user.getMiscData().isHasSetClientSensitivity()) return;


            float pitchDifference = Math.abs(e.getFrom().getPitch() - e.getTo().getPitch());
            long gcd = MathUtil.gcd((long) (pitchDifference * offset), (long) (user.getCheckData().lastAimBPitchDiff * offset));

            float diff = Math.abs(user.getMovementData().getTo().getPitch() - user.getMovementData().getFrom().getPitch());

            if (gcd > 131072L && user.getCheckData().lastGCD > 131072L && diff > 0.005 && diff > 0) {
                user.getCheckData().rotationNigger.add(diff);
                if (user.getCheckData().rotationNigger.size() >= 15) {
                    user.getCheckData().rotationNigger.removeFirst();
                }
            }


            float yaw = user.getPlayer().getLocation().getYaw();

            double minValue = user.getCheckData().rotationNigger.stream().mapToDouble(i -> i).min().orElse(0);

            double clamped = Math.round(yaw / minValue) * minValue;

            if ((System.currentTimeMillis() - user.getCombatData().getLastUseEntityPacket()) < 500L) {

                double predict = Math.abs(clamped - yaw);

                double trim = MathUtil.trim(3, predict);

                double offset = Math.abs(user.getCheckData().lastAimMGay - trim);

                if (offset > 0.0) {

                   if (isValueValid(offset) && user.getCheckData().aimMVerbose21.flag(3, 999L)) {
                        flag(user, "offset="+offset, "verbose="+user.getCheckData().aimMVerbose21.getVerbose());
                    }
                }

                user.getCheckData().lastAimMGay = trim;
            }

            user.getCheckData().lastGCD = gcd;
        }
    }

    private boolean isValueValid(double offset) {
        return offset == 0.011 || offset == 0.027 || offset == 0.028 || offset == 0.025 || offset == 0.026 || offset == 0.021 || offset == 0.022;
    }
}
