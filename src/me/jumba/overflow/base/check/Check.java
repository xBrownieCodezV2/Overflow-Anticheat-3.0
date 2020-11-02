package me.jumba.overflow.base.check;

import lombok.Getter;
import lombok.Setter;
import me.jumba.overflow.Overflow;
import me.jumba.overflow.base.event.SparkyListener;
import me.jumba.overflow.base.user.User;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 05/01/2020 Package me.jumba.sparky.base.check
 */
@Getter
@Setter
public class Check implements SparkyListener {
    private String checkName, type;

    private CheckType checkType;

    private boolean enabled, autobans = true, experimental, ignoreLagBacks, allowed;

    private int astorment;


    public Check(String checkName, String type, CheckType checkType, boolean enabled) {
        this.checkName = checkName;
        this.type = type;
        this.checkType = checkType;
        this.enabled = enabled;
    }

    public void flagDev(User user, String... data) {

    }

    public void flag(User user, String... data) {
        if (Overflow.getInstance().isLagging() || !Overflow.getInstance().isActive() || user.flag
                || Overflow.getInstance().getCheckManager().isFlag()) return;

        user.setLastFlag(System.currentTimeMillis());
        if ((System.currentTimeMillis() - user.getLastBan()) < 1000L) return;

        Overflow.getInstance().getCheckExecutor().execute(() -> {

            if (!isExperimental()) {
                user.getCheatPrediction().getViolations().add(user.getViolation());
            }

            if (user.getFlaggedChecks().containsKey(this)) {
                user.getFlaggedChecks().put(this, user.getFlaggedChecks().get(this) + 2);
            } else user.getFlaggedChecks().put(this, 1);


            /*if (user.getViolation() >= Sparky.getInstance().getConfigManager().getMaxPunishmentVL() && !user.isBanWaiting()) {
                user.setBanWaiting(true);
                new BukkitRunnable() {
                    int i = 0;
                    int max = MathUtil.getRandomInteger(20, 40);
                    @Override
                    public void run() {
                        if (i > max) {
                            user.setWatchdogBan(true);
                            this.cancel();
                        }
                        i++;
                    }
                }.runTaskTimer(Sparky.getInstance(), 20L, 20L);
            }*/

            if (user.getViolation() >= Overflow.getInstance().getConfigManager().getMaxPunishmentVL() && Overflow.getInstance().getConfigManager().isPunishmentsEnabled() && (System.currentTimeMillis() - user.getLastBan()) > 1000L) {
                user.setViolation(0);
                user.setLastBan(System.currentTimeMillis());

                String playerNmae = user.getPlayer().getName();

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        for (String commands : Overflow.getInstance().getConfigManager().getBanCommands()) {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Overflow.getInstance().getConfigLoader().convertColor(commands.replace("%PLAYER%", playerNmae)));
                        }

                        if (Overflow.getInstance().getConfigManager().isPunishmentBroadcast()) {
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    for (String message : Overflow.getInstance().getConfigManager().getBanMessages()) {
                                        Bukkit.broadcastMessage(Overflow.getInstance().getConfigLoader().convertColor(message.replace("%LINE%", " ").replace("%PLAYER%", playerNmae)));
                                    }
                                }
                            }.runTask(Overflow.getLauncherInstance());
                        }

                    }
                }.runTask(Overflow.getLauncherInstance());

            }

            StringBuilder dataStr = new StringBuilder();
            for (String s : data) {
                dataStr.append(s).append((data.length == 1 ? "" : ", "));
            }


            //String alert = ChatColor.YELLOW + "Sparky" + ChatColor.GRAY + " > " + ChatColor.GOLD + user.getPlayer().getName() + ChatColor.GRAY + " has failed " + ChatColor.GOLD + checkName + ChatColor.GRAY + "(" + ChatColor.GOLD + type + ChatColor.GRAY + ")" + ChatColor.RED + " x" + user.getViolation();

            String alert = Overflow.getInstance().getConfigManager().getAlertMessage().replace("%CHECK%", checkName).replace("%TYPE%", type).replace("%VL%", String.valueOf(user.getViolation())).replace("%PLAYER%", user.getPlayer().getName()).replace("%PING%", String.valueOf(user.getLagProcessor().getCurrentPing()));

            if (experimental) {
                alert += " " + ChatColor.RED + "(Experimental)";
            }

            TextComponent textComponent = new TextComponent(alert);

            if (dataStr.length() > 0) {
                textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GRAY + dataStr.toString()).create()));
            }


            Overflow.getInstance().getUserManager().getUsers().stream().parallel().filter(staff -> (staff.getPlayer().isOp() || staff.getPlayer().hasPermission("overflow.alerts")) && staff.isAlerts()).forEach(staff -> staff.getPlayer().spigot().sendMessage(textComponent));

            if (!experimental) {
                if (autobans) user.setViolation(user.getViolation() + 1);
            }

            if (Overflow.getInstance().getSessionLogs().containsKey(user.getUuid())) {
                Overflow.getInstance().getSessionLogs().get(user.getUuid()).add(checkName + type);
            } else {
                List<String> dataList = new ArrayList<>();
                dataList.add(checkName + type);
                Overflow.getInstance().getSessionLogs().put(user.getUuid(), dataList);
                dataList.clear();
            }

            if (Overflow.getInstance().getTotalLogs().containsKey(user.getUuid())) {
                Overflow.getInstance().getTotalLogs().get(user.getUuid()).add(checkName + ":" + type + ":" + user.getViolation() + ";" + Overflow.getInstance().getLogsProcessor().getTime());
            } else {
                List<String> dataList = new ArrayList<>();
                dataList.add(checkName + ":" + type + ":" + user.getViolation() + ";" + Overflow.getInstance().getLogsProcessor().getTime());
                Overflow.getInstance().getTotalLogs().put(user.getUuid(), dataList);
                dataList.clear();
            }
        });
    }


    public void setCheckEnabled(boolean enabledBoolean) {


        this.enabled = enabledBoolean;
        this.allowed = true;

        if (enabledBoolean) {
            Overflow.getInstance().getEventManager().registerListeners(this, Overflow.getLauncherInstance());
        } else {
            Overflow.getInstance().getEventManager().unregisterListener(this);
        }
    }

    public void debug(User user, String s, boolean b) {
        if (b) {
            user.debug(s, checkName, type);
        } else {
            user.debug(s);
        }
    }

    public void debug(User user, String s) {
        user.debug(s);
    }
}
