package me.jumba.overflow.checks.movement.invalid;

import me.jumba.overflow.Overflow;
import me.jumba.overflow.base.check.Check;
import me.jumba.overflow.base.check.CheckType;
import me.jumba.overflow.base.event.Listen;
import me.jumba.overflow.base.event.impl.PacketEvent;
import me.jumba.overflow.base.tinyprotocol.api.Packet;
import me.jumba.overflow.base.tinyprotocol.packet.out.WrappedOutVelocityPacket;
import me.jumba.overflow.base.user.User;

/**
 * Created on 15/04/2020 Package me.jumba.sparky.checks.movement.invalid
 */
public class InvalidO extends Check {
    public InvalidO(String checkName, String type, CheckType checkType, boolean enabled) {
        super(checkName, type, checkType, enabled);
    }

    @Listen
    public void onPacket(PacketEvent e) {
        User user = e.getUser();
        if (user != null) {
            if (e.getType().equalsIgnoreCase(Packet.Server.ENTITY_VELOCITY)) {
                WrappedOutVelocityPacket wrappedOutVelocityPacket = new WrappedOutVelocityPacket(e.getPacket(), e.getPlayer());
                if (wrappedOutVelocityPacket.getId() == user.getPlayer().getEntityId() && (System.currentTimeMillis() - user.getCheckData().getLastInvalidOFall()) < 5L && !user.getCheckData().invalidOAllowedDamage && user.getMovementData().isClientGround() && !user.getMovementData().isLastClientGround() && user.getMovementData().getGroundTicks() < 5 && user.getMovementData().getAirTicks() < 3 && (System.currentTimeMillis() - user.getCombatData().getLastEntityDamageAttack()) > 1000L && (System.currentTimeMillis() - user.getCombatData().getLastRandomDamage()) > 1000L) {
              //      flag(user, "damage="+(System.currentTimeMillis() - user.getMovementData().getLastFallDamage()));
                    user.getMovementData().setLastFallDamage(System.currentTimeMillis() * 99999L);
                }
            }

            if (e.isPacketMovement()) {

                if (Overflow.getInstance().isLagging()) return;

                if ((System.currentTimeMillis() - user.getMovementData().getLastEnderpearl()) < 1000L) {
                    user.getCheckData().invalidOAllowedDamage = true;
                    return;
                }

                if (!user.getMovementData().isOnGround() && !user.getMovementData().isClientGround()) {
                    double delta = (user.getMovementData().getTo().getY() - user.getMovementData().getFrom().getY());

                    if (user.getBlockData().fenceTicks > 0 || user.getBlockData().blockAboveTicks > 0) {
                        user.getCheckData().invalidOAllowedDamage = true;
                        return;
                    }

                    if (delta <= -0.4) {
                        user.getCheckData().invalidOAllowedDamage = true;
                    } else {
                        user.getCheckData().invalidOAllowedDamage = false;
                    }
                } else if (user.getMovementData().getGroundTicks() > 15) {
                    user.getCheckData().invalidOAllowedDamage = false;
                }


                if ((System.currentTimeMillis() - user.getCheckData().getLastInvalidOFall()) < 100L && !user.getCheckData().invalidOAllowedDamage) {
                    //      flag(user, "time="+(System.currentTimeMillis() - user.getCheckData().getLastInvalidOFall()));
                    //        user.getMovementData().setLastFallDamage(System.currentTimeMillis() * 99999L);
                }
            }
        }
    }
}
