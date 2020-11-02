package me.jumba.overflow.checks.combat.aim;

import me.jumba.overflow.base.check.Check;
import me.jumba.overflow.base.check.CheckType;
import me.jumba.overflow.base.event.Listen;
import me.jumba.overflow.base.event.impl.PacketEvent;
import me.jumba.overflow.base.tinyprotocol.api.Packet;
import me.jumba.overflow.base.tinyprotocol.packet.in.WrappedInFlyingPacket;
import me.jumba.overflow.base.user.User;
import me.jumba.overflow.util.math.MathUtil;

/**
 * Created on 16/03/2020 Package me.jumba.sparky.checks.combat.aim
 */
public class AimK extends Check {
    public AimK(String checkName, String type, CheckType checkType, boolean enabled) {
        super(checkName, type, checkType, enabled);
       // setExperimental(true);
    }

    @Listen
    public void onPacket(PacketEvent e) {
        User user = e.getUser();
        if (user != null) {

            boolean attacking = (System.currentTimeMillis() - user.getCombatData().getLastUseEntityPacket()) < 1000L;

            if (e.getType().equalsIgnoreCase(Packet.Client.POSITION)) {
                user.getCheckData().aimKThreshold2 = user.getCheckData().aimKThreshold = 0;
            }

            if (e.getType().equalsIgnoreCase(Packet.Client.POSITION_LOOK)) {
                if (attacking) {

                } else {
                    user.getCheckData().aimKThreshold2 = user.getCheckData().aimKThreshold = 0;
                }
            }
        }
    }
}
