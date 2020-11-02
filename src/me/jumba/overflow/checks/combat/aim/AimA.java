package me.jumba.overflow.checks.combat.aim;

import me.jumba.overflow.base.check.Check;
import me.jumba.overflow.base.check.CheckType;
import me.jumba.overflow.base.event.Listen;
import me.jumba.overflow.base.event.impl.PacketEvent;
import me.jumba.overflow.base.tinyprotocol.api.Packet;
import me.jumba.overflow.base.user.User;

/**
 * Created on 06/01/2020 Package me.jumba.sparky.checks.combat.aim
 */
public class AimA extends Check {
    public AimA(String checkName, String type, CheckType checkType, boolean enabled) {
        super(checkName, type, checkType, enabled);
    }

    /*
        Compares packet differences and looks for strange patterns in the packets being sent
         to determine if the user is using something that is modifying their aim (this also affects the way packets are sent)
     */

    @Listen
    public void onPacket(PacketEvent e) {
        User user = e.getUser();
        if (user != null) {
            if (e.isPacketMovement()) {

                /*
                    NOTE:
                        This check works well without a verbose but sometimes randomly false flags, so i added a verbose of 20 to fix that (20 to make sure otherwise it will spam off)
                 */



                //Simple time check for checking is the player is attacking (can be improved)
                boolean isAttacking = ((System.currentTimeMillis() - user.getCombatData().getLastUseEntityPacket()) < 235L);

                //Checking for WTapping
                if (isAttacking) {

                    double yawDiff = Math.abs(user.getMovementData().getTo().getYaw() - user.getMovementData().getFrom().getYaw());
                    double pitchDiff = Math.abs(user.getMovementData().getTo().getPitch() - user.getMovementData().getFrom().getPitch());

                    //Checks for W-Tapping

                    if (user.getConnectedTick() % 20 == 0) {
                        if (yawDiff == 0.0f || pitchDiff == 0.0f) {
                            user.getCheckData().aimAStableTicks1 = 50;
                        } else {
                            if (user.getCheckData().aimAStableTicks1 > 0) user.getCheckData().aimAStableTicks1--;
                        }
                    }

                    double max = 1.50f;
                    if (Math.abs(e.getTo().getYaw() - e.getFrom().getYaw()) > max || Math.abs(e.getTo().getPitch() - e.getFrom().getPitch()) > max) {
                        if (user.getCheckData().aimAStableTicks < 50) user.getCheckData().aimAStableTicks+=2;
                    } else {
                        if (user.getCheckData().aimAStableTicks > 0) user.getCheckData().aimAStableTicks--;
                    }
                }

                if (user.getCheckData().aimAStableTicks1 > 0) return;

                //Flag method here
                if (user.getCheckData().aimAStableTicks > 6 && user.getCheckData().aimAPosLookBal > 45 && user.getCheckData().aimAPosBal > 46 && user.getCheckData().aimAPosLookBal >= user.getCheckData().aimAPosBal && (System.currentTimeMillis() - user.getCheckData().lastAimAReset) < 1299L && user.getCheckData().aimAVerbose.flag(20, 999L)) {
                    flag(user, "LP-Bal=" + user.getCheckData().aimAPosLookBal, "P-Bal=" + user.getCheckData().aimAPosBal, "verbose="+user.getCheckData().aimAVerbose.getVerbose(), "time=" + (System.currentTimeMillis() - user.getCheckData().lastAimAReset));
                }


                if (e.getType().equalsIgnoreCase(Packet.Client.POSITION_LOOK)) {
                    if (isAttacking) {
                        if (user.getCheckData().aimAPosBal > 0) user.getCheckData().aimAPosBal--;
                        if (user.getCheckData().aimAPosLookBal < 50) user.getCheckData().aimAPosLookBal++;

                        //Set the finder for the pattern finder
                        if (!user.getCheckData().aimAExpected && user.getCheckData().aimAPosLookSent > 3) {
                            user.getCheckData().aimAExpected = true;
                        }
                        user.getCheckData().aimAPosLookSent++;
                    }

                } else if (e.getType().equalsIgnoreCase(Packet.Client.POSITION)) {
                    if (isAttacking) {
                        if (user.getCheckData().aimAPosBal < 50) user.getCheckData().aimAPosBal+=4;
                        if (user.getCheckData().aimAPosLookBal > 0) user.getCheckData().aimAPosLookBal--;

                        //Look for patterns of aura
                        if (user.getCheckData().aimAExpected && user.getCheckData().aimAPosSent > 3) {
                            user.getCheckData().aimAExpected = false;
                            user.getCheckData().aimAPosLookSent = user.getCheckData().aimAPosSent = 0;
                            user.getCheckData().lastAimAReset = System.currentTimeMillis();
                        }
                        user.getCheckData().aimAPosSent++;
                    }

                } else if (e.getType().equalsIgnoreCase(Packet.Client.FLYING)) {
                    //Check if the player is AFK or just sending this packets
                    //TODO: Create a check that checks for "Packet.Client.FLYING" and "Packet.Client.POSITION_LOOK" within the same tick.

                    if (user.getCheckData().aimAPosBal > 0) user.getCheckData().aimAPosBal--;
                    if (user.getCheckData().aimAPosLookBal > 0) user.getCheckData().aimAPosLookBal--;

                } else if (e.getType().equalsIgnoreCase(Packet.Client.LOOK) && user.isUsingOptifine()) {
                    //Check if the player is using optifine

                    if (user.getCheckData().aimAPosBal > 0) user.getCheckData().aimAPosBal--;
                    if (user.getCheckData().aimAPosLookBal > 0) user.getCheckData().aimAPosLookBal--;
                }
            }
        }
    }
}
