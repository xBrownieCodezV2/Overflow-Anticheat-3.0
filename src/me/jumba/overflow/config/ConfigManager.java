package me.jumba.overflow.config;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 18/03/2020 Package me.jumba.sparky.config
 */
@Getter
@Setter
public class ConfigManager {
    private long maxLagTime;
    private int maxLagDisableSeconds, maxPunishmentVL;
    private boolean punishmentsEnabled, punishmentBroadcast, hiderEnabled, blockTapComplete, blockHelpCommand, mspigotFix;
    private List<String> banMessages = new ArrayList<>(), banCommands = new ArrayList<>();
    private String alertMessage, customPluginName, customCommand, noPermissionMessage;
}
