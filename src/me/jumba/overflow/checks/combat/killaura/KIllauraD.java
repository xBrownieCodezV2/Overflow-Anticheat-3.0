package me.jumba.overflow.checks.combat.killaura;

import me.jumba.overflow.base.check.Check;
import me.jumba.overflow.base.check.CheckType;
import me.jumba.overflow.base.event.Listen;
import me.jumba.overflow.base.event.impl.PacketEvent;
import me.jumba.overflow.base.tinyprotocol.api.Packet;
import me.jumba.overflow.base.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import me.jumba.overflow.base.user.User;
import me.jumba.overflow.hook.HookManager;
import me.jumba.overflow.util.time.TimeUtils;

/**
 * Created on 07/01/2020 Package me.jumba.sparky.checks.combat.killaura
 */
public class KIllauraD extends Check {
    public KIllauraD(String checkName, String type, CheckType checkType, boolean enabled) {
        super(checkName, type, checkType, enabled);
    }

    /*
        Invalid Attack Order Check - Detects if the player doesn't send the 'ARM_ANIMATION' & 'USE_ENTITY' in the correct order
     */

    @Listen
    public void onPacket(PacketEvent e) {
        User user = e.getUser();
        if (user != null) {

            if (e.isPacketMovement()) {
                if (user.getLagProcessor().isLagging() || TimeUtils.secondsFromLong(user.getLagProcessor().getLastPreLag()) < 10L) {
                    user.getCheckData().killauraDVerbose.setVerbose(0);
                    return;
                }
            }

            if (e.getType().equalsIgnoreCase(Packet.Client.ARM_ANIMATION)) {
                user.getCheckData().lastKillauraDSwing = System.currentTimeMillis();
                user.getCheckData().killauraDArmSwong = true;
            }

            if (e.getType().equalsIgnoreCase(Packet.Client.USE_ENTITY)) {
                WrappedInUseEntityPacket wrappedInUseEntityPacket = new WrappedInUseEntityPacket(e.getPacket(), e.getPlayer());
                if (wrappedInUseEntityPacket.getEntity() != null && wrappedInUseEntityPacket.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {

                    boolean isDelayedClientVersion = !(user.getCurrentClientVersion() == HookManager.Helper.Versions.V1_7 || user.getCurrentClientVersion() == HookManager.Helper.Versions.V1_8);

                    user.getCheckData().lastKillauraDUseEntity = System.currentTimeMillis();

                    if ((System.currentTimeMillis() - user.getCheckData().lastKillauraDSwing) > (isDelayedClientVersion ? 350L : 80L) && user.getCheckData().killauraDVerbose.flag(5, 999L)) {
                        flag(user, "swing="+(System.currentTimeMillis() - user.getCheckData().lastKillauraDSwing), "verbose="+user.getCheckData().killauraDVerbose.getVerbose());
                    }
                }
            }
        }
    }
}
