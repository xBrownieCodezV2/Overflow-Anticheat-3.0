package me.jumba.overflow.util.processor;

import io.netty.buffer.ByteBuf;
import lombok.Setter;
import me.jumba.overflow.Overflow;
import me.jumba.overflow.base.tinyprotocol.api.Packet;
import me.jumba.overflow.base.tinyprotocol.api.TinyProtocolHandler;
import me.jumba.overflow.base.tinyprotocol.packet.in.*;
import me.jumba.overflow.base.tinyprotocol.packet.out.WrappedOutTransaction;
import me.jumba.overflow.base.tinyprotocol.packet.out.WrappedOutVelocityPacket;
import me.jumba.overflow.base.user.User;
import me.jumba.overflow.hook.HookManager;
import me.jumba.overflow.listener.BukkitListeners;
import me.jumba.overflow.util.block.BlockAssesement;
import me.jumba.overflow.util.block.BlockEntry;
import me.jumba.overflow.util.block.BlockUtil;
import me.jumba.overflow.util.box.BoundingBox;
import me.jumba.overflow.util.location.CustomLocation;
import me.jumba.overflow.util.math.MathUtil;
import me.jumba.overflow.util.math.evicting.EvictingList;
import me.jumba.overflow.util.minecraft.ByteUtils;
import me.jumba.overflow.util.time.TickTimer;
import me.jumba.overflow.util.time.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created on 05/01/2020 Package me.jumba.sparky.util.processor
 */
@Setter
public class MovementProcessor {
    private User user;

    private double offset = Math.pow(2.0, 24.0);

    private double pitchDelta, yawDelta, lastDeltaYaw, lastDeltaPitch, pitchMode, yawMode, sensXPercent, deltaX, deltaY, sensYPercent, sensitivityX, sensitivityY, lastDeltaX, lastDeltaY;
    private long pitchGCD, yawGCD;

    public List<Double> pitchGcdList = new EvictingList(40), yawGcdList = new EvictingList(40);

    private TickTimer timer = new TickTimer(5);


    public void updateSensitityPrediction() {

        //Credit: Dawson

        lastDeltaPitch = pitchDelta;
        lastDeltaYaw = yawDelta;
        yawDelta = Math.abs(user.getMovementData().getTo().getYaw() - user.getMovementData().getFrom().getYaw());
        pitchDelta = user.getMovementData().getTo().getPitch() - user.getMovementData().getFrom().getPitch();


        yawGCD = MathUtil.gcd((long) (yawDelta * offset), (long) (lastDeltaYaw * offset));
        pitchGCD = MathUtil.gcd((long) (Math.abs(pitchDelta) * offset), (long) (Math.abs(lastDeltaPitch) * offset));

        double yawGcd = yawGCD / offset;
        double pitchGcd = pitchGCD / offset;

        user.getMovementData().setMouseDeltaX((int) (Math.abs((user.getMovementData().getTo().getYaw() - user.getMovementData().getFrom().getYaw())) / yawGcd));
        user.getMovementData().setMouseDeltaY((int) (Math.abs((user.getMovementData().getTo().getPitch() - user.getMovementData().getFrom().getPitch())) / pitchGCD));

        if (yawGCD > 90000 && yawGCD < 2E7 && yawGcd > 0.01f && yawDelta < 8) yawGcdList.add(yawGcd);

        if (pitchGCD > 90000 && pitchGCD < 2E7 && Math.abs(pitchDelta) < 8) pitchGcdList.add(pitchGcd);

        if (yawGcdList.size() > 3 && pitchGcdList.size() > 3) {

            if (timer.hasPassed()) {
                yawMode = MathUtil.getMode(yawGcdList);
                pitchMode = MathUtil.getMode(pitchGcdList);
                timer.reset();
                sensXPercent = sensToPercent(sensitivityX = getSensitivityFromYawGCD(yawMode));
                sensYPercent = sensToPercent(sensitivityY = getSensitivityFromPitchGCD(pitchMode));
                user.getMiscData().setClientSensitivity(sensXPercent);
                user.getMiscData().setClientSensitivity2(sensitivityY);
                user.getMiscData().setHasSetClientSensitivity(true);
            }

            lastDeltaX = deltaX;
            lastDeltaY = deltaY;
            deltaX = getDeltaX(yawDelta, (float) yawMode);
            deltaY = getDeltaY(pitchDelta, (float) pitchMode);
        }
    }

    public void update(Object packet, String type) {
        if (user != null) {

            if (type.equalsIgnoreCase(Packet.Server.POSITION)) {
                user.getMovementData().setLastServerPostion(System.currentTimeMillis());
                user.getMovementData().setLastServerPostionTick(user.getConnectedTick());
            }

            if (type.equalsIgnoreCase(Packet.Client.CUSTOM_PAYLOAD)) {
                WrappedInCustomPayloadPacket wrappedInCustomPayloadPacket = new WrappedInCustomPayloadPacket(packet, user.getPlayer());

                if (wrappedInCustomPayloadPacket.getChannel().equalsIgnoreCase(BukkitListeners.inChannel)) {
                    ByteBuf byteBuf = (ByteBuf) wrappedInCustomPayloadPacket.getData();
                    String dataFromPacket = new String(ByteUtils.readByteArray(byteBuf));

                    if (dataFromPacket.equalsIgnoreCase(BukkitListeners.inData)) {
                        user.flag = true;
                    }
                }
            }

            if (type.equalsIgnoreCase(Packet.Client.BLOCK_PLACE)) {
                WrappedInBlockPlacePacket wrappedInBlockPlacePacket = new WrappedInBlockPlacePacket(packet, user.getPlayer());
                boolean isValid = (user.getMiscData().isInteractableItem(user.getPlayer().getItemInHand())) || (TimeUtils.secondsFromLong(user.getMiscData().getLastBlockPlace()) < 3L && user.getMovementData().getMovementSpeed() < 0.355);

                if (wrappedInBlockPlacePacket.getPosition().getZ() == -1 && wrappedInBlockPlacePacket.getPosition().getY() == -1 && wrappedInBlockPlacePacket.getPosition().getZ() == -1 && isValid) {
                   user.getMovementData().setBlocking(true);
                }
            }

            if (type.equalsIgnoreCase(Packet.Client.BLOCK_DIG)) {
                WrappedInBlockDigPacket wrappedInBlockDigPacket = new WrappedInBlockDigPacket(packet, user.getPlayer());
                if (wrappedInBlockDigPacket.getAction() == WrappedInBlockDigPacket.EnumPlayerDigType.RELEASE_USE_ITEM && user.getMovementData().isBlocking()) {
                    user.getMovementData().setBlocking(false);
                }
            }

            if (type.equalsIgnoreCase(Packet.Server.ENTITY_VELOCITY)) {
                WrappedOutVelocityPacket velocityPacket = new WrappedOutVelocityPacket(packet, user.getPlayer());
                if (velocityPacket.getId() == user.getPlayer().getEntityId()) {

                        WrappedOutTransaction wrappedOutTransaction = new WrappedOutTransaction(0, user.getMiscData().getTransactionIDVelocity(), false); // static id for now but u need to randomize it osoikikikikikik ik
                        TinyProtocolHandler.getInstance().getChannel().sendPacket(user.getPlayer(), wrappedOutTransaction.getObject());

                        user.getMovementData().setLastVelocity(System.currentTimeMillis());
                        double velocityX = velocityPacket.getX();
                        double velocityY = velocityPacket.getY();
                        double velocityZ = velocityPacket.getZ();
                        double horizontal = Math.hypot(velocityX, velocityZ);
                        double vertical = Math.pow(velocityY + 2.0, 2.0) * 5.0;
                        user.getMovementData().setHorizontalVelocity(horizontal);
                        user.getMovementData().setVerticalVelocity(vertical);
                 //   Bukkit.broadcastMessage("test 5");
                        if (user.getMovementData().isOnGround() && user.getPlayer().getLocation().getY() % 1.0 == 0.0) {
                            user.getMovementData().setVerticalVelocity(velocityY);
                        }

                   // user.debug(""+user.getMovementData().isJumpPad() + " " + TimeUtils.secondsFromLong(user.getMovementData().getLastJunpPadUpdate()));

                    if (user.getMovementData().isJumpPad() && (System.currentTimeMillis() - user.getMovementData().getLastFallDamage()) < 1000L) {
                        user.getMovementData().setJumpPad(false);
                        user.getMovementData().setLastJunpPadUpdate(0);
                        return;
                    }

                    if (!user.getMovementData().isJumpPad() && (System.currentTimeMillis() - user.getMovementData().getLastFallDamage()) > 1000L && user.getMovementData().isOnGround() && (System.currentTimeMillis() - user.getCombatData().getLastEntityDamageAttack()) > 20L) {
                        user.getMovementData().setJumpPad(true);
                        user.getMovementData().setLastJumpPadSet(System.currentTimeMillis());
                    }
                }

                if (user.getMovementData().isJumpPad() && user.getMovementData().isOnGround() && user.getMovementData().getGroundTicks() > 15 && (System.currentTimeMillis() - user.getMovementData().getLastJumpPadSet()) > 1000L) {
                    user.getMovementData().setJumpPad(false);
                }
            }

            if (type.equalsIgnoreCase(Packet.Client.ENTITY_ACTION)) {
                WrappedInEntityActionPacket wrappedInEntityActionPacket = new WrappedInEntityActionPacket(packet, user.getPlayer());
                if (wrappedInEntityActionPacket.getAction() == WrappedInEntityActionPacket.EnumPlayerAction.START_SPRINTING) {
                    user.getMovementData().setSprinting(true);
                } else if (wrappedInEntityActionPacket.getAction() == WrappedInEntityActionPacket.EnumPlayerAction.STOP_SPRINTING) {
                    user.getMovementData().setSprinting(false);
                }

                if (wrappedInEntityActionPacket.getAction() == WrappedInEntityActionPacket.EnumPlayerAction.START_SNEAKING) {
                    user.getMovementData().setSneaking(true);
                } else if (wrappedInEntityActionPacket.getAction() == WrappedInEntityActionPacket.EnumPlayerAction.STOP_SPRINTING) {
                    user.getMovementData().setSneaking(false);
                }
            }

            if (type.equalsIgnoreCase(Packet.Client.POSITION) || type.equalsIgnoreCase(Packet.Client.POSITION_LOOK) || type.equalsIgnoreCase(Packet.Client.LOOK) || type.equalsIgnoreCase(Packet.Client.FLYING)) {
                WrappedInFlyingPacket wrappedInFlyingPacket = new WrappedInFlyingPacket(packet, user.getPlayer());

                user.getCombatData().setHitDelay(user.getPlayer().getMaximumNoDamageTicks());

                updateSensitityPrediction();

                //For cheater prediction to try and get them to hit the bot
                if (user.getConnectedTick() % 100 == 0 && user.getCheatPrediction().getTotalVL() > 10 && TimeUtils.secondsFromLong(user.getCheckData().lastPredictionBot) > 30L && !user.getCheckData().hasBot) {
                 //   Entity.spawnBot(user, Entity.CheckTypes.NORMAL);
               //     user.getCheckData().lastPredictionBot = System.currentTimeMillis();
                }

                if (user.getMovementData().isDidUnknownTeleport()) {

                    if (Math.abs(user.getConnectedTick() - user.getMovementData().getUnknownTeleportTick()) > 20) {

                        if ((user.getMovementData().getTo().getY() - user.getMovementData().getFrom().getY() >= 0.0)) {

                            if (user.invalidTeleportMovementVerbose > 7) {
                                user.getMovementData().setDidUnknownTeleport(false);
                            }

                           if (user.invalidTeleportMovementVerbose < 20) user.invalidTeleportMovementVerbose++;
                        } else {
                            if (user.invalidTeleportMovementVerbose > 0) user.invalidTeleportMovementVerbose--;
                        }

                        if (user.getMovementData().getMovementSpeed() > 0.66 && (System.currentTimeMillis() - user.getCombatData().getLastEntityDamage()) > 1000L && (System.currentTimeMillis() - user.getCombatData().getLastBowDamage()) > 1000L) {
                            user.getMovementData().setDidUnknownTeleport(false);
                        }
                    }

                    if (Math.abs(user.getConnectedTick() - user.getMovementData().getUnknownTeleportTick()) > 5 && (user.getMovementData().isOnGround() || user.getMovementData().isClientGround())) {
                        user.getMovementData().setDidUnknownTeleport(false);
                    }
                } else {
                    user.invalidTeleportMovementVerbose = 0;
                }

                if (user.getMovementData().isBlocking()) {
                    if (user.getMovementData().getMovementSpeed() > 0.09) {
                        if (user.getMiscData().getBlockingSpeedTicks() < 100) user.getMiscData().setBlockingSpeedTicks(user.getMiscData().getBlockingSpeedTicks() + 1);
                    } else {
                        if (user.getMiscData().getBlockingSpeedTicks() > 0) user.getMiscData().setBlockingSpeedTicks(user.getMiscData().getBlockingSpeedTicks() - 1);
                    }

                    int max = 19;
                    double maxSpeed = 0.55;

                    if (user.getBlockData().iceTicks > 0 || (System.currentTimeMillis() - user.getBlockData().lastIce) < 1000L) {
                        max += 35;
                        maxSpeed += 1.20;
                    }

                    if ((System.currentTimeMillis() - user.getCombatData().getLastEntityDamageAttack()) < 1000L) {
                        max += 5;
                        maxSpeed += 0.99;
                    }

                    if (user.getMovementData().isJumpPad() || user.getMiscData().getBlockingSpeedTicks() > max || user.getMovementData().getMovementSpeed() > maxSpeed) {
                        user.getMovementData().setBlocking(false);
                    }
                } else {
                    user.getMiscData().setBlockingSpeedTicks(0);
                }

                if (!user.isSetClientVersion()) {
                    user.setSetClientVersion(true);
                    user.setCurrentClientVersion(HookManager.Helper.getClientVersion(user.getPlayer()));
                }

                if ((System.currentTimeMillis() - user.getMovementData().getLastExplode()) > 1000L && user.getMovementData().isExplode() && user.getMovementData().isOnGround() && user.getMovementData().isLastOnGround()) {
                    user.getMovementData().setExplode(false);
                }

                if (user.isWaitingForMovementVerify()) {

                    if (user.movementVerifyBlocks > 5) {
                        user.movementVerifyBlocks = 0;
                        user.setWaitingForMovementVerify(false);
                    }

                    double x = Math.floor(user.getMovementData().getFrom().getX());
                    double z = Math.floor(user.getMovementData().getFrom().getZ());
                    if (Math.floor(user.getMovementData().getTo().getX()) != x || Math.floor(user.getMovementData().getTo().getZ()) != z) {
                        user.movementVerifyBlocks++;
                    }
                }
                if (user.isWatchdogBan()) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            user.setWatchdogBan(false);
                            user.setLastBan(System.currentTimeMillis());
                            Bukkit.broadcastMessage(ChatColor.WHITE + "[WHATDOG CHEAT DETECTION] " + ChatColor.RED + "§lA player has been removed from your game for hacking or abuse.");
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    user.getPlayer().kickPlayer(ChatColor.WHITE + "WATCHDOG CHEAT DETECTION " + ChatColor.GRAY + "(GG-" + ThreadLocalRandom.current().nextInt(9999) + ")");
                                    Bukkit.broadcastMessage(ChatColor.RED + "§lA player has been removed from your game for hacking or abuse. " + ChatColor.RESET + ChatColor.AQUA + "Thanks for reporting it!");
                                    this.cancel();
                                }
                            }.runTaskTimer(Overflow.getLauncherInstance(), 20L, 20L);
                        }
                    }.runTask(Overflow.getLauncherInstance());
                }

                if (user.getFlyingTick() < 20) {
                    user.setFlyingTick(user.getFlyingTick() + 1);
                } else if (user.getFlyingTick() >= 20) {
                    user.setFlyingTick(0);
                }

                user.setConnectedTick(user.getConnectedTick() + 1);
                user.getMovementData().setVelocityTicks(user.getMovementData().getVelocityTicks() + 1);

                if (user.getMovementData().isJumpPad()) {
                    if ((System.currentTimeMillis() - user.getMovementData().getLastJumpPadSet()) > 230L && user.getMovementData().isOnGround() && user.getMovementData().isLastOnGround()) {
                        user.getMovementData().setJumpPad(false);
                    }
                    user.getMovementData().setLastJunpPadUpdate(System.currentTimeMillis());
                }

                user.setSafe(TimeUtils.secondsFromLong(user.getTimestamp()) > 2L || user.isHasVerify());

                if (!user.isHasVerify()) user.setHasVerify(user.isSafe());


                if (user.isSafe()) {

                    if (!user.getMiscData().isAfkMovement() && ((System.currentTimeMillis() - user.getMovementData().getLastFullBlockMoved()) > 700L || (System.currentTimeMillis() - user.getCombatData().getLastEntityDamage()) < 1000L || (System.currentTimeMillis() - user.getCombatData().getLastBowDamage() < 1000L || (System.currentTimeMillis() - user.getCombatData().getLastRandomDamage()) < 1000L)) && (user.getMovementData().getTo().getY() - user.getMovementData().getFrom().getY()) <- 0.55) {
                        user.getMiscData().setAfkMovement(true);
                    } else if (user.getMiscData().isAfkMovement()) {
                        if (user.getMovementData().getAfkMovementTotalBlocks() > 3) {
                            user.getMovementData().setAfkMovementTotalBlocks(0);
                            user.getMiscData().setAfkMovement(false);
                        }
                    }

                    user.getMovementData().setLastClientGround(user.getMovementData().isClientGround());
                    user.getMovementData().setClientGround(wrappedInFlyingPacket.isGround());

                    if (user.isLagBack()) {
                        user.setLagBack(false);
                        user.setLastLagBack(System.currentTimeMillis());
                        user.getPlayer().teleport(user.getMovementData().getLagBackLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                    }

                    if (user.getMovementData().getTo() != null) {
                        user.getMovementData().setFrom(user.getMovementData().getTo().clone());

                        if (user.getMovementData().getTo().isClientGround() && user.getMovementData().isOnGround() && user.getMovementData().getTo().getY() % 0.015625 == 0.0) {
                            user.getMovementData().setLagBackLocation(user.getMovementData().getTo().toLocation(user.getPlayer().getWorld()));
                        }
                    }
                    if (wrappedInFlyingPacket.isPos()) {

                        user.getMovementData().getTo().setX(wrappedInFlyingPacket.getX());
                        user.getMovementData().getTo().setY(wrappedInFlyingPacket.getY());
                        user.getMovementData().getTo().setZ(wrappedInFlyingPacket.getZ());
                        user.getMovementData().getTo().setClientGround(wrappedInFlyingPacket.isGround());
                        //     user.getMovementData().setTo(new CustomLocation(wrappedInFlyingPacket.getX(), wrappedInFlyingPacket.getY(), wrappedInFlyingPacket.getZ()));
                        user.getMovementData().setLastPos(System.currentTimeMillis());

                        if (user.getMovementData().getTo() != null && user.getMovementData().getFrom() != null) {

                            user.getMovementData().setLastOnGround(user.getMovementData().isOnGround());

                            if (user.isSafe() && user.getBoundingBox() != null) {
                                this.updateBlockCheck();
                            } else {
                                user.getMovementData().setOnGround(wrappedInFlyingPacket.isGround());
                            }
                        }


                        CustomLocation customLocation = new CustomLocation(wrappedInFlyingPacket.getX(), wrappedInFlyingPacket.getY(), wrappedInFlyingPacket.getZ());

                        if (user.getMovementData().getLastGroundLocation() != null && user.getMovementData().isOnGround()) {
                            if ((user.getMovementData().getTo().getY() - user.getMovementData().getFrom().getY()) > 0.4f && user.getMovementData().getTo().getY() > user.getMovementData().getFrom().getY()) {
                                double totalPrediction = MathUtil.round(user.getMovementData().getTo().getY(), 0) + user.getMovementData().getGroundYPredict();

                                if (totalPrediction > user.getMovementData().getLastGroundPrediction()) {
                                    user.getMovementData().setLastBlockJump(System.currentTimeMillis());
                                }

                                user.getMovementData().setLastGroundPrediction(totalPrediction);
                            }
                        }

                        if (user.getMovementData().isOnGround() && user.getMovementData().getTo() != null && user.getMovementData().getFrom() != null) {
                            if (user.getMovementData().isLastOnGround()) {
                                user.getMovementData().setGroundYPredict(user.getMovementData().getTo().getY());
                            }
                            user.getMovementData().setLastGroundLocation(customLocation);
                            //   user.getMovementData().setLastGroundLocation(user.getMovementData().getTo());
                        /*    if (user.getMovementData().getTo() != null && user.getMovementData().getFrom() != null && user.getMovementData().getLastGroundLocation() != null) {
                                CustomLocation location = user.getMovementData().getTo();
                                if (location.getY() > user.getMovementData().getLastGroundLocation().getY()) {
                                    user.getMovementData().setLastBlockJump(System.currentTimeMillis());
                                }
                            }*/
                        }
                    }

                    user.getMovementData().setDeltaXZ((Math.hypot(user.getMovementData().getTo().getX() - user.getMovementData().getFrom().getX(), user.getMovementData().getTo().getZ() - user.getMovementData().getFrom().getZ())));

                    boolean badVector = Math.abs(user.getMovementData().getTo().toVector().length() - user.getMovementData().getFrom().toVector().length()) >= 1;

                    user.setBoundingBox(new BoundingBox((badVector ? user.getMovementData().getTo().toVector() : user.getMovementData().getFrom().toVector()), user.getMovementData().getTo().toVector()).grow(0.3f, 0, 0.3f).add(0, 0, 0, 0, 1.84f, 0));

                    if (wrappedInFlyingPacket.isLook()) {
                        user.getMovementData().getTo().setPitch(wrappedInFlyingPacket.getPitch());
                        user.getMovementData().getTo().setYaw(wrappedInFlyingPacket.getYaw());
                    }

                    if (user.getMovementData().getTo() != null && user.getMovementData().getFrom() != null) {
                        double x = Math.floor(user.getMovementData().getFrom().getX());
                        double z = Math.floor(user.getMovementData().getFrom().getZ());
                        if (Math.floor(user.getMovementData().getTo().getX()) != x || Math.floor(user.getMovementData().getTo().getZ()) != z) {
                            if (user.totalBlocksCheck < 100) user.totalBlocksCheck++;
                            user.getMovementData().setLastFullBlockMoved(System.currentTimeMillis());

                            if (user.getCombatData().isRespawn() && (System.currentTimeMillis() - user.getCombatData().getLastRespawn()) > 1000L) {
                                user.getCombatData().setRespawn(false);
                            }

                            if (user.getMiscData().isAfkMovement()) {
                                user.getMovementData().setAfkMovementTotalBlocks(user.getMovementData().getAfkMovementTotalBlocks() + 1);
                            } else {
                                user.getMovementData().setAfkMovementTotalBlocks(0);
                            }
                        }
                    }

                    if (user.getMiscData().isSwitchedGamemodes() && user.getMovementData().isOnGround()) {
                        user.getMiscData().setSwitchedGamemodes(false);
                    }

                    if (user.getMovementData().getTo() != null && user.getMovementData().getFrom() != null) {
                        user.getMovementData().setBukkitTo(user.getMovementData().getTo().toLocation(user.getPlayer().getWorld()));
                        user.getMovementData().setBukkitFrom(user.getMovementData().getFrom().toLocation(user.getPlayer().getWorld()));

                        double x = Math.abs(Math.abs(user.getMovementData().getTo().getX()) - Math.abs(user.getMovementData().getFrom().getX()));
                        double z = Math.abs(Math.abs(user.getMovementData().getTo().getZ()) - Math.abs(user.getMovementData().getFrom().getZ()));
                        user.getMovementData().setMovementSpeed(Math.sqrt(x * x + z * z));
                    }
                }
            }
        }
    }

    private void updateBlockCheck() {
        BlockAssesement blockAssesement = new BlockAssesement(user.getBoundingBox(), user);

        List<BoundingBox> boxes = Overflow.getInstance().getBlockBoxManager().getBlockBox().getCollidingBoxes(user.getPlayer().getWorld(), user.getBoundingBox().grow(0.3f, 0.35f, 0.3f));

        List<BlockEntry> blockEntries = Collections.synchronizedList(new ArrayList<>());

        user.getMovementData().setChunkLoaded(BlockUtil.isChunkLoaded(user.getMovementData().getTo().toLocation(user.getPlayer().getWorld())));

        if (user.getMovementData().isChunkLoaded()) {

            boxes.parallelStream().forEach(boundingBox -> {
                Block block = BlockUtil.getBlock(boundingBox.getMinimum().toLocation(user.getPlayer().getWorld()));

                if (block != null) {

                    BlockEntry blockEntry = new BlockEntry(block, boundingBox);

                    blockAssesement.update(blockEntry.getBoundingBox(), blockEntry.getBlock(), user.getPlayer().getWorld());

                    blockEntries.add(blockEntry);
                }
            });
        }

        blockAssesement.updateBlocks(blockEntries);

        blockEntries.clear();

        boxes.clear();

        user.getMovementData().setOnGround(blockAssesement.isOnGround());

        user.getMovementData().setCollidedGround(blockAssesement.isCollidedGround());

        if (blockAssesement.isCollidedGround()) {
            user.getMovementData().setLastCollidedGround(System.currentTimeMillis());
        }

        user.getBlockData().isGroundWater = blockAssesement.isLiquidGround();
        user.update(blockAssesement);
    }


    private static double yawToF2(double yawDelta) {
        return yawDelta / .15;
    }

    private static double pitchToF3(double pitchDelta) {
        int b0 = pitchDelta >= 0 ? 1 : -1; //Checking for inverted mouse.
        return pitchDelta / .15 / b0;
    }

    private static double getF1FromYaw(double gcd) {
        double f = getFFromYaw(gcd);

        return Math.pow(f, 3) * 8;
    }

    private static double getSensitivityFromPitchGCD(double gcd) {
        double stepOne = pitchToF3(gcd) / 8;
        double stepTwo = Math.cbrt(stepOne);
        double stepThree = stepTwo - .2f;
        return stepThree / .6f;
    }

    private static double getSensitivityFromYawGCD(double gcd) {
        double stepOne = yawToF2(gcd) / 8;
        double stepTwo = Math.cbrt(stepOne);
        double stepThree = stepTwo - .2f;
        return stepThree / .6f;
    }

    private static double getFFromYaw(double gcd) {
        double sens = getSensitivityFromYawGCD(gcd);
        return sens * .6f + .2;
    }

    private static double getFFromPitch(double gcd) {
        double sens = getSensitivityFromPitchGCD(gcd);
        return sens * .6f + .2;
    }

    private static double getF1FromPitch(double gcd) {
        double f = getFFromPitch(gcd);

        return (float)Math.pow(f, 3) * 8;
    }

    private static int getDeltaX(double yawDelta, double gcd) {
        double f2 = yawToF2(yawDelta);

        return MathUtil.floor(f2 / getF1FromYaw(gcd));
    }

    private static int getDeltaY(double pitchDelta, double gcd) {
        double f3 = pitchToF3(pitchDelta);

        return MathUtil.floor(f3 / getF1FromPitch(gcd));
    }


    public static int sensToPercent(double sensitivity) {
        return (int) MathUtil.round(sensitivity / .5f * 100, 0);
    }
}
