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
public class AimC extends Check {
    public AimC(String checkName, String type, CheckType checkType, boolean enabled) {
        super(checkName, type, checkType, enabled);
    }


    /*
        Rounded aura check
     */

    @Listen
    public void onPacket(PacketEvent e) {
        if (e.getType().equalsIgnoreCase(Packet.Client.POSITION_LOOK)) {
            User user = e.getUser();
            if (user.isHasVerify() && (System.currentTimeMillis() - user.getCombatData().getLastUseEntityPacket()) < 1000L) {
                if (user.getCombatData().getLastEntityAttacked() != null && user.getCombatData().getLastEntityAttacked().getWorld().equals(user.getPlayer().getWorld()) && user.getPlayer().getLocation().distance(user.getCombatData().getLastEntityAttacked().getLocation()) > 1.55) {
                    if (Math.abs(e.getTo().getPitch()) == 90.0) return;
                    double yaw = e.getTo().getYaw(), pitch = e.getTo().getPitch();
                    double roundYaw1 = Math.round(yaw), roundPitch1 = Math.round(pitch);
                    double roundYaw2 = MathUtil.preciseRound(yaw, 1), roundPitch2 = MathUtil.preciseRound(pitch, 1);

                    if ((yaw == roundYaw1 || roundPitch1 == pitch || roundYaw2 == yaw || roundPitch2 == pitch) && user.getCheckData().getAimCVerbose().flag(3, 999L)) {
                        flag(user, "verbose=" + user.getCheckData().aimCVerbose.getVerbose());
                    }
                }
            }
        }
    }
}
