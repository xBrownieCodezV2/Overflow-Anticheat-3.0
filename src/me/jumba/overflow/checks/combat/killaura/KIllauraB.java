package me.jumba.overflow.checks.combat.killaura;

import me.jumba.overflow.base.check.Check;
import me.jumba.overflow.base.check.CheckType;
import me.jumba.overflow.base.event.Listen;
import me.jumba.overflow.base.event.impl.PacketEvent;
import me.jumba.overflow.base.user.User;
import me.jumba.overflow.util.math.MathUtil;
import org.bukkit.entity.Player;

/**
 * Created on 07/01/2020 Package me.jumba.sparky.checks.combat.killaura
 */
public class KIllauraB extends Check {
    public KIllauraB(String checkName, String type, CheckType checkType, boolean enabled) {
        super(checkName, type, checkType, enabled);
    }

    /*
        Detects keep-sprints in most kill-auras
     */

    @Listen
    public void onPacket(PacketEvent e) {
        User user = e.getUser();
        if (user != null) {

            if (e.isPacketMovement()) {
                if (user.getCombatData().getLastEntityAttacked() != null && user.getCombatData().getLastEntityAttacked() instanceof Player && (System.currentTimeMillis() - user.getCombatData().getLastUseEntityPacket()) < 150L) {
                    double baseSpeed = MathUtil.getBaseSpeed(user.getPlayer());
                    double speed = user.getMovementData().getMovementSpeed();

                    if (user.generalCancel()) {
                        user.getCheckData().getKillauraBVerbose().setVerbose(0);
                        return;
                    }

                    if (user.getBlockData().blockAboveTicks < 1 && user.getBlockData().iceTicks < 1 && user.getMovementData().isSprinting() && user.getMovementData().isOnGround() && user.getMovementData().isLastOnGround()) {
                        if (speed > baseSpeed) {
                            if (user.getCheckData().getKillauraBVerbose().flag(15, 510L)) {
                                flag(user, "speed="+MathUtil.trim(2, speed), "baseSpeed="+baseSpeed, "verbose="+user.getCheckData().getKillauraBVerbose().getVerbose());
                            }
                        } else {
                            user.getCheckData().getKillauraBVerbose().takeaway();
                        }
                    } else {
                        user.getCheckData().getKillauraBVerbose().setVerbose(0);
                    }
                }
            }
        }
    }
}
