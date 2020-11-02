package me.jumba.overflow.checks.movement.invalid;

import me.jumba.overflow.base.check.Check;
import me.jumba.overflow.base.check.CheckType;
import me.jumba.overflow.base.event.Listen;
import me.jumba.overflow.base.event.impl.PacketEvent;
import me.jumba.overflow.base.user.User;
import me.jumba.overflow.hook.HookManager;
import me.jumba.overflow.util.math.MathUtil;
import me.jumba.overflow.util.time.TimeUtils;

/**
 * Created on 14/06/2020 Package me.jumba.overflow.checks.movement.invalid
 */
public class InvalidR extends Check {
    public InvalidR(String checkName, String type, CheckType checkType, boolean enabled) {
        super(checkName, type, checkType, enabled);
    }

    @Listen
    public void onPacket(PacketEvent e) {
        if (e.isPacketMovement()) {
            User user = e.getUser();
            if (user != null) {


                if (!user.getCheckData().invalidPIsReady && TimeUtils.secondsFromLong(user.getTimestamp()) > 5L) {
                    user.getCheckData().invalidPIsReady = true;
                }

                if (user.getBlockData().slimeTicks > 0 || !user.getMovementData().isChunkLoaded() || (user.getMovementData().getLastServerPostion() - user.getConnectedTick()) < 100) {
                    user.getCheckData().invalidPLastGroundPostiomY = user.getMovementData().getTo().getY();
                    return;
                }

                if ((System.currentTimeMillis() - user.getMiscData().getLastMount()) < 1000L || user.getMiscData().getMountedTicks() > 0 || (System.currentTimeMillis() - user.getMovementData().getLastTeleport()) < 1000L || user.getBlockData().doorTicks > 0 || user.getBlockData().trapDoorTicks > 0 || user.getMiscData().getMountedTicks() > 0 || (System.currentTimeMillis() - user.getMiscData().getLastMount() < 1000L) || (System.currentTimeMillis() - user.getCombatData().getLastRespawn() < 1000L) || (System.currentTimeMillis() - user.getMovementData().getLastTeleport()) < 1000L || user.getMiscData().getJumpPotionTicks() > 0 || user.getBlockData().trapDoorTicks > 0 || user.getBlockData().slime || user.getBlockData().slimeTicks > 0 || (System.currentTimeMillis() - user.getCombatData().getLastRespawn()) < 1000L || user.getMovementData().isDidUnknownTeleport() || (System.currentTimeMillis() - user.getMovementData().getLastTeleport()) < 1000L || (System.currentTimeMillis() - user.getCombatData().getLastRandomDamage()) < 1000L || user.generalCancel() || (System.currentTimeMillis() - user.getCombatData().getLastBowDamage()) < 1000L || user.getBlockData().slime || user.getBlockData().slimeTicks > 0 || user.getBlockData().liquidTicks > 0 || user.getBlockData().climbableTicks > 0 || user.getMovementData().isJumpPad() || TimeUtils.secondsFromLong(user.getMovementData().getLastJunpPadUpdate()) < 5L) {
                    user.getCheckData().invalidPLastGroundPostiomY = user.getMovementData().getTo().getY();
                    return;
                }

                if (user.getMovementData().isClientGround()) {
                    user.getCheckData().invalidPLastGroundPostiomY = user.getMovementData().getTo().getY();

                } else {
                    double difference = (user.getMovementData().getTo().getY() - user.getCheckData().invalidPLastGroundPostiomY);

                    boolean isAbove1_8 = user.getCurrentClientVersion() != HookManager.Helper.Versions.V1_7 && user.getCurrentClientVersion() != HookManager.Helper.Versions.V1_8;

                    double predicted = (isAbove1_8 ? 1.50 : 1.42f);


                    if (user.getMiscData().getJumpPotionTicks() > 0) {
                        predicted = (predicted + user.getMiscData().getJumpPotionMultiplyer() * 0.1F);
                        predicted += 1.42f;
                    }

                    if ((System.currentTimeMillis() - user.getMovementData().getLastFallDamage()) < 1000L) {
                        predicted += 1.84f;
                    }

                    if ((System.currentTimeMillis() - user.getCombatData().getLastEntityDamage()) < 1000L) {
                        predicted += 1.42f;
                    }

                    if (difference > predicted && user.isSafe() && user.getCheckData().invalidPIsReady) {
                        flag(user, "difference="+ MathUtil.trim(4, difference), "predicted="+predicted);
                    }
                }
            }
        }
    }
}
