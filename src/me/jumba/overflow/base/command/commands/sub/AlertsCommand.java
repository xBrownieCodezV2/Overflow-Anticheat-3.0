package me.jumba.overflow.base.command.commands.sub;

import me.jumba.overflow.Overflow;
import me.jumba.overflow.base.user.User;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created on 25/02/2020 Package me.jumba.sparky.base.command.commands.sub
 */
public class AlertsCommand {


    public void execute(String[] args, String s, CommandSender commandSender) {
        User user = Overflow.getInstance().getUserManager().getUser(((Player) commandSender).getUniqueId());
        if (user != null) {
            if (user.isAlerts()) {
                user.setAlerts(false);
                commandSender.sendMessage(ChatColor.RED + "Alerts have been toggled off!");
            } else {
                user.setAlerts(true);
                commandSender.sendMessage(ChatColor.GREEN + "Alerts have been toggled on!");
            }
        }
    }
}
