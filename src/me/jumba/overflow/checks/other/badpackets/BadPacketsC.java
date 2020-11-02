package me.jumba.overflow.checks.other.badpackets;

import me.jumba.overflow.Overflow;
import me.jumba.overflow.base.check.Check;
import me.jumba.overflow.base.check.CheckType;
import me.jumba.overflow.base.event.Listen;
import me.jumba.overflow.base.event.impl.PacketEvent;
import me.jumba.overflow.base.user.User;
import me.jumba.overflow.util.time.TimeUtils;

/**
 * Created on 08/01/2020 Package me.jumba.sparky.checks.other.badpackets
 */
public class BadPacketsC extends Check {
    public BadPacketsC(String checkName, String type, CheckType checkType, boolean enabled) {
        super(checkName, type, checkType, enabled);
    }
    /*
        Ground Spoof Check - Detects if the player is sending an iilegal "onGround" packet
     */

    @Listen
    public void onPacket(PacketEvent e) {
        if (e.isPacketMovement()) {
            User user = e.getUser();
            if (user != null && user.getMovementData().isChunkLoaded()) {

                boolean nigger = (user.getMovementData().getTo().getY() % 1/64 == 0.0 || user.getMovementData().getFrom().getY() % 1/64 == 0.0);

                if (Overflow.getInstance().isLagging() || nigger || (System.currentTimeMillis() - user.getCombatData().getLastRespawn()) < 1000L || (System.currentTimeMillis() - user.getMiscData().getLastBlockCancel()) < 1000L || user.getMiscData().isAfkMovement()) {
                    user.getCheckData().lastBadPacketsCFallDistance = 0;
                    user.getCheckData().badPacketsCReset = false;
                    return;
                }

                if (user.getMovementData().isCollidedGround() || user.getPlayer().isFlying() || user.getPlayer().getAllowFlight() || user.getCombatData().hasBowBoosted() && !user.getCheckData().badPacketsCReset && (System.currentTimeMillis() - user.getMovementData().getLastFullBlockMoved()) > 1133L) {
                    user.getCheckData().badPacketsCReset = true;
                }

                if (user.getCheckData().badPacketsCReset) {
                    if (user.getMovementData().isOnGround() && user.getMovementData().isLastOnGround() && user.getMovementData().getGroundTicks() > 15 && user.getMovementData().getAirTicks() < 2) {
                        user.getCheckData().lastBadPacketsCFallDistance = 0;
                        user.getCheckData().badPacketsCReset = false;
                    }
                    return;
                }

                if (user.getMovementData().isExplode() || user.isWaitingForMovementVerify() || user.generalCancel() || user.getBlockData().fenceTicks > 0 || user.getBlockData().wallTicks > 0 || user.getBlockData().glassPaneTicks > 0 || user.getBlockData().halfBlockTicks > 0) {
                    user.getCheckData().lastBadPacketsCFallDistance = 0;
                    return;
                }

                boolean faked = user.isSafe() && user.totalBlocksCheck > 5 && user.getMiscData().getBoatTicks() < 1 && !user.getMiscData().isNearBoat() && TimeUtils.secondsFromLong(user.getMovementData().getLastTeleport()) > 5L && user.getMovementData().getTo().isClientGround() && !user.getMovementData().isOnGround() && !user.getMovementData().isLastOnGround();

                if (user.getCheckData().isBadPacketsCReady() && user.getMovementData().isOnGround() && user.getMovementData().isLastOnGround()) {
                    user.getCheckData().badPacketsCReady = false;
                 //   user.getPlayer().setFallDistance(user.getCheckData().lastBadPacketsCFallDistance);
                    user.getCheckData().lastBadPacketsCFallDistance = 0;
                }

                if (user.getCheckData().badPacketsCDamaged && user.getMovementData().isOnGround() && user.getMovementData().isLastOnGround()) user.getCheckData().badPacketsCDamaged = false;

                if (user.getMovementData().getTo().getY() % 0.015625 != 0.0 && (user.getMovementData().getTo().getY() - user.getMovementData().getFrom().getY()) < 0) {
                    user.getCheckData().lastBadPacketsCFallDistance = (float) (user.getCheckData().lastBadPacketsCFallDistance + Math.abs((user.getMovementData().getTo().getY() - user.getMovementData().getFrom().getY())));
                }

                if (faked && user.getCheckData().getBadpacketsCVerbose().flag(2, 1000L)) {
                    flag(user, "cg=" + user.getMovementData().getTo().isClientGround(), "sg=" + user.getMovementData().isOnGround(), "lsg=" + user.getMovementData().isLastOnGround(), "verbose=" + user.getCheckData().getBadpacketsCVerbose().getVerbose());

                    if (!user.getCheckData().badPacketsCDamaged) {
                        user.getCheckData().badPacketsCDamaged = true;
                        user.getCheckData().badPacketsCReady = true;
                    }
                }
            }
        }
    }
}
