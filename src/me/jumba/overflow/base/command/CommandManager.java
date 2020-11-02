package me.jumba.overflow.base.command;

import lombok.Getter;
import me.jumba.overflow.Overflow;
import me.jumba.overflow.base.command.commands.MainCommand;
import me.jumba.overflow.util.command.CommandUtils;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 25/02/2020 Package me.jumba.sparky.base.commands
 */
@Getter
public class CommandManager {

    private List<Command> commandList = new ArrayList<>();

    public CommandManager() {
        this.setup();
    }

    public void setup() {
        String command = (Overflow.getInstance().getConfigManager().isHiderEnabled() ? Overflow.getInstance().getConfigManager().getCustomCommand().replace("/", "") : "overflow");

        addCommand(new Command(new MainCommand(command), "%s", null, "Main command.", true));
        addCommand(new Command(new MainCommand(command), "%s gui", "/%s gui", "Opens the Overflow GUI.", true));
        addCommand(new Command(new MainCommand(command), "%s info", "/%s info <player>", "Information command.", true));
        addCommand(new Command(new MainCommand(command), "%s alerts", "/%s alerts", "Alerts toggle command.", true));
        addCommand(new Command(new MainCommand(command), "%s forcebot", "/%s forcebot <Player> <Mode> <Wait seconds>", "Force bot command.", true));
        addCommand(new Command(new MainCommand(command), "%s logs", "/%s logs <Mode> <Player>", "Logs Command.", true));
    }

    private void addCommand(Command... commands) {
        for (Command command : commands) {
            commandList.add(command);

           if (command.isEnabled()) CommandUtils.registerCommand(command);
        }
    }
}
