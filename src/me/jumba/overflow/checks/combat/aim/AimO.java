package me.jumba.overflow.checks.combat.aim;

import me.jumba.overflow.base.check.Check;
import me.jumba.overflow.base.check.CheckType;
import me.jumba.overflow.base.event.Listen;
import me.jumba.overflow.base.event.impl.PacketEvent;
import me.jumba.overflow.base.tinyprotocol.packet.in.WrappedInFlyingPacket;
import me.jumba.overflow.base.user.User;
import me.jumba.overflow.util.minecraft.MathHelper;

/**
 * Created on 08/04/2020 Package me.jumba.sparky.checks.combat.aim
 */
public class AimO extends Check {
    public AimO(String checkName, String type, CheckType checkType, boolean enabled) {
        super(checkName, type, checkType, enabled);
    }

    @Listen
    public void onPacket(PacketEvent e) {
        if (e.isPacketMovement()) {
            User user = e.getUser();
            if (user != null) {

                if (user.getMiscData().isHasSetClientSensitivity()) {

                    WrappedInFlyingPacket wrappedInFlyingPacket = new WrappedInFlyingPacket(e.getPacket(), e.getPlayer());

                    if (wrappedInFlyingPacket.isPos()) {

                        float var132 = (float) (user.getMiscData().getClientSensitivity() * 0.6F + 0.2F);
                        float var141 = var132 * var132 * var132 * 8.0F;
                        float var15 = (float) user.getMovementData().getMouseDeltaX() * var141;
                        float var16 = (float) user.getMovementData().getMouseDeltaY() * var141;
                        byte var18 = 1;

                        double prediction = Math.abs((var15 - user.getCheckData().aimOLastRotationYaw));

                        setAngles(var15, var16 * (float) var18, user);
                    }
                }
            }
        }
    }

    public void setAngles(float yaw, float pitch, User user) {
        float f = user.getMovementData().getFrom().getPitch();
        float f1 = user.getMovementData().getFrom().getYaw();
        user.getCheckData().aimOLastRotationYaw  = (float) ((double) user.getCheckData().aimOLastRotationYaw  + (double) yaw * 0.15D);
        user.getCheckData().aimOLastRotationPitch = (float) ((double) user.getCheckData().aimOLastRotationPitch - (double) pitch * 0.15D);
        user.getCheckData().aimOLastRotationPitch = MathHelper.clamp_float(user.getCheckData().aimOLastRotationPitch, -90.0F, 90.0F);
    }
}
