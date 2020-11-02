package me.jumba.overflow.util.processor;

import me.jumba.overflow.base.user.User;
import me.jumba.overflow.util.math.MathUtil;
import me.jumba.overflow.util.time.TimeUtils;

/**
 * Created on 21/10/2019 Package me.jumba.processor
 */
public class OptifineProcessor {

    //Cancer ik stfu and deal with it

    private long time;

    public void update(User user) {
        if ((System.currentTimeMillis() - time) >= 50L) {
            time = System.currentTimeMillis();
            updateOpti(user);
            processSmoother(user);
            updateOptifine(user);
        }
    }

    public void updateOpti(User user) {
        if ((System.currentTimeMillis() - user.lastOptifine) > 2000L) {
            if (user.optifineSmoothing2 > 0) user.optifineSmoothing2--;
            user.optifineSmoothing = 0;
        }
        if ((System.currentTimeMillis() - user.getMovementData().getLastFullBlockMoved()) >= 1000L) {
            user.optifineSameCount = 0;
            user.optifineSmoothing2 = 0;
            user.optifineSmoothing = 0;
        }
    }

    public void processSmoother(User user) {

        float yawDelta = MathUtil.getDelta(user.getMovementData().getTo().getYaw(), user.getMovementData().getFrom().getYaw()), pitchDelta = MathUtil.getDelta(user.getMovementData().getTo().getPitch(), user.getMovementData().getFrom().getPitch());
        float smooth = user.newYawSmoothing.smooth(yawDelta, convertToMouseDelta(user.lastYawDelta)), smooth2 = user.newPitchSmoothing.smooth(pitchDelta, convertToMouseDelta(pitchDelta));
        double yaw = Math.abs(smooth - user.lastSmoothYaw), pitch = Math.abs(smooth2 - user.lastSmoothPitch1);
        if (String.valueOf(yawDelta).contains("E")) {
            user.lastRetardOpitfineSpam = System.currentTimeMillis();
        }
        if ((yaw > 0.0 && yaw < 0.09) || (pitch > 0.0 && pitch < 0.12)) {
            if (user.optifineConstantVL < 30) user.optifineConstantVL++;
            if (user.optifineSmoothingFix < 30) user.optifineSmoothingFix++;
        } else {
            if (user.optifineConstantVL > 0) user.optifineConstantVL--;
            if (user.optifineSmoothingFix > 0) user.optifineSmoothingFix -= 5;
        }
        if (user.optifineConstantVL > 9) {
            if (user.optifineConstantVL2 < 30) user.optifineConstantVL2++;
        } else {
            if (user.optifineConstantVL2 > 0) user.optifineConstantVL2--;
        }
        if (user.optifineConstantVL2 > 5) {
            user.lastOptifineREE = System.currentTimeMillis();
        }
        user.lastOptifinePitchSmoothidfklol = convertToMouseDelta(Math.abs(smooth - user.lastSmoothYaw));
        user.lastSmoothPitch1 = smooth2;
        user.lastSmoothYaw = smooth;
        user.lastYawDelta = yawDelta;
        user.lastPitchDelta = pitchDelta;
        user.newPitchSmoothing.reset();
        user.newYawSmoothing.reset();
    }

    public void updateOptifine(User user) {
        int ace = (user.killauraAYawReset > 2 ? (user.killauraAPitchReset * user.killauraAYawReset) : user.killauraAYawReset);
        if (ace > 3) {
            user.lastAimAssistACE = System.currentTimeMillis();
        }
        if (TimeUtils.elapsed(user.lastAimAssistACE) <= 100L) {
            if (user.aimAssistsACount > 0) user.aimAssistsACount--;
        }
        float yawDelta = MathUtil.getDelta(user.getMovementData().getTo().getYaw(), user.getMovementData().getFrom().getYaw()), pitchDelta = MathUtil.getDelta(user.getMovementData().getTo().getPitch(), user.getMovementData().getFrom().getPitch());
        float yawShit = convertToMouseDelta(yawDelta), pitchShit = convertToMouseDelta(pitchDelta);
        float smooth = user.yaw.smooth(yawShit, yawShit * 0.05f), smooth2 = user.pitch.smooth(pitchShit, pitchShit * 0.05f);

        user.cineCamera = MathUtil.getDelta(smooth, yawShit) < 0.08f || MathUtil.getDelta(smooth2, pitchShit) < 0.04f;
        if (user.cineCamera) {
            user.optifineSmoothingTicks++;
        } else if (user.optifineSmoothingTicks > 0) {
            user.optifineSmoothingTicks--;
        }
        if (user.getMovementData().getTo().getPitch() == user.getMovementData().getFrom().getPitch()) {
            user.killauraAPitchReset++;
        } else {
            user.killauraAPitchReset = 0;
        }
        if (user.getMovementData().getTo().getYaw() == user.getMovementData().getFrom().getYaw() || Math.abs(user.getMovementData().getTo().getYaw() - user.getMovementData().getFrom().getYaw()) == 0.0) {
            if (user.killauraAYawReset < 20) user.killauraAYawReset++;
        } else {
            if (user.killauraAYawReset > 0) user.killauraAYawReset--;
        }
        float yawDelta1 = Math.abs(user.getMovementData().getFrom().getYaw() - user.getMovementData().getTo().getYaw()), pitchDelta1 = Math.abs(user.getMovementData().getFrom().getYaw() - user.getMovementData().getTo().getYaw());
        float yaw = convertToMouseDelta(yawDelta1), pitch = convertToMouseDelta(pitchDelta1);
        float smoothing = ((float) Math.cbrt((yawDelta1 / 0.15f) / 8f) - 0.2f) / .6f;
        float smoothingpitchDelta = ((float) Math.cbrt((yawDelta1 / 0.15f) / 8f) - 0.2f) / .6f;
        float smooth1 = user.smooth.smooth(yaw, pitch * 0.05f);
        if ((Math.abs(user.lastSmoothingRot - smoothingpitchDelta) < 1F)) {
            if (user.optifinePitchSmooth < 20) user.optifinePitchSmooth++;
        } else {
            user.optifinePitchSmooth = 0;
        }
        boolean smoothing1 = (Math.abs(user.lastSmoothingRot - smoothing) < 0.1);
        boolean smoothing2 = (Math.abs(smooth1 - smoothing) > 0.2 && smoothing > 1.2);
        if ((smoothing1 || smoothing2) && MathUtil.looked(user.getMovementData().getFrom(), user.getMovementData().getTo())) {
            if (MathUtil.looked(user.getMovementData().getFrom(), user.getMovementData().getTo()) && user.getMovementData().getFrom().getYaw() != user.getMovementData().getTo().getYaw() && user.getMovementData().getFrom().getPitch() != user.getMovementData().getTo().getPitch()) {
                if (user.optifineSmoothSens < 20) user.optifineSmoothSens++;
            } else {
                if (user.optifineSmoothSens > 0) user.optifineSmoothSens--;
            }
        } else {
            if (user.optifineSmoothSens > 0) user.optifineSmoothSens--;
        }
        if (Math.abs(user.smoothingCounter - user.LastSmoothingCounter) > 0) {
            user.smoothingCounter = 0;
        }
        if (user.optifineSameCount > 2) {
            user.optifineSmoothing = 0;
        }
        if (user.smoothingCounter > 4) {
            if (user.lastClientSmoothingValue == user.smoothingCounter) {
                user.optifineSameCount += 2;
            } else {
                if (user.optifineSameCount > 0) user.optifineSameCount--;
            }
            user.lastClientSmoothingValue = user.smoothingCounter;
            user.lastOptifine = System.currentTimeMillis();
            if (user.optifineSmoothing < 30) user.optifineSmoothing++;
        } else {
            if (user.optifineSmoothing > 0) user.optifineSmoothing -= 2;
        }
        if (smoothing1 || smoothing2) {
            if (MathUtil.looked(user.getMovementData().getFrom(), user.getMovementData().getTo()) && user.getMovementData().getFrom().getYaw() != user.getMovementData().getTo().getYaw() && user.getMovementData().getFrom().getPitch() != user.getMovementData().getTo().getPitch()) {
                user.smoothingCounter++;
            } else {
                if (user.smoothingCounter > 0) user.smoothingCounter--;
            }
            user.LastSmoothingCounter = user.smoothingCounter;
        } else {
            user.smoothingCounter = 0;
        }
        user.lastSmoothingRot = smoothing;
        user.lastSmoothingRot2 = smoothingpitchDelta;

    }

    private float convertToMouseDelta(float value) {
        return ((float) Math.cbrt((value / 0.15f) / 8f) - 0.2f) / .6f;
    }


}
