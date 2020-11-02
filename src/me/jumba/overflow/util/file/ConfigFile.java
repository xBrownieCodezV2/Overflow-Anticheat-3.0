package me.jumba.overflow.util.file;

import me.jumba.overflow.Overflow;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfigFile {

    private ConfigFile() { }

    static ConfigFile instance = new ConfigFile();

    public static ConfigFile getInstance() {
        return instance;
    }

    Plugin p;

    FileConfiguration config;
    File cfile;

    FileConfiguration data;
    File dfile;

    public void setup(Plugin p) {
        config = p.getConfig();
        if (!p.getDataFolder().exists()) {
            p.getDataFolder().mkdir();
        }

        dfile = new File(String.format("plugins/Overflow/%s", Overflow.getInstance().getClassResolver().getData(Overflow.getInstance().getClassResolver().IDENT4)));

        if (!dfile.exists()) {
            try {
                dfile.createNewFile();
            }
            catch (IOException e) {
            }
        }

        data = YamlConfiguration.loadConfiguration(dfile);

    }

    public FileConfiguration getData() {
        return data;
    }


    public void writeDefaults() {
        if (!data.contains("Lag.maxTime")) data.set("Lag.maxTime", 5000L);
        if (!data.contains("Lag.maxDisableSeconds")) data.set("Lag.maxDisableSeconds", 10);

        if (!data.contains("Messages.Alert")) data.set("Messages.Alert", "&7[&cOverflow&7] &c%PLAYER% &7has failed &c%CHECK%&7(&c%TYPE%&c&7) &cx%VL%");

        if (!data.contains("Punishment.enabled")) data.set("Punishment.enabled", false);
        if (!data.contains("Punishment.maxViolation")) data.set("Punishment.maxViolation", 20);
        if (!data.contains("Punishment.broadcast")) data.set("Punishment.broadcast", true);
        List<String> broadcastMessages = new ArrayList<>();
        broadcastMessages.add("&cOverflow has removed &c%PLAYER%&c for cheating!");
        if (!data.contains("Punishment.broadcastMessage")) data.set("Punishment.broadcastMessage", broadcastMessages);

        List<String> banCommands = new ArrayList<>();
        banCommands.add("ban %PLAYER% cheating");
        if (!data.contains("Punishment.banCommands")) data.set("Punishment.banCommands", banCommands);

        if (!data.contains("Hider.enabled")) data.set("Hider.enabled", true);
        if (!data.contains("Hider.BlockTabComplete")) data.set("Hider.BlockTabComplete", true);
        if (!data.contains("Hider.BlockHelpCommand")) data.set("Hider.BlockHelpCommand", true);
        if (!data.contains("Hider.PluginName")) data.set("Hider.PluginName", "Kauri");
        if (!data.contains("Hider.Command")) data.set("Hider.Command", "kauri");
        if (!data.contains("Hider.NoPermissionMessage")) data.set("Hider.NoPermissionMessage", "Unknown command. Type \"/help\" for help.");


        if (!data.contains("Fixes.mSpigotFixer")) data.set("Fixes.mSpigotFixer", false);

        saveData();
    }

    public void saveData() {
        try {
            data.save(dfile);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reloadData() {
        data = YamlConfiguration.loadConfiguration(dfile);
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void saveConfig() {
        try {
            config.save(cfile);
        }
        catch (IOException e) {
        }
    }

    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(cfile);
    }

    public PluginDescriptionFile getDesc() {
        return p.getDescription();
    }
}