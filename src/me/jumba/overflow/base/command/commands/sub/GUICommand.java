package me.jumba.overflow.base.command.commands.sub;

import me.jumba.overflow.Overflow;
import me.jumba.overflow.base.check.Check;
import me.jumba.overflow.util.gui.GUIUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created on 25/02/2020 Package me.jumba.sparky.base.command.commands.sub
 */
public class GUICommand {

    private int maxSlots = 9 * 3;

    public void execute(String[] args, String s, CommandSender commandSender) {
        Inventory inventory = Bukkit.getServer().createInventory(null, maxSlots, ChatColor.RED + "Overflow");
        Player player = (Player) commandSender;

        List<Check> enabledChecks = new ArrayList<>();

        Overflow.getInstance().getCheckManager().getCheckList().stream().filter(Check::isEnabled).forEach(enabledChecks::add);

        inventory.setItem(11, GUIUtils.generateItem(new ItemStack(Material.DIAMOND_SWORD, 1), ChatColor.RED + "Checks", Collections.singletonList(ChatColor.RED + "Click to manage all checks")));
        inventory.setItem(13, GUIUtils.generateItem(new ItemStack(Material.COMPASS, 1), ChatColor.RED + "Overflow Anticheat", Arrays.asList(ChatColor.RED + "Checks: " + ChatColor.GREEN + enabledChecks.size(), ChatColor.RED + "Loaded User Objects: " + ChatColor.GREEN + Overflow.getInstance().getUserManager().getUsers().size(), " ", ChatColor.RED + "Version: " + ChatColor.GRAY + Overflow.getInstance().getVersion())));


        for (int slots = 0; slots < maxSlots; slots++) {
            if (inventory.getItem(slots) == null) inventory.setItem(slots, GUIUtils.createSpacer((byte) 14));
        }
        player.openInventory(inventory);
    }
}
