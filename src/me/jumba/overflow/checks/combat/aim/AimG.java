package me.jumba.overflow.checks.combat.aim;

import me.jumba.overflow.base.check.Check;
import me.jumba.overflow.base.check.CheckType;
import me.jumba.overflow.base.event.Listen;
import me.jumba.overflow.base.event.impl.PacketEvent;
import me.jumba.overflow.base.tinyprotocol.api.Packet;
import me.jumba.overflow.base.user.User;

/**
 * Created on 16/03/2020 Package me.jumba.sparky.checks.combat.aim
 */
public class AimG extends Check {
    public AimG(String checkName, String type, CheckType checkType, boolean enabled) {
        super(checkName, type, checkType, enabled);
    }

    @Listen
    public void onPacket(PacketEvent e) {
        if (e.getTo() == null || e.getFrom() == null) return;
        if (e.getType().equalsIgnoreCase(Packet.Client.POSITION_LOOK)) {
            User user = e.getUser();
            if ((System.currentTimeMillis() - user.getCombatData().getLastUseEntityPacket()) < 1000L) {
                double pitchDiff = (e.getTo().getPitch() - e.getFrom().getPitch());
                if ((System.currentTimeMillis() - user.getLagProcessor().getLastPreLag()) > 1000L && !user.isUsingOptifine() && pitchDiff == user.lastAimCPitchDiff && Math.abs(e.getTo().getYaw() - e.getFrom().getYaw()) > 3.00 && Math.abs(e.getTo().getYaw() - e.getFrom().getYaw()) < 90.0 && user.getCombatData().getLastEntityAttacked() != null && user.getCombatData().getLastEntityAttacked().getWorld().equals(user.getPlayer().getWorld()) && user.getPlayer().getLocation().distance(user.getCombatData().getLastEntityAttacked().getLocation()) > 1.55) {
                    if (user.aimCVerbose++ > 3) {
                        flag(user);
                        user.aimCVerbose = 0;
                    }
                } else user.aimCVerbose -= user.aimCVerbose > 0 ? 1 : 0;
                user.lastAimCPitchDiff = pitchDiff;
            }
        }
    }
}
