package me.jumba.overflow.checks.other.badpackets;

import me.jumba.overflow.Overflow;
import me.jumba.overflow.base.check.Check;
import me.jumba.overflow.base.check.CheckType;
import me.jumba.overflow.base.event.Listen;
import me.jumba.overflow.base.event.impl.PacketEvent;
import me.jumba.overflow.base.tinyprotocol.api.Packet;
import me.jumba.overflow.base.tinyprotocol.packet.in.WrappedInFlyingPacket;
import me.jumba.overflow.base.user.User;
import me.jumba.overflow.util.math.MathUtil;
import me.jumba.overflow.util.time.TimeUtils;

/**
 * Created on 08/01/2020 Package me.jumba.sparky.checks.other.badpackets
 */
public class BadPacketsD extends Check {
    public BadPacketsD(String checkName, String type, CheckType checkType, boolean enabled) {
        super(checkName, type, checkType, enabled);
    }
    /*
        Illegal position check - Checks if the is in an illegal position that was set by the client
     */

    @Listen
    public void onPacket(PacketEvent e) {
        if (e.getType().equalsIgnoreCase(Packet.Client.POSITION) || e.getType().equalsIgnoreCase(Packet.Client.POSITION_LOOK)) {
            WrappedInFlyingPacket wrappedInFlyingPacket = new WrappedInFlyingPacket(e.getPacket(), e.getPlayer());

            User user = e.getUser();
            if (Overflow.getInstance().isLagging()) return;

            if (user.getMovementData().isChunkLoaded() || (System.currentTimeMillis() - user.getMovementData().getLastTeleport()) < 1000L || user.generalCancel() || TimeUtils.secondsFromLong(user.getCombatData().getLastRespawn()) < 5L || TimeUtils.secondsFromLong(user.getCombatData().getLastDeath()) < 5L) {
                user.getCheckData().badPacketsDYInvalid = false;
                return;
            }

            if (wrappedInFlyingPacket.isPos()) {
                double postionY = wrappedInFlyingPacket.getY();

                double diff = (postionY - user.getCheckData().lastBadPacketsDPostionY);

                if (diff > 5.00 && (user.getMovementData().didSendServerPostion(9) || user.getMovementData().getLastServerPostion() < 0) && user.getCheckData().badPacketsDVerbose.flag(2, 999L)) {

                    flag(user, "diff="+ MathUtil.trim(4, diff), "serverPostion=" + Math.abs(user.getMovementData().getLastServerPostion() - user.getConnectedTick()));
                }

                if (postionY <= 0.0) {
                    user.getCheckData().badPacketsDYInvalid = true;
                    user.getCheckData().badPacketsDDown = System.currentTimeMillis();
                }

                if (postionY > 0.0 && user.getCheckData().badPacketsDYInvalid) {

                    flag(user, "postionY="+postionY);

                    user.getCheckData().badPacketsDYInvalid = false;
                    user.getMovementData().setLastServerPostion(-1);
                }

                user.getCheckData().lastBadPacketsDPostionY = postionY;
            }
        }
    }
}
