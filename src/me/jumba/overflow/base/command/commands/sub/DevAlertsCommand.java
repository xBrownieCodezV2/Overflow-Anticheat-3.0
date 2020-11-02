package me.jumba.overflow.base.command.commands.sub;

import me.jumba.overflow.Overflow;
import me.jumba.overflow.base.user.User;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created on 25/02/2020 Package me.jumba.sparky.base.command.commands.sub
 */
public class DevAlertsCommand {


    public void execute(String[] args, String s, CommandSender commandSender) {
        User user = Overflow.getInstance().getUserManager().getUser(((Player) commandSender).getUniqueId());
        if (user != null) {
            if (user.isDevAlerts()) {
                user.setDevAlerts(false);
                commandSender.sendMessage(ChatColor.RED + "Developer alerts have been toggled off!");
            } else {
                user.setDevAlerts(true);
                commandSender.sendMessage(ChatColor.GREEN + "Developer alerts have been toggled on!");
            }
        }
    }
}
