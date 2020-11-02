package me.jumba.overflow.base.command;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.defaults.BukkitCommand;

/**
 * Created on 25/02/2020 Package me.jumba.sparky.base.command
 */
@Getter
@Setter
public class Command {
    private String command, usage, description;
    private BukkitCommand commandExecutor;
    private boolean enabled;

    public Command(BukkitCommand commandExecutor, String command, String usage, String description, boolean enabled) {
        this.commandExecutor = commandExecutor;
        this.command = command;
        this.usage = usage;
        this.description = description;
        this.enabled = enabled;
    }
}
