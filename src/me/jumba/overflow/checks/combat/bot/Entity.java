package me.jumba.overflow.checks.combat.bot;

import com.mojang.authlib.GameProfile;
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
import me.jumba.overflow.util.entity.BotUtils;
import me.jumba.overflow.util.math.MathUtil;
import me.jumba.overflow.util.time.TimeUtils;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created on 31/01/2020 Package me.jumba.sparky.checks.combat
 */
public class Entity extends Check {
    public Entity(String checkName, String type, CheckType checkType, boolean enabled) {
        super(checkName, type, checkType, enabled);
    }

    private Random rnd = new Random();

    @Listen
    public void onPacket(PacketEvent e) {

        User user = e.getUser();

        if (user != null) {

            if (e.isPacketMovement() && user.getEntityHelper1_8().entityPlayer != null) {

                if ((System.currentTimeMillis() - user.getCheckData().lastEntityBotHit) > 500L) {
                    if (user.getCheckData().entityHitTime > 0) user.getCheckData().entityHitTime--;
                }

                if (user.getCheckData().entityHitTime > 7 && (System.currentTimeMillis() - user.getCheckData().lastEntityBotHit) < 320L) {
                   if (user.getForcedUser() == null) {
                       flag(user, "verbose=" + user.getCheckData().entityHitTime);
                   } else {
                       user.getCheckData().entityAReportedFlags++;
                   }
                }

                if (TimeUtils.secondsFromLong(user.getCheckData().lastEntitySpawn) > 10L) {
                    if (user.getForcedUser() != null && user.getForcedUser().getPlayer().isOnline()) {
                        user.getForcedUser().getPlayer().sendMessage(ChatColor.GRAY + "Bot report for " + ChatColor.RED + user.getPlayer().getName());
                        user.getForcedUser().getPlayer().sendMessage(ChatColor.GRAY + "Sample taken: " + ChatColor.GREEN + TimeUtils.secondsFromLong(user.getCheckData().lastEntitySpawn) + "s");
                        user.getForcedUser().getPlayer().sendMessage(ChatColor.GRAY + "Total Attacks: " + ChatColor.GREEN + user.getCheckData().entityATotalAttacks);
                        user.getForcedUser().getPlayer().sendMessage(ChatColor.GRAY + "Total Valid Attacks (Flags): " + ChatColor.GREEN + user.getCheckData().entityAReportedFlags);
                        user.getForcedUser().getPlayer().sendMessage(ChatColor.GRAY + "Prediction:");
                        user.getForcedUser().getPlayer().sendMessage(ChatColor.GRAY + " - " + (user.getCheckData().entityAReportedFlags > 3 ? ChatColor.RED + "Cheating" : (user.getViolation() > 5 ? ChatColor.YELLOW + "Possibly Legit" : ChatColor.GREEN + "Legit")));
                    }
                    remove(user);
                    user.setForcedUser(null);
                    user.getCheckData().entityATotalAttacks = 0;
                    user.getCheckData().entityAReportedFlags = 0;
                    user.setWaitingForBot(false);
                    return;
                }

                Location loc = BotUtils.getBehind(user.getPlayer(), (user.getCheckData().moveBot ? (!(user.getMovementData().getTo().getPitch() < -21.00f) ? -0.10 : -2.90) : -2.90));

                if (user.getBotCheckType() == CheckTypes.WATCHDOG) loc = user.getPlayer().getLocation();

                boolean random = ThreadLocalRandom.current().nextBoolean();
                double offset = (random ? MathUtil.getRandomDouble(0.20, 0.55) : 0.0);
                boolean hit = ((System.currentTimeMillis() - user.getCheckData().lastEntityBotHit) < 122L);


                if (user.getCheckData().botTicks > 50) {
                    if (!user.getCheckData().moveBot) {
                        user.getCheckData().moveBot = true;
                    }
                    user.getCheckData().botTicks = 0;
                }

                if (user.getCheckData().moveBot && user.getCheckData().movedBotTicks > 20) {
                    user.getCheckData().movedBotTicks = 0;
                    user.getCheckData().moveBot = false;
                }

                if (user.getBotCheckType() == CheckTypes.NORMAL) {
                    user.getEntityHelper1_8().entityPlayer.setLocation(loc.getX() + offset, ((hit || user.getCheckData().moveBot) && !(user.getMovementData().getTo().getPitch() < -6.00f) ? loc.getY() + 3.42 : loc.getY() + (random && MathUtil.getRandomInteger(1, 20) < 15 ? MathUtil.getRandomDouble(0.10, 0.99) : 0.0)), loc.getZ() - offset, (float) (loc.getYaw() + MathUtil.getRandomDouble(0.10f, 0.50f)), (float) MathUtil.getRandomDouble(-90.0f, 90.0f));
                } else if (user.getBotCheckType() == CheckTypes.WATCHDOG) {
                    double incressment = MathUtil.getRandomDouble(0.95, 1.40);
                    user.getEntityHelper1_8().entityPlayer.setLocation(loc.getX() + Math.sin(Math.toRadians(-(user.getEntityAStartYaw() + user.getEntityAMovementOffset()))) * incressment, loc.getY() + (ThreadLocalRandom.current().nextBoolean() ? (ThreadLocalRandom.current().nextBoolean() ? MathUtil.getRandomDouble(0.35f, 0.42f) : 0.42f) : 0.0f), loc.getZ() + Math.cos(Math.toRadians(-(user.getEntityAStartYaw() + user.getEntityAMovementOffset()))) * incressment, (float) (loc.getYaw() + MathUtil.getRandomDouble(0.10f, 0.50f)), (float) MathUtil.getRandomDouble(-90.0f, 90.0f));
                } else if (user.getBotCheckType() == CheckTypes.FOLLOW) {
                    if (Math.abs(user.getEntityAFollowDistance()) > 1.20) {
                        user.setEntityAFollowDistance(user.getEntityAFollowDistance() + 0.05f);
                    }

                    double yaw = 0.0f, amount = -user.getEntityAFollowDistance();
                    yaw = Math.toRadians(yaw);
                    double dX = -Math.sin(yaw) * amount;
                    double dZ = Math.cos(yaw) * amount;

                    user.getEntityHelper1_8().entityPlayer.setLocation(loc.getX() + dX, user.getMovementData().getTo().getY(), loc.getZ() + dZ, user.getMovementData().getTo().getYaw(), user.getMovementData().getTo().getPitch());
                }

                sendPacket(user, new PacketPlayOutEntityTeleport(user.getEntityHelper1_8().entityPlayer), user.getForcedUser());

                if (!user.getCheckData().moveBot) user.getCheckData().botTicks++;
                else user.getCheckData().movedBotTicks++;
                user.getCheckData().randomBotSwingTicks++;
                user.getCheckData().randomBotDamageTicks++;
                user.setEntityAMovementOffset(user.getEntityAMovementOffset() + 20.0f);
            }

            if (e.getType().equalsIgnoreCase(Packet.Client.USE_ENTITY)) {
                WrappedInUseEntityPacket wrappedInUseEntityPacket = new WrappedInUseEntityPacket(e.getPacket(), e.getPlayer());
                if (wrappedInUseEntityPacket.getEntity() != null && wrappedInUseEntityPacket.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {
                    if (user.getViolation() >= 3) {
                        spawnBot(user);
                    }
                }

                if (user.getCheckData().hasBot && wrappedInUseEntityPacket.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK && wrappedInUseEntityPacket.getId() == user.getCheckData().entityBotID) {
                    if (user.getCheckData().entityHitTime < 20) user.getCheckData().entityHitTime++;
                    user.getCheckData().lastEntityBotHit = System.currentTimeMillis();
                    user.getCheckData().entityATotalAttacks++;
                }
            }
        }
    }

    public static void spawnBot(User user) {
        spawnBot(user, null, CheckTypes.NORMAL);
    }

    public static void spawnBot(User user, CheckTypes checkType) {
        spawnBot(user, null, checkType);
    }

    public static void spawnBot(User user, User forcedFrom, CheckTypes checkType) {
        if (!user.getCheckData().hasBot && user.getCombatData().getLastEntityAttacked() != null) {
            user.setEntityAFollowDistance(-10.00);
            user.getCheckData().entityAReportedFlags = 0;
            user.setEntityAMovementOffset(0.0f);
            user.setEntityAStartYaw(user.getMovementData().getTo().getYaw());
            user.getCheckData().hasBot = true;
            user.getCheckData().entityHitTime = 0;

            user.setBotCheckType(checkType);

            if (forcedFrom != null) user.setForcedUser(forcedFrom);


            Player randomPlayer = getRandomPlayer(user);

            UUID uuid = randomPlayer.getUniqueId();
            String name = randomPlayer.getName();

            MinecraftServer minecraftServer = ((CraftServer) Bukkit.getServer()).getServer();
            WorldServer worldServer = ((CraftWorld) user.getPlayer().getWorld()).getHandle();
            EntityPlayer entityPlayer = new EntityPlayer(minecraftServer, worldServer, new GameProfile(UUID.fromString(String.valueOf(uuid)), name), new PlayerInteractManager(worldServer));
            entityPlayer.onGround = true;
            entityPlayer.playerInteractManager.b(WorldSettings.EnumGamemode.CREATIVE);
            entityPlayer.setInvisible(false);
            entityPlayer.setHealth((float) MathUtil.getRandomDouble(MathUtil.getRandomDouble(1.20, 5.32), 20.0));
            entityPlayer.ping = ((CraftPlayer) randomPlayer).getHandle().ping;
            user.getCheckData().entityBotID = entityPlayer.getId();

            user.getCheckData().lastEntitySpawn = System.currentTimeMillis();

            user.getEntityHelper1_8().entityPlayer = entityPlayer;

            sendPacket(user, new PacketPlayOutNamedEntitySpawn(user.getEntityHelper1_8().entityPlayer), forcedFrom);

            entityPlayer.setLocation(user.getPlayer().getLocation().getX(), user.getPlayer().getLocation().getY() + 0.42f, user.getPlayer().getLocation().getZ(), user.getPlayer().getLocation().getYaw(), (float) MathUtil.getRandomDouble(-90.0f, 90.0f));
            sendPacket(user, new PacketPlayOutEntityTeleport(entityPlayer), forcedFrom);

            sendPacket(user, new PacketPlayOutUpdateAttributes(), forcedFrom);

            if (randomPlayer.getItemInHand() != null) sendPacket(user, new PacketPlayOutEntityEquipment(entityPlayer.getId(), 0, CraftItemStack.asNMSCopy(randomPlayer.getItemInHand())), forcedFrom);
            sendPacket(user, new PacketPlayOutEntityEquipment(entityPlayer.getId(), 1, CraftItemStack.asNMSCopy(randomPlayer.getInventory().getBoots())), forcedFrom);
            sendPacket(user, new PacketPlayOutEntityEquipment(entityPlayer.getId(), 2, CraftItemStack.asNMSCopy(randomPlayer.getInventory().getLeggings())), forcedFrom);
            sendPacket(user, new PacketPlayOutEntityEquipment(entityPlayer.getId(), 3, CraftItemStack.asNMSCopy(randomPlayer.getInventory().getChestplate())), forcedFrom);
            sendPacket(user, new PacketPlayOutEntityEquipment(entityPlayer.getId(), 4, CraftItemStack.asNMSCopy(randomPlayer.getInventory().getHelmet())), forcedFrom);
            sendPacket(user, new PacketPlayOutUpdateAttributes(), forcedFrom);
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
        if (user.getCheckData().hasBot) {
            user.getCheckData().hasBot = false;
            user.getCheckData().entityHitTime = 0;

            user.getEntityHelper1_8().entityPlayer.setPosition(user.getPlayer().getLocation().getX() * -9999, user.getPlayer().getLocation().getY() * -9999, user.getPlayer().getLocation().getZ() * -9999);
            sendPacket(user, new PacketPlayOutEntityTeleport(user.getEntityHelper1_8().entityPlayer), user.getForcedUser());

            user.getEntityHelper1_8().entityPlayer = null;
        }
    }

    private static void sendPacket(User user, net.minecraft.server.v1_8_R3.Packet packet) {
        TinyProtocolHandler.sendPacket(user.getPlayer(), packet);
    }

    private static void sendPacket(User user, net.minecraft.server.v1_8_R3.Packet packet, User forcedUser) {
        TinyProtocolHandler.sendPacket(user.getPlayer(), packet);

        if (forcedUser != null) {
            TinyProtocolHandler.sendPacket(forcedUser.getPlayer(), packet);
        }
    }

    public enum CheckTypes {
        NORMAL,
        WATCHDOG,
        FOLLOW
    }
}
