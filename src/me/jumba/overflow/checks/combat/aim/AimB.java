package me.jumba.overflow.checks.combat.aim;

import me.jumba.overflow.base.check.Check;
import me.jumba.overflow.base.check.CheckType;
import me.jumba.overflow.base.event.Listen;
import me.jumba.overflow.base.event.impl.PacketEvent;
import me.jumba.overflow.base.tinyprotocol.api.Packet;
import me.jumba.overflow.base.user.User;
import me.jumba.overflow.util.math.MathUtil;

/**
 * Created on 09/01/2020 Package me.jumba.sparky.checks.combat.aim
 */
public class AimB extends Check {
    public AimB(String checkName, String type, CheckType checkType, boolean enabled) {
        super(checkName, type, checkType, enabled);
    }

    /*
        Basic GCD Check
     */

    private double offset = Math.pow(2.0, 24.0);

    @Listen
    public void onPacket(PacketEvent e) {
        if (e.getType().equalsIgnoreCase(Packet.Client.POSITION_LOOK)) {
            User user = e.getUser();
            if (user != null) {

                boolean work = (System.currentTimeMillis() - user.getCombatData().getLastUseEntityPacket() < 1000L) || (System.currentTimeMillis() - user.getMiscData().getLastBlockPlace()) < 155L;

                if (user.isUsingOptifine()) {
                    if (user.getCheckData().aimBVerbose > 0) user.getCheckData().aimBVerbose-=7;
                }

                float pitchDifference = Math.abs(e.getFrom().getPitch() - e.getTo().getPitch());
                long gcd = MathUtil.gcd((long) (pitchDifference * offset), (long) (user.getCheckData().lastAimBPitchDiff * offset));

                if (((e.getTo().getYaw() != e.getFrom().getYaw()) && (e.getTo().getPitch() != e.getFrom().getPitch()))) {
                    if (Math.abs(e.getTo().getPitch() - e.getFrom().getPitch()) > 0.0 && Math.abs(e.getTo().getPitch()) != 90.0f) {
                        if (gcd < 131072L && work) {
                            if (user.getCheckData().aimBVerbose < 10) user.getCheckData().aimBVerbose++;
                        } else {
                            if (user.getCheckData().aimBVerbose > 0) user.getCheckData().aimBVerbose--;
                        }
                    }
                }

                if (user.getCheckData().aimBVerbose > 9) flag(user, "verbose="+user.getCheckData().aimBVerbose, "GCD="+gcd);

                user.getCheckData().lastAimBPitchDiff = pitchDifference;
            }
        }
    }
}
