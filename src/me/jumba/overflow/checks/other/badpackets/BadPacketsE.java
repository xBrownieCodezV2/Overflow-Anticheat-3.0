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
public class BadPacketsE extends Check {
    public BadPacketsE(String checkName, String type, CheckType checkType, boolean enabled) {
        super(checkName, type, checkType, enabled);
    }

    /*
        Slot Check - checks if the player sends a 'HELD_ITEM_SLOT'HELD_ITEM_SLOT with the same slot in the packet (gets alot of scaffolds)
     */

    @Listen
    public void onPacket(PacketEvent e) {


        if (e.getType().equalsIgnoreCase(Packet.Client.HELD_ITEM_SLOT)) {
            User user = e.getUser();
            WrappedInHeldItemSlotPacket wrappedInHeldItemSlotPacket = new WrappedInHeldItemSlotPacket(e.getPacket(), e.getPlayer());
            int slot = wrappedInHeldItemSlotPacket.getSlot();

            if (user.getCheckData().badPacketsETotalSlots < 20) user.getCheckData().badPacketsETotalSlots++;

            if (slot == user.getCheckData().badPacketsELastSlot && user.getCheckData().badPacketsETotalSlots > 1) {
                flag(user, "slot="+slot, "lastSlot="+user.getCheckData().badPacketsELastSlot);
            }
            user.getCheckData().badPacketsELastSlot = slot;
        }
    }
}
