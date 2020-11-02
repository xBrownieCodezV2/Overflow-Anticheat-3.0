package me.jumba.overflow.checks.movement.invalid;

import me.jumba.overflow.Overflow;
import me.jumba.overflow.base.check.Check;
import me.jumba.overflow.base.check.CheckType;
import me.jumba.overflow.base.event.Listen;
import me.jumba.overflow.base.event.impl.PacketEvent;
import me.jumba.overflow.base.user.User;
import me.jumba.overflow.util.math.MathUtil;
import me.jumba.overflow.util.time.TimeUtils;
import org.bukkit.Statistic;

/**
 * Created on 10/03/2020 Package me.jumba.sparky.checks.movement.invalid
 */
public class InvalidF extends Check {
    public InvalidF(String checkName, String type, CheckType checkType, boolean enabled) {
        super(checkName, type, checkType, enabled);
        setExperimental(true);
    }

    /*
        Jump stat check - checks if the client is jumping legitmently when a jump stat is sent from the client
     */

    private double expectedJumpValue = 0.41999998688697815F;

    @Listen
    public void onPacket(PacketEvent e) {
        if (e.isPacketMovement()) {
            User user = e.getUser();
            if (user != null) {

                int jumpStat = user.getPlayer().getStatistic(Statistic.JUMP);

                if (Overflow.getInstance().isLagging() || (System.currentTimeMillis() - user.getCombatData().getLastEntityDamage()) < 1000L || user.getBlockData().blockAboveTicks > 0 || user.getMovementData().isJumpPad() || TimeUtils.secondsFromLong(user.getMovementData().getLastJunpPadUpdate()) < 5L || user.generalCancel() || user.getBlockData().stairTicks > 0 || user.getBlockData().slabTicks > 0 || user.getBlockData().liquidTicks > 0 || user.getBlockData().halfBlockTicks > 0 || user.getBlockData().climbableTicks > 0) {
                    user.getCheckData().invalidFThreshold = -(user.getCheckData().invalidFThreshold > 0.0 ? Math.min(user.getCheckData().invalidFThreshold - 1, 0.0) : 0.0);
                    return;
                }

                double diff = (user.getMovementData().getTo().getY() - user.getMovementData().getFrom().getY());
                double predicted = getJumpPrediction(user);

                if (jumpStat != user.getCheckData().lastInvalidFJumpStat) {

                    if (diff > 0.0 && diff != predicted) {
                        if (user.getCheckData().invalidFThreshold < 20.0) user.getCheckData().invalidFThreshold+=1.20f;
                    } else {
                        user.getCheckData().invalidFThreshold = -(user.getCheckData().invalidFThreshold > 0.0 ? Math.min(user.getCheckData().invalidFThreshold - 1, 0.0) : 0.0);
                    }
                }

                double trimmedThreshold = MathUtil.trim(2, user.getCheckData().invalidFThreshold);

                if (trimmedThreshold > 2.6 && diff > 0.0 && predicted != 0.0) flag(user, "threshold="+trimmedThreshold, "diff="+MathUtil.trim(4, diff), "predicted="+MathUtil.trim(4, predicted));

                user.getCheckData().lastInvalidFJumpStat = jumpStat;
            }
        }
    }

    private double getJumpPrediction(User user) {

        double predictedJump = this.expectedJumpValue;

        if (user.getMiscData().getJumpPotionTicks() > 0 && user.getMiscData().isHasJumpPotion()) {
            predictedJump = (this.expectedJumpValue + user.getMiscData().getJumpPotionMultiplyer() * 0.1F);
        }

        return predictedJump;
    }
}
