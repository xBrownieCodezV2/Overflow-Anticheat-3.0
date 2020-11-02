package me.jumba.overflow.base.command.commands.sub;

import me.jumba.overflow.Overflow;
import me.jumba.overflow.base.check.Check;
import me.jumba.overflow.base.user.User;
import me.jumba.overflow.hook.HookManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created on 25/02/2020 Package me.jumba.sparky.base.command.commands.sub
 */
public class InformationCommand {

    public void execute(String[] args, String s, CommandSender commandSender) {
        try {
            Player target = Bukkit.getPlayer(args[1]);
            if (target != null) {
                commandSender.sendMessage(ChatColor.GRAY + "Known information for " + ChatColor.RED + target.getName());

                User targetUser = Overflow.getInstance().getUserManager().getUser(target.getUniqueId());

                if (targetUser != null) {

                    int max = 6;

                    commandSender.sendMessage(ChatColor.GRAY + "Ping: " + ChatColor.GREEN + (Overflow.getInstance().getConfigManager().isMspigotFix() ? targetUser.getLagProcessor().getLastTransaction()  : targetUser.getLagProcessor().getLastPing()));
                    commandSender.sendMessage(ChatColor.GRAY + "Transaction Ping: " + ChatColor.GREEN + targetUser.getLagProcessor().getLastTransaction());
                    commandSender.sendMessage(ChatColor.GRAY + "Violations: " + ChatColor.GREEN + targetUser.getViolation());
                    commandSender.sendMessage(ChatColor.GRAY + "Client Version: " + ChatColor.GREEN + (targetUser.getCurrentClientVersion() == HookManager.Helper.Versions.UNKNOWN ? HookManager.Helper.Versions.V1_8.name() : targetUser.getCurrentClientVersion().name()));
                    commandSender.sendMessage(ChatColor.GRAY + "Client Brand: " + ChatColor.GREEN + targetUser.getMiscData().getClientBrand());
                    commandSender.sendMessage(ChatColor.GRAY + "Lagging: " + (targetUser.getLagProcessor().isLagging() ? ChatColor.GREEN + "Yes" : ChatColor.RED + "No"));
                    commandSender.sendMessage(ChatColor.GRAY + "Recent Checks flagged: " + ChatColor.GREEN + max+"/"+ targetUser.getFlaggedChecks().size());

                    HashMap<Check, Integer> tmp = new HashMap<>();
                    AtomicInteger total = new AtomicInteger();

                    targetUser.getFlaggedChecks().forEach((c, i) -> {
                        if (total.get() <= max) {
                            tmp.put(c, i);
                        }
                        total.getAndIncrement();
                    });

                    tmp.forEach((c, v) -> commandSender.sendMessage(ChatColor.GRAY + " - " + ChatColor.YELLOW + c.getCheckName() + "("+c.getType()+")" + ChatColor.RED + " x"+v));
                }
            } else commandSender.sendMessage(ChatColor.RED + "Target is not online.");
        } catch (Exception ingored) {
            commandSender.sendMessage(ChatColor.RED + "Please supply a players name!");
        }
    }
}
