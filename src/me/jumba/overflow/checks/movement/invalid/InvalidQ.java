package me.jumba.overflow.checks.movement.invalid;

import me.jumba.overflow.base.check.Check;
import me.jumba.overflow.base.check.CheckType;
import me.jumba.overflow.base.event.Listen;
import me.jumba.overflow.base.event.impl.PacketEvent;
import me.jumba.overflow.base.user.User;

/**
 * Created on 14/06/2020 Package me.jumba.overflow.checks.movement.invalid
 */
public class InvalidQ extends Check {
    public InvalidQ(String checkName, String type, CheckType checkType, boolean enabled) {
        super(checkName, type, checkType, enabled);
    }

    @Listen
    public void onPacket(PacketEvent e) {
        if (e.isPacketMovement()) {
            User user = e.getUser();
            if (user != null && user.isSafe()) {

                if ((user.getConnectedTick() > 100 && Math.abs(user.getBlockData().lastSlimeTick - user.getConnectedTick()) < 100)
                        || user.getBlockData().slime || user.getBlockData().slimeTicks > 0
                        || user.getMiscData().isNearBoat() || user.getMiscData().getBoatTicks() > 0) return;

                if (user.getMovementData().isChunkLoaded() && user.getMovementData().isOnGround() && user.getMovementData().isLastOnGround() && !user.getMovementData().isJumpPad() && user.getMiscData().getJumpPotionTicks() < 1) {
                    double diff = user.getMovementData().getTo().getY() - user.getMovementData().getFrom().getY();

                    if (diff >= 0.9f
                            && (System.currentTimeMillis() - user.getMovementData().getLastFullTeleport()) > 1000L
                            && (System.currentTimeMillis() - user.getMovementData().getLastTeleport()) > 1000L
                            && !user.getBlockData().slime && user.getBlockData().wallTicks < 1
                            && user.getBlockData().fenceTicks < 1
                            && user.getBlockData().halfBlockTicks < 1 && !user.generalCancel()) {
                        flag(user, "y="+diff);
                    }
                }
            }
        }
    }
}
