package me.jumba.overflow.base.user;

import lombok.Getter;
import lombok.Setter;
import me.jumba.overflow.Overflow;
import me.jumba.overflow.base.check.Check;
import me.jumba.overflow.base.tinyprotocol.api.ProtocolVersion;
import me.jumba.overflow.base.user.sub.*;
import me.jumba.overflow.checks.combat.bot.Entity;
import me.jumba.overflow.hook.HookManager;
import me.jumba.overflow.util.block.BlockAssesement;
import me.jumba.overflow.util.block.BlockUtil;
import me.jumba.overflow.util.box.BoundingBox;
import me.jumba.overflow.util.entity.EntityHelper1_8;
import me.jumba.overflow.util.location.CustomLocation;
import me.jumba.overflow.util.math.MCSmoothing;
import me.jumba.overflow.util.math.MathUtil;
import me.jumba.overflow.util.math.Verbose;
import me.jumba.overflow.util.processor.*;
import me.jumba.overflow.util.processor.lag.LagProcessor;
import me.jumba.overflow.util.time.TimeUtils;
import me.jumba.overflow.util.version.VersionUtil;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created on 05/01/2020 Package me.jumba.sparky.base.user
 */
@Getter
@Setter
public class User {

    private Player player;
    private UUID uuid;

    private BoundingBox boundingBox;
    private MovementData movementData;
    private CombatData combatData;
    private CheckData checkData;
    private BlockData blockData;
    private MiscData miscData;
    private CheatPrediction cheatPrediction;

    private long timestamp, lastLagBack, lastFlag;
    private boolean safe, hasVerify;

    private MovementProcessor movementProcessor;
    private CombatProcessor combatProcessor;
    private LagProcessor lagProcessor;
    private OtherProcessor otherProcessor;
    private PredictionProcessor predictionProcessor;
    private OptifineProcessor optifineProcessor;

    private int devViolation, connectedTick, flyingTick, violation, spawnedEntityID;
    public int movementVerifyBlocks, movementVerifyStage = 0, totalBlocksCheck = 0;
    private ProtocolVersion protocolVersion;
    private boolean devAlerts, banWaiting, setClientVersion, wasFlying, alerts = true, isWaitingForMovementVerify, watchdogTimerSet, watchdogBan, lagBack, waitingForBot;
    private WeakHashMap<Check, Integer> flaggedChecks = new WeakHashMap<>();
    private User forcedUser;
    private Entity.CheckTypes botCheckType;
    private double entityAStartYaw, entityAMovementOffset, entityAFollowDistance;
    private HookManager.Helper.Versions currentClientVersion = HookManager.Helper.Versions.V1_8;




    //Optifine things
    public long lastBan, lastOptifine, lastOptifineREE, lastRetardOpitfineSpam, lastAimAssistACE;
    public int optifineSmoothing2, lastClientSmoothingValue, optifineSmoothing, LastSmoothingCounter, smoothingCounter, optifineSmoothSens, optifinePitchSmooth, optifineSameCount, optifineConstantVL2, optifineConstantVL, optifineSmoothingFix, killauraAYawReset, killauraAPitchReset, aimAssistsACount, optifineSmoothingTicks;
    public MCSmoothing aimWSmooth = new MCSmoothing(), newPitchSmoothing = new MCSmoothing(), newYawSmoothing = new MCSmoothing(), yaw = new MCSmoothing(), pitch = new MCSmoothing(), smooth = new MCSmoothing();
    public double lastAimCPitchDiff, lastSmoothingRot2, lastSmoothingRot, lastPitchDelta, lastSmoothPitch1, lastOptifinePitchSmoothidfklol;
    public float lastYawDelta, lastSmoothYaw;
    public boolean flag, cineCamera;


    //Old checks cba to move into CheckData class
    public long lastAimAssistHLook, lastAimAssistHPostion, lastAimAssistHGCDPitch, lastAimAssistHGCDYaw, lastAimAssistHVal1;
    public float lastAimAssistHPitch, lastAimAssistHYaw;
    public int aimCVerbose, invalidTeleportMovementVerbose, aimAssistHVerbose1Last, aimAssistHVerbose1, aimAssistHPitchSame, aimAssistHCounter;
    public Verbose aimAssistHVerbose2 = new Verbose();

    public EntityHelper1_8 entityHelper1_8;

    private List<String> patternChecksFlagged = new ArrayList<>();

    public WeakHashMap<Short, Long> transactionMap = new WeakHashMap<>();

    private List<String> developerUUIDS = Arrays.asList("b5deccab-d244-4496-b30b-0045fa86805b", "b91ad27b-fec9-47c8-870c-7a1b77534a0d", "d18cb3d4-0322-4cb5-b980-69a6f3e311ae", "725af6f0-420c-478b-8118-3b393d267bfe");

    private ScheduledExecutorService executorService;

    public User(Player player) {
        this.player = player;
        this.uuid = player.getUniqueId();

        executorService = Executors.newSingleThreadScheduledExecutor();

        timestamp = System.currentTimeMillis();

        movementData = new MovementData(this);

        combatData = new CombatData();

        combatData.setup(this);

        checkData = new CheckData(this);

        blockData = new BlockData();

        miscData = new MiscData();

        cheatPrediction = new CheatPrediction();

        movementData.setTo(new CustomLocation(0.0, 0.0, 0.0));
        movementData.setFrom(movementData.getFrom());

        boundingBox = new BoundingBox(0f, 0f, 0f, 0f, 0f, 0f);

        optifineProcessor = new OptifineProcessor();


        setupProcessors();

        if (Overflow.getInstance().getVersionUtil().getVersion() == VersionUtil.Version.V1_8) {
            entityHelper1_8 = new EntityHelper1_8();
        }

        if (developerUUIDS.contains(player.getUniqueId().toString())) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    getPlayer().sendMessage(ChatColor.RED + "This server is running Overflow " + ChatColor.GRAY + Overflow.getInstance().getVersion());
                    getPlayer().sendMessage(ChatColor.GRAY + "This copy is registered to " + ChatColor.GREEN + (Verbose.licensedTo != null ? Verbose.licensedTo : "UnRegistered"));
                }
            }.runTaskLater(Overflow.getLauncherInstance(), 70L);
        }
    }

    private void setupProcessors() {

        movementProcessor = new MovementProcessor();
        movementProcessor.setUser(this);

        combatProcessor = new CombatProcessor();
        combatProcessor.setUser(this);

        lagProcessor = new LagProcessor();
        lagProcessor.setUser(this);

        predictionProcessor = new PredictionProcessor(this);

        otherProcessor = new OtherProcessor();
        otherProcessor.setUser(this);
    }

    public boolean isDead() {
        return TimeUtils.secondsFromLong(getCombatData().getLastRespawn()) < 5L || TimeUtils.secondsFromLong(getCombatData().getLastDeath()) < 5L;
    }

    public void resetViolation() {
        violation = 0;
        devViolation = 0;
        flaggedChecks.clear();
    }

    public void update(BlockAssesement blockAssesement) {
        /*
            Update ticks here for mostly anything
         */

        if ((getPlayer().isFlying() || getPlayer().getAllowFlight()) && !wasFlying) {
            wasFlying = true;
        } else if (wasFlying && !(getPlayer().isFlying() || getPlayer().getAllowFlight())) {
            if (getMovementData().isOnGround() && getMovementData().isLastOnGround()) {
                wasFlying = false;
            }
        }


        getMovementData().setCollidesHorizontally(blockAssesement.isCollidesHorizontally());

        getMovementData().setLastCollidedVrtically(getMovementData().isCollidesVertically());
        getMovementData().setCollidesVertically(blockAssesement.isCollidesVertically());

        getMovementData().setWalkSpeed(getPlayer().getWalkSpeed());


        if (getMovementData().getTo() != null && getMovementData().getFrom() != null && getMovementData().getTo() != getMovementData().getFrom()) {
            boolean nearBoat = BlockUtil.isOnFuckingBoat(getPlayer());

            if (nearBoat) {
                getMiscData().setLastNearBoat(System.currentTimeMillis());
            }

            getMiscData().setNearBoat(nearBoat);

            if (nearBoat) {
                if (getMiscData().getBoatTicks() < 20) getMiscData().setBoatTicks(getMiscData().getBoatTicks() + 1);
            } else {
                if (getMiscData().getBoatTicks() > 0) getMiscData().setBoatTicks(getMiscData().getBoatTicks() - 1);
            }
        }

        if (getBoundingBox() != null && getMovementData().getBukkitTo() != null) {

            Block block = BlockUtil.getBlock(getMovementData().getBukkitTo().clone().add(0, -1, 0));
            if (block != null) {

                if (blockAssesement.isLeaves()) {
                    if (getBlockData().leaveTicks < 50) getBlockData().leaveTicks++;
                } else {
                    if (getBlockData().leaveTicks > 0) getBlockData().leaveTicks--;
                }


                getMovementData().setServerBlockBelow(BlockUtil.getBlock(getMovementData().getTo().toLocation(getPlayer().getWorld()).clone().add(0, -1f, 0)));

                if (Overflow.getInstance().getVersionUtil().getVersion() != VersionUtil.Version.V1_7) {
                    if (getMovementData().isOnGround()) {

                        if (block.getType() == Material.SLIME_BLOCK) {
                            if (!getBlockData().slime) {
                                getBlockData().slime = true;
                            }
                            getMovementData().setLastSlimeLocation(getMovementData().getTo().clone());
                            //getMovementData().setLastSlimeLocation(new CustomLocation(getMovementData().getTo().getX(), getMovementData().getTo().getY(), getMovementData().getTo().getZ()));
                        }

                        if (getBlockData().slime && block.getType() != Material.AIR && block.getType() != Material.SLIME_BLOCK) {
                            getBlockData().slime = false;
                            getMovementData().setLastSlimeLocation(null);
                        }
                    } else {
                        if (getMovementData().getLastSlimeLocation() != null && getBlockData().slime) {
                            Location loc = getMovementData().getLastSlimeLocation().toLocation(getPlayer().getWorld()), currentLoc = getMovementData().getTo().toLocation(getPlayer().getWorld()).clone();
                            loc.setY(0.0f);
                            currentLoc.setY(0.0f);

                            double distance = loc.distance(currentLoc);

                            if (distance > 5 && Math.abs(getMovementData().getTo().getY() - getMovementData().getFrom().getY()) == 0.0) {
                                getBlockData().slime = false;
                                getMovementData().setLastSlimeLocation(null);
                            }

                            if (distance > 10) {
                                getBlockData().slime = false;
                                getMovementData().setLastSlimeLocation(null);
                            }
                        }
                    }
                }
            }
        }

        if (blockAssesement.isChests()) {
            if (getBlockData().chestTicks < 20) getBlockData().chestTicks++;
        } else {
            if (getBlockData().chestTicks > 0) getBlockData().chestTicks--;
        }

        if (blockAssesement.isTrapDoor()) {
           if (getBlockData().trapDoorTicks < 20) getBlockData().trapDoorTicks++;
        } else {
            if (getBlockData().trapDoorTicks > 0) getBlockData().trapDoorTicks--;
        }

        if (getMiscData().getMountedTicks() > 0) {
            getMiscData().setLastMoutUpdate(System.currentTimeMillis());
        }

        if (getPlayer().getVehicle() != null) {
            getMiscData().setLastMount(System.currentTimeMillis());
            if (getMiscData().getMountedTicks() < 20) getMiscData().setMountedTicks(getMiscData().getMountedTicks() + 1);
        } else {
            if (getMiscData().getMountedTicks() > 0) getMiscData().setMountedTicks(getMiscData().getMountedTicks() - 1);
        }

        boolean hasSpeed = getPlayer().hasPotionEffect(PotionEffectType.SPEED), hasJump = getPlayer().hasPotionEffect(PotionEffectType.JUMP);

        getMiscData().setSpeedPotionEffectLevel(MathUtil.getPotionEffectLevel(getPlayer(), PotionEffectType.SPEED));

        getMiscData().setHasSpeedPotion(hasSpeed);
        getMiscData().setHasJumpPotion(hasJump);

        if (hasJump) {
            if (getMiscData().getJumpPotionTicks() < 20) getMiscData().setJumpPotionTicks(getMiscData().getJumpPotionTicks() + 1);
            getMiscData().setJumpPotionMultiplyer(MathUtil.getPotionEffectLevel(getPlayer(), PotionEffectType.JUMP));
        } else {
            if (getMiscData().getJumpPotionTicks() > 0) getMiscData().setJumpPotionTicks(getMiscData().getJumpPotionTicks() - 1);
        }

        if (hasSpeed) {
            if (getMiscData().getSpeedPotionTicks() < 20) getMiscData().setSpeedPotionTicks(getMiscData().getSpeedPotionTicks() + 1);
        } else {
            if (getMiscData().getSpeedPotionTicks() > 0) getMiscData().setSpeedPotionTicks(getMiscData().getSpeedPotionTicks() - 1);
        }


        if (getMovementData().isCollidedGround()) {
            if (getMovementData().getCollidedGroundTicks() < 20) getMovementData().setCollidedGroundTicks(getMovementData().getCollidedGroundTicks() + 1);
        } else {
            if (getMovementData().getCollidedGroundTicks() > 0) getMovementData().setCollidedGroundTicks(getMovementData().getCollidedGroundTicks() - 1);
        }

        int groundTicks = getMovementData().getGroundTicks();
        int airTicks = getMovementData().getAirTicks();

        if (blockAssesement.isHopper()) {
            if (getBlockData().hopperTicks < 50) getBlockData().hopperTicks++;
        } else {
            if (getBlockData().hopperTicks > 0) getBlockData().hopperTicks--;
        }

        if (blockAssesement.isWall()) {
            if (getBlockData().wallTicks < 20) getBlockData().wallTicks++;
        } else {
            if (getBlockData().wallTicks > 0) getBlockData().wallTicks--;
        }


        if (blockAssesement.isLillyPad()) {
            if (getBlockData().lillyPadTicks < 50) getBlockData().lillyPadTicks++;
        } else {
            if (getBlockData().lillyPadTicks > 0) getBlockData().lillyPadTicks--;
        }

        if (blockAssesement.isOnGround()) {

            if (blockAssesement.isAnvil()) {
                if (getBlockData().anvilTicks < 20) getBlockData().anvilTicks++;
            } else {
                if (getBlockData().anvilTicks > 0) getBlockData().anvilTicks--;
            }

            if (groundTicks < 20) groundTicks++;
            airTicks = 0;
        } else {
            if (airTicks < 20) airTicks++;
            groundTicks = 0;
        }

        if (blockAssesement.isBlockAbove()) {
            if (blockData.blockAboveTicks < 20) blockData.blockAboveTicks++;
            blockData.lastBlockAbove = System.currentTimeMillis();
        } else {
            if (blockData.blockAboveTicks > 0) blockData.blockAboveTicks--;
        }

        if (blockAssesement.isSnow()) {
            if (blockData.snowTicks < 20) blockData.snowTicks++;
        } else {
            if (blockData.snowTicks > 0) blockData.snowTicks--;
        }

        getBlockData().climable = blockAssesement.isClimbale();

        if (blockAssesement.isClimbale()) {
            if (blockData.climbableTicks < 20) blockData.climbableTicks++;
        } else {
            if (blockData.climbableTicks > 0) blockData.climbableTicks--;
        }

        if (blockAssesement.isWeb()) {
            if (blockData.webTicks < 20) blockData.webTicks++;
        } else {
            if (blockData.webTicks > 0) blockData.webTicks--;
        }

        if (blockAssesement.isSoulSand()) {
            if (blockData.soulSandTicks < 20) blockData.soulSandTicks++;
            getBlockData().lastSoulSand = System.currentTimeMillis();
        } else {
            if (blockData.soulSandTicks > 0) blockData.soulSandTicks--;
        }

        if (blockAssesement.isHalfblock()) {
            if (blockData.halfBlockTicks < 20) blockData.halfBlockTicks++;
        } else {
            if (blockData.halfBlockTicks > 0) blockData.halfBlockTicks--;
        }

        if (blockAssesement.isLiquid()) {
            if (blockData.liquidTicks < 100) blockData.liquidTicks++;
        } else {
            if (blockData.liquidTicks > 0) blockData.liquidTicks--;
        }


        getBlockData().ice = blockAssesement.isOnIce();

        if (blockAssesement.isOnIce() || blockAssesement.isOnNearIce()) {
            if (blockData.iceTicks < 20) blockData.iceTicks++;
            blockData.lastIce = System.currentTimeMillis();
        } else {
            if (blockData.iceTicks > 0) blockData.iceTicks--;
        }

        if (blockAssesement.isStair()) {
            if (blockData.stairTicks < 20) blockData.stairTicks++;
        } else {
            if (blockData.stairTicks > 0) blockData.stairTicks--;
        }

        if (blockAssesement.isSlab()) {
            if (blockData.slabTicks < 20) blockData.slabTicks++;
        } else {
            if (blockData.slabTicks > 0) blockData.slabTicks--;
        }

        if (blockAssesement.isFence()) {
            if (blockData.fenceTicks < 20) blockData.fenceTicks++;
        } else {
            if (blockData.fenceTicks > 0) blockData.fenceTicks--;
        }


        if (blockAssesement.isRail()) {
            if (blockData.railTicks < 20) blockData.railTicks++;
        } else {
            if (blockData.railTicks > 0) blockData.railTicks--;
        }

        if (blockAssesement.isSlime()) {
            blockData.lastSline = System.currentTimeMillis();
            if (blockData.slimeTicks < 20) blockData.slimeTicks++;
            blockData.lastSlimeTick = getConnectedTick();
        } else {
            if (blockData.slimeTicks > 0) blockData.slimeTicks--;
        }


        if (blockAssesement.isHalfGlass()) {
            if (blockData.glassPaneTicks < 20) blockData.glassPaneTicks++;
        } else {
            if (blockData.glassPaneTicks > 0) blockData.glassPaneTicks--;
        }

        if (blockAssesement.isDoor()) {
            if (blockData.doorTicks < 20) blockData.doorTicks++;
        } else {
            if (blockData.doorTicks > 0) blockData.doorTicks--;
        }


        getMovementData().setAirTicks(airTicks);
        getMovementData().setGroundTicks(groundTicks);
    }

    public boolean generalCancel() {
        return wasFlying || getPlayer().isFlying() || getPlayer().getAllowFlight() || getPlayer().getGameMode().equals(GameMode.CREATIVE) || (Overflow.getInstance().getVersionUtil().getVersion() == VersionUtil.Version.V1_8 && getPlayer().getGameMode().equals(GameMode.SPECTATOR));
    }

    public boolean isUsingOptifine() {
        return optifineSmoothing > 0 || optifineSmoothingFix > 2 || optifineConstantVL2 > 5;
    }

    public void debug(String s, String check, String checkType) {
        getPlayer().sendMessage(ChatColor.GRAY + "[" + ChatColor.RED + "Debug ("+check + checkType+")" + ChatColor.GRAY + "] " + ChatColor.WHITE + s);
    }

    public void debug(String s) {
        getPlayer().sendMessage(ChatColor.GRAY + "[" + ChatColor.RED + "Debug" + ChatColor.GRAY + "] " + ChatColor.WHITE + s);
    }

    public int getTick() {
        return this.connectedTick;
    }
}
