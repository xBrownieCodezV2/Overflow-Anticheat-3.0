package me.jumba.overflow.checks.other.scaffold;

import me.jumba.overflow.base.check.Check;
import me.jumba.overflow.base.check.CheckType;
import me.jumba.overflow.base.event.Listen;
import me.jumba.overflow.base.event.impl.PacketEvent;
import me.jumba.overflow.base.tinyprotocol.api.Packet;
import me.jumba.overflow.base.tinyprotocol.packet.in.WrappedInBlockPlacePacket;
import me.jumba.overflow.base.user.User;
import me.jumba.overflow.util.block.BlockUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * Created on 20/03/2020 Package me.jumba.sparky.checks.other.scaffold
 */
public class ScaffoldA extends Check {
    public ScaffoldA(String checkName, String type, CheckType checkType, boolean enabled) {
        super(checkName, type, checkType, enabled);
        setExperimental(true);
    }

    @Listen
    public void onPacket(PacketEvent e) {

        if (e.getType().equalsIgnoreCase(Packet.Client.BLOCK_PLACE)) {
            User user = e.getUser();
            if (user != null) {
                WrappedInBlockPlacePacket wrappedInBlockPlacePacket = new WrappedInBlockPlacePacket(e.getPacket(), e.getPlayer());
                if (wrappedInBlockPlacePacket.getFace().getAdjacentY() == 0 && Math.abs(user.getConnectedTick() - user.getMiscData().getLastBlockPlaceTick()) < 5) {

                    if (wrappedInBlockPlacePacket.getItemStack().getType() != Material.AIR) {
                        if ((user.getMovementData().getTo().getY() - user.getMovementData().getFrom().getY()) < 0.42f) {

                            Location location = user.getMovementData().getTo().toLocation(user.getPlayer().getWorld()).clone().add(0, -1, 0);
                            Block block = BlockUtil.getBlock(location);

                            if (location.getBlockY() == wrappedInBlockPlacePacket.getPosition().getY() && block != null && block.getType() == Material.AIR) {

                                if (user.getMovementData().isSprinting() && user.getMovementData().isOnGround() && user.getMovementData().isLastOnGround() && user.getMovementData().getAirTicks() < 1 && user.getMovementData().getGroundTicks() > 19) {
                                    flag(user, "sprint="+user.getMovementData().isSprinting(), "ground="+user.getMovementData().isOnGround(), "groundTicks="+user.getMovementData().getGroundTicks(), "airTicks="+user.getMovementData().getAirTicks());
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
