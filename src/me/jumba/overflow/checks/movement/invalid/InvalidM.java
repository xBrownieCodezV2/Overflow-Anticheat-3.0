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
public class InvalidM extends Check {
    public InvalidM(String checkName, String type, CheckType checkType, boolean enabled) {
        super(checkName, type, checkType, enabled);
    }

    @Listen
    public void onPacket(PacketEvent e) {
        if (e.isPacketMovement()) {
            User user = e.getUser();
            if (user != null && user.isSafe()) {

                if (Overflow.getInstance().isLagging() || user.getBlockData().slime || user.getBlockData().slimeTicks > 0 || user.getBlockData().liquidTicks > 0 || user.getBlockData().webTicks > 0 || (System.currentTimeMillis() - user.getCombatData().getLastRandomDamage()) < 1000L) {
                    user.getCheckData().invalidMVerbose.setVerbose(0);
                    user.getCheckData().invalidMThreshold = 0.0;
                    return;
                }


                if (!user.getMovementData().isClientGround() && user.getMovementData().isLastClientGround() && Math.abs(user.getMovementData().getTo().getY() - user.getMovementData().getFrom().getY()) >= 0.42f) {
                //    user.debug("ok " + user.getConnectedTick());
                    user.getCheckData().invalidMThreshold = 0.0;
                    user.getCheckData().lastInvalidMReset = System.currentTimeMillis();
                    return;
                }

                if ((System.currentTimeMillis() - user.getCheckData().lastInvalidMReset) < 1000L) {
                    user.getCheckData().invalidMThreshold = 0.0;
                    return;
                }

                boolean clientGround = user.getMovementData().isClientGround();

                double diff = Math.abs((e.getTo().getY() - e.getFrom().getY()));

                int needed = 1;

                if ((System.currentTimeMillis() - user.getMiscData().getLastBlockPlace()) < 1000L) needed += 5;

                if (diff > 0.0 && !clientGround && user.getMovementData().isOnGround() && user.getCheckData().invalidMVerbose.flag(needed, 999L)) {
                    if ((user.getMovementData().getTo().getY() % 1/64) > 0.0) {
                        user.getCheckData().invalidMThreshold+=0.50f;
                    }

                    if (user.getCheckData().invalidMThreshold > 1.60) {
                        flagDev(user, "diff="+diff, "verbose="+user.getCheckData().invalidMVerbose.getVerbose(), "ground="+user.getMovementData().isOnGround(), "clientGround="+user.getMovementData().isClientGround());
                    }
                } else if (clientGround) {
                    user.getCheckData().invalidMVerbose.setVerbose(0);
                    user.getCheckData().invalidMThreshold = 0.0;
                }
            }
        }
    }
}
