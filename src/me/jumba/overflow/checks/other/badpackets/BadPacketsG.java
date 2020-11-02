package me.jumba.overflow.checks.other.badpackets;

import me.jumba.overflow.base.check.Check;
import me.jumba.overflow.base.check.CheckType;
import me.jumba.overflow.base.event.Listen;
import me.jumba.overflow.base.event.impl.PacketEvent;
import me.jumba.overflow.base.tinyprotocol.api.Packet;
import me.jumba.overflow.base.tinyprotocol.packet.in.WrappedInKeepAlivePacket;

/**
 * Created on 08/01/2020 Package me.jumba.sparky.checks.other.badpackets
 */
public class BadPacketsG extends Check {
    public BadPacketsG(String checkName, String type, CheckType checkType, boolean enabled) {
        super(checkName, type, checkType, enabled);
    }

    /*
        KeepAlive Time Check - checks if the keep-alive packets time is 0 (sent by some clients)
     */

    @Listen
    public void onPacket(PacketEvent e) {

        if (e.getType().equalsIgnoreCase(Packet.Client.KEEP_ALIVE)) {
            WrappedInKeepAlivePacket wrappedInKeepAlivePacket = new WrappedInKeepAlivePacket(e.getPacket(), e.getPlayer());
            if (wrappedInKeepAlivePacket.getTime() == 0L) {
                flag(e.getUser(), "time="+wrappedInKeepAlivePacket.getTime());
            }
        }
    }
}
