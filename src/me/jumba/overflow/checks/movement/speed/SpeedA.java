package me.jumba.overflow.checks.movement.speed;

import me.jumba.overflow.Overflow;
import me.jumba.overflow.base.check.Check;
import me.jumba.overflow.base.check.CheckType;
import me.jumba.overflow.base.event.Listen;
import me.jumba.overflow.base.event.impl.PacketEvent;
import me.jumba.overflow.base.user.User;
import me.jumba.overflow.util.math.MathUtil;
import me.jumba.overflow.util.time.TimeUtils;

/**
 * Created on 07/01/2020 Package me.jumba.sparky.checks.movement.speed
 */
public class SpeedA extends Check {
    public SpeedA(String checkName, String type, CheckType checkType, boolean enabled) {
        super(checkName, type, checkType, enabled);
    }

    /*
        Limit check - checks if the player goes over a certain amount of speed
     */

    @Listen
    public void onPacket(PacketEvent e) {
        if (e.isPacketMovement()) {
            User user = e.getUser();
            if (user != null) {

                if ((user.getConnectedTick() - user.getBlockData().lastSlimeTick) < 20 || user.getBlockData().slimeTicks > 0 || Overflow.getInstance().isLagging() || user.getMovementData().getWalkSpeed() > 0.2f || user.getMiscData().isSwitchedGamemodes() || user.getMovementData().isExplode() || user.getPlayer().isFlying() || user.getPlayer().getAllowFlight() || user.getCombatData().hasBowBoosted() || user.getMovementData().isJumpPad() || TimeUtils.secondsFromLong(user.getMovementData().getLastJunpPadUpdate()) < 3L || (System.currentTimeMillis() - user.getCombatData().getLastEntityDamageAttack()) < 1000L || user.generalCancel() || user.getBlockData().slimeTicks > 0 || user.getMiscData().isSwitchedGamemodes() || user.getBlockData().stairTicks > 0 || user.getBlockData().slabTicks > 0) {
                    user.getCheckData().speedAVerbose = 0;
                    return;
                }

                float threshold = (float) (user.getMovementData().getAirTicks() > 0 ? user.getMovementData().getAirTicks() < 0 ? 0.4163 * Math.pow(0.984, user.getMovementData().getAirTicks()) : 0.4163 * Math.pow(0.984, 9) : user.getMovementData().getGroundTicks() > 24 ? 0.291 : 0.375);

                if (user.getBlockData().slabTicks > 0 || user.getBlockData().stairTicks > 0) {
                    threshold += 0.3;
                }

                if (user.getBlockData().blockAboveTicks > 0 && user.getBlockData().iceTicks < 1) {
                    threshold += 0.4;
                }

                if ((System.currentTimeMillis() - user.getCombatData().getLastEntityDamage()) < 1000L) {
                    threshold += (System.currentTimeMillis() - user.getCombatData().getLastEntityDamage() < 640L ? 0.15f : 0.8f);
                }

                if (user.getBlockData().iceTicks > 0 && user.getBlockData().blockAboveTicks > 0) {
                    threshold += 1.1;
                }

                if ((System.currentTimeMillis() - user.getBlockData().lastIce) < 1000L || (System.currentTimeMillis() - user.getMovementData().getLastBlockJump()) < 1000L) {
                    threshold += 0.4;
                }

                threshold += user.getMiscData().getSpeedPotionEffectLevel() * 0.2;

                if (!user.getMiscData().isHasSpeedPotion() && user.getMiscData().getSpeedPotionTicks() > 0) {
                    user.getCheckData().speedAVerbose = 0;
                    return;
                }

               /* boolean forceFlag = false;

                if (user.getCheckData().speedAVerbose > 0 && user.getCheckData().lastSpeedAVerbose > 0) {
                    if ((System.currentTimeMillis() - user.getCheckData().lastSpeedASet) < 500L) {
                        if (user.getCheckData().speedAVerbose2++ > 4) {
                            forceFlag = true;
                        }
                    } else {
                        user.getCheckData().speedAVerbose2 -= (user.getCheckData().speedAVerbose2 > 0 ? Math.min(user.getCheckData().speedAVerbose2 - 1, 1) : 0);
                    }
                    user.getCheckData().lastSpeedASet = System.currentTimeMillis();
                }*/


                if (user.getMovementData().getMovementSpeed() > threshold /*|| forceFlag*/) {
                    if (user.getCheckData().speedAVerbose++ > 1) {
                        flag(user, "verbose=" + user.getCheckData().speedAVerbose, "speed="+MathUtil.preciseRound(user.getMovementData().getMovementSpeed(), 2), "threshold=" + MathUtil.preciseRound(threshold, 2));
                        user.getCheckData().speedAVerbose = 0;
                    }
                } else {
                    user.getCheckData().speedAVerbose -= (user.getCheckData().speedAVerbose > 0 ? 1 : 0);
                }

                user.getCheckData().lastSpeedAVerbose = user.getCheckData().speedAVerbose;
            }
        }
    }
}
