package me.jumba.overflow.base.user.sub;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import me.jumba.overflow.base.user.User;
import me.jumba.overflow.checks.combat.aim.AimL;
import me.jumba.overflow.util.location.PastLocation;
import me.jumba.overflow.util.math.RollingAverageDouble;
import me.jumba.overflow.util.math.Verbose;
import me.jumba.overflow.util.math.evicting.EvictingList;
import me.jumba.overflow.util.time.Interval;
import me.jumba.overflow.util.time.Timer;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * Created on 06/01/2020 Package me.jumba.sparky.base.user.sub
 */
@Getter
@Setter
public class CheckData {

    public float lastAimAssistAPitch, aimOLastRotationYaw, aimOLastRotationPitch, aimMLastYaw, lastBadPacketsCFallDistance, lastKillauraPitch, lastKillauraAYaw, lastAimAPitchDiff;
    public long lastTimerAMS, pingSpoofALastTransactionSent, lastAutoClickerCheckE, lastAutoClickerTimerE, autocliekrALastClick, lastAimAssistGCD, lastKillauraHBad, lastPredictionBot, lastMotionBlock, lastMotionBJump, timerALastTime, lastInvalidOFall, lastInvalidMReset, motionALastJump, lastScaffoldBJump, speedHLastJump, lastGCD, lastAimMPossibleLag, lastAimMPostion, lastNoneMoveSpeedG, lastInventoryAInventoryClose, lastKillauraHPostionLook, lastMotionASpeedPotion, lastInvalidCFallBlock, lastAimFPos, lastAimILCM, lastUnknownValidTeleport, lastKillauraFBlockDig, lastKillauraFAttack, lastKillauraEInteractAT, lastKillauraEInteract, lastExploitBKeepAlive, lastExpectedVelocityA, lastVelocityATransactionSent, lastFreecamMove, lastFreecamFlying, lastTimerB, lastTimerA, lastInvalidMotioNXZCheck, lastBotMove, lastEntityBotHit, lastEntitySpawn, lastRaycastSpawn, lastRaycastGood, lastAimEPos, lastAimEPosLook, lastInvalidDCount, lastKillauraFInteractPacket, lastKillauraFInteractATPacket, lastKillauraFUseEntityPacket, lastKillauraFBlockPacket, lastAutoClickerASwing, lastSpeedASet, lastKillauraEUseEntity, lastKillauraEBlockDig, lastKillauraDUseEntity, lastKillauraDSwing, lastKillauraCUseEntity, lastKillauraCSwing, badPacketsDUp, badPacketsDDown, lastBadLandLocationInvalidA, lastKillauraAPitchGCD, lastAimAReset;
    public int aimAssistBThreshold2, aimAssistBThreshold, autoclickerABuffer, autoclickerATicks, pingSpoofAVerbose, timerAMountTick, lastAutoclickerGOutlier, autoclickerGClicks, autoclickerGOutliers, autoclickerGFlying, autoclickerETicks, aimassistBYawInvalidJolt, aimassistBThrehsold, aimassistBLastUseEntityTick, motionAStartSprintTick, motionAStopSprintTick, aimLInvalid, autoClickerDTicks, autoClickerCTicks, autoClickerACPSTicks, autoClickerATicks, aimAStableTicks1, kys, raycastEntity2HitTimes, killauraKVerbose, killauraKLastTicks, killauraKTicksTotal, killauraKTicks, motionALastUseEntityTick, motionBLastLength, flightETotalFlags, killauraJStreak, killauraJTotalTicks, killauraJBuffer, killauraJTicks, killauraJLastTick, badPacketsKLastUseEntityTick, flightABuffer, killauraISwings, killauraIAttacks, speedHTicks, aimLLength, aimJPredict1, aimJPredict2, aimMVerbose3, aimMStable, aimMVerbose2, aimMVerbose, aimMPostion, aimMPostionLook, strafeBHops2, strafeBHops, strafeAFlags, aimKThreshold2, aimKThreshold, lastAimKPostionTick, lastAimJPostionTick, lastAimJPostionLookTick, badPachetsHClientGroundTicks, lastInvalidCBlockY, aimFOptifineTicks, aimIAimTicks, aimAStableTicks, stableMotionACount, stableMotionADecrease, motionASamePrediction, reachAVerbose, lastInvalidHFlightStat, lastInvalidHJumpStat, lastInvalidGFlightStat, lastInvalidFJumpStat, invalidEValidTicks, killauraGLastBlockPlaceTick, entityATotalAttacks, entityAReportedFlags, invalidEBlocksMoved, killauraDTick, exploitBTransactionQueueSize, speedHVerbose2, lastVelocityATransactionPing, velocityAAirTicks, badPacketsETotalSlots, freeCamTeleportTicks, timerBVerbose, randomBotDamageTicks, randomBotSwingTicks, movedBotTicks, botTicks, entityHitTime, entityBotID, rayCastFailHitTimes, rayCastEntityID, aimEValidation, invalidDPossible, invalidDupdateTicks, autoClickerAVerbose, speedGVerbose, speedAVerbose2, lastSpeedAVerbose, invalidDLastStage, invalidDStage, badPacketsELastSlot, invalidCThreshold, noSlowAVerbose, aimBVerbose, invalidAVerbose, invalidAFallTicks, killauraTicksUp, killauraTicksDown, killauraAVerbose, aimAPosLookBal, aimAPosBal, aimAPosLookSent, aimAPosSent, speedAVerbose, speedBVerbose, badPacketsBVerbose, badPacketsBStable, flightADistance, flightBVerbose;
    public boolean timerAMount, lastFromGround, motionIsLastSprinting, lastSprintVelocity, aimAssistAWork, invalidPIsReady, invalidDAllowCheck, invalidOAllowedDamage, badPacketsCReset, killauraDArmSwong, lastExploitAClientGround, invalidDLastGround, invalidDGround, timerAReady, motionARetarded, badPacketsCReady, badPacketsCDamaged, motionXZReaady, moveBot, hasBot, hasHitRaycast, hasRaycastBot, lastSpeedGOnGround, speedGOnGround, speedBLastGroundLocation, badPacketsDYInvalid, aimAExpected, speedBLastGround;
    public double aimAssistBLastYaw2, aimAssistBLastYaw, timerAVerboseBuffer, motionCLastDelta, autoclickerGThreshold, autoClickerEVerbose, autoClickerEVerbose3, lastACESpeed, aimassistBCertanity, aimassistBlastPitch, aimassistBlastYaw, motionAWTappingThreshold, deltaXZVelocity, autoClickerBCPSTicks, autoClickerBTicks, autoclickerAThreshold, lastAutoclickerAdv, lastAutoclickerASTD, lastAutoclickerRatio, lastFixerIDK, lastAimAssistAYaw, lastKillauraKSTD, aimOTollerance, lastAimOPrediction, motionBLastPrediction, motionBThreshold, invalidPLastGroundPostiomY, invalidMThreshold, invalidLThreshold, lastInvalidLGroundPostionY, lastAimODiff, killauraILastDiff, jesusAVerbose, aimLDev, aimLLastOffset, aimLThreshold, lastAimMGay, lastAimMDiff2, lastAimMDiff, lastDirectionStrafeB, lastBadPacketsDPostionY, lastStrafeBSpeed, lastStrafeADistance, flightDLastDevide, speedGLastMovementSpeed, aimJThreshold, inventoryAClosedThreshold, inventoryAThreshold, speedFThreshold, lastAimIPitch, lastMotionAPrediction, motionAThreshold, lastMotionAPredictionDiff, invalidHThreshold, invalidFThreshold, speedGThreshold, lastSpeedHPredictX, lastSpeedHPredictZ, speedHPredictX, speedHPredictZ, lastSpeedHAirSpeed, invalidEThreshold, exploitBThreshold, exploitAThreshold, speedH3Threshold, invalidDThreshold, invalidDDistance, timerBBal, timerABal, rayCastSpinSize, rayCastStartYaw, rayCastEntityRoation, lastSpeedHSpeed, lastAimDPitch, invalidDLastEntityYaw, invalidDLastSpeed, invalidDLastX, invalidDLastZ, lastKillauraFRolling, lastKillauraFPitchDiff, lastAutoClickerAAdv, autoclickerAdvDevation, autoclickerAdvClicks, lastSpeedGDistance, lastSpeedFSpeed, lastSpeedESpeed, lastAimCYawDiff, lastAimBPitchDiff, lastSpeedBDtance, lastBadPacketsBY, badPacketsBDiff, lastFlighBDiff, lastSpeedBDistance;
    public Verbose velocityAVerbose = new Verbose(), badPacketsDVerbose = new Verbose(), motionCVerbose = new Verbose(), autoClickerEVerbose2 = new Verbose(), velocityCVerbose = new Verbose(), velocityBVerbose = new Verbose(), aimAssistVerbose = new Verbose(), flightCVerbose1 = new Verbose(), flightEVerbose = new Verbose(), timerAVerbose = new Verbose(), timerALagVerbose = new Verbose(), badPacketsIVerbose = new Verbose(), invalidMVerbose = new Verbose(), killauraIVerbose = new Verbose(), scaffoldBVerbose = new Verbose(), aimMVerbose21 = new Verbose(), strafeBVerbose = new Verbose(), strafeAVerbose2 = new Verbose(), flightDVerbose = new Verbose(), aimLVerbose = new Verbose(), invalidJVerbose = new Verbose(), killauraHVerbose = new Verbose(), killaursCVerbose = new Verbose(), aimFVerbose = new Verbose(), aimHVerbose = new Verbose(), aimGVerbose = new Verbose(), killauraFVerbose = new Verbose(), speedHVerbose = new Verbose(), flightCVerbose = new Verbose(), badPacketsHVerbose = new Verbose(), motionBVerbose = new Verbose(), motionXZVerbose = new Verbose(), hitBoxAVerbose = new Verbose(), aimDVerbose = new Verbose(), aimCVerbose = new Verbose(), speedFVerbose = new Verbose(), speedEVerbose = new Verbose(), killauraEVerbose = new Verbose(), killauraDVerbose = new Verbose(), invalidBVerbose = new Verbose(), noSlowAVerbose1 = new Verbose(), killauraAVerbose1 = new Verbose(), aimAVerbose = new Verbose(), killauraBVerbose = new Verbose(), speedCVerbose = new Verbose(), badpacketsCVerbose = new Verbose();
    public RollingAverageDouble rollingAverageDouble = new RollingAverageDouble(20, 0.0D), killauraFRolling = new RollingAverageDouble(100, 0.0D), autoClickerERate = new RollingAverageDouble(40, 50.0);
    public PastLocation hitBoxPastLocations = new PastLocation();
    public short velocityATransactionID;
    public Timer invalidETimer = new Timer();
    public PastLocation reachALocations = new PastLocation(), reachBPastLocations = new PastLocation();
    public Deque<Float> rotationNigger = new LinkedList<>();
    public List<Double> reachBValues = new ArrayList<>();
    public List<Long> aimAssistA = new ArrayList<>();
    public List<AimL.Data> aimLData = new ArrayList<>();
    public Interval autoclickerAInv = new Interval(100);
    public List<Double> autoClickerBPattern = Lists.newArrayList();
    public LinkedList<Integer> autoClickerCCount = new LinkedList<>(), autoClickerDCount = new LinkedList<>();
    public LinkedList<Double> aimassistBJoltYawList = new LinkedList<>(), aimassitBPitchJoltList = new LinkedList<>();
    public EvictingList<Long> timerASampleTimes = new EvictingList(10);

    public CheckData(User user) {
        badPacketsELastSlot = user.getPlayer().getInventory().getHeldItemSlot();
        invalidETimer.reset();
    }
}
