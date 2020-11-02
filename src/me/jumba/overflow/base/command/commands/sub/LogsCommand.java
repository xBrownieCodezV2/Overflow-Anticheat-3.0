package me.jumba.overflow.base.command.commands.sub;

import me.jumba.overflow.Overflow;
import me.jumba.overflow.util.http.HTTPUtil;
import me.jumba.overflow.util.string.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created on 07/08/2020 Package me.jumba.overflow.base.command.commands.sub
 */
public class LogsCommand {

    public void execute(String[] args, String s, CommandSender commandSender) {
        try {
            if (args.length > 0) {
                boolean found = false;

                if (args[1].equalsIgnoreCase("clear")) {
                    found = true;
                    try {
                        String name = args[2];
                        if (name.length() > 0) {

                            new Thread(() -> {
                                commandSender.sendMessage(ChatColor.GRAY + "Grabbing UUID for " + ChatColor.RED + name);
                                String uuid = HTTPUtil.getUUID(name);

                                if (uuid != null) {
                                    Overflow.getInstance().getLogsProcessor().removeLogs(uuid);
                                    commandSender.sendMessage(ChatColor.GREEN + "Removed logs for " + ChatColor.GRAY + name);
                                } else {
                                    commandSender.sendMessage(ChatColor.RED + "Unable to find UUID for " + ChatColor.GRAY + name);
                                }
                            }).start();
                        } else {
                            commandSender.sendMessage(ChatColor.RED + "Please supply a name!");
                        }
                    } catch (Exception ignored) {
                        commandSender.sendMessage(ChatColor.RED + "Error in arguments!");
                    }
                }

                if (args[1].equalsIgnoreCase("total")) {
                    found = true;

                    try {
                        String name = args[2];
                        if (name.length() > 0) {

                            new Thread(() -> {
                                commandSender.sendMessage(ChatColor.GRAY + "Grabbing UUID for " + ChatColor.RED + name);
                                String uuid = HTTPUtil.getUUID(name);

                                if (uuid != null) {

                                    List<String> dataFromServer = Overflow.getInstance().getLogsProcessor().getLogs(uuid);

                                    if (dataFromServer.size() > 0) {

                                        HashMap<String, Integer> stack = new HashMap<>();

                                        dataFromServer.forEach(s1 -> {
                                            if (s1 != null && s1.contains(":")) {
                                                String[] data = s1.split(":");

                                                String compile = data[0] + "(" + data[1] + ")";

                                                if (!stack.containsKey(compile)) {
                                                    stack.put(compile, 1);
                                                } else {
                                                    stack.put(compile, stack.get(compile) + 1);
                                                }
                                            }
                                        });

                                        if (dataFromServer.size() > 0) {
                                            String lastFlag = dataFromServer.get(dataFromServer.size() - 1).split(";")[1];

                                            commandSender.sendMessage(ChatColor.GRAY + "Logs for: " + ChatColor.GREEN + name + ChatColor.GRAY + " (total)");
                                            commandSender.sendMessage(ChatColor.GRAY + "Amount of logs: " + ChatColor.GREEN + dataFromServer.size());
                                            commandSender.sendMessage(ChatColor.GRAY + "Last flagged: " + ChatColor.GREEN + lastFlag);

                                            Map<String, Integer> sortedMap = StringUtils.sortMap(stack);

                                            stack.clear();

                                            sortedMap.forEach(((s1, integer) -> commandSender.sendMessage(ChatColor.GRAY + "> " + ChatColor.YELLOW + s1 + ChatColor.GRAY + "(" + ChatColor.RED + integer + ChatColor.GRAY + ")")));

                                            sortedMap.clear();
                                        }
                                    } else {
                                        commandSender.sendMessage(ChatColor.RED + name + " " + ChatColor.GRAY + "has no logs.");
                                    }


                                } else {
                                    commandSender.sendMessage(ChatColor.RED + "Unable to find UUID for " + ChatColor.GRAY + name);
                                }
                            }).start();
                        } else {
                            commandSender.sendMessage(ChatColor.RED + "Please supply a name!");
                        }
                    } catch (Exception ignored) {
                        commandSender.sendMessage(ChatColor.RED + "Error in arguments!");
                    }
                }

                if (args[1].equalsIgnoreCase("session")) {
                    found = true;
                    try {
                        String name = args[2];
                        if (name.length() > 0) {
                            new Thread(() -> {
                                commandSender.sendMessage(ChatColor.GRAY + "Grabbing UUID for " + ChatColor.RED + name);
                                String uuid = HTTPUtil.getUUID(name);

                                if (uuid != null) {

                                    UUID fromStringUUID = UUID.fromString(uuid);

                                    if (Overflow.getInstance().getSessionLogs().containsKey(fromStringUUID)) {
                                        List<String> logs = Overflow.getInstance().getSessionLogs().get(fromStringUUID);
                                        HashMap<String, Integer> stack = new HashMap<>();

                                        logs.forEach(s1 -> {
                                            if (!stack.containsKey(s1)) {
                                                stack.put(s1, 1);
                                            } else {
                                                stack.put(s1, stack.get(s1) + 1);
                                            }
                                        });

                                        if (logs.size() > 0) {
                                            commandSender.sendMessage(ChatColor.GRAY + "Logs for: " + ChatColor.GREEN + name + ChatColor.GRAY + " (current session)");
                                            commandSender.sendMessage(ChatColor.GRAY + "Amount of logs: " + ChatColor.GREEN + logs.size());
                                            stack.forEach(((s1, integer) -> commandSender.sendMessage(ChatColor.GRAY + "> " + ChatColor.YELLOW + s1 + ChatColor.GRAY + "(" + ChatColor.RED + integer + ChatColor.GRAY + ")")));
                                        }
                                    } else {
                                        commandSender.sendMessage(ChatColor.RED + name + " " + ChatColor.GRAY + "has no logs.");
                                    }

                                } else {
                                    commandSender.sendMessage(ChatColor.RED + "Unable to find UUID for " + ChatColor.GRAY + name);
                                }
                            }).start();
                        } else {
                            commandSender.sendMessage(ChatColor.RED + "Please supply a name!");
                        }
                    } catch (Exception ignored) {
                        commandSender.sendMessage(ChatColor.RED + "Error in arguments!");
                    }
                }

                if (!found) {
                    commandSender.sendMessage(ChatColor.RED + "Invalid mode, selectable modes are - " + ChatColor.GRAY + "session, total, clear");
                }
            } else {
                commandSender.sendMessage(ChatColor.RED + "Please supply a mode!");
            }
        } catch (Exception ignored) {
            commandSender.sendMessage(ChatColor.RED + "Invalid mode, selectable modes are - " + ChatColor.GRAY + "session, total, clear");
        }
    }
}
