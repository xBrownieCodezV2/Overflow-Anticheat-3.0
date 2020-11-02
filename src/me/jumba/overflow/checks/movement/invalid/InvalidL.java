package me.jumba.overflow.checks.movement.invalid;

import me.jumba.overflow.Overflow;
import me.jumba.overflow.base.check.Check;
import me.jumba.overflow.base.check.CheckType;
import me.jumba.overflow.base.event.Listen;
import me.jumba.overflow.base.event.impl.PacketEvent;
import me.jumba.overflow.base.user.User;

/**
 * Created on 14/04/2020 Package me.jumba.sparky.checks.movement.invalid
 */
public class InvalidL extends Check {
    public InvalidL(String checkName, String type, CheckType checkType, boolean enabled) {
        super(checkName, type, checkType, enabled);
    }

    @Listen
    public void onPacket(PacketEvent e) {
        if (e.isPacketMovement()) {
            User user = e.getUser();
            if (user != null && user.isSafe()) {
                boolean clientGround = user.getMovementData().isClientGround();

                if (Overflow.getInstance().isLagging() || (System.currentTimeMillis() - user.getMovementData().getLastTeleport()) < 1000L || user.getBlockData().webTicks > 0) {
                    user.getCheckData().invalidLThreshold = 0.0f;
                    return;
                }

                if (!clientGround && user.getBlockData().liquidTicks < 1) {
                    double postion = (user.getMovementData().getTo().getY() % 1/64);

                    if (postion == user.getCheckData().lastInvalidLGroundPostionY) {
                        if (user.getCheckData().invalidLThreshold > 1.20) {
               //             flagDev(user, "(2)", "thershold="+user.getCheckData().invalidLThreshold, "postion="+postion, "lastPostion="+user.getCheckData().lastInvalidLGroundPostionY, "postionDifference="+Math.abs(postion - user.getCheckData().lastInvalidLGroundPostionY));
                        }
                        user.getCheckData().invalidLThreshold+= 0.50f;
                    } else {
                        user.getCheckData().invalidLThreshold = 0.0f;
                    }

                    user.getCheckData().lastInvalidLGroundPostionY = postion;
                } else {
                    if (user.getMovementData().isOnGround() || user.getMovementData().isClientGround()) {
                        user.getCheckData().invalidLThreshold = 0.0f;
                    }
                }

                if (!clientGround && isGround(user.getMovementData().getTo().getY()) && user.getBlockData().halfBlockTicks < 1 && user.getBlockData().blockAboveTicks < 1) {
             //     flagDev(user, "(1)", "clientGround="+user.getMovementData().isClientGround(), "lastClientGround="+user.getMovementData().isLastClientGround(), "serverGround="+user.getMovementData().isOnGround(), "lastServerGround="+user.getMovementData().isLastOnGround(), "1/64="+(user.getMovementData().getTo().getY() % 1/64), "2/64="+(user.getMovementData().getTo().getY() % 2/64));
                }
            }
        }
    }

    private boolean isGround(double positonY) {
        return (positonY % 0.015625 == 0.0);
    }
}
