package me.jumba.overflow.checks.other.badpackets;


import me.jumba.overflow.Overflow;
import me.jumba.overflow.base.check.Check;
import me.jumba.overflow.base.check.CheckType;
import me.jumba.overflow.base.event.Listen;
import me.jumba.overflow.base.event.impl.PacketEvent;
import me.jumba.overflow.base.user.User;
import me.jumba.overflow.util.time.TimeUtils;

/**
 * Created on 23/03/2020 Package me.jumba.sparky.checks.other.badpackets
 */
public class BadPacketsI extends Check {

    public BadPacketsI(String checkName, String type, CheckType checkType, boolean enabled) {
        super(checkName, type, checkType, enabled);
    }

    @Listen
    public void onPacket(PacketEvent e) {
        if (e.isPacketMovement()) {
            User user = e.getUser();
            if (user != null) {

                if (Overflow.getInstance().isLagging()) return;

                if (!user.getMovementData().isChunkLoaded() || (System.currentTimeMillis() - user.getMovementData().getLastFullTeleport()) < 1000L || user.getBlockData().liquidTicks > 0 || (System.currentTimeMillis() - user.getMiscData().getLastBlockBreakCancel() < 1000L) || user.getBlockData().trapDoorTicks > 0 || user.getBlockData().blockAboveTicks > 0 || user.getBlockData().webTicks > 0 || user.getBlockData().halfBlockTicks > 0 || user.getBlockData().slime || user.getBlockData().slabTicks > 0 || user.getMiscData().getMountedTicks() > 0 || user.getMiscData().getBoatTicks() > 0 || (System.currentTimeMillis() - user.getCombatData().getLastEntityDamageAttack()) < 1000L || TimeUtils.secondsFromLong(user.getBlockData().lastBlockAbove) < 5L || user.isDead() || user.getBlockData().blockAboveTicks > 0 || user.getBlockData().soulSandTicks > 0 || (System.currentTimeMillis() - user.getMovementData().getLastUnknownTeleport()) < 1000L || user.getBlockData().slime || user.getBlockData().slimeTicks > 0 || (System.currentTimeMillis() - user.getCheckData().lastUnknownValidTeleport) < 1000L || user.generalCancel() || user.getBlockData().blockAboveTicks > 0 || !user.isHasVerify() || (System.currentTimeMillis() - user.getMovementData().getLastTeleport()) < 1000L || user.getBlockData().stairTicks > 0 || user.getBlockData().slabTicks > 0) {
                    user.getCheckData().badPacketsIVerbose.setVerbose(0);
                    return;
                }

                if ((user.getMovementData().getTo().getY() % 0.015625) == 0.0 && !user.getMovementData().isClientGround() && user.getCheckData().badPacketsIVerbose.flag(2, 999L)) {
                  //  flag(user, "y="+user.getMovementData().getTo().getY(), "cg="+user.getMovementData().isClientGround(), "sg="+user.getMovementData().isOnGround());
                }
            }
        }
    }
}
