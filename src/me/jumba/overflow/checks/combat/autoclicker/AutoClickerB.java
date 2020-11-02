package me.jumba.overflow.checks.combat.autoclicker;

import me.jumba.overflow.base.check.Check;
import me.jumba.overflow.base.check.CheckType;
import me.jumba.overflow.base.event.Listen;
import me.jumba.overflow.base.event.impl.PacketEvent;
import me.jumba.overflow.base.tinyprotocol.api.Packet;
import me.jumba.overflow.base.user.User;
import me.jumba.overflow.hook.HookManager;

/**
 * Created on 14/06/2020 Package me.jumba.overflow.checks.combat.autoclicker
 */
public class AutoClickerB extends Check {
    public AutoClickerB(String checkName, String type, CheckType checkType, boolean enabled) {
        super(checkName, type, checkType, enabled);
    }

    @Listen
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        if (user != null) {

            boolean isAbove1_8 = event.getUser().getCurrentClientVersion() != HookManager.Helper.Versions.V1_7 && event.getUser().getCurrentClientVersion() != HookManager.Helper.Versions.V1_8;

            if (isAbove1_8) return;

            if (event.isPacketMovement()) {
                if (user.getCheckData().autoclickerABuffer++ == 20) {
                    if (user.getCheckData().autoclickerATicks > 20) {
                        flag(user, "cps="+user.getCheckData().autoclickerATicks);
                    }
                    user.getCheckData().autoclickerABuffer = user.getCheckData().autoclickerATicks = 0;
                }
            }

            if (event.getType().equalsIgnoreCase(Packet.Client.ARM_ANIMATION)) {
                if (user.getCombatData().isBreakingBlock()) {
                    user.getCheckData().autoclickerATicks = 0;
                } else {
                    user.getCheckData().autoclickerATicks++;
                }
            }
        }
    }
}
