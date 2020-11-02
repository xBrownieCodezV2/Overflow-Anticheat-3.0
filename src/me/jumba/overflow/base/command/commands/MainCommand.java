package me.jumba.overflow.base.command.commands;

import me.jumba.overflow.Overflow;
import me.jumba.overflow.base.command.commands.sub.*;
import me.jumba.overflow.util.command.CommandUtils;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Created on 25/02/2020 Package me.jumba.sparky.base.command.commands
 */
public class MainCommand extends BukkitCommand {

    private String line = ChatColor.GRAY + "§m------------------------------------------";

    private InformationCommand informationCommand = new InformationCommand();

    private GUICommand guiCommand = new GUICommand();

    private ForceBotCommand forceBotCommand = new ForceBotCommand();

    private AlertsCommand alertsCommand = new AlertsCommand();

    private DevAlertsCommand devAlertsCommand = new DevAlertsCommand();

    private LogsCommand logsCommand = new LogsCommand();

    public MainCommand(String name) {
        super(name);
        this.description = "Anticheat command.";
        this.usageMessage = "/" + name;
        this.setAliases(new ArrayList<>());
    }

    @Override
    public boolean execute(CommandSender commandSender, String commandLabel, String[] args) {
        if (commandLabel.equalsIgnoreCase((Overflow.getInstance().getConfigManager().isHiderEnabled() ? Overflow.getInstance().getConfigManager().getCustomCommand() : "overflow"))) {

            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage(ChatColor.RED + "Only players can execute this command.");
                return true;
            }

            if (commandSender.isOp() || commandSender.hasPermission("overflow.command")) {
                if (args.length < 1) {
                    commandSender.sendMessage(ChatColor.RED + (Overflow.getInstance().getConfigManager().isHiderEnabled() ? Overflow.getInstance().getConfigManager().getCustomPluginName() : "Overflow")  + ChatColor.GRAY + " - " + ChatColor.RED + Overflow.getInstance().getVersion());
                    commandSender.sendMessage(line);

                    Player player = (Player) commandSender;

                    Overflow.getInstance().getCommandManager().getCommandList().forEach(command -> {
                        TextComponent textComponent = new TextComponent(ChatColor.GRAY + "» " + ChatColor.RED + "/" + String.format(command.getCommand(), (Overflow.getInstance().getConfigManager().isHiderEnabled() ? Overflow.getInstance().getConfigManager().getCustomCommand() : "overflow")) + ChatColor.GRAY + " - " + ChatColor.RED + command.getDescription());
                        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder((command.getUsage() != null ? ChatColor.RED + String.format(command.getUsage(), (Overflow.getInstance().getConfigManager().isHiderEnabled() ? Overflow.getInstance().getConfigManager().getCustomCommand() : "overflow")) : ChatColor.WHITE + "No usage found.")).create()));
                        player.spigot().sendMessage(textComponent);
                    });

                    commandSender.sendMessage(line);
                } else {
                    String s = args[0];
                    boolean found = false;

                    if (s.equalsIgnoreCase("info")) {
                        found = true;
                        informationCommand.execute(args, s, commandSender);
                    } else if (s.equalsIgnoreCase("gui")) {
                        found = true;
                        guiCommand.execute(args, s, commandSender);
                    } else if (s.equalsIgnoreCase("forcebot")) {
                        found = true;
                        forceBotCommand.execute(args, s, commandSender);
                    } else if (s.equalsIgnoreCase("alerts")) {
                        found = true;
                        alertsCommand.execute(args, s, commandSender);
                    } else if (s.equalsIgnoreCase("devalerts")) {
                        found = true;
                        devAlertsCommand.execute(args, s, commandSender);
                    } else if (s.equalsIgnoreCase("reload")) {
                        found = true;

                        Overflow.getInstance().getCommandManager().getCommandList().forEach(CommandUtils::unRegisterBukkitCommand);
                        CommandUtils.unRegisterBukkitCommand("overflow");
                        CommandUtils.unRegisterBukkitCommand(Overflow.getInstance().getConfigManager().getCustomCommand());

                        Overflow.getInstance().getConfigLoader().load();
                        Overflow.getInstance().getCommandManager().getCommandList().clear();
                        Overflow.getInstance().getCommandManager().setup();

                        commandSender.sendMessage(ChatColor.GREEN + "Reloaded!");
                    } else if (s.contains("logs")) {
                        found = true;

                        logsCommand.execute(args, s, commandSender);
                    }

                    if (!found) commandSender.sendMessage(ChatColor.RED + "Sub command doesn't exist!");
                }
            } else {
                commandSender.sendMessage(ChatColor.RED + Overflow.getInstance().getConfigManager().getNoPermissionMessage());
            }
        }
        return false;
    }
}
