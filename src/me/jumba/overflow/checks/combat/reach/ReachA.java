package me.jumba.overflow.checks.combat.reach;

import me.jumba.overflow.Overflow;
import me.jumba.overflow.base.check.Check;
import me.jumba.overflow.base.check.CheckType;
import me.jumba.overflow.base.event.Listen;
import me.jumba.overflow.base.event.impl.PacketEvent;
import me.jumba.overflow.base.tinyprotocol.api.Packet;
import me.jumba.overflow.base.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import me.jumba.overflow.base.user.User;
import me.jumba.overflow.util.location.CustomLocation;
import me.jumba.overflow.util.version.VersionUtil;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created on 16/03/2020 Package me.jumba.sparky.checks.combat.reach
 */
public class ReachA extends Check {
    public ReachA(String checkName, String type, CheckType checkType, boolean enabled) {
        super(checkName, type, checkType, enabled);
    }

    @Listen
    public void onPacket(PacketEvent e) {
        User user = e.getUser();
        if (user != null) {

            if (e.getType().equalsIgnoreCase(Packet.Client.USE_ENTITY)) {
                WrappedInUseEntityPacket wrappedInUseEntityPacket = new WrappedInUseEntityPacket(e.getPacket(), e.getPlayer());
                if (wrappedInUseEntityPacket.getEntity() != null && wrappedInUseEntityPacket.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {
                    if (wrappedInUseEntityPacket.getEntity() instanceof Player) {

                        if (user.getLagProcessor().isTotalLag() || Overflow.getInstance().isLagging() || user.getCombatData().cancelTicks > 5 || user.getPlayer().isFlying() || user.getPlayer().getAllowFlight() || user.getPlayer().getGameMode().equals(GameMode.CREATIVE) || (Overflow.getInstance().getVersionUtil().getVersion() != VersionUtil.Version.V1_7 && user.getPlayer().getGameMode().equals(GameMode.SPECTATOR))) {
                            user.getCheckData().reachAVerbose = 0;
                            return;
                        }

                        User targetUser = Overflow.getInstance().getUserManager().getUser(wrappedInUseEntityPacket.getEntity().getUniqueId());

                        if (targetUser != null) {

                            double maxDistance = 3.1f;

                            if (targetUser.getMovementData().isCollidesHorizontally()) {
                                maxDistance += 3.5f;
                            }

                            if (user.getCombatData().isInCombo()) {
                                maxDistance += 3.3f;
                            }

                            Location origin = user.getPlayer().getLocation();
                            List<Vector> pastLocation = user.getCheckData().reachALocations.getEstimatedLocation(user.getLagProcessor().getLastTransaction(), 150).stream().map(CustomLocation::toVector).collect(Collectors.toList());
                            float distance = (float) pastLocation.stream().mapToDouble(vec -> vec.clone().setY(0).distance(origin.toVector().clone().setY(0)) - 0.3f).min().orElse(0);
                            if (distance > maxDistance) {
                                if (user.getCheckData().reachAVerbose++ > 5) {
                                    flag(user, "distance=" + distance, "max=" + maxDistance, "verbose=" + user.getCheckData().reachAVerbose);
                                }
                            } else {
                                user.getCheckData().reachAVerbose -= user.getCheckData().reachAVerbose > 0 ? 1 : 0;
                            }
                        }
                    }
                }
            }

            if (e.getType().equalsIgnoreCase(Packet.Client.POSITION_LOOK) || e.getType().equalsIgnoreCase(Packet.Client.POSITION) || e.getType().equalsIgnoreCase(Packet.Client.LOOK) || e.getType().equalsIgnoreCase(Packet.Client.FLYING)) {
                if (user.getCombatData().getLastEntityAttacked() != null) {
                    user.getCheckData().reachALocations.addLocation(user.getCombatData().getLastEntityAttacked().getLocation());
                }
            }
        }
    }
}
