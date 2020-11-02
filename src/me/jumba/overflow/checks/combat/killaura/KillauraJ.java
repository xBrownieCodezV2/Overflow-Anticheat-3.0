package me.jumba.overflow.checks.combat.killaura;

import me.jumba.overflow.base.check.Check;
import me.jumba.overflow.base.check.CheckType;
import me.jumba.overflow.base.event.Listen;
import me.jumba.overflow.base.event.impl.PacketEvent;
import me.jumba.overflow.base.tinyprotocol.api.Packet;
import me.jumba.overflow.base.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import me.jumba.overflow.base.user.User;

/**
 * Created on 18/04/2020 Package me.jumba.sparky.checks.combat.killaura
 */
public class KillauraJ extends Check {
    public KillauraJ(String checkName, String type, CheckType checkType, boolean enabled) {
        super(checkName, type, checkType, enabled);
    }

    /*
        Thanks elevated!! - liquidbounce check
     */

    @Listen
    public void onPakcet(PacketEvent e) {
        User user = e.getUser();
        if (user != null) {

            if (e.isPacketMovement()) {
                user.getCheckData().killauraJTicks++;
            }


            if (e.getType().equalsIgnoreCase(Packet.Client.USE_ENTITY)) {
                WrappedInUseEntityPacket wrappedInUseEntityPacket = new WrappedInUseEntityPacket(e.getPacket(), e.getPlayer());
                if (wrappedInUseEntityPacket.getEntity() != null && wrappedInUseEntityPacket.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {

                    if (user.getCheckData().killauraJTicks < 8) {
                        if (user.getCheckData().killauraJLastTick == user.getCheckData().killauraJTicks) {
                            ++user.getCheckData().killauraJBuffer;
                        }

                        if (++user.getCheckData().killauraJTotalTicks == 25) {


                            if (user.getCheckData().killauraJBuffer > 22) {
                                flag(user, "t=1", "buffer="+user.getCheckData().killauraJBuffer);
                            }

                            if (user.getCheckData().killauraJBuffer > 15) {
                                if (++user.getCheckData().killauraJStreak > 1) {
                                    flag(user, "t=2", "buffer="+user.getCheckData().killauraJBuffer, "streak="+user.getCheckData().killauraJStreak);
                                }
                            } else {
                                user.getCheckData().killauraJStreak = 0;
                            }

                            user.getCheckData().killauraJBuffer = 0;
                            user.getCheckData().killauraJTotalTicks = 0;
                        }
                    }

                    user.getCheckData().killauraJLastTick = user.getCheckData().killauraJTicks;
                    user.getCheckData().killauraJTicks = 0;
                }
            }
        }
    }
}
