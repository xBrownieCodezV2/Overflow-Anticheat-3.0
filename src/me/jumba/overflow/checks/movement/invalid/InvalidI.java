package me.jumba.overflow.checks.movement.invalid;

import me.jumba.overflow.Overflow;
import me.jumba.overflow.base.check.Check;
import me.jumba.overflow.base.check.CheckType;
import me.jumba.overflow.base.event.Listen;
import me.jumba.overflow.base.event.impl.PacketEvent;
import me.jumba.overflow.base.user.User;
import me.jumba.overflow.util.math.MathUtil;
import me.jumba.overflow.util.time.TimeUtils;

/**
 * Created on 19/03/2020 Package me.jumba.sparky.checks.movement.invalid
 */
public class InvalidI extends Check {
    public InvalidI(String checkName, String type, CheckType checkType, boolean enabled) {
        super(checkName, type, checkType, enabled);
    }

    /*
        Gravity Check - Checks if the player jumps a legitment height
     */

    @Listen
    public void onPacket(PacketEvent e) {
        if (e.isPacketMovement()) {
            User user = e.getUser();
            if (user != null) {

                if ((user.getMovementData().getLastServerPostion() - user.getConnectedTick()) < 100) return;

                if (Overflow.getInstance().isLagging() || (System.currentTimeMillis() - user.getMovementData().getLastServerPostion()) < 1000L || user.getBlockData().blockAboveTicks > 0 || (System.currentTimeMillis() - user.getMiscData().getLastBlockCancel()) < 1000L || (System.currentTimeMillis() - user.getMovementData().getLastFallDamage()) < 1000L || user.getBlockData().webTicks > 0 || user.getMiscData().getMountedTicks() > 0 || user.getMiscData().getBoatTicks() > 0 || user.getBlockData().anvilTicks > 0 || (System.currentTimeMillis() - user.getMovementData().getLastFullBlockMoved()) > 155L || (System.currentTimeMillis() - user.getMovementData().getLastBlockJump()) < 1000L || user.generalCancel() || user.getMovementData().isCollidesHorizontally() || user.getBlockData().fenceTicks > 0 || user.getBlockData().wallTicks > 0 || user.getBlockData().climbableTicks > 0 || user.getBlockData().slime || user.getBlockData().slimeTicks > 0 || (System.currentTimeMillis() - user.getBlockData().lastSline) < 1000L || user.getMovementData().isJumpPad() || TimeUtils.secondsFromLong(user.getMovementData().getLastJunpPadUpdate()) < 4L || user.getBlockData().stairTicks > 0 || user.getMovementData().isExplode() || (System.currentTimeMillis() - user.getCombatData().getLastEntityDamageAttack()) < 1000L || (System.currentTimeMillis() - user.getCombatData().getLastBowDamage()) < 1000L || user.getBlockData().blockAboveTicks > 0 || user.getBlockData().halfBlockTicks > 0 || user.getBlockData().slimeTicks > 0) {
                    return;
                }

                double max = 0.41999998688697815F;


                if (user.getMiscData().getJumpPotionTicks() > 0 && user.getMiscData().isHasJumpPotion()) {
                    max = (max + user.getMiscData().getJumpPotionMultiplyer() * 0.1F);
                }

                if (!user.getMovementData().isClientGround() && user.getMovementData().isLastClientGround()) {

                    double motionY = (user.getMovementData().getTo().getY() - user.getMovementData().getFrom().getY());
                    if ((System.currentTimeMillis() - user.getMovementData().getLastBlockJump()) < 1000L && (motionY <= 0.404445 || motionY > 0.404444)) return;

                    double motionYTrim = MathUtil.trim(8, motionY);

                    if (user.getMovementData().isCollidesVertically() && motionYTrim == -0.0784 && (user.getMovementData().getTo().getY() < user.getMovementData().getFrom().getY())) return;

                    if (Math.abs(motionY) > 0.0 && ((motionY > max || motionY < max))) {
                        flag(user, "diff=" + motionYTrim, "max=" + MathUtil.trim(8, max));
                    }
                }
            }
        }
    }
}
