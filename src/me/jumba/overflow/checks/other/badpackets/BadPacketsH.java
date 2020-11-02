package me.jumba.overflow.checks.other.badpackets;


import me.jumba.overflow.Overflow;
import me.jumba.overflow.base.check.Check;
import me.jumba.overflow.base.check.CheckType;
import me.jumba.overflow.base.event.Listen;
import me.jumba.overflow.base.event.impl.PacketEvent;
import me.jumba.overflow.base.user.User;
import me.jumba.overflow.util.time.TimeUtils;

/**
 * Created on 08/01/2020 Package me.jumba.sparky.checks.other.badpackets
 */
public class BadPacketsH extends Check {
    public BadPacketsH(String checkName, String type, CheckType checkType, boolean enabled) {
        super(checkName, type, checkType, enabled);
    }

    /*
        Illegal ground state check - checks if player is on ground server side and is not client side
     */

    @Listen
    public void onPacket(PacketEvent e) {
        if (e.isPacketMovement()) {
            User user = e.getUser();
            if (user != null) {

                if (Overflow.getInstance().isLagging()) return;

                if (!user.getMovementData().isChunkLoaded() || (System.currentTimeMillis() - user.getMovementData().getLastTeleport()) < 1000L) {
                    user.getCheckData().badPacketsHVerbose.setVerbose(0);
                    user.getCheckData().badPachetsHClientGroundTicks = 0;
                    return;
                }

                if (user.getMovementData().isClientGround()) {
                    user.getCheckData().badPachetsHClientGroundTicks = 0;
                } else {
                    if (user.getCheckData().badPachetsHClientGroundTicks < 20) user.getCheckData().badPachetsHClientGroundTicks++;
                }

                boolean isOK = user.getMovementData().getGroundTicks() > 2 && user.getCheckData().badPachetsHClientGroundTicks > 15;

                if ((System.currentTimeMillis() - user.getMovementData().getLastEnderpearl()) > 1000L && user.getBlockData().halfBlockTicks < 1 && user.getBlockData().doorTicks < 1 && !user.getBlockData().slime && user.getBlockData().slimeTicks < 1 && !user.generalCancel() && TimeUtils.secondsFromLong(user.getMiscData().getLastMount()) > 3L && user.getBlockData().climbableTicks < 1 && user.getMiscData().getMountedTicks() < 1 && user.getBlockData().webTicks < 1 && (System.currentTimeMillis() - user.getCombatData().getLastRandomDamage()) > 1000L && user.getBlockData().liquidTicks < 1 && user.getMiscData().getBoatTicks() < 1 && user.getMovementData().isOnGround() && !user.getMovementData().isClientGround() && isOK) {
                    if (user.getCheckData().badPacketsHVerbose.flag(7, 500L)) {
                        flag(user, "server-ground=" + user.getMovementData().isOnGround(), "client-ground=" + user.getMovementData().isClientGround(), "verbose=" + user.getCheckData().badPacketsHVerbose.getVerbose());
                    }
                } else user.getCheckData().badPacketsHVerbose.setVerbose(0);
            }
        }
    }
}
