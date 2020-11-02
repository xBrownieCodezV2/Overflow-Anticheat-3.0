package me.jumba.overflow.listener;

import me.jumba.overflow.Overflow;
import me.jumba.overflow.base.event.Listen;
import me.jumba.overflow.base.event.SparkyListener;
import me.jumba.overflow.base.event.impl.PacketEvent;
import me.jumba.overflow.base.tinyprotocol.api.Packet;
import me.jumba.overflow.base.tinyprotocol.packet.in.WrappedInTabComplete;
import me.jumba.overflow.base.user.User;

/**
 * Created on 05/01/2020 Package me.jumba.sparky.listener
 */
public class PacketListener implements SparkyListener {
    @Listen
    public void onPacket(PacketEvent e) {
        User user = e.getUser();
        if (user != null) {

            user.getMovementProcessor().update(e.getPacket(), e.getType());

            user.getCombatProcessor().update(e.getPacket(), e.getType());

            user.getLagProcessor().update(e.getPacket(), e.getType());

            user.getOtherProcessor().update(e.getPacket(), e.getType());

            user.getPredictionProcessor().update(e);

            if (e.isPacketMovement()) {
                user.getOptifineProcessor().update(user);
            }

            if (e.getType().equalsIgnoreCase(Packet.Client.TAB_COMPLETE) && Overflow.getInstance().getConfigManager().isBlockTapComplete() && !e.getPlayer().isOp()) {

                WrappedInTabComplete wrappedInTabComplete = new WrappedInTabComplete(e.getPacket(), e.getPlayer());
                String cmd = wrappedInTabComplete.getMessage().toLowerCase();
                if (Overflow.getInstance().getConfigManager().isHiderEnabled() && Overflow.getInstance().getConfigManager().isBlockTapComplete()) {

                    if (cmd.contains("/minecraft:o") || cmd.isEmpty() || cmd.startsWith("/")) {
                        e.setCancelled(true);
                    }

                    if ((cmd.contains("/o") && !cmd.contains(" ")) || (cmd.startsWith("/ver") && !cmd.contains("  ")) || (cmd.startsWith("/version") && !cmd.contains("  ")) || (cmd.startsWith("/?") && !cmd.contains("  ")) || (cmd.startsWith("/about") && !cmd.contains("  ")) || (cmd.startsWith("/help") && !cmd.contains("  "))) {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }
}
