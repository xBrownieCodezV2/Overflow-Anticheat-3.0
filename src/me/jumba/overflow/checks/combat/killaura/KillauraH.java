package me.jumba.overflow.checks.combat.killaura;

import me.jumba.overflow.base.check.Check;
import me.jumba.overflow.base.check.CheckType;
import me.jumba.overflow.base.event.Listen;
import me.jumba.overflow.base.event.impl.PacketEvent;
import me.jumba.overflow.base.tinyprotocol.api.Packet;
import me.jumba.overflow.base.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import me.jumba.overflow.base.user.User;

/**
 * Created on 19/03/2020 Package me.jumba.sparky.checks.combat.killaura
 */
public class KillauraH extends Check {
    public KillauraH(String checkName, String type, CheckType checkType, boolean enabled) {
        super(checkName, type, checkType, enabled);
    }

    /*
        Post check - checks if the 'POSITION_LOOK' was sent within the time that the 'USE_ENTITY' checks for
     */

    @Listen
    public void onPacket(PacketEvent e) {
        User user = e.getUser();
        if (user != null) {

            if (user.getLagProcessor().isLagging()) {
                return;
            }

            if (e.getType().equalsIgnoreCase(Packet.Client.USE_ENTITY)) {
                WrappedInUseEntityPacket wrappedInUseEntityPacket = new WrappedInUseEntityPacket(e.getPacket(), e.getPlayer());

                if (wrappedInUseEntityPacket.getEntity() != null
                        && wrappedInUseEntityPacket.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK
                        && user.getLagProcessor().isTotalLag()) {

                    long time = (System.currentTimeMillis() - user.getCheckData().lastKillauraHPostionLook);

                    if (time < (user.getLagProcessor().isLagging() ? 10L : 35L) && user.getCheckData().killauraHVerbose.flag((time < 20L ? 2 : 4), 920L)) {
                        flag(user, "time=" + time, "verbose=" + user.getCheckData().killauraHVerbose.getVerbose(), "lagging=" + user.getLagProcessor().isLagging());
                    }
                }
            }

            if (e.getType().equalsIgnoreCase(Packet.Client.POSITION_LOOK)) {
                user.getCheckData().lastKillauraHPostionLook = System.currentTimeMillis();
            }
        }
    }
}
