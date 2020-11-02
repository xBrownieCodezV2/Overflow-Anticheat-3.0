package me.jumba.overflow.checks.combat.aim;

import me.jumba.overflow.base.check.Check;
import me.jumba.overflow.base.check.CheckType;
import me.jumba.overflow.base.event.Listen;
import me.jumba.overflow.base.event.impl.PacketEvent;
import me.jumba.overflow.base.tinyprotocol.api.Packet;
import me.jumba.overflow.base.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import me.jumba.overflow.base.user.User;

/**
 * Created on 31/01/2020 Package me.jumba.sparky.checks.combat.aim
 */
public class AimE extends Check {
    public AimE(String checkName, String type, CheckType checkType, boolean enabled) {
        super(checkName, type, checkType, enabled);
    }

    /*
        Tick aura check - detects auras that aim in ticks or just mess with 'POSITION_LOOK' & 'POSITION'
     */

    @Listen
    public void onPacket(PacketEvent e) {
        User user = e.getUser();

        if (e.isPacketMovement()) {



            if ((System.currentTimeMillis() - user.getCombatData().getLastUseEntityPacket()) < 1000L) {
                if (e.getType().equalsIgnoreCase(Packet.Client.POSITION_LOOK)) {
                    user.getCheckData().lastAimEPosLook = System.currentTimeMillis();
                }

                if (e.getType().equalsIgnoreCase(Packet.Client.POSITION)) {
                    user.getCheckData().lastAimEPos = System.currentTimeMillis();
                }
            }
        }

        if (e.getType().equalsIgnoreCase(Packet.Client.USE_ENTITY)) {
            WrappedInUseEntityPacket wrappedInUseEntityPacket = new WrappedInUseEntityPacket(e.getPacket(), e.getPlayer());
            if (wrappedInUseEntityPacket.getEntity() != null && wrappedInUseEntityPacket.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {



                long pos = (System.currentTimeMillis() - user.getCheckData().lastAimEPos);
                long posLook = (System.currentTimeMillis() - user.getCheckData().lastAimEPosLook);


                if ((System.currentTimeMillis() - user.getMovementData().getLastFullBlockMoved()) > 500L || Math.abs(user.getConnectedTick() - user.getLagProcessor().getLastLagTick()) < 100) {
                    user.getCheckData().aimEValidation = 0;
                    return;
                }

                if (pos < 200L && posLook < 300L && user.getMovementData().getMovementSpeed() > 0.155 && (e.getTo().getYaw() != e.getFrom().getYaw())) {
                    if (user.getCheckData().aimEValidation < 20) user.getCheckData().aimEValidation++;
                } else {
                    if (user.getCheckData().aimEValidation > 0) user.getCheckData().aimEValidation-=5;
                }

                if (user.getCheckData().aimEValidation > 5) {
                    flag(user, "verbose="+user.getCheckData().aimEValidation, "pos="+pos, "posLook="+posLook);
                }
            }
        }
    }
}
