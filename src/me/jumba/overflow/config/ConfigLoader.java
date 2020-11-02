package me.jumba.overflow.config;

import me.jumba.overflow.Overflow;
import me.jumba.overflow.util.file.ConfigFile;

/**
 * Created on 18/03/2020 Package me.jumba.sparky.config
 */
public class ConfigLoader {

    public void load() {
        ConfigFile.getInstance().setup(Overflow.getLauncherInstance());
        ConfigFile.getInstance().writeDefaults();

        Overflow.getInstance().getConfigManager().setMaxLagTime(ConfigFile.getInstance().getData().getLong("Lag.maxTime"));
        Overflow.getInstance().getConfigManager().setMaxLagDisableSeconds(ConfigFile.getInstance().getData().getInt("Lag.maxDisableSeconds"));

        Overflow.getInstance().getConfigManager().setAlertMessage(convertColor(ConfigFile.getInstance().getData().getString("Messages.Alert")));

        Overflow.getInstance().getConfigManager().setPunishmentsEnabled(ConfigFile.getInstance().getData().getBoolean("Punishment.enabled"));
        Overflow.getInstance().getConfigManager().setMaxPunishmentVL(ConfigFile.getInstance().getData().getInt("Punishment.maxViolation"));
        Overflow.getInstance().getConfigManager().setPunishmentBroadcast(ConfigFile.getInstance().getData().getBoolean("Punishment.broadcast"));
        Overflow.getInstance().getConfigManager().setBanMessages(ConfigFile.getInstance().getData().getStringList("Punishment.broadcastMessage"));
        Overflow.getInstance().getConfigManager().setBanCommands(ConfigFile.getInstance().getData().getStringList("Punishment.banCommands"));

        Overflow.getInstance().getConfigManager().setHiderEnabled(ConfigFile.getInstance().getData().getBoolean("Hider.enabled"));
        Overflow.getInstance().getConfigManager().setCustomPluginName(ConfigFile.getInstance().getData().getString("Hider.PluginName"));
        Overflow.getInstance().getConfigManager().setCustomCommand(ConfigFile.getInstance().getData().getString("Hider.Command"));
        Overflow.getInstance().getConfigManager().setBlockTapComplete(ConfigFile.getInstance().getData().getBoolean("Hider.BlockTabComplete"));
        Overflow.getInstance().getConfigManager().setBlockHelpCommand(ConfigFile.getInstance().getData().getBoolean("Hider.BlockHelpCommand"));
        Overflow.getInstance().getConfigManager().setNoPermissionMessage(ConfigFile.getInstance().getData().getString("Hider.NoPermissionMessage"));
        Overflow.getInstance().getConfigManager().setMspigotFix(ConfigFile.getInstance().getData().getBoolean("Fixes.mSpigotFixer"));

    }

    public String convertColor(String in) {
        return in.replace("&", "§");
    }
}
