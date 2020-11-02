package me.jumba.overflow.checks.combat.killaura;

import me.jumba.overflow.base.check.Check;
import me.jumba.overflow.base.check.CheckType;
import me.jumba.overflow.base.event.Listen;
import me.jumba.overflow.base.event.impl.PacketEvent;
import me.jumba.overflow.base.tinyprotocol.api.Packet;
import me.jumba.overflow.base.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import me.jumba.overflow.base.user.User;

/**
 * Created on 17/05/2020 Package me.jumba.sparky.checks.combat.killaura
 */
public class KillauraK extends Check {
    public KillauraK(String checkName, String type, CheckType checkType, boolean enabled) {
        super(checkName, type, checkType, enabled);
    }

    @Listen
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        if (user != null) {

            if (event.getType().equalsIgnoreCase(Packet.Client.USE_ENTITY)) {
                WrappedInUseEntityPacket wrappedInUseEntityPacket = new WrappedInUseEntityPacket(event.getPacket(), event.getPlayer());

                if (wrappedInUseEntityPacket.getEntity() != null && wrappedInUseEntityPacket.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {
                    double pitchDiff = (user.getMovementData().getTo().getPitch() - user.getMovementData().getFrom().getPitch());


                }
            }
        }
    }
}
