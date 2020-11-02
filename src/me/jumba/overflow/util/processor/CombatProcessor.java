package me.jumba.overflow.util.processor;

import lombok.Setter;
import me.jumba.overflow.Overflow;
import me.jumba.overflow.base.tinyprotocol.api.Packet;
import me.jumba.overflow.base.tinyprotocol.api.TinyProtocolHandler;
import me.jumba.overflow.base.tinyprotocol.packet.in.WrappedInTransactionPacket;
import me.jumba.overflow.base.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import me.jumba.overflow.base.tinyprotocol.packet.out.WrappedOutTransaction;
import me.jumba.overflow.base.tinyprotocol.packet.out.WrappedOutVelocityPacket;
import me.jumba.overflow.base.user.User;
import me.jumba.overflow.util.math.MathUtil;
import me.jumba.overflow.util.version.VersionUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

import java.util.HashSet;
import java.util.Set;

/**
 * Created on 05/01/2020 Package me.jumba.sparky.util.processor
 */
@Setter
public class CombatProcessor {
    private User user;

    public void update(Object packet, String type) {
        if (user != null) {

            if (type.equalsIgnoreCase(Packet.Server.ENTITY_VELOCITY)) {
                WrappedOutVelocityPacket wrappedOutVelocityPacket = new WrappedOutVelocityPacket(packet, user.getPlayer());

                if (wrappedOutVelocityPacket.getId() == user.getPlayer().getEntityId()) {
                    TinyProtocolHandler.getInstance().getChannel().sendPacket(user.getPlayer(),
                            new WrappedOutTransaction(0, (short) 1337, false).getObject());

                    double velocityY = wrappedOutVelocityPacket.getY();

                    user.getMovementData().setVelX(wrappedOutVelocityPacket.getX());
                    user.getMovementData().setVelZ(wrappedOutVelocityPacket.getZ());

                    double vertical = Math.pow(velocityY + 2.0, 2.0) * 5.0;

                    user.getMovementData().setHorizontalVelocity(MathUtil.hypot(user.getMovementData().velX, user.getMovementData().velZ));
                    user.getMovementData().setVerticalVelocity(vertical);

                    if (user.getMovementData().isOnGround() && user.getPlayer().getLocation().getY() % 1.0 == 0.0) {
                        user.getMovementData().setVerticalVelocity(velocityY);
                    }
                }
            }

            if (type.equalsIgnoreCase(Packet.Client.TRANSACTION)) {
                WrappedInTransactionPacket wrappedInTransactionPacket = new WrappedInTransactionPacket(packet, user.getPlayer());

                short id = wrappedInTransactionPacket.getAction();
                short currentIDVelocity = user.getMiscData().getTransactionIDVelocity();

                if (id == 1337) {
                    user.getLagProcessor().setVelocityPing((System.currentTimeMillis() - user.getLagProcessor().getHitTime()));
                    user.getMovementData().getLastVelocityHorizontal().put(MathUtil.hypot(user.getMovementData().velX,
                            user.getMovementData().velZ), currentIDVelocity);

                    user.getMovementData().getLastVelocityVertical().put(user.getMovementData().getVerticalVelocity(), currentIDVelocity);
                    user.getCombatData().setVelocityTicks(0);
                }
            }

            if (type.equalsIgnoreCase(Packet.Client.POSITION_LOOK) || type.equalsIgnoreCase(Packet.Client.POSITION)
                    || type.equalsIgnoreCase(Packet.Client.LOOK) || type.equalsIgnoreCase(Packet.Client.FLYING)) {
                user.getCombatData().setVelocityTicks(user.getCombatData().getVelocityTicks() + 1);
            }

            if ((type.equalsIgnoreCase(Packet.Client.POSITION_LOOK)
                    || type.equalsIgnoreCase(Packet.Client.POSITION)
                    || type.equalsIgnoreCase(Packet.Client.FLYING)) && user.getConnectedTick() % 20 == 0) {
                user.getCombatData().setCps(0);
            }

            if (type.equalsIgnoreCase(Packet.Client.ARM_ANIMATION)) {
                user.getCombatData().setCps(user.getCombatData().getCps() + 1);

                if (Overflow.getInstance().getVersionUtil().getVersion() == VersionUtil.Version.V1_8) {
                    if (user.getPlayer().getTargetBlock((Set<Material>) null, 5).getType() != Material.AIR) {
                        user.getCombatData().setBreakingBlock(true);
                    } else if (user.getPlayer().getTargetBlock((Set<Material>) null, 5).getType() == Material.AIR) {
                        user.getCombatData().setBreakingBlock(false);
                    }
                }

                if (Overflow.getInstance().getVersionUtil().getVersion() == VersionUtil.Version.V1_7) {
                    if (user.getPlayer().getTargetBlock((HashSet<Byte>) null, 5).getType() != Material.AIR) {
                        user.getCombatData().setBreakingBlock(true);
                    } else if (user.getPlayer().getTargetBlock((HashSet<Byte>) null, 5).getType() == Material.AIR) {
                        user.getCombatData().setBreakingBlock(false);
                    }
                }
            }

            if (type.equalsIgnoreCase(Packet.Client.USE_ENTITY)) {
                WrappedInUseEntityPacket wrappedInUseEntityPacket = new WrappedInUseEntityPacket(packet, user.getPlayer());
                if (wrappedInUseEntityPacket.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK && wrappedInUseEntityPacket.getEntity() != null) {

                    user.getCombatData().getUseEntityTimer().reset();

                    if (wrappedInUseEntityPacket.getEntity() instanceof Player) {
                        User attackedUser = Overflow.getInstance().getUserManager().getUser(wrappedInUseEntityPacket.getEntity().getUniqueId());
                        if (attackedUser != null) user.getCombatData().setTargetUser(attackedUser);
                    }

                    if (user.getCombatData().getLastEntityAttacked() != null) {
                        if (user.getCombatData().getLastEntityAttacked() != wrappedInUseEntityPacket.getEntity()) {
                            user.getCombatData().constantEntityTicks = 0;
                        } else {
                            user.getCombatData().constantEntityTicks++;
                        }
                    }

                    if (wrappedInUseEntityPacket.getEntity() instanceof Player || wrappedInUseEntityPacket.getEntity() instanceof Villager) {
                        user.getCombatData().setLastEntityAttacked(wrappedInUseEntityPacket.getEntity());
                        user.getCombatData().setLastUseEntityPacket(System.currentTimeMillis());
                    }
                }
            }
        }
    }
}
