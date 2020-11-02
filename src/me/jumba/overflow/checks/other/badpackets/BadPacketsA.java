package me.jumba.overflow.checks.other.badpackets;

import me.jumba.overflow.base.check.Check;
import me.jumba.overflow.base.check.CheckType;
import me.jumba.overflow.base.event.Listen;
import me.jumba.overflow.base.event.impl.PacketEvent;
import me.jumba.overflow.base.user.User;

/**
 * Created on 07/01/2020 Package me.jumba.sparky.checks.other.badpackets
 */
public class BadPacketsA extends Check {
    public BadPacketsA(String checkName, String type, CheckType checkType, boolean enabled) {
        super(checkName, type, checkType, enabled);
    }

    /*
        Invalid head position check
     */

    @Listen
    public void onPacket(PacketEvent e) {
        if (e.isPacketMovement()) {
            User user = e.getUser();
            if (user != null && Math.abs(user.getPlayer().getLocation().getPitch()) > 90.0) flag(user, "pitch= " + user.getPlayer().getLocation().getPitch());
        }
    }
}
