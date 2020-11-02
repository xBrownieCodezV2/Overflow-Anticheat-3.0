package me.jumba.overflow.checks.movement.invalid;

import me.jumba.overflow.base.check.Check;
import me.jumba.overflow.base.check.CheckType;
import me.jumba.overflow.base.event.Listen;
import me.jumba.overflow.base.event.impl.PacketEvent;
import me.jumba.overflow.base.user.User;

/**
 * Created on 19/04/2020 Package me.jumba.sparky.checks.movement.invalid
 */
public class InvalidP extends Check {
    public InvalidP(String checkName, String type, CheckType checkType, boolean enabled) {
        super(checkName, type, checkType, enabled);
    }

    /*
        Accesions check - checks if the player is flying up illegally
     */

    @Listen
    public void onPacket(PacketEvent e) {
        if (e.isPacketMovement()) {
            User user = e.getUser();
            if (user != null) {


            }
        }
    }
}
