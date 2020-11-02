package me.jumba.overflow.checks.combat.autoclicker;

import me.jumba.overflow.base.check.Check;
import me.jumba.overflow.base.check.CheckType;
import me.jumba.overflow.base.event.Listen;
import me.jumba.overflow.base.event.impl.PacketEvent;
import me.jumba.overflow.base.tinyprotocol.api.Packet;
import me.jumba.overflow.base.user.User;

/**
 * Created on 22/05/2020 Package me.jumba.sparky.checks.combat.autoclicker
 */
public class AutoClickerA extends Check {
    public AutoClickerA(String checkName, String type, CheckType checkType, boolean enabled) {
        super(checkName, type, checkType, enabled);
        setExperimental(true);
    }

    /*
        Outliers check - checks for bad clients autoclickers like vape
     */

    @Listen
    public void onPacket(PacketEvent event) {
        User user = event.getUser();
        if (user != null) {


            if (event.isPacketMovement()) {

                if (user.getCombatData().isBreakingBlock()) {
                    user.getCheckData().autoclickerGFlying = user.getCheckData().autoclickerGOutliers = -1;
                    return;
                }

                user.getCheckData().autoclickerGFlying++;
            }

            if (event.getType().equalsIgnoreCase(Packet.Client.ARM_ANIMATION) || event.getType().equalsIgnoreCase(Packet.Client.BLOCK_DIG)) {
                if (user.getCheckData().autoclickerGFlying > 3 && user.getCheckData().autoclickerGFlying < 10) {
                    user.getCheckData().autoclickerGOutliers++;
                }

                int currentOutliers = user.getCheckData().autoclickerGOutliers;
                int diff = Math.abs(currentOutliers - user.getCheckData().lastAutoclickerGOutlier);

                if (currentOutliers == 0 && diff == currentOutliers) {

                    if (user.getCheckData().autoclickerGThreshold > 16) {
                        flag(user, "threshold="+user.getCheckData().autoclickerGThreshold, "outlier="+currentOutliers, "diffOutlier="+diff);
                    }

                    user.getCheckData().autoclickerGThreshold+=0.10f;
                } else {
                    user.getCheckData().autoclickerGThreshold = 0.0f;
                }

                user.getCheckData().lastAutoclickerGOutlier = currentOutliers;
                user.getCheckData().autoclickerGFlying = user.getCheckData().autoclickerGOutliers = 0;
            }
        }
    }
}
