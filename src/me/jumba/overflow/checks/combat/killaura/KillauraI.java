package me.jumba.overflow.checks.combat.killaura;

import me.jumba.overflow.base.check.Check;
import me.jumba.overflow.base.check.CheckType;
import me.jumba.overflow.base.event.Listen;
import me.jumba.overflow.base.event.impl.PacketEvent;
import me.jumba.overflow.base.tinyprotocol.api.Packet;
import me.jumba.overflow.base.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import me.jumba.overflow.base.user.User;
import me.jumba.overflow.util.math.MathUtil;

/**
 * Created on 06/04/2020 Package me.jumba.sparky.checks.combat.killaura
 */
public class KillauraI extends Check {
    public KillauraI(String checkName, String type, CheckType checkType, boolean enabled) {
        super(checkName, type, checkType, enabled);
    }

    @Listen
    public void onPacket(PacketEvent e) {
        User user = e.getUser();

        if (user != null) {

            if (e.isPacketMovement()) {

                if (Math.abs(user.getConnectedTick() - user.getLagProcessor().getLastLagTick()) < 100 || user.getLagProcessor().isLagging() || user.getCombatData().cancelTicks > 0 || user.getBlockData().liquidTicks > 0) {
                    user.getCheckData().killauraISwings = 2;
                    user.getCheckData().killauraIAttacks = 2;
                    return;
                }

                double pitch = Math.abs(e.getTo().getPitch() - e.getFrom().getPitch());
                double yaw = Math.abs(e.getTo().getYaw() - e.getFrom().getYaw());
                
                if ((System.currentTimeMillis() - user.getCombatData().getLastUseEntityPacket() < 1000L)) {
                    if (user.getCheckData().killauraISwings > 0 && user.getCheckData().killauraIAttacks > 0) {
                        int diff = Math.abs(user.getCheckData().killauraIAttacks - user.getCheckData().killauraISwings);

                        if (diff <= 0 && diff == user.getCheckData().killauraILastDiff && pitch > 1 && yaw > 1.5) {
                            if (user.getCheckData().killauraIVerbose.flag(80, 950L)) {
                                flag(user, "diff="+ MathUtil.trimFloat(4, diff), "verbose="+user.getCheckData().killauraIVerbose.getVerbose(), "swings="+user.getCheckData().killauraISwings, "attacks="+user.getCheckData().killauraIAttacks);
                            }
                        }

                        if (user.getCheckData().killauraISwings > 50) {
                            user.getCheckData().killauraISwings = 0;
                            user.getCheckData().killauraIAttacks = 0;
                        }
                        user.getCheckData().killauraILastDiff = diff;
                    }
                }
            }

            if (e.getType().equalsIgnoreCase(Packet.Client.ARM_ANIMATION)) {
                user.getCheckData().killauraISwings++;
            }

            if (e.getType().equalsIgnoreCase(Packet.Client.USE_ENTITY)) {
                WrappedInUseEntityPacket useEntityPacket = new WrappedInUseEntityPacket(e.getPacket(), e.getPlayer());
                if (useEntityPacket.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {
                    user.getCheckData().killauraIAttacks++;
                }
            }
        }
    }
}
