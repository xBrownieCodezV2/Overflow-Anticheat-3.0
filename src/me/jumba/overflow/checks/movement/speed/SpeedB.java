package me.jumba.overflow.checks.movement.speed;

import me.jumba.overflow.Overflow;
import me.jumba.overflow.base.check.Check;
import me.jumba.overflow.base.check.CheckType;
import me.jumba.overflow.base.event.Listen;
import me.jumba.overflow.base.event.impl.PacketEvent;
import me.jumba.overflow.base.user.User;
import me.jumba.overflow.util.block.BlockUtil;
import me.jumba.overflow.util.location.CustomLocation;
import me.jumba.overflow.util.math.MathUtil;
import me.jumba.overflow.util.time.TimeUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * Created on 07/01/2020 Package me.jumba.sparky.checks.movement.speed
 */
public class SpeedB extends Check {
    public SpeedB(String checkName, String type, CheckType checkType, boolean enabled) {
        super(checkName, type, checkType, enabled);
    }

    /*
        Friction check - checks if the players friction is not legit
     */

    @Listen
    public void onPacket(PacketEvent e) {
        if (e.isPacketMovement()) {
            User user = e.getUser();
            if (user != null) {

                if (Overflow.getInstance().isLagging() || (System.currentTimeMillis() - user.getCombatData().getLastRespawn()) < 1000L || user.getPlayer().isFlying() || user.getPlayer().getAllowFlight() || TimeUtils.secondsFromLong(user.getMovementData().getLastJunpPadUpdate()) < 3L || user.getMiscData().getBoatTicks() > 0 || user.generalCancel() || TimeUtils.secondsFromLong(user.getMovementData().getLastTeleport()) < 3L || user.getMiscData().getMountedTicks() > 0 || user.getMiscData().isSwitchedGamemodes() || user.getBlockData().stairTicks > 0 || user.getBlockData().slabTicks > 0) {
                    user.getCheckData().speedBVerbose = 0;
                    return;
                }

                CustomLocation from = user.getMovementData().getTo(), to = user.getMovementData().getFrom();
                double distX = to.getX() - from.getX();
                double distZ = to.getZ() - from.getZ();
                double dist = (distX * distX) + (distZ * distZ);

                double lastDist = user.getCheckData().lastSpeedBDistance;
                user.getCheckData().lastSpeedBDistance = dist;
                boolean onGround = user.getMovementData().isOnGround(), lastOnGround = user.getCheckData().speedBLastGround;
                double friction = 0.91F;
                double shiftDist = lastDist * friction;
                double equalness = dist - shiftDist;
                double fix = equalness * 138;

                Block block = BlockUtil.getBlock(user.getMovementData().getTo().toLocation(user.getPlayer().getWorld()).clone().add(0, -0.1, 0));
                boolean ground = (block != null && block.getType() != Material.AIR && block.getType().isSolid());

                if (fix >= 1.04 && !onGround && !lastOnGround && e.getTo().getY() != e.getFrom().getY() && !ground && !user.getCheckData().speedBLastGroundLocation) {
                    if (user.getCheckData().speedBVerbose++ > 1) {
                        flag(user, "verbose=" + user.getCheckData().speedBVerbose, "friction="+ MathUtil.preciseRound(fix, 2), "ground="+onGround, "lastGround="+lastOnGround, "dist="+MathUtil.preciseRound(lastDist, 2));
                    }
                } else {
                    user.getCheckData().speedBVerbose -= (user.getCheckData().speedBVerbose > 0 ? 1 : 0);
                }
                user.getCheckData().speedBLastGroundLocation = ground;
                user.getCheckData().speedBLastGround = onGround;
            }
        }
    }
}
