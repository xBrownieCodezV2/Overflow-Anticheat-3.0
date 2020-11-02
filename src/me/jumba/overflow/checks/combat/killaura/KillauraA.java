package me.jumba.overflow.checks.combat.killaura;

import me.jumba.overflow.base.check.Check;
import me.jumba.overflow.base.check.CheckType;
import me.jumba.overflow.base.event.Listen;
import me.jumba.overflow.base.event.impl.PacketEvent;
import me.jumba.overflow.base.user.User;
import me.jumba.overflow.util.math.MathUtil;
import org.bukkit.Bukkit;

/**
 * Created on 06/01/2020 Package me.jumba.sparky.checks.combat.killaura
 */
public class KillauraA extends Check {
    public KillauraA(String checkName, String type, CheckType checkType, boolean enabled) {
        super(checkName, type, checkType, enabled);
    }

    private double offset = Math.pow(2, 24);

    /*
        GCD Constancy check - was made for LiquidBounce, Aim A flags it faster
     */

    @Listen
    public void onPacket(PacketEvent e) {
        User user = e.getUser();
        if (user != null) {
            if (e.isPacketMovement()) {

                if (user.getCombatData().getCancelTicks() > 3 || user.getMovementData().getAirTicks() > 2 || (user.getCombatData().getLastEntityAttacked() != null && Bukkit.getPlayer(user.getCombatData().getLastEntityAttacked().getUniqueId()).isOnline())) {
                    return;
                }

                int t1 = user.getCheckData().killauraTicksUp, t2 = user.getCheckData().killauraTicksDown;
             //   double offset = Math.pow(2, 24);
                long pitchDiffer = (long) (user.getCheckData().lastKillauraPitch * offset);
                long gcf = GCF((long) Math.abs(e.getTo().getPitch() - e.getFrom().getPitch()), pitchDiffer);

                long lcm = LCM((long) Math.abs(e.getTo().getPitch() - e.getFrom().getPitch()), pitchDiffer);

                if (Math.abs(e.getTo().getPitch() - e.getFrom().getPitch()) > 3 && Math.abs(e.getTo().getYaw() - e.getFrom().getYaw()) > 0.20) {
                    long diff = (gcf - lcm);
                    if (diff < 1) {
                        if (t1 > 0) t1--;
                        if (t2 < 100) t2++;
                    } else {
                        if (t1 < 100) t1++;
                        if (t2 > 0) t2 -= 2;
                    }

                    if (t1 > 2) {
                        t1 = 0;
                        t2 = 0;
                    }
                } else {
                    if (t1 > 0) t1--;
                    if (t2 > 0) t2--;
                }

                if ((System.currentTimeMillis() - user.getCombatData().getLastUseEntityPacket()) > 1000L || (System.currentTimeMillis() - user.getMovementData().getLastFullBlockMoved()) > 1000L) {
                    if (t1 > 0) t1--;
                    if (t2 > 0) t2--;
                }

                user.getCheckData().killauraTicksUp = t1;
                user.getCheckData().killauraTicksDown = t2;
                float pitch = MathUtil.getDistanceBetweenAngles(e.getTo().getPitch(), user.getCheckData().lastKillauraPitch);
                long p1 = (long) (pitch * offset), p2 = (long) (user.getCheckData().lastKillauraPitch * offset), gcdPitch = MathUtil.gcd(p1, p2);

                long diff = Math.abs(gcdPitch - user.getCheckData().lastKillauraAPitchGCD);

                if ((System.currentTimeMillis() - user.getMovementData().getLastFullBlockMoved()) > 500L) {
                    if (user.getCheckData().killauraAVerbose > 0) user.getCheckData().killauraAVerbose -= 6;
                }


                if (Math.abs(e.getTo().getPitch() - e.getFrom().getPitch()) > 3 && Math.abs(e.getTo().getYaw() - e.getFrom().getYaw()) > 1.05) {
                    if (diff > 0 && diff < 920L) {
                        if (user.getCheckData().killauraAVerbose < 100) user.getCheckData().killauraAVerbose += 10;
                    } else {
                        if (user.getCheckData().killauraAVerbose > 0) user.getCheckData().killauraAVerbose--;
                    }

                    if (user.getCheckData().killauraAVerbose > 14 && t2 > 2) {
                        //2 - changed from that to 5
                        if (user.getCheckData().killauraAVerbose1.flag(7, 999L)) {
                            flag(user, "Verbose=" + user.getCheckData().killauraAVerbose, "t1=" + t1, "t2=" + t2, "GCD=" + gcdPitch, "GCF=" + gcf, "LCM=" + lcm);
                        }
                    }


                } else {
                    if (user.getCheckData().killauraAVerbose > 0) user.getCheckData().killauraAVerbose--;
                }

                user.getCheckData().lastKillauraAPitchGCD = gcdPitch;
                user.getCheckData().lastKillauraAYaw = e.getTo().getYaw();
                user.getCheckData().lastKillauraPitch = e.getTo().getPitch();
            }
        }
    }

    /**
     * Java method to calculate lowest common multiplier of two numbers
     *
     * @param a
     * @param b
     * @return LCM of two numbers
     */
    public static long LCM(long a, long b) {
        return (a * b) / GCF(a, b);
    }

    /**
     * Java method to calculate greatest common factor of two numbers
     *
     * @param a
     * @param b
     * @return GCF of two numbers using Euclid's algorithm
     */
    public static long GCF(long a, long b) {
        if (b == 0) {
            return a;
        } else {
            return (GCF(b, a % b));
        }
    }
}
