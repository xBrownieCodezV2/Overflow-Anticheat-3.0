package me.jumba.overflow.checks.combat.aim;

import me.jumba.overflow.base.check.Check;
import me.jumba.overflow.base.check.CheckType;
import me.jumba.overflow.base.event.Listen;
import me.jumba.overflow.base.event.impl.PacketEvent;
import me.jumba.overflow.base.tinyprotocol.api.Packet;
import me.jumba.overflow.base.user.User;
import me.jumba.overflow.util.math.MathUtil;

/**
 * Created on 31/01/2020 Package me.jumba.sparky.checks.combat.aim
 */
public class AimD extends Check {
    public AimD(String checkName, String type, CheckType checkType, boolean enabled) {
        super(checkName, type, checkType, enabled);
    }

    /*
        Same pitch check
     */

    @Listen
    public void onPacket(PacketEvent e) {

        if (e.getType().equalsIgnoreCase(Packet.Client.POSITION_LOOK)) {
            User user = e.getUser();
            if ((System.currentTimeMillis() - user.getCombatData().getLastUseEntityPacket()) < 1000L) {
                double pitchDiff = (e.getTo().getPitch() - e.getFrom().getPitch());
                if ((System.currentTimeMillis() - user.getLagProcessor().getLastPreLag()) < 1000L && !user.isUsingOptifine() && pitchDiff == user.getCheckData().lastAimDPitch && Math.abs(e.getTo().getYaw() - e.getFrom().getYaw()) > 3.00 && Math.abs(e.getTo().getYaw() - e.getFrom().getYaw()) < 90.0 && user.getCombatData().getLastEntityAttacked() != null && user.getCombatData().getLastEntityAttacked().getWorld().equals(user.getPlayer().getWorld()) && user.getPlayer().getLocation().distance(user.getCombatData().getLastEntityAttacked().getLocation()) > 1.55 && user.getCheckData().aimDVerbose.flag(20, 920L)) {
                    flag(user, "diff=" + MathUtil.preciseRound(pitchDiff, 2), "verbose= " + user.getCheckData().aimDVerbose.getVerbose(), "optifine=" + user.isUsingOptifine());
                }
                user.getCheckData().lastAimDPitch = pitchDiff;
            }
        }
    }
}
