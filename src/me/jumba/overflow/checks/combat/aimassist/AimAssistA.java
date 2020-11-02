package me.jumba.overflow.checks.combat.aimassist;

import me.jumba.overflow.base.check.Check;
import me.jumba.overflow.base.check.CheckType;
import me.jumba.overflow.base.event.Listen;
import me.jumba.overflow.base.event.impl.PacketEvent;
import me.jumba.overflow.base.user.User;
import me.jumba.overflow.util.math.MathUtil;
import me.jumba.overflow.util.time.TimeUtils;

/**
 * Created on 18/05/2020 Package me.jumba.sparky.checks.combat.aimassist
 */
public class AimAssistA extends Check {
    public AimAssistA(String checkName, String type, CheckType checkType, boolean enabled) {
        super(checkName, type, checkType, enabled);
        setExperimental(true);
    }

    //Old check for vape aimassist

    @Listen
    public void onPacket(PacketEvent event) {
        if (event.isPacketMovement()) {
            User user = event.getUser();

            if (user != null) {

                if (user.getLagProcessor().isLagging() || Math.abs(user.getConnectedTick() - user.getLagProcessor().getLastLagTick()) < 100 || (System.currentTimeMillis() - user.getCombatData().getLastUseEntityPacket()) > 1000L) {
                    user.aimAssistsACount = 0;
                    user.getCheckData().lastAimAssistGCD = 0;
                    return;
                }

                if (user.optifineSmoothing > 1 || user.optifineSmoothSens >= 3 || user.getCheckData().aimAssistAWork) {
                    user.aimAssistsACount = 0;
                    user.getCheckData().lastAimAssistGCD = 0;
                    return;
                }

                if (Math.abs(user.getMovementData().getTo().getPitch() - user.getMovementData().getFrom().getPitch()) == 0.0) {
                    if (user.getCheckData().kys > 0) user.getCheckData().kys--;
                } else {
                    if (user.getCheckData().kys < 30) user.getCheckData().kys++;
                }

                if (user.getCheckData().kys < 7) return;

                user.getCheckData().lastFixerIDK = user.getPlayer().getLocation().getPitch();
                if ((user.killauraAYawReset > 2 ? (user.killauraAPitchReset * user.killauraAYawReset) : user.killauraAYawReset) > 3) {
                    user.lastAimAssistACE = System.currentTimeMillis();
                }

                if (TimeUtils.elapsed(user.lastAimAssistACE) <= 100L) {
                    if (user.aimAssistsACount > 0) user.aimAssistsACount--;
                }

                if (TimeUtils.elapsed(user.getMovementData().getLastFullBlockMoved()) > 500L) {
                    if (user.aimAssistsACount > 0) user.aimAssistsACount--;
                } else if (user.killauraAYawReset >= 5) {
                    user.aimAssistsACount = 0;
                }

                if (TimeUtils.elapsed(user.getCombatData().getLastUseEntityPacket()) <= 999L) {
                    float pitch = MathUtil.getDistanceBetweenAngles(user.getMovementData().getTo().getPitch(), user.getCheckData().lastAimAssistAPitch);
                    long p1 = (long) (pitch * Math.pow(2, 24)), p2 = (long) (user.getCheckData().lastAimAssistAPitch * Math.pow(2, 24));

                    if (MathUtil.gcd(p1, p2) == user.getCheckData().lastAimAssistGCD) {
                        user.aimAssistsACount += 2;
                    } else {
                        if (user.aimAssistsACount > 0) user.aimAssistsACount -= 2;
                    }

                    if (Math.abs(user.getMovementData().getTo().getPitch() - user.getMovementData().getFrom().getPitch()) <= 0.0) {
                        if (user.killauraAYawReset > 1) {
                            user.aimAssistsACount = 0;
                            return;
                        }
                        if (user.getMovementData().getDeltaXZ() < 0.20) {
                            if (user.aimAssistsACount > 0) user.aimAssistsACount--;
                        }
                        if (user.aimAssistsACount >= 10 && user.getCheckData().aimAssistVerbose.flag(1, 999L)) {
                            flag(user);
                        }

                        user.getCheckData().lastAimAssistGCD = MathUtil.gcd(p1, p2);
                        user.getCheckData().lastAimAssistAYaw = user.getMovementData().getTo().getYaw();
                        user.getCheckData().lastAimAssistAPitch = user.getMovementData().getTo().getPitch();
                    }
                }
            }
        }
    }
}
