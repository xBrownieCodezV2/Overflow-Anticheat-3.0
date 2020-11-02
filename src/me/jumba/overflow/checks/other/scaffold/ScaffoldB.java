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
public class ScaffoldB extends Check {
    public ScaffoldB(String checkName, String type, CheckType checkType, boolean enabled) {
        super(checkName, type, checkType, enabled);
        setExperimental(true);
    }

    @Listen
    public void onPacket(PacketEvent e) {

        User user = e.getUser();
        if (user != null) {
            if (e.isPacketMovement()) {
                if (user.getMovementData().getAirTicks() > 6) {
                    user.getCheckData().lastScaffoldBJump = System.currentTimeMillis();
                }
            }

            if (e.getType().equalsIgnoreCase(Packet.Client.BLOCK_PLACE)) {

                WrappedInBlockPlacePacket wrappedInBlockPlacePacket = new WrappedInBlockPlacePacket(e.getPacket(), e.getPlayer());
                if (wrappedInBlockPlacePacket.getFace().getAdjacentY() == 0 && Math.abs(user.getConnectedTick() - user.getMiscData().getLastBlockPlaceTick()) < 5) {

                    if (wrappedInBlockPlacePacket.getItemStack().getType() != Material.AIR) {
                        if ((System.currentTimeMillis() - user.getCheckData().lastScaffoldBJump) < 1000L) {
                            user.getCheckData().scaffoldBVerbose.setVerbose(0);
                            return;
                        }
                        if ((user.getMovementData().getTo().getY() - user.getMovementData().getFrom().getY()) < 0.42f) {


                            Location location = user.getMovementData().getTo().toLocation(user.getPlayer().getWorld()).clone().add(0, -1, 0);
                            Block block = BlockUtil.getBlock(location);

                            if (location.getBlockY() == wrappedInBlockPlacePacket.getPosition().getY() && block != null && block.getType() != Material.AIR) {
                                if ((wrappedInBlockPlacePacket.getVecX() > 0.0 || wrappedInBlockPlacePacket.getVecY() > 0.0 || wrappedInBlockPlacePacket.getVecZ() > 0.0) && user.getCheckData().scaffoldBVerbose.flag(5, 20, 999L)) {
                                    flag(user, "x="+wrappedInBlockPlacePacket.getVecX(), "y="+wrappedInBlockPlacePacket.getVecY(), "z="+wrappedInBlockPlacePacket.getVecZ(), "verbose="+user.getCheckData().scaffoldBVerbose.getVerbose());
                                }
                            } else {
                                user.getCheckData().scaffoldBVerbose.setVerbose(0);
                            }
                        } else {
                            user.getCheckData().lastScaffoldBJump = System.currentTimeMillis();
                        }
                    }
                }
            }
        }
    }
}
