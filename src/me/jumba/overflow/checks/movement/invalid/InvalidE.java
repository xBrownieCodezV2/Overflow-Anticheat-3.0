package me.jumba.overflow.checks.movement.invalid;

import me.jumba.overflow.Overflow;
import me.jumba.overflow.base.check.Check;
import me.jumba.overflow.base.check.CheckType;
import me.jumba.overflow.base.event.Listen;
import me.jumba.overflow.base.event.impl.PacketEvent;
import me.jumba.overflow.base.user.User;
import me.jumba.overflow.util.math.MathUtil;

/**
 * Created on 10/03/2020 Package me.jumba.sparky.checks.movement.invalid
 */
public class InvalidE extends Check {
    public InvalidE(String checkName, String type, CheckType checkType, boolean enabled) {
        super(checkName, type, checkType, enabled);
    }

    /*
        Ladder speed check - checks if the player is climbing a ladder faster than legit
     */

    private float maxLadderSpeed = 0.1f;

    @Listen
    public void onPacket(PacketEvent e) {
        if (e.isPacketMovement()) {
            User user = e.getUser();
            if (user != null) {

                if (Overflow.getInstance().isLagging() || user.getBlockData().liquidTicks < 0 || !user.generalCancel() && user.getBlockData().climable && user.getMiscData().getJumpPotionTicks() < 1) {

                    double yDifference = (user.getMovementData().getTo().getY() - user.getMovementData().getFrom().getY());

                    if (MathUtil.trim(1, yDifference) > maxLadderSpeed) {

                        if (user.getCheckData().invalidEValidTicks > 6) {
                            flag(user, "yDifference="+MathUtil.trim(4, yDifference), "ticks="+user.getCheckData().invalidEValidTicks);
                        }

                        if (user.getCheckData().invalidEValidTicks < 20) user.getCheckData().invalidEValidTicks++;
                    } else {
                        user.getCheckData().invalidEValidTicks -= (user.getCheckData().invalidEValidTicks > 0 ? Math.min(user.getCheckData().invalidEValidTicks - 1, 1) : 0);
                    }
                } else {
                    user.getCheckData().invalidEValidTicks = 0;
                }
            }
        }
    }
}
