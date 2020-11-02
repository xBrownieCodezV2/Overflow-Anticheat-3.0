package me.jumba.overflow.checks.combat.aim;

import me.jumba.overflow.base.check.Check;
import me.jumba.overflow.base.check.CheckType;
import me.jumba.overflow.base.event.Listen;
import me.jumba.overflow.base.event.impl.PacketEvent;
import me.jumba.overflow.base.tinyprotocol.api.Packet;
import me.jumba.overflow.base.user.User;
import me.jumba.overflow.util.time.TimeUtils;

/**
 * Created on 16/03/2020 Package me.jumba.sparky.checks.combat.aim
 */
public class AimF extends Check {
    public AimF(String checkName, String type, CheckType checkType, boolean enabled) {
        super(checkName, type, checkType, enabled);
    }

    /*
        Yaw smoothness check - checks the smoothness on players yaw while attacking a player
     */

    @Listen
    public void onPacket(PacketEvent e) {

        User user = e.getUser();

        if (e.getType().equalsIgnoreCase(Packet.Client.POSITION)) {
            user.getCheckData().lastAimFPos = System.currentTimeMillis();
        }

        if (e.isPacketMovement()) {

        }
    }
}
