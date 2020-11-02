package me.jumba.overflow.checks.combat.killaura;

import me.jumba.overflow.base.check.Check;
import me.jumba.overflow.base.check.CheckType;
import me.jumba.overflow.base.event.Listen;
import me.jumba.overflow.base.event.impl.PacketEvent;
import me.jumba.overflow.base.tinyprotocol.api.Packet;
import me.jumba.overflow.base.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import me.jumba.overflow.base.user.User;
import me.jumba.overflow.util.time.TimeUtils;

/**
 * Created on 07/01/2020 Package me.jumba.sparky.checks.combat.killaura
 */
public class KIllauraC extends Check {
    public KIllauraC(String checkName, String type, CheckType checkType, boolean enabled) {
        super(checkName, type, checkType, enabled);
    }

    /*
        No Swing - Detects if the player doesn't swing while attacking
     */

    @Listen
    public void onPacket(PacketEvent e) {
        User user = e.getUser();
        if (user != null) {

            if (e.getType().equalsIgnoreCase(Packet.Client.USE_ENTITY)) {
                WrappedInUseEntityPacket wrappedInUseEntityPacket = new WrappedInUseEntityPacket(e.getPacket(), e.getPlayer());
                if (wrappedInUseEntityPacket.getEntity() != null && wrappedInUseEntityPacket.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {
                    user.getCheckData().lastKillauraCUseEntity = System.currentTimeMillis();
                }
            } else if (e.getType().equalsIgnoreCase(Packet.Client.ARM_ANIMATION)) {
                user.getCheckData().lastKillauraCSwing = System.currentTimeMillis();
            }

            if (e.isPacketMovement() && user.isHasVerify()) {

                if (!user.getLagProcessor().isLagging() && TimeUtils.secondsFromLong(user.getLagProcessor().getLastPreLag()) > 5L) {


                    long attack = (System.currentTimeMillis() - user.getCheckData().lastKillauraCUseEntity);
                    long swing = (System.currentTimeMillis() - user.getCheckData().lastKillauraCSwing);


                    if (attack < 1000L && swing > 1000L && user.getCheckData().killaursCVerbose.flag(5,999L)) {
                        flag(user, "attack="+attack, "swing="+swing);
                    }
                }
            }
        }
    }
}
