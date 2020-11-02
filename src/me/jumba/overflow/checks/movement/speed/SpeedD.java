package me.jumba.overflow.checks.movement.speed;


import me.jumba.overflow.Overflow;
import me.jumba.overflow.base.check.Check;
import me.jumba.overflow.base.check.CheckType;
import me.jumba.overflow.base.event.Listen;
import me.jumba.overflow.base.event.impl.PacketEvent;
import me.jumba.overflow.base.user.User;
import me.jumba.overflow.util.time.TimeUtils;

/**
 * Created on 07/01/2020 Package me.jumba.sparky.checks.movement.speed
 */
public class SpeedD extends Check {
    public SpeedD(String checkName, String type, CheckType checkType, boolean enabled) {
        super(checkName, type, checkType, enabled);
    }
    /*
        Limit check - checks if the player is going faster than legit
     */

    @Listen
    public void onPacket(PacketEvent e) {
        if (e.isPacketMovement()) {
            User user = e.getUser();
            if (user != null) {

                if ((user.getMovementData().getLastServerPostion() - user.getConnectedTick()) < 100) return;

                if (user.getBlockData().slimeTicks > 0 || (System.currentTimeMillis() - user.getCombatData().getLastRespawn()) < 1000L || Overflow.getInstance().isLagging() || ((System.currentTimeMillis() - user.getCombatData().getLastRespawn()) < 1000L) || (System.currentTimeMillis() - user.getMovementData().getLastServerPostion()) < 1000L || user.getMovementData().getWalkSpeed() > 0.2f || user.getMovementData().isDidUnknownTeleport() || user.getBlockData().anvilTicks > 0 || (System.currentTimeMillis() - user.getMovementData().getLastTeleport()) < 1000L || (System.currentTimeMillis() - user.getCheckData().lastUnknownValidTeleport) < 1000L || user.getMiscData().getSpeedPotionEffectLevel() > 2.00 || (System.currentTimeMillis() - user.getCombatData().getLastRespawn()) < 1000L || user.getCombatData().hasBowBoosted() || TimeUtils.secondsFromLong(user.getMiscData().getLastMount()) < 2L || (System.currentTimeMillis() - user.getMovementData().getLastTeleport()) < 1000L || (System.currentTimeMillis() - user.getCombatData().getLastEntityDamageAttack()) < 1000L || user.getMiscData().getBoatTicks() > 0 || (System.currentTimeMillis() - user.getMiscData().getLastMoutUpdate()) < 1099L || user.getMiscData().getMountedTicks() > 0 || user.generalCancel() || user.getMiscData().isSwitchedGamemodes()) {
                    return;
                }

                int max = 1;

                if (user.getMiscData().getSpeedPotionTicks() > 0) {
                    max += user.getMiscData().getSpeedPotionEffectLevel() * 0.2;
                }

                if (!user.getMovementData().isJumpPad() && TimeUtils.secondsFromLong(user.getMovementData().getLastJunpPadUpdate()) > 3L && TimeUtils.secondsFromLong(user.getTimestamp()) > 5L && user.getBlockData().iceTicks < 1 && (System.currentTimeMillis() - user.getBlockData().lastIce > 1000L) && user.getBlockData().blockAboveTicks < 1 && user.getMovementData().getMovementSpeed() >= max) {
                    if (user.getMovementData().getMovementSpeed() < 100) {
                        flag(user, "speed=" + user.getMovementData().getMovementSpeed());
                    }
                }
            }
        }
    }
}
