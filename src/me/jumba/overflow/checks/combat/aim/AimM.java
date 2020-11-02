package me.jumba.overflow.checks.combat.aim;

import me.jumba.overflow.base.check.Check;
import me.jumba.overflow.base.check.CheckType;
import me.jumba.overflow.base.event.Listen;
import me.jumba.overflow.base.event.impl.PacketEvent;
import me.jumba.overflow.base.tinyprotocol.api.Packet;
import me.jumba.overflow.util.time.TimeUtils;

/**
 * Created on 25/03/2020 Package me.jumba.sparky.checks.combat.aim
 */
public class AimM extends Check {
    public AimM(String checkName, String type, CheckType checkType, boolean enabled) {
        super(checkName, type, checkType, enabled);
    }

    /*
        Invalid Smoothing - Checks if the yaw of the player has been smoothed incorrectly
     */

    @Listen
    public void onPacket(PacketEvent e) {

        if (e.getType().equalsIgnoreCase(Packet.Client.POSITION)) {
            if ((System.currentTimeMillis() - e.getUser().getCombatData().getLastUseEntityPacket()) < 1000L
                    && Math.abs(e.getTo().getPitch() - e.getFrom().getPitch()) > 0.01) {
                double yaw = e.getUser().getMovementData().getTo().getYaw();
                double sensitivity = e.getUser().getMiscData().getClientSensitivity();

                float f = (float) (sensitivity * 0.6F + 0.2F);
                float gcd = f * f * f * 1.2F;

                yaw = -yaw % gcd;

                double real = Math.abs(e.getUser().getMovementData().getTo().getYaw());

                if (real == e.getUser().getCheckData().lastAimMDiff && yaw == e.getUser().getCheckData().lastAimMDiff2) {

                    if ((System.currentTimeMillis() - e.getUser().getCheckData().lastAimMPossibleLag) < 1500L) {
                        e.getUser().getCheckData().aimMVerbose++;
                    } else {
                        if (e.getUser().getCheckData().aimMVerbose > 0 && (System.currentTimeMillis() - e.getUser().getCheckData().lastAimMPossibleLag) > 1000l)
                            e.getUser().getCheckData().aimMVerbose--;
                    }

                    e.getUser().getCheckData().lastAimMPossibleLag = System.currentTimeMillis();

                    if (e.getUser().getCheckData().aimMVerbose > 2) {
                        flag(e.getUser(), "verbose="+e.getUser().getCheckData().aimMVerbose, "spoof="+yaw, "real="+real);
                    }
                } else {
                    if (e.getUser().getCheckData().aimMVerbose > 0 && TimeUtils.secondsFromLong(e.getUser().getCheckData().lastAimMPossibleLag) > 2L) {
                        e.getUser().getCheckData().aimMVerbose--;
                    }
                }

                e.getUser().getCheckData().lastAimMDiff = real;
                e.getUser().getCheckData().lastAimMDiff2 = yaw;

            }
        }
    }
}
