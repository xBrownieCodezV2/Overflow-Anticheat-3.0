package me.jumba.overflow.checks.other.badpackets;

import me.jumba.overflow.base.check.Check;
import me.jumba.overflow.base.check.CheckType;
import me.jumba.overflow.base.event.Listen;
import me.jumba.overflow.base.event.impl.PacketEvent;
import me.jumba.overflow.base.tinyprotocol.api.Packet;

/**
 * Created on 07/04/2020 Package me.jumba.sparky.checks.other.badpackets
 */
public class BadPacketsJ extends Check {
    public BadPacketsJ(String checkName, String type, CheckType checkType, boolean enabled) {
        super(checkName, type, checkType, enabled);
    }

    /*
        Client payload check - checks for clients that send custom payloads
     */

    @Listen
    public void onPacket(PacketEvent e) {
        if (e.getType().equalsIgnoreCase(Packet.Client.CUSTOM_PAYLOAD)) {

        }
    }
}
