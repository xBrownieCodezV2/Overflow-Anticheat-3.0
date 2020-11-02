package me.jumba.overflow.checks.combat.killaura;

import me.jumba.overflow.base.check.Check;
import me.jumba.overflow.base.check.CheckType;
import me.jumba.overflow.base.event.Listen;
import me.jumba.overflow.base.event.impl.PacketEvent;
import me.jumba.overflow.base.tinyprotocol.api.Packet;
import me.jumba.overflow.base.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import me.jumba.overflow.base.user.User;
import org.bukkit.entity.Player;

/**
 * Created on 19/01/2020 Package me.jumba.sparky.checks.combat.killaura
 */
public class KillauraE extends Check {
    public KillauraE(String checkName, String type, CheckType checkType, boolean enabled) {
        super(checkName, type, checkType, enabled);
    }

    /*
        Interact autoblock check - checks if the player sends 'INTERACT' & 'INTERACT_AT' while blocking and attacking
     */

    @Listen
    public void onPacket(PacketEvent e) {
        User user = e.getUser();
        if (user != null) {

            if (e.getType().equalsIgnoreCase(Packet.Client.BLOCK_DIG)) {
                user.getCheckData().lastKillauraEBlockDig = System.currentTimeMillis();
            }

            if (e.getType().equalsIgnoreCase(Packet.Client.USE_ENTITY)) {
                WrappedInUseEntityPacket wrappedInUseEntityPacket = new WrappedInUseEntityPacket(e.getPacket(), e.getPlayer());
                if (wrappedInUseEntityPacket.getEntity() != null && wrappedInUseEntityPacket.getEntity() instanceof Player) {

                    if (wrappedInUseEntityPacket.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.INTERACT) {
                        user.getCheckData().lastKillauraEInteract = System.currentTimeMillis();
                    }

                    if (wrappedInUseEntityPacket.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.INTERACT_AT) {
                        user.getCheckData().lastKillauraEInteractAT = System.currentTimeMillis();
                    }

                    if (wrappedInUseEntityPacket.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {

                        if (user.getCombatData().isBreakingBlock() || !user.getMiscData().isSword(user.getPlayer().getInventory().getItemInHand())) {
                            user.getCheckData().killauraEVerbose.setVerbose(0);
                            return;
                        }

                        long lastBlockDig = (System.currentTimeMillis() - user.getCheckData().lastKillauraEBlockDig);
                        long lastInteract = (System.currentTimeMillis() - user.getCheckData().lastKillauraEInteract);
                        long lastInteractAT = (System.currentTimeMillis() - user.getCheckData().lastKillauraEInteractAT);

                        if (lastBlockDig < 500L) {
                            if (lastInteract > 400L && lastInteractAT > 400L) {
                                if (user.getCheckData().killauraEVerbose.flag(8, 999L)) {
                                    flag(user, "lastBlockDig="+lastBlockDig, "lastInteract="+lastInteract, "lastInteractAT="+lastInteractAT, "verbose="+user.getCheckData().killauraEVerbose.getVerbose());
                                }
                            } else {
                                user.getCheckData().killauraEVerbose.takeaway();
                            }
                        }
                    }
                }
            }
        }
    }


}
