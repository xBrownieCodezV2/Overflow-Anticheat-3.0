package me.jumba.overflow.util.processor;

import me.jumba.overflow.Overflow;
import me.jumba.overflow.base.event.impl.PacketEvent;
import me.jumba.overflow.base.tinyprotocol.api.Packet;
import me.jumba.overflow.base.tinyprotocol.api.TinyProtocolHandler;
import me.jumba.overflow.base.tinyprotocol.packet.in.WrappedInBlockDigPacket;
import me.jumba.overflow.base.tinyprotocol.packet.in.WrappedInBlockPlacePacket;
import me.jumba.overflow.base.tinyprotocol.packet.in.WrappedInKeepAlivePacket;
import me.jumba.overflow.base.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import me.jumba.overflow.base.tinyprotocol.packet.out.WrappedOutKeepAlivePacket;
import me.jumba.overflow.base.user.User;
import me.jumba.overflow.util.block.BlockUtil;
import me.jumba.overflow.util.math.MathUtil;
import me.jumba.overflow.util.time.TimeUtils;
import me.jumba.overflow.util.version.VersionUtil;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created on 02/02/2020 Package me.jumba.sparky.util.processor
 */
public class PredictionProcessor {
    private User user;

    public PredictionProcessor(User user) {
        this.user = user;
    }

    private boolean fMath = false;

    public List<Integer> keepAliveList = new ArrayList<>();

    public double lmotionX, lmotionY, lmotionZ = 0;
    public double rmotionX, rmotionY, rmotionZ = 0;
    public boolean fastMath, walkSpecial, lastVelocity, lastSprint = false;
    public boolean useSword, useItem, hitPlayer, dropItem, velocity, hitall = false;

    public long lastUseItem, itemWaitPredict;

    private double lastSpeedMotion;
    public int speedPotionShit;

    public float moveForward, moveStrafe;

    public String pressKey = "";


    public void update(PacketEvent event) {

        try {

            if (user.getMiscData().isSwitchedGamemodes() || user.getCombatData().isRespawn() || TimeUtils.secondsFromLong(user.getCombatData().getLastRespawn()) < 3L)
                return;

            if (user.getPlayer().isFlying() || user.getPlayer().getAllowFlight() || user.getPlayer().getGameMode() == GameMode.CREATIVE || (Overflow.getInstance().getVersionUtil().getVersion() != VersionUtil.Version.V1_7 && user.getPlayer().getGameMode().equals(GameMode.SPECTATOR))) {
                return;
            }

            if (!user.getCheckData().motionXZReaady) {
                user.getCheckData().motionXZReaady = TimeUtils.secondsFromLong(user.getTimestamp()) > 5L;
                return;
            }

            String pt = event.getType();


            if (event.getDirection() == PacketEvent.Direction.CLIENT) {
                if (user.getPlayer().getItemInHand().getType().isEdible() || isPotion(user.getPlayer().getItemInHand())) {
                    boolean attack = (System.currentTimeMillis() - user.getCombatData().getLastEntityDamageAttack()) < 1000L;
                    useItem = (System.currentTimeMillis() - lastUseItem) < (6000L) && !user.getMovementData().isSprinting() && (MathUtil.trim(1, user.getMovementData().getMovementSpeed()) < 0.2 || attack) || user.getMovementData().isBlocking() || user.getPlayer().isBlocking();
                }
            }

            if (pt.equals(Packet.Server.POSITION)) {
                sendKeepAlive();
            }

            if (pt.equalsIgnoreCase(Packet.Server.ENTITY_VELOCITY)) {
                WrappedInUseEntityPacket wrappedInUseEntityPacket = new WrappedInUseEntityPacket(pt, user.getPlayer());
                if (wrappedInUseEntityPacket.getId() == user.getPlayer().getEntityId()) {
                    sendKeepAlive();
                }
            }

            if (pt.equalsIgnoreCase(Packet.Client.KEEP_ALIVE)) {
                WrappedInKeepAlivePacket wrappedInKeepAlivePacket = new WrappedInKeepAlivePacket(pt, user.getPlayer());
                int value = (int) wrappedInKeepAlivePacket.getTime();
                if (keepAliveList.contains(value)) {
                    keepAliveList.remove(value);
                    velocity = true;
                }
            }

            if (pt.equals(Packet.Client.HELD_ITEM_SLOT)) {
                useSword = false;
                useItem = false;
            }


            if (pt.equalsIgnoreCase(Packet.Client.BLOCK_PLACE)) {
                WrappedInBlockPlacePacket wrappedInBlockPlacePacket = new WrappedInBlockPlacePacket(event.getPacket(), user.getPlayer());
                if (wrappedInBlockPlacePacket.getPosition().getX() == -1 && wrappedInBlockPlacePacket.getPosition().getY() == -1 && wrappedInBlockPlacePacket.getPosition().getZ() == -1) {
                    itemWaitPredict = System.currentTimeMillis();
                    if (wrappedInBlockPlacePacket.getItemStack().getType().name().toLowerCase().contains("sword") || wrappedInBlockPlacePacket.getItemStack().getType().name().toLowerCase().contains("bow")) {
                        if (!hitall) {
                            useSword = true;
                        }
                    } else {
                        useItem = true;
                    }
                }
            }

            if (pt.equalsIgnoreCase(Packet.Client.USE_ENTITY)) {
                WrappedInUseEntityPacket wrappedInUseEntityPacket = new WrappedInUseEntityPacket(event.getPacket(), user.getPlayer());

                if (wrappedInUseEntityPacket.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {
                    hitall = true;
                    if (wrappedInUseEntityPacket.getEntity() != null && wrappedInUseEntityPacket.getEntity() instanceof Player)
                        hitPlayer = true;
                }
            }

            if (pt.equalsIgnoreCase(Packet.Client.BLOCK_DIG)) {
                WrappedInBlockDigPacket wrappedInBlockDigPacket = new WrappedInBlockDigPacket(event.getPacket(), user.getPlayer());

                useItem = false;
                useSword = false;

                if (wrappedInBlockDigPacket.getAction() == WrappedInBlockDigPacket.EnumPlayerDigType.RELEASE_USE_ITEM) {
                    useItem = false;
                    useSword = false;
                } else if (wrappedInBlockDigPacket.getAction() == WrappedInBlockDigPacket.EnumPlayerDigType.DROP_ITEM) {
                    dropItem = true;
                }
            }


            if (pt.equals(Packet.Client.POSITION_LOOK) || pt.equals(Packet.Client.POSITION) || pt.equals(Packet.Client.FLYING)
                    || pt.equals(Packet.Client.LOOK)) {
                rmotionX = (user.getMovementData().getTo().getX() - user.getMovementData().getFrom().getX());
                rmotionY = (user.getMovementData().getTo().getY() - user.getMovementData().getFrom().getY());
                rmotionZ = (user.getMovementData().getTo().getZ() - user.getMovementData().getFrom().getZ());

                fMath = fastMath;

                try {
                    if (!pt.equals(Packet.Client.FLYING) && !pt.equals(Packet.Client.LOOK)) {
                        if (!walkSpecial && user.getMovementData().getTo().getYaw() != 0.0F) {
                            if (checkConditions()) {
                                calc(hitPlayer);
                            }
                        }
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }

                if (dropItem) {
                    useItem = false;
                    useSword = false;
                }

                dropItem = false;

                double multiplier = 0.9100000262260437D;

                rmotionX *= multiplier;
                rmotionZ *= multiplier;

                if (user.getMovementData().isLastOnGround()) {
                    multiplier = 0.60000005239967D;
                    rmotionX *= multiplier;
                    rmotionZ *= multiplier;
                }

                if (Math.abs(rmotionX) < 0.005D)
                    rmotionX = 0.0D;
                if (Math.abs(rmotionY) < 0.005D)
                    rmotionY = 0.0D;
                if (Math.abs(rmotionZ) < 0.005D)
                    rmotionZ = 0.0D;

                lmotionX = rmotionX;
                lmotionY = rmotionY;
                lmotionZ = rmotionZ;

                hitPlayer = false;
                hitall = false;
                lastVelocity = velocity;
                velocity = false;

                walkSpecial = checkSpecialBlock();
                fastMath = fMath;
                lastSprint = user.getMovementData().isSprinting();
            }
        } catch (Exception ignored) {}
    }

    public boolean checkSpecialBlock() {
        if (user.getMovementData().isClientGround() || user.getMovementData().isLastClientGround()) {
            try {
                final Material lBlock = Objects.requireNonNull(BlockUtil.getBlock(new Location(user.getPlayer().getWorld(), user.getMovementData().getFrom().getX(), user.getMovementData().getFrom().getY() - 0.43, user.getMovementData().getFrom().getZ()))).getType();
                final Material block = Objects.requireNonNull(BlockUtil.getBlock(new Location(user.getPlayer().getWorld(), user.getMovementData().getTo().getX(), user.getMovementData().getTo().getY() - 0.10, user.getMovementData().getTo().getZ()))).getType();
                if (block == Material.ICE || block == Material.PACKED_ICE || block == Material.SLIME_BLOCK
                        || lBlock == Material.ICE || lBlock == Material.PACKED_ICE || lBlock == Material.SLIME_BLOCK || user.getBlockData().iceTicks > 0 || user.getBlockData().soulSandTicks > 0 || user.getBlockData().slime || user.getBlockData().slimeTicks > 0) {
                    return true;
                }
            } catch (Exception e2) {
            }
        }
        return false;
    }

    boolean checkConditions() {
        try {
            if (user.getMovementData().getFrom().getX() == 0 && user.getMovementData().getFrom().getY() == 0 && user.getMovementData().getFrom().getZ() == 0) {
                return false;
            }

            if (user.getMovementData().isLastOnGround() && !user.getMovementData().isClientGround())
                return false;

            if (rmotionX == 0 && rmotionZ == 0 && user.getMovementData().isClientGround())
                return false;

            if (Math.hypot(lmotionX, lmotionZ) > 11)
                return false;
            if (Math.hypot(user.getMovementData().getTo().getX() - user.getMovementData().getFrom().getX(), user.getMovementData().getTo().getZ() - user.getMovementData().getFrom().getZ()) > 10)
                return false;

            if (user.getPlayer().getGameMode() == GameMode.SPECTATOR) {
                return false;
            }

            if (user.getPlayer().isFlying()) {
                return false;
            }

            final Material m = Objects.requireNonNull(BlockUtil.getBlock(new Location(user.getPlayer().getWorld(), user.getMovementData().getTo().getX(), user.getMovementData().getTo().getY(), user.getMovementData().getTo().getZ()))).getType();

            if (m == Material.LADDER) {
                return false;
            }

            if (m == Material.VINE) {
                return false;
            }

            if (m == Material.STATIONARY_WATER) {
                return false;
            }

            if (m == Material.WATER) {
                return false;
            }

            if (m == Material.LAVA) {
                return false;
            }

            if (m == Material.STATIONARY_LAVA) {
                return false;
            }
        } catch (Exception ignored) {}

        return true;
    }

    @SuppressWarnings("unused")
    void calc(final boolean hit) {
        boolean flag = true;
        int precision = String.valueOf((int) Math.abs(Math.hypot(user.getMovementData().getTo().getX(), user.getMovementData().getTo().getZ()))).length();
        precision = 15 - precision;
        double preD = Double.valueOf("0.5E-" + precision);

        if (user.getMovementData().isSprinting() && hit) {
            lmotionX *= 0.6D;
            lmotionZ *= 0.6D;
        }

        double mx = rmotionX - lmotionX;
        double mz = rmotionZ - lmotionZ;

        float motionYaw = (float) (Math.atan2(mz, mx) * 180.0D / Math.PI) - 90.0F;

        int direction = 6;

        motionYaw -= user.getMovementData().getTo().getYaw();

        while (motionYaw > 360.0F)
            motionYaw -= 360.0F;
        while (motionYaw < 0.0F)
            motionYaw += 360.0F;

        motionYaw /= 45.0F;

        float moveS = 0.0F;
        float moveF = 0.0F;
        String key = "-";

        if (Math.abs(Math.abs(mx) + Math.abs(mz)) > preD) {
            direction = (int) new BigDecimal(motionYaw).setScale(1, RoundingMode.HALF_UP).doubleValue();

            if (direction == 1) {
                moveF = 1F;
                moveS = -1F;
                key = "W + D";
            } else if (direction == 2) {
                moveS = -1F;
                key = "D";
            } else if (direction == 3) {
                moveF = -1F;
                moveS = -1F;
                key = "S + D";
            } else if (direction == 4) {
                moveF = -1F;
                key = "S";
            } else if (direction == 5) {
                moveF = -1F;
                moveS = 1F;
                key = "S + A";
            } else if (direction == 6) {
                moveS = 1F;
                key = "A";
            } else if (direction == 7) {
                moveF = 1F;
                moveS = 1F;
                key = "W + A";
            } else if (direction == 8) {
                moveF = 1F;
                key = "W";
            } else if (direction == 0) {
                moveF = 1F;
                key = "W";
            }
            pressKey = key;
        }

        moveF *= 0.98F;
        moveS *= 0.98F;


        String diffString = "";
        double diff = -1337;
        double closestdiff = 1337;

        int loops = 0;
        found:
        for (int fastLoop = 2; fastLoop > 0; fastLoop--) {
            fastMath = fastLoop == 2 ? fMath : !fMath;
            {
                boolean isBlocking = useSword;
                if (useItem)
                    isBlocking = true;

                loops++;

                float moveStrafing = moveS;
                float moveForward = moveF;

                if (isBlocking) {
                    moveForward *= 0.2F;
                    moveStrafing *= 0.2F;
                }

                if (user.getMovementData().isSneaking()) {
                    if (user.getMovementData().isSprinting()) return;
                    moveForward *= 0.3F;
                    moveStrafing *= 0.3F;
                }


                float jumpMovementFactor = 0.02F;
                if (lastSprint) {
                    jumpMovementFactor = 0.025999999F;
                }

                float var5;
                float var3 = 0.54600006F;

                float getAIMoveSpeed = 0.1F;
                if (user.getMovementData().isSprinting()) getAIMoveSpeed = 0.13000001F;
                float var4 = 0.16277136F / (var3 * var3 * var3);

                if (user.getMovementData().isLastOnGround()) {
                    var5 = getAIMoveSpeed * var4;
                } else {
                    var5 = jumpMovementFactor;
                }

                double motionX = lmotionX;
                double motionZ = lmotionZ;

                float var14 = moveStrafing * moveStrafing + moveForward * moveForward;
                if (var14 >= 1.0E-4F) {
                    var14 = sqrt_float(var14);
                    if (var14 < 1.0F)
                        var14 = 1.0F;
                    var14 = var5 / var14;
                    moveStrafing *= var14;
                    moveForward *= var14;

                    final float var15 = sin(user.getMovementData().getTo().getYaw() * (float) Math.PI / 180.0F);
                    final float var16 = cos(user.getMovementData().getTo().getYaw() * (float) Math.PI / 180.0F);
                    motionX += (double) (moveStrafing * var16 - moveForward * var15);
                    motionZ += (double) (moveForward * var16 + moveStrafing * var15);

                    this.moveForward = moveForward;
                    this.moveStrafe = moveStrafing;
                }

                final double diffX = rmotionX - motionX;
                final double diffZ = rmotionZ - motionZ;

                diff = Math.hypot(diffX, diffZ);

                diff = roundToDouble(diff, precision + 2);
                //  diffString = roundToString(diff, precision + 2);

                double parse = Double.parseDouble(roundToString(closestdiff, precision + 2));

                user.getMovementData().setPredictedDiff(diff);

                boolean bad = user.getBlockData().soulSandTicks > 0 || user.getBlockData().liquidTicks > 0 || (System.currentTimeMillis() - user.getMovementData().getLastFullBlockMoved()) > 500L || user.getBlockData().blockAboveTicks > 0 || user.getBlockData().webTicks > 0 || user.getBlockData().halfBlockTicks > 0 || (System.currentTimeMillis() - user.getMovementData().getLastJunpPadUpdate()) < 1000L || user.getMovementData().isJumpPad() || user.getMovementData().isCollidesHorizontally() || user.getBlockData().stairTicks > 0 || user.getBlockData().slabTicks > 0 || !user.getCheckData().motionXZReaady || user.getBlockData().fenceTicks > 0;

                double gay = MathUtil.trim(3, diff);


                if (user.getMovementData().isClientGround() && user.getMovementData().isLastOnGround() && user.getMovementData().getAirTicks() < 1) {
                    if (user.getMiscData().getSpeedPotionTicks() > 0) {
                        if (gay > 0.0) {
                            if (lastSpeedMotion == gay) {
                                if (lastSpeedMotion == gay) {
                                    if (speedPotionShit < 20) speedPotionShit++;
                                }
                            } else {
                                if (speedPotionShit > 0) speedPotionShit -= 5;
                            }

                            lastSpeedMotion = gay;
                        }
                    } else {
                        speedPotionShit = 0;
                    }
                } else speedPotionShit = 0;


                if (diff < preD || bad) {
                    flag = false;
                    fMath = fastMath;
                    break found;
                }

                if (diff < closestdiff) {
                    closestdiff = diff;
                }
            }
        }


        user.getMovementData().setInvalidMotionXZ(flag);
        if ((System.currentTimeMillis() - user.getMovementData().getLastFullBlockMoved()) < 320L) {
            user.getCheckData().lastMotionAPredictionDiff = Math.abs(preD - diff);
        } else {
            user.getCheckData().lastMotionAPredictionDiff = 1.0E-5;
        }
    }

    public boolean isStrafing() {
        return pressKey.equalsIgnoreCase("A") || pressKey.equalsIgnoreCase("D") || pressKey.equalsIgnoreCase("S");
    }

    public BigDecimal round(double value, int i) {
        return new BigDecimal(value).setScale(i, RoundingMode.HALF_UP);
    }

    public double roundToDouble(double value, int i) {
        return round(value, i).doubleValue();
    }

    public String roundToString(double value, int i) {
        return round(value, i).toPlainString();
    }

    private static final float[] SIN_TABLE_FAST = new float[4096];
    private static final float[] SIN_TABLE = new float[65536];

    public float sin(float p_76126_0_) {
        return fastMath ? SIN_TABLE_FAST[(int) (p_76126_0_ * 651.8986F) & 4095]
                : SIN_TABLE[(int) (p_76126_0_ * 10430.378F) & 65535];
    }

    public float cos(float p_76134_0_) {
        return fastMath ? SIN_TABLE_FAST[(int) ((p_76134_0_ + ((float) Math.PI / 2F)) * 651.8986F) & 4095]
                : SIN_TABLE[(int) (p_76134_0_ * 10430.378F + 16384.0F) & 65535];
    }

    static {
        int i;

        for (i = 0; i < 65536; ++i) {
            SIN_TABLE[i] = (float) Math.sin((double) i * Math.PI * 2.0D / 65536.0D);
        }

        for (i = 0; i < 4096; ++i) {
            SIN_TABLE_FAST[i] = (float) Math.sin((double) (((float) i + 0.5F) / 4096.0F * ((float) Math.PI * 2F)));
        }

        for (i = 0; i < 360; i += 90) {
            SIN_TABLE_FAST[(int) ((float) i * 11.377778F) & 4095] = (float) Math
                    .sin((double) ((float) i * 0.017453292F));
        }
    }

    public static float sqrt_float(float p_76129_0_) {
        return (float) Math.sqrt((double) p_76129_0_);
    }

    public static float sqrt_double(double p_76133_0_) {
        return (float) Math.sqrt(p_76133_0_);
    }

    public void sendKeepAlive() {
        int id = 233 + user.getPlayer().getEntityId() + ThreadLocalRandom.current().nextInt(999);
        TinyProtocolHandler.sendPacket(user.getPlayer(), new WrappedOutKeepAlivePacket(id).getObject());
    }

    private boolean isPotion(ItemStack item) {

        try {
            Potion.fromItemStack(item);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }
}
