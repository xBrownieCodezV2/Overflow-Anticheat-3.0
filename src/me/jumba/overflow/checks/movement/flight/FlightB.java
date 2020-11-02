package me.jumba.overflow.checks.movement.flight;

import me.jumba.overflow.Overflow;
import me.jumba.overflow.base.check.Check;
import me.jumba.overflow.base.check.CheckType;
import me.jumba.overflow.base.event.Listen;
import me.jumba.overflow.base.event.impl.PacketEvent;
import me.jumba.overflow.base.user.User;
import me.jumba.overflow.util.block.BlockUtil;
import me.jumba.overflow.util.math.MathUtil;
import me.jumba.overflow.util.time.TimeUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * Created on 07/01/2020 Package me.jumba.sparky.checks.movement.flight
 */
public class FlightB extends Check {
    public FlightB(String checkName, String type, CheckType checkType, boolean enabled) {
        super(checkName, type, checkType, enabled);
    }
    /*
        Float check - Detects static flights or very slow gliding flys
     */

    @Listen
    public void onPacket(PacketEvent e) {
        if (e.isPacketMovement()) {
            User user = e.getUser();
            if (user != null && user.isSafe()) {
                if (Overflow.getInstance().isLagging() || !user.isSafe() || TimeUtils.secondsFromLong(user.getMovementData().getLastCollidedGround()) < 5L || user.getMovementData().isCollidedGround() || user.getMovementData().getCollidedGroundTicks() > 0 || user.getMovementData().isExplode() || user.getPlayer().isFlying() || user.getPlayer().getAllowFlight() || TimeUtils.secondsFromLong(user.getMiscData().getLastBlockCancel()) < 3L || user.getMiscData().isSwitchedGamemodes() || user.isWaitingForMovementVerify() || (System.currentTimeMillis() - user.getMovementData().getLastTeleport()) < 1000L || (System.currentTimeMillis() - user.getCombatData().getLastRandomDamage()) < 1000L || TimeUtils.secondsFromLong(user.getBlockData().lastSline) < 5L || user.getBlockData().slimeTicks > 0 || user.getBlockData().slime || (System.currentTimeMillis() - user.getMovementData().getLastEnderpearl()) < 1000L || user.getMiscData().getJumpPotionTicks() > 0 || (System.currentTimeMillis() - user.getCombatData().getLastFireDamage()) < 1000L || (System.currentTimeMillis() - user.getCombatData().getLastBowDamage()) < 1000L || user.getMiscData().isNearBoat() || user.getMiscData().getBoatTicks() > 0 || user.getBlockData().climbableTicks > 0 || user.getBlockData().slimeTicks > 0 || user.getBlockData().slime || (System.currentTimeMillis() - user.getCombatData().getLastEntityDamageAttack()) < 1000L || user.generalCancel() || user.getBlockData().liquidTicks > 0 || user.generalCancel() || user.getMiscData().isNearBoat() || user.getMiscData().getBoatTicks() > 0) {
                    user.getCheckData().flightBVerbose = 0;
                    return;
                }

                double diff = (e.getTo().getY() - e.getFrom().getY());

                Block block = BlockUtil.getBlock(user.getMovementData().getBukkitTo().clone().add(0, -1, 0));

                if (block != null) {

                    if (!user.getMovementData().isOnGround() && !user.getMovementData().isLastOnGround() && !user.getLagProcessor().isLagging() && Math.abs(user.getConnectedTick() - user.getLagProcessor().getLastLagTick()) > 100) {

                        double abs = Math.abs(diff);

                        if (abs < (user.getLagProcessor().isLagging() ? 0.10 : 0.42) && user.getMovementData().getAirTicks() > 5 && block.getType() == Material.AIR) {
                            if (user.getCheckData().flightBVerbose < 50) user.getCheckData().flightBVerbose++;
                        } else {
                            if (user.getCheckData().flightBVerbose > 0) user.getCheckData().flightBVerbose--;
                        }

                        if (user.getCheckData().flightBVerbose > (user.getLagProcessor().isLagging() ? 20 : 6))
                            flag(user, "verbose=" + user.getCheckData().flightBVerbose, "diff=" + MathUtil.preciseRound(abs, 2));
                    } else {
                        user.getCheckData().flightBVerbose = 0;
                    }

                    user.getCheckData().lastFlighBDiff = diff;
                }
            }
        }
    }
}
