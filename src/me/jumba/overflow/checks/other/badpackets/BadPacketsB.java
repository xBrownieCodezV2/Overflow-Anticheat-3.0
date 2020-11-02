package me.jumba.overflow.checks.other.badpackets;

import me.jumba.overflow.Overflow;
import me.jumba.overflow.base.check.Check;
import me.jumba.overflow.base.check.CheckType;
import me.jumba.overflow.base.event.Listen;
import me.jumba.overflow.base.event.impl.PacketEvent;
import me.jumba.overflow.base.user.User;
import me.jumba.overflow.util.time.TimeUtils;
import org.bukkit.GameMode;

/**
 * Created on 07/01/2020 Package me.jumba.sparky.checks.other.badpackets
 */
public class BadPacketsB extends Check {
    public BadPacketsB(String checkName, String type, CheckType checkType, boolean enabled) {
        super(checkName, type, checkType, enabled);
    }

    /*
        Checks for invalid motion changes while on the ground
     */

    @Listen
    public void onPacket(PacketEvent e) {
        if (e.isPacketMovement()) {
            User user = e.getUser();
            if (user != null) {

                if (Overflow.getInstance().isLagging()) return;

                if (user.getBlockData().climbableTicks > 0 || user.getBlockData().snowTicks > 0 || user.getBlockData().slimeTicks > 0 || user.getBlockData().stairTicks > 0 || user.getBlockData().slime || user.getBlockData().liquidTicks > 0 || (user.getBlockData().blockAboveTicks > 0 || (user.getBlockData().slabTicks > 0 || TimeUtils.elapsed(user.getTimestamp()) < 4L || user.getBlockData().fenceTicks > 0 || user.getPlayer().getAllowFlight() || user.getPlayer().getGameMode().equals(GameMode.CREATIVE) || user.getPlayer().isFlying()))) {
                    user.getCheckData().badPacketsBVerbose = 0;
                    return;
                }

                double y = e.getTo().getY();
                double diff = Math.abs(y - user.getCheckData().lastBadPacketsBY);

                if (diff > 0.0 && Math.abs(diff - user.getCheckData().badPacketsBDiff) < 0.055) {
                    if (user.getCheckData().badPacketsBStable < 50) user.getCheckData().badPacketsBStable++;
                } else {
                    if (user.getCheckData().badPacketsBStable > 0) user.getCheckData().badPacketsBStable--;
                }

                user.getCheckData().badPacketsBDiff = diff;

                if (user.getCheckData().badPacketsBStable > 3) {
                    if (Overflow.getInstance().isLagging() || user.getBlockData().stairTicks > 0 || user.getMiscData().getMountedTicks() > 0 || user.getBlockData().railTicks > 0 || (System.currentTimeMillis() - user.getMovementData().getLastBlockJump()) < 1000L) {
                        user.getCheckData().badPacketsBVerbose = 0;
                        return;
                    }

                    if (!user.getMovementData().isOnGround()) {
                        if (user.getCheckData().badPacketsBVerbose > 0) user.getCheckData().badPacketsBVerbose = 0;
                    }

                    if ((user.getMovementData().isOnGround()) && e.getTo().getY() > e.getFrom().getY() && Math.abs(e.getTo().getY() - e.getFrom().getY()) > 0.0) {
                        if (user.getCheckData().badPacketsBVerbose < 20) user.getCheckData().badPacketsBVerbose += 2;
                    }

                    if (user.getCheckData().badPacketsBVerbose > 15 && user.getMovementData().isOnGround()) {
                        if (Math.abs(e.getTo().getY() - e.getFrom().getY()) == 0.0) {
                            user.getCheckData().badPacketsBVerbose = 0;
                        } else {
                            flag(user, "verbose=" + user.getCheckData().badPacketsBVerbose, "ground=" + user.getMovementData().isOnGround(), "yDiff=" + Math.abs(e.getTo().getY() - e.getFrom().getY()));
                        }
                    }
                }
                user.getCheckData().lastBadPacketsBY = y;
            }
        }
    }
}
