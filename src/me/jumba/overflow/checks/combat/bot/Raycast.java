package me.jumba.overflow.checks.combat.bot;

import me.jumba.overflow.Overflow;
import me.jumba.overflow.base.check.Check;
import me.jumba.overflow.base.check.CheckType;
import me.jumba.overflow.base.event.Listen;
import me.jumba.overflow.base.event.impl.PacketEvent;
import me.jumba.overflow.base.event.impl.ServerShutdownEvent;
import me.jumba.overflow.base.tinyprotocol.api.Packet;
import me.jumba.overflow.base.tinyprotocol.api.TinyProtocolHandler;
import me.jumba.overflow.base.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import me.jumba.overflow.base.user.User;
import me.jumba.overflow.util.math.MathUtil;
import me.jumba.overflow.util.time.TimeUtils;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created on 31/01/2020 Package me.jumba.sparky.checks.combat
 */
public class Raycast extends Check {
    public Raycast(String checkName, String type, CheckType checkType, boolean enabled) {
        super(checkName, type, checkType, enabled);
    }

    /*
        Raycast check - checks if the player attacks the zombie inside the player if they don't they are cheating
     */

    @Listen
    public void onPacket(PacketEvent e) {

        User user = e.getUser();

        if (user != null) {

            if (e.isPacketMovement() && user.getEntityHelper1_8().entityZombie != null && user.getCheckData().hasRaycastBot && user.getCombatData().getLastEntityAttacked() != null && Bukkit.getPlayer(user.getCombatData().getLastEntityAttacked().getUniqueId()).isOnline()) {


                if ((user.getCheckData().hasRaycastBot && TimeUtils.secondsFromLong(user.getCheckData().lastRaycastSpawn) > 20L) || (user.getCombatData().getLastEntityAttacked() instanceof Player && !Bukkit.getPlayer(user.getCombatData().getLastEntityAttacked().getUniqueId()).isOnline()) || (user.getCombatData().getLastEntityAttacked().isDead())) {
                    user.getCheckData().hasHitRaycast = false;
                    user.getCheckData().rayCastEntityRoation = 0.0;
                    user.getCheckData().rayCastStartYaw = user.getCombatData().getLastEntityAttacked().getLocation().getYaw();
                    user.getCheckData().hasHitRaycast = false;
                    remove(user);
                    return;
                }

                if (user.getCheckData().hasHitRaycast && TimeUtils.secondsFromLong(user.getCheckData().lastRaycastGood) > 5L) {
                    user.getCheckData().hasHitRaycast = false;
                }

                double yaw = user.getCheckData().rayCastStartYaw + user.getCheckData().rayCastEntityRoation;
                Location loc = user.getCombatData().getLastEntityAttacked().getLocation();

                double incressment = 0.55;

                user.getEntityHelper1_8().entityZombie.setLocation(loc.getX() + Math.sin(Math.toRadians(-yaw)) * incressment, (user.getCheckData().hasHitRaycast ? loc.getY() + 15 : (ThreadLocalRandom.current().nextBoolean() ? loc.getY() + ThreadLocalRandom.current().nextDouble(1.42f) : loc.getY())) , loc.getZ() + Math.cos(Math.toRadians(-yaw)) * incressment, (float) MathUtil.getRandomDouble(0.0f, 360.0f), (float) (loc.getPitch() + MathUtil.getRandomDouble(2.44f, 7.33f)));
                sendPacket(user, new PacketPlayOutEntityTeleport(user.getEntityHelper1_8().entityZombie));

                if (user.getEntityHelper1_8().entityPlayer2 != null) {

                    if ((System.currentTimeMillis() - user.getEntityHelper1_8().lastEntity2Attack) < 3000L) {
                        double offset = MathUtil.getRandomDouble(0.60, 1.30) + (user.getConnectedTick() % 20 == 0 ? MathUtil.getRandomDouble(1.99, 3.20) : 0.0);
                        user.getEntityHelper1_8().entityPlayer2.setLocation(user.getPlayer().getLocation().getX() + Math.sin(Math.toRadians(-yaw)) * incressment + offset + MathUtil.getRandomDouble(0.42, 0.75), user.getPlayer().getLocation().getY() + 3.42f , user.getPlayer().getLocation().getZ() + Math.cos(Math.toRadians(-yaw)) * incressment + offset, (float) MathUtil.getRandomDouble(0.0f, 360.0f), (float) (loc.getPitch() + MathUtil.getRandomDouble(2.44f, 7.33f)));

                    } else {
                        user.getEntityHelper1_8().entityPlayer2.setLocation(loc.getX(), loc.getY() + 3.42f , loc.getZ(), (float) MathUtil.getRandomDouble(0.0f, 360.0f), (float) (loc.getPitch() + MathUtil.getRandomDouble(2.44f, 7.33f)));
                    }

                    sendPacket(user, new PacketPlayOutEntityTeleport(user.getEntityHelper1_8().entityPlayer2));
                }

                if ((System.currentTimeMillis() - user.getEntityHelper1_8().lastEntity2Attack) > 1000L) {
                    if (user.getCheckData().raycastEntity2HitTimes > 0) user.getCheckData().raycastEntity2HitTimes--;
                }

                user.getCheckData().rayCastEntityRoation+=20.0;
            }

            if (e.getType().equalsIgnoreCase(Packet.Client.USE_ENTITY)) {
                WrappedInUseEntityPacket wrappedInUseEntityPacket = new WrappedInUseEntityPacket(e.getPacket(), e.getPlayer());
                if (user.getCombatData().getLastEntityAttacked().getUniqueId() != null && Bukkit.getPlayer(user.getCombatData().getLastEntityAttacked().getUniqueId()).isOnline()) {
                    if (wrappedInUseEntityPacket.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {

                        if (wrappedInUseEntityPacket.getId() == user.getEntityHelper1_8().entity2ID) {
                            if ((System.currentTimeMillis() - user.getEntityHelper1_8().lastEntity2Attack) < 500L) {
                                if (user.getCheckData().raycastEntity2HitTimes > 5) {
                                    flag(user, "t=2", "v=" + user.getCheckData().raycastEntity2HitTimes);
                                }
                                user.getCheckData().raycastEntity2HitTimes++;
                            }
                            user.getEntityHelper1_8().lastEntity2Attack = System.currentTimeMillis();
                        }

                        if (wrappedInUseEntityPacket.getEntity() != null) {
                            if (!user.getCheckData().hasRaycastBot && user.getCombatData().getLastEntityAttacked() != null) {
                                user.getCheckData().rayCastEntityRoation = 0.0;
                                user.getCheckData().rayCastStartYaw = user.getCombatData().getLastEntityAttacked().getLocation().getYaw();
                                user.getCheckData().hasHitRaycast = false;
                                user.getCheckData().rayCastFailHitTimes = 0;

                                WorldServer worldServer = ((CraftWorld) user.getPlayer().getWorld()).getHandle();
                                EntityZombie entityZombie = new EntityZombie(worldServer);
                                entityZombie.setHealth((float) MathUtil.getRandomDouble(MathUtil.getRandomDouble(1.20, 5.32), 20.0));

                                EntityZombie entityPlayer = new EntityZombie(worldServer);
                                entityPlayer.onGround = true;
                                entityPlayer.setInvisible(false);
                                entityPlayer.setHealth((float) MathUtil.getRandomDouble(MathUtil.getRandomDouble(1.20, 5.32), 20.0));
                                user.getEntityHelper1_8().entity2ID = entityPlayer.getId();

                                entityPlayer.setInvisible(true);

                                entityZombie.setInvisible(true);
                                entityZombie.onGround = true;
                                user.getCheckData().rayCastEntityID = entityZombie.getId();


                                user.getEntityHelper1_8().entityZombie = entityZombie;
                                user.getEntityHelper1_8().entityPlayer2 = entityPlayer;

                                user.getCheckData().hasRaycastBot = true;

                                user.getCheckData().lastRaycastSpawn = System.currentTimeMillis();

                                sendPacket(user, new PacketPlayOutSpawnEntityLiving(entityZombie));
                                sendPacket(user, new PacketPlayOutSpawnEntityLiving(entityPlayer));

                                sendPacket(user, new PacketPlayOutUpdateAttributes());
                            }
                        }
                    }

                    int id = wrappedInUseEntityPacket.getId();
                    if (id == user.getCheckData().rayCastEntityID) {
                        // user.debug("action: " + wrappedInUseEntityPacket.getAction().name());
                        user.getCheckData().hasHitRaycast = true;
                        user.getCheckData().lastRaycastGood = System.currentTimeMillis();
                        //     remove(user);
                        user.getCheckData().rayCastFailHitTimes = 0;
                    } else {
                        if (user.getCheckData().hasRaycastBot) {
                            if ((System.currentTimeMillis() - user.getCombatData().getLastUseEntityPacket()) < 105L) {
                                if (!user.getCheckData().hasHitRaycast) {
                                    if (user.getCheckData().rayCastFailHitTimes < 50) {
                                        user.getCheckData().rayCastFailHitTimes++;
                                    }
                                    if (user.getCheckData().rayCastFailHitTimes > 35)
                                        flag(user, "verbose=" + user.getCheckData().rayCastFailHitTimes);
                                } else {
                                    if (user.getCheckData().rayCastFailHitTimes > 0)
                                        user.getCheckData().rayCastFailHitTimes--;
                                }
                            } else {
                                if (user.getCheckData().rayCastFailHitTimes > 0)
                                    user.getCheckData().rayCastFailHitTimes--;
                            }
                        } else {
                            user.getCheckData().rayCastFailHitTimes = 0;
                        }
                    }
                }
            }
        }
    }

    private static Player getRandomPlayer(User user) {
        Player randomPlayer;
        if (Bukkit.getServer().getOnlinePlayers().size() > 1) {
            List<Player> onlinePlayers = new ArrayList<>();
            for (Player online : Bukkit.getServer().getOnlinePlayers()) {
                if (!online.getUniqueId().toString().equalsIgnoreCase(user.getPlayer().getUniqueId().toString()))
                    onlinePlayers.add(online);
            }
            randomPlayer = onlinePlayers.get(new Random().nextInt(onlinePlayers.size()));
        } else {
            randomPlayer = user.getPlayer();
        }
        return randomPlayer;
    }


    @Listen
    public void onShutdown(ServerShutdownEvent e) {
        Overflow.getInstance().getUserManager().getUsers().forEach(this::remove);
    }

    private void remove(User user) {
        if (user.getCheckData().hasRaycastBot) {
            user.getCheckData().rayCastFailHitTimes = 0;
            user.getCheckData().hasRaycastBot = false;

            user.getEntityHelper1_8().entityZombie.setPosition(user.getPlayer().getLocation().getX() *- 9999, user.getPlayer().getLocation().getY() *- 9999, user.getPlayer().getLocation().getZ() *- 9999);
            sendPacket(user, new PacketPlayOutEntityTeleport(user.getEntityHelper1_8().entityZombie));

            user.getEntityHelper1_8().entityPlayer2.setPosition(user.getPlayer().getLocation().getX() *- 9999, user.getPlayer().getLocation().getY() *- 9999, user.getPlayer().getLocation().getZ() *- 9999);
            sendPacket(user, new PacketPlayOutEntityTeleport(user.getEntityHelper1_8().entityPlayer2));

            user.getEntityHelper1_8().entityPlayer2 = null;
            user.getEntityHelper1_8().entityZombie = null;
        }
    }

    private void sendPacket(User user, net.minecraft.server.v1_8_R3.Packet packet) {
        TinyProtocolHandler.sendPacket(user.getPlayer(), packet);
    }
}
