package me.jumba.overflow.checks.combat.killaura;

import me.jumba.overflow.base.check.Check;
import me.jumba.overflow.base.check.CheckType;
import me.jumba.overflow.base.event.Listen;
import me.jumba.overflow.base.event.impl.PacketEvent;
import me.jumba.overflow.base.tinyprotocol.api.Packet;
import me.jumba.overflow.base.tinyprotocol.packet.in.WrappedInBlockDigPacket;
import me.jumba.overflow.base.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import me.jumba.overflow.base.user.User;

/**
 * Created on 19/01/2020 Package me.jumba.sparky.checks.combat.killaura
 */
public class KillauraG extends Check {
    public KillauraG(String checkName, String type, CheckType checkType, boolean enabled) {
        super(checkName, type, checkType, enabled);
    }

    /*
        Autoblock check - checks to see if the player sends a 'BLOCK_DIG' & 'USE_ENTITY' as the same tick
     */

    @Listen
    public void onPacket(PacketEvent e) {
        User user = e.getUser();
        if (user != null && user.getConnectedTick() > 1) {

            if (e.getType().equalsIgnoreCase(Packet.Client.BLOCK_DIG)) {
                WrappedInBlockDigPacket wrappedInBlockPlacePacket = new WrappedInBlockDigPacket(e.getPacket(), e.getPlayer());

                if (wrappedInBlockPlacePacket.getDirection().getAdjacentY() == -1) {
                    user.getCheckData().killauraGLastBlockPlaceTick = (user.getFlyingTick() + user.getConnectedTick());
                }
            }

            if (e.getType().equalsIgnoreCase(Packet.Client.USE_ENTITY)) {
                WrappedInUseEntityPacket wrappedInUseEntityPacket = new WrappedInUseEntityPacket(e.getPacket(), e.getPlayer());

                if (wrappedInUseEntityPacket.getEntity() != null && wrappedInUseEntityPacket.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK && user.getCheckData().killauraGLastBlockPlaceTick == (user.getFlyingTick() + user.getConnectedTick()) && user.getMiscData().isSword(user.getPlayer().getInventory().getItemInHand())) {
                    flag(user, "tick="+user.getFlyingTick(), "blockDigTick="+(user.getCheckData().killauraGLastBlockPlaceTick - user.getConnectedTick()));
                }
            }
        }
    }
}
