package me.jumba.overflow.base.command.commands.sub;

import me.jumba.overflow.Overflow;
import me.jumba.overflow.base.user.User;
import me.jumba.overflow.checks.combat.bot.Entity;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created on 25/02/2020 Package me.jumba.sparky.base.command.commands.sub
 */
public class ForceBotCommand {

    public void execute(String[] args, String s, CommandSender commandSender) {

        int waitTime = 5;

        try {
            Player target = Bukkit.getPlayer(args[1]);
            String checkType = args[2];

            try {
                waitTime = Integer.parseInt(args[3]);
            } catch (NumberFormatException ignored) {
            }

            if (checkType != null) {

                checkType = checkType.toUpperCase();

                boolean found = false;

                for (Entity.CheckTypes checkTypes : Entity.CheckTypes.values()) {
                    if (checkTypes.name().equalsIgnoreCase(checkType)) {
                        found = true;
                    }
                }

                if (!found) {
                    commandSender.sendMessage(ChatColor.GRAY + checkType + ChatColor.RED + " Is not a valid check mode!");

                    commandSender.sendMessage(ChatColor.RED + "Check modes:");

                    for (Entity.CheckTypes checkTypes : Entity.CheckTypes.values()) {
                        commandSender.sendMessage(ChatColor.GRAY + " - " + checkTypes.name().substring(0, 1).toUpperCase() + checkTypes.name().substring(1).toLowerCase());
                    }
                    return;
                }

                if (target != null) {
                    User targetUser = Overflow.getInstance().getUserManager().getUser(target.getUniqueId()), senderUser = Overflow.getInstance().getUserManager().getUser(((Player) commandSender).getUniqueId());

                    if (targetUser != null && senderUser != null) {
                        if (Overflow.getInstance().getCheckManager().isEnabled("Entity")) {
                            if (!targetUser.getCheckData().hasBot && !targetUser.isWaitingForBot()) {

                               if (waitTime > 0) commandSender.sendMessage(ChatColor.RED + target.getName() + ChatColor.GRAY + " will be checked in " + waitTime + " seconds!");

                                String finalCheckType = checkType;
                                int finalWaitTime = waitTime;

                                if (waitTime == 0) {
                                    spawnBot(targetUser, senderUser, target, commandSender, finalCheckType);
                                } else {
                                    new BukkitRunnable() {
                                        int i = 0;

                                        @Override
                                        public void run() {
                                            if (i >= finalWaitTime) {
                                                spawnBot(targetUser, senderUser, target, commandSender, finalCheckType);
                                                this.cancel();
                                            }
                                            i++;
                                        }
                                    }.runTaskTimer(Overflow.getLauncherInstance(), 20L, 20L);
                                }
                                targetUser.setWaitingForBot(true);
                            } else {
                                commandSender.sendMessage(ChatColor.GRAY + target.getName() + ChatColor.RED + " already has a bot!");
                            }
                        } else {
                            commandSender.sendMessage(ChatColor.RED + "The \"Entity A\" check is not enabled! please enable it before forcing a bot check!");
                        }
                    }
                } else commandSender.sendMessage(ChatColor.RED + "Target is not online.");
            } else {
                commandSender.sendMessage(ChatColor.RED + "Please supply a mode:");

                for (Entity.CheckTypes checkTypes : Entity.CheckTypes.values()) {
                    commandSender.sendMessage(ChatColor.GRAY + " - " + checkTypes.name().substring(0, 1).toUpperCase() + checkTypes.name().substring(1).toLowerCase());
                }
            }
        } catch (Exception ingored) {
            commandSender.sendMessage(ChatColor.RED + "Please supply a players name & check mode & spawn wait time!");

            commandSender.sendMessage(ChatColor.RED + "Check modes:");

            for (Entity.CheckTypes checkTypes : Entity.CheckTypes.values()) {
                commandSender.sendMessage(ChatColor.GRAY + " - " + checkTypes.name().substring(0, 1).toUpperCase() + checkTypes.name().substring(1).toLowerCase());
            }
        }
    }

    private void spawnBot(User targetUser, User senderUser, Player target, CommandSender commandSender, String checkType) {
        if (targetUser.getCombatData().getLastEntityAttacked() == null) targetUser.getCombatData().setLastEntityAttacked(senderUser.getPlayer());
        commandSender.sendMessage(ChatColor.GRAY + "Forcing bot check on " + ChatColor.RED + target.getName() + ChatColor.GRAY + " with the mode " + ChatColor.GREEN + checkType.substring(0, 1).toUpperCase() + checkType.substring(1).toLowerCase());
        Entity.spawnBot(targetUser, senderUser, Entity.CheckTypes.valueOf(checkType));
    }
}
