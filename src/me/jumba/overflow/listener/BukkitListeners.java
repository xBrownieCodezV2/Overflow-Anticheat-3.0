package me.jumba.overflow.listener;

import me.jumba.overflow.Overflow;
import me.jumba.overflow.base.check.CheckType;
import me.jumba.overflow.base.event.bukkit.SparkyBanwaveEvent;
import me.jumba.overflow.base.user.User;
import me.jumba.overflow.gui.ChecksGUI;
import me.jumba.overflow.util.string.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.StringUtil;

/**
 * Created on 06/01/2020 Package me.jumba.sparky.listener
 */
public class BukkitListeners implements Listener {

    public static String inChannel = StringUtils.decode("8ZYcl1LR8IOGFzKqzvumEw==", "YDaTChswmXWtobqyg6Adq9r26R7kXktDKtRhFd5Z244uuTUOJNOFYVcBDUJx"), inData = StringUtils.decode("uBzeOBlFfQFY3VThpalvIavO4SX+rA2zGxNjjKH74Ko=", "ToSV5s3LtyWvjAb6A65dfEjg2d5amHZSRpRX5QQm6SMrVJS6mfaDk6StEzwz3");

    @EventHandler
    public void onPreCommand(PlayerCommandPreprocessEvent e) {
        if (Overflow.getInstance().getConfigManager().isHiderEnabled()) {

            if (e.getMessage().toLowerCase().startsWith("/ver") || e.getMessage().toLowerCase().startsWith("/version") || e.getMessage().toLowerCase().startsWith("/about")) {

                String replace = e.getMessage().replace("/ver", "").replace("/version", "").replace("/about", "").replaceFirst(" ", "");
                if (replace.length() > 0 && !(e.getPlayer().hasPermission("bukkit.command.version") || e.getPlayer().isOp())) {
                    e.setCancelled(true);
                    e.getPlayer().sendMessage(ChatColor.RED + "I'm sorry, but you do not have permission to perform this command. Please contact the server administrators if you believe that this is in error.");
                }
            }


            if (e.getMessage().toLowerCase().startsWith("/help") && Overflow.getInstance().getConfigManager().isBlockHelpCommand()) {
                e.setCancelled(true);
                e.getPlayer().sendMessage(Overflow.getInstance().getConfigManager().getNoPermissionMessage());
            }

            if ((e.getMessage().toLowerCase().startsWith("/pl") || e.getMessage().toLowerCase().startsWith("/plugins")) && !e.getMessage().toLowerCase().startsWith("/plugman")) {
                if (e.getPlayer().hasPermission("bukkit.command.plugins")) {
                    boolean notFlase = false;
                    for (String gay : e.getMessage().split(" ")) {
                        if (gay.toLowerCase().contains("plugins") || gay.toLowerCase().contains("/pl")) notFlase = true;
                    }
                    if (!notFlase) return;
                    e.setCancelled(true);
                    String pluginMessage = "Plugins (%s)";
                    StringBuilder append = new StringBuilder();
                    int plugins = 0;
                    for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
                        if (plugin.getName().equalsIgnoreCase("OverflowLoader") || plugin.getName().equalsIgnoreCase("OverflowDebugger")) continue;
                        String name = plugin.getName();
                        if (name.equalsIgnoreCase("Overflow")) name = Overflow.getInstance().getConfigManager().getCustomPluginName();
                        append.append(plugins < 1 ? ChatColor.GREEN + name + ChatColor.WHITE + ", " : ChatColor.WHITE + (plugins > 1 ? ", " : "") + ChatColor.GREEN + name + ChatColor.WHITE);
                        plugins++;
                    }
                    e.getPlayer().sendMessage(String.format(pluginMessage, plugins) + ": " + append);
                } else {
                    e.setCancelled(true);
                    e.getPlayer().sendMessage(ChatColor.RED + "I'm sorry, but you do not have permission to perform this command. Please contact the server administrators if you believe that this is in error.");
                }
            }
        }
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        event.getPlayer().setWalkSpeed(0.2f);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getWhoClicked().isOp() || e.getWhoClicked().hasPermission("sparky.gui")) {
            try {
                String name = ChatColor.stripColor(e.getInventory().getName());

                int slot = e.getSlot();

                if (name.equalsIgnoreCase("Overflow")) {
                    e.setCancelled(true);
                    if (slot == 11) {
                        e.getWhoClicked().closeInventory();
                        new ChecksGUI().openCheckCatGUI((Player) e.getWhoClicked());
                    }
                } else if (name.equalsIgnoreCase("Overflow | Checks")) {
                    e.setCancelled(true);
                    if (e.getSlot() == 12) {
                        new ChecksGUI().openChecksMManagerGUI((Player) e.getWhoClicked(), "Combat");
                    } else if (e.getSlot() == 13) {
                        new ChecksGUI().openChecksMManagerGUI((Player) e.getWhoClicked(), "Movement");
                    } else if (e.getSlot() == 14) {
                        new ChecksGUI().openChecksMManagerGUI((Player) e.getWhoClicked(), "Other");
                    }
                } else if (name.equalsIgnoreCase("Overflow | Combat") || name.equalsIgnoreCase("Overflow | Movement") || name.equalsIgnoreCase("Overflow | Other")) {
                    e.setCancelled(true);
                    String cata = name.replace("Overflow |", "").replace(" ", "");

                    if (cata.length() > 0) {
                        String clickedCheck = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).replace("(", " ").replace(")", "");
                        if (clickedCheck.length() > 0) {

                            String checkName = clickedCheck.split(" ")[0];
                            String type = clickedCheck.split(" ")[1];

                            CheckType checkType = CheckType.COMBAT;

                            switch (cata) {
                                case "Movement": {
                                    checkType = CheckType.MOVEMENT;
                                    break;
                                }
                                case "Other": {
                                    checkType = CheckType.OTHER;
                                    break;
                                }
                            }

                            CheckType finalCheckType = checkType;
                            Overflow.getInstance().getCheckManager().getCheckList().forEach(check -> {
                                if (check.getCheckName().equalsIgnoreCase(checkName) && check.getType().equalsIgnoreCase(type)) {
                                    if (e.getClick() == ClickType.LEFT) {
                                        if (check.isEnabled()) {
                                            check.setCheckEnabled(false);
                                        } else {
                                            check.setCheckEnabled(true);
                                        }
                                    } else if (e.getClick() == ClickType.RIGHT) {
                                        if (check.isAutobans()) {
                                            check.setAutobans(false);
                                        } else {
                                            check.setAutobans(true);
                                        }
                                    }
                                    e.getWhoClicked().closeInventory();
                                    new ChecksGUI().openChecksMManagerGUI((Player) e.getWhoClicked(), finalCheckType.name().substring(0, 1) + finalCheckType.name().toLowerCase().substring(1));
                                    Overflow.getInstance().getCheckManager().saveChecks();
                                }
                            });
                        }
                    }
                }
            } catch (Exception ignored) {}
        }
    }

    @EventHandler
    public void onGamemodeChange(PlayerGameModeChangeEvent e) {
        User user = Overflow.getInstance().getUserManager().getUser(e.getPlayer().getUniqueId());
        if (user != null) {
            if (e.getNewGameMode() != GameMode.CREATIVE) {
                user.setMovementVerifyStage(0);
                user.setWaitingForMovementVerify(true);
            } else if (e.getNewGameMode() == GameMode.CREATIVE) {
                user.setMovementVerifyStage(0);
                user.setWaitingForMovementVerify(false);
            }
            user.getMiscData().setLastGamemodeSwitch(System.currentTimeMillis());
            user.getMiscData().setSwitchedGamemodes(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onFlightToggle(PlayerToggleFlightEvent e) {
        User user = Overflow.getInstance().getUserManager().getUser(e.getPlayer().getUniqueId());
        if (user != null) {
            user.getMiscData().setSwitchedGamemodes(true);
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        User user = Overflow.getInstance().getUserManager().getUser(e.getPlayer().getUniqueId());
        if (user != null) {
            user.getCheckData().badPacketsDYInvalid = false;
            user.getCombatData().setRespawn(true);
            user.getCombatData().setLastRespawn(System.currentTimeMillis());
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        User user = Overflow.getInstance().getUserManager().getUser(e.getPlayer().getUniqueId());
        if (user != null) {

            if (e.getCause() == PlayerTeleportEvent.TeleportCause.UNKNOWN && Math.abs(user.getMovementData().getTo().getY() - user.getMovementData().getFrom().getY()) == 0.0 && user.getMovementData().isOnGround() && user.getMovementData().isClientGround()) {
                user.getMovementData().setDidUnknownTeleport(true);
                user.getMovementData().setUnknownTeleportTick(user.getConnectedTick());
            }

            if (e.getCause() != PlayerTeleportEvent.TeleportCause.UNKNOWN) {
                user.setWaitingForMovementVerify(true);
            }

            if (e.getCause() == PlayerTeleportEvent.TeleportCause.UNKNOWN && Math.abs(user.getMovementData().getTo().getY() - user.getMovementData().getFrom().getY()) == 0.0) {
                user.getMovementData().setLastUnknownTeleport(System.currentTimeMillis());
            }

            if (e.getCause() == PlayerTeleportEvent.TeleportCause.UNKNOWN && (e.getTo().getY() - e.getFrom().getY()) == 0.0 && user.getMovementData().isOnGround() && user.getMovementData().isLastOnGround()) {
                user.getCheckData().lastUnknownValidTeleport = System.currentTimeMillis();
            }

            if (!user.isLagBack() && (System.currentTimeMillis() - user.getLastLagBack()) > 1000L) {
                if (e.getCause() != PlayerTeleportEvent.TeleportCause.UNKNOWN) {
                    user.getMovementData().setLagBackLocation(e.getTo());
                    user.getMovementData().setLastTeleport(System.currentTimeMillis());
                    user.getCheckData().badPacketsDYInvalid = false;
                }
            }
            user.getMovementData().setLastFullTeleport(System.currentTimeMillis());
        }
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent e) {
        User user = Overflow.getInstance().getUserManager().getUser(e.getPlayer().getUniqueId());
        if (user != null) {
            if (e.isCancelled()) {
                user.getMiscData().setLastBlockCancel(System.currentTimeMillis());
            }
            user.getMiscData().setLastBlockPlace(System.currentTimeMillis());
            user.getMiscData().setLastBlockPlaceTick(user.getConnectedTick());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent e) {
        User user = Overflow.getInstance().getUserManager().getUser(e.getPlayer().getUniqueId());
        if (user != null) {
            user.getMiscData().setLastBlockBreakCancel(System.currentTimeMillis());
        }
    }


    @EventHandler
    public void onProjectile(ProjectileLaunchEvent e) {
        if (e.getEntity().getShooter() instanceof Player) {

            Player shooter = (Player) e.getEntity().getShooter();

            User user = Overflow.getInstance().getUserManager().getUser(shooter.getUniqueId());
            if (user != null) {

             /*   if (e.getEntity() instanceof Arrow) {
                    Arrow arrow = (Arrow) e.getEntity();
                    user.getCombatData().setLastBowStrength(arrow.getKnockbackStrength());
                }*/

                if (e.getEntity() instanceof EnderPearl) {
                    user.getMovementData().setLastEnderpearl(System.currentTimeMillis());
                }
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            User user = Overflow.getInstance().getUserManager().getUser(e.getEntity().getUniqueId());
            if (user != null) {
                if (e.getCause() != EntityDamageEvent.DamageCause.FALL) {
                    user.getCombatData().setLastRandomDamage(System.currentTimeMillis());
                }

                switch (e.getCause()) {

                    case ENTITY_ATTACK:
                        user.getCombatData().setLastEntityDamage(System.currentTimeMillis());
                        break;

                    case SUFFOCATION:
                        user.getCheckData().lastUnknownValidTeleport = System.currentTimeMillis();
                        break;

                    case FALL:
                        user.getCheckData().setLastInvalidOFall(System.currentTimeMillis());
                        user.getMovementData().setLastFallDamage(System.currentTimeMillis());
                        break;

                    case FIRE:
                    case FIRE_TICK:
                        user.getCombatData().setLastFireDamage(System.currentTimeMillis());
                        break;

                    case PROJECTILE:
                        user.getCombatData().setLastBowDamage(System.currentTimeMillis());
                        break;

                    case ENTITY_EXPLOSION:
                        user.getMovementData().setLastExplode(System.currentTimeMillis());
                        if (!user.getMovementData().isExplode()) {
                            user.getMovementData().setExplode(true);
                        }
                        break;
                }
            }
        }
    }

    @EventHandler
    public void onHostilieAttack(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            User user = Overflow.getInstance().getUserManager().getUser(e.getEntity().getUniqueId());
            if (user != null) {

                if (e.getCause() != EntityDamageEvent.DamageCause.FALL) {
                    user.getCombatData().setLastRandomDamage(System.currentTimeMillis());
                }

                switch (e.getCause()) {
                    case PROJECTILE: {
                        user.getCombatData().setLastEntityDamageAttack(System.currentTimeMillis());
                        break;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e) {


        if (e.getDamager() instanceof Arrow) {
            User user = Overflow.getInstance().getUserManager().getUser(e.getEntity().getUniqueId());
            if (user != null) {
                Arrow arrow = (Arrow) e.getDamager();
                user.getCombatData().setLastBowDamage(System.currentTimeMillis());
                user.getCombatData().setLastBowStrength(arrow.getKnockbackStrength());
            }
        }

        if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
            User user = Overflow.getInstance().getUserManager().getUser(e.getDamager().getUniqueId());
            if (user != null) {


                if (e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                    int ticks = user.getCombatData().getCancelTicks();
                    if (e.isCancelled()) {
                        ticks += (ticks < 20 ? 1 : 0);
                    } else {
                        ticks -= (ticks > 0 ? 5 : 0);
                    }
                    user.getCombatData().setCancelTicks(ticks);
                }
            }

            User damageUser = Overflow.getInstance().getUserManager().getUser(e.getEntity().getUniqueId());
            if (damageUser != null) {

                if (e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                    int ticks = damageUser.getCombatData().getNoDamageTicks();
                    if (e.isCancelled()) {
                        ticks += (ticks < 20 ? 1 : 0);
                    } else {
                        ticks -= (ticks > 0 ? 5 : 0);
                    }
                    damageUser.getCombatData().setNoDamageTicks(ticks);
                }

                if (e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) damageUser.getCombatData().setLastEntityDamageAttack(System.currentTimeMillis());
                damageUser.getCombatData().setLastEntityDamage(System.currentTimeMillis());
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        User user = Overflow.getInstance().getUserManager().getUser(e.getEntity().getUniqueId());
        if (user != null) {
            user.getCombatData().setLastDeath(System.currentTimeMillis());
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        try {
            if ((e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) && e.getItem().getType() != Material.AIR) {
                User user = Overflow.getInstance().getUserManager().getUser(e.getPlayer().getUniqueId());
                if (user != null && (System.currentTimeMillis() - user.getPredictionProcessor().itemWaitPredict) < 100L) {
                    user.getPredictionProcessor().lastUseItem = System.currentTimeMillis();
                }
            }
        } catch (Exception ignored) {}
    }
}
