package me.jumba.overflow.checks.combat.killaura;

import me.jumba.overflow.base.check.Check;
import me.jumba.overflow.base.check.CheckType;
import me.jumba.overflow.base.event.Listen;
import me.jumba.overflow.base.event.impl.PacketEvent;
import me.jumba.overflow.base.tinyprotocol.api.Packet;
import me.jumba.overflow.base.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import me.jumba.overflow.base.user.User;

/**
 * Created on 19/01/2020 Package me.jumba.sparky.checks.combat.killaura
 */
public class KillauraF extends Check {
    public KillauraF(String checkName, String type, CheckType checkType, boolean enabled) {
        super(checkName, type, checkType, enabled);
    }

    /*
        Autoblock Check - Checks if the player is blocking within the same time as a 'USE_ENTITY' packet
     */

    @Listen
    public void onPacket(PacketEvent e) {
        User user = e.getUser();
        if (user != null) {

            if (e.isPacketMovement()) {
                if (Math.abs(user.getConnectedTick() - user.getLagProcessor().getLastLagTick()) < 100 || user.getLagProcessor().isLagging()) {
                    user.getCheckData().killauraFVerbose.setVerbose(0);
                    return;
                }
            }

            if (e.getType().equalsIgnoreCase(Packet.Client.USE_ENTITY)) {
                WrappedInUseEntityPacket wrappedInUseEntityPacket = new WrappedInUseEntityPacket(e.getPacket(), e.getPlayer());
                if (wrappedInUseEntityPacket.getEntity() != null && wrappedInUseEntityPacket.getEntity() != null) {

                    if (Math.abs(user.getConnectedTick() - user.getLagProcessor().getLastLagTick()) < 100 || user.getLagProcessor().isLagging() || user.getCombatData().isBreakingBlock()) {
                        user.getCheckData().killauraFVerbose.setVerbose(0);
                        return;
                    }

                    if ((System.currentTimeMillis() - user.getCheckData().lastKillauraFAttack) < 350L) {
                        if ((System.currentTimeMillis() - user.getCheckData().lastKillauraFBlockDig) < 50L) {
                            if (user.getCheckData().killauraFVerbose.flag(8, 20, 500L)) {
                                flag(user, "lastAttack="+(System.currentTimeMillis() - user.getCheckData().lastKillauraFAttack), "lastBlockDig="+(System.currentTimeMillis() - user.getCheckData().lastKillauraFBlockDig), "verbose="+user.getCheckData().killauraFVerbose.getVerbose());
                            }
                        } else {
                            user.getCheckData().killauraFVerbose.takeaway();
                        }
                    } else {
                        user.getCheckData().killauraFVerbose.takeaway();
                    }

                    user.getCheckData().lastKillauraFAttack = System.currentTimeMillis();
                }
            }

            if (e.getType().equalsIgnoreCase(Packet.Client.BLOCK_DIG)) {
                user.getCheckData().lastKillauraFBlockDig = System.currentTimeMillis();
            }
        }
    }
}
