package me.jumba.overflow.checks.other.badpackets;

import me.jumba.overflow.base.check.Check;
import me.jumba.overflow.base.check.CheckType;
import me.jumba.overflow.base.event.Listen;
import me.jumba.overflow.base.event.impl.PacketEvent;
import me.jumba.overflow.base.tinyprotocol.api.Packet;
import me.jumba.overflow.base.tinyprotocol.packet.in.WrappedInHeldItemSlotPacket;
import me.jumba.overflow.base.user.User;

/**
 * Created on 08/01/2020 Package me.jumba.sparky.checks.other.badpackets
 */
public class BadPacketsF extends Check {
    public BadPacketsF(String checkName, String type, CheckType checkType, boolean enabled) {
        super(checkName, type, checkType, enabled);
    }

    /*
        Slot Check - checks if player sends a packet with their slot set to invalid number
     */

    @Listen
    public void onPacket(PacketEvent e) {
        if (e.getType().equalsIgnoreCase(Packet.Client.HELD_ITEM_SLOT)) {
            User user = e.getUser();
            WrappedInHeldItemSlotPacket wrappedInHeldItemSlotPacket = new WrappedInHeldItemSlotPacket(e.getPacket(), e.getPlayer());
            int slot = wrappedInHeldItemSlotPacket.getSlot();
            if (slot < 0) flag(user, "slot="+slot);
        }
    }
}
