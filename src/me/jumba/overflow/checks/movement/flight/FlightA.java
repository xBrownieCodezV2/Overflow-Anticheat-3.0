package me.jumba.overflow.checks.movement.flight;

import me.jumba.overflow.Overflow;
import me.jumba.overflow.base.check.Check;
import me.jumba.overflow.base.check.CheckType;
import me.jumba.overflow.base.event.Listen;
import me.jumba.overflow.base.event.impl.PacketEvent;
import me.jumba.overflow.base.user.User;
import me.jumba.overflow.util.box.ReflectionUtil;
import me.jumba.overflow.util.time.TimeUtils;

/**
 * Created on 07/01/2020 Package me.jumba.sparky.checks.movement.flight
 */
public class FlightA extends Check {
    public FlightA(String checkName, String type, CheckType checkType, boolean enabled) {
        super(checkName, type, checkType, enabled);
    }
    /*
        Checks distance between last ground location
     */

    private float expectFloatValue = 0.0f;

    private float maxExpectedFallValue = 0.3f;

    @Listen
    public void onPacket(PacketEvent e) {
        if (e.isPacketMovement()) {
            User user = e.getUser();
            if (user != null) {



                if (Overflow.getInstance().isLagging() || (ReflectionUtil.isHypreionSpigot && (System.currentTimeMillis() - user.getMovementData().getLastFallDamage()) < 250L) || user.getMiscData().isAfkMovement() || user.getBlockData().webTicks > 0 || user.getBlockData().liquidTicks > 0 || user.getMovementData().isExplode() || user.getPlayer().isFlying() || user.getPlayer().getAllowFlight() || (System.currentTimeMillis() - user.getMiscData().getLastBlockCancel()) < 1000L || user.isWaitingForMovementVerify() || (System.currentTimeMillis() - user.getMovementData().getLastTeleport()) < 1000L || user.getCombatData().hasBowBoosted() || user.getBlockData().slime || user.getBlockData().slimeTicks > 0 | user.getMiscData().getBoatTicks() > 0 || user.getMiscData().isNearBoat() || user.getBlockData().climbableTicks > 0 || user.generalCancel() || (user.getMovementData().isOnGround() || user.getMovementData().isLastOnGround()) || user.getMovementData().getGroundTicks() > 15 || e.getTo().getY() < 0.0) {
                    user.getCheckData().flightADistance = 0;
                    return;
                }

                if (!user.generalCancel() || user.getMiscData().isSwitchedGamemodes() || user.getBlockData().stairTicks > 0 || user.getBlockData().slabTicks > 0) {
                    if ((user.getMovementData().isOnGround() || user.getMovementData().isLastOnGround()) || user.getMovementData().getGroundTicks() > 15) {
                        user.getCheckData().flightADistance = 0;
                    } else {
                        if (user.getMovementData().getTo() != null && user.getMovementData().getFrom() != null && user.getMovementData().getAirTicks() > 8) {
                            double x = Math.floor(user.getMovementData().getFrom().getX());
                            double z = Math.floor(user.getMovementData().getFrom().getZ());
                            if (Math.floor(user.getMovementData().getTo().getX()) != x || Math.floor(user.getMovementData().getTo().getZ()) != z) {
                                user.getCheckData().flightADistance++;
                            }

                            int max = 15;

                            double diff = (e.getTo().getY() - e.getFrom().getY());

                            double absDiff = Math.abs(diff);

                            if ((Math.abs(user.getConnectedTick() - user.getLagProcessor().getLastLagTick()) > 101) && (absDiff == this.expectFloatValue || Math.abs(user.getMovementData().getTo().getY() - user.getMovementData().getFrom().getY()) == this.expectFloatValue) || absDiff <= this.maxExpectedFallValue) {
                                max = (user.getMiscData().getJumpPotionTicks() > 0 ? 2 : 1);
                            }

                            if ((user.getMovementData().getTo().getY() - user.getMovementData().getFrom().getY()) <- 0.1) {
                                max += 2;
                            }

                            if (user.getMiscData().isNearBoat() || user.getMiscData().getBoatTicks() > 0 || TimeUtils.secondsFromLong(user.getMiscData().getLastNearBoat()) < 5L) {
                                max += 8;
                            }

                            if ((System.currentTimeMillis() - user.getMovementData().getLastEnderpearl()) < 1000L) {
                                max += 5;
                            }

                            if ((System.currentTimeMillis() - user.getCombatData().getLastEntityDamage()) < 1000L) {
                                max += 10;
                            }

                            if (user.getBlockData().fenceTicks > 0) {
                                max += 5;
                            }

                            if (TimeUtils.secondsFromLong(user.getMovementData().getLastJunpPadUpdate()) < 5L) {
                                max += 5;
                            }


                            if ((System.currentTimeMillis() - user.getCombatData().getLastBowDamage()) < 1000L) {
                                max += 5;
                            }

                            if ((System.currentTimeMillis() - user.getMovementData().getLastBlockJump()) < 1000L) {
                                max += 3;
                            }

                            if (user.getMovementData().isCollidedGround() || user.getMovementData().getCollidedGroundTicks() > 0 || (System.currentTimeMillis() - user.getMovementData().getLastCollidedGround()) < 1000L) {
                                max += 5;
                            }

                            if ((System.currentTimeMillis() - user.getMiscData().getLastBlockPlace()) < 1000L && !user.getMovementData().isOnGround() && !user.getMovementData().isLastOnGround()) {
                                max += 2;
                            }

                            if (user.getMiscData().getJumpPotionTicks() > 0) {
                                max += 4;
                            }

                            if ((e.getTo().getY() - e.getFrom().getY()) <- 0.56) {
                                max += 20;
                            }

                            if ((e.getTo().getY() - e.getFrom().getY() <- 2.00)) {
                                if (user.getCheckData().flightABuffer > 15) {
                                    max += 25;
                                }
                                user.getCheckData().flightABuffer++;
                            } else {
                                user.getCheckData().flightABuffer = 0;
                            }

                            if (user.getBlockData().stairTicks > 0 || user.getBlockData().slabTicks > 0) {
                                max += 3;
                            }

                            if (user.getLagProcessor().isLagging() || Math.abs(user.getConnectedTick() - user.getLagProcessor().getLastLagTick()) < 155) {
                                max += 8;
                            }

                            if (user.getCombatData().isInCombo()) {
                                max += 15;
                            }

                            int distance = user.getCheckData().flightADistance;

                            if (distance >= max) {
                                flag(user, "distance="+distance, "max="+max, "diff="+diff, "ground="+user.getMovementData().isOnGround(), "lastGround="+user.getMovementData().isLastOnGround());
                            }
                        }
                    }
                }
            }
        }
    }
}
