package me.jumba.overflow.gui;

import me.jumba.overflow.Overflow;
import me.jumba.overflow.base.check.CheckType;
import me.jumba.overflow.util.gui.GUIUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;

/**
 * Created on 18/03/2020 Package me.jumba.sparky.gui
 */
public class ChecksGUI {

    private int maxSlots = 9 * 3;

    public void openCheckCatGUI(Player player) {
        Inventory inventory = Bukkit.getServer().createInventory(null, maxSlots, ChatColor.RED + "Overflow | Checks");

        inventory.setItem(12, GUIUtils.generateItem(new ItemStack(Material.BOW, 1), ChatColor.RED + "Combat", Collections.singletonList(ChatColor.RED + "Click here to manage all combat checks")));
        inventory.setItem(13, GUIUtils.generateItem(new ItemStack(Material.FEATHER, 1), ChatColor.RED + "Movement", Collections.singletonList(ChatColor.RED + "Click here to manage all movement checks")));
        inventory.setItem(14, GUIUtils.generateItem(new ItemStack(Material.FLINT, 1), ChatColor.RED + "Other", Collections.singletonList(ChatColor.RED + "Click here to manage all other checks")));

        for (int slots = 0; slots < maxSlots; slots++) {
            if (inventory.getItem(slots) == null) inventory.setItem(slots, GUIUtils.createSpacer((byte) 14));
        }
        player.openInventory(inventory);
    }

    public void openChecksMManagerGUI(Player player, String cata) {
        Inventory inventory = Bukkit.getServer().createInventory(null, 54, ChatColor.RED + "Overflow | " + cata);

        final int[] slot = {0};

        Overflow.getInstance().getCheckManager().getCheckList().forEach(check -> {
            CheckType checkType = CheckType.COMBAT;

            switch (cata) {
                case "Movement": {
                    checkType = CheckType.MOVEMENT;
                    break;
                }
                case "Other": {
                    checkType = CheckType.OTHER;
                    break;
                }
            }

            if (check.getCheckType() == checkType) {
                inventory.setItem(slot[0], GUIUtils.generateItem(new ItemStack((check.isEnabled() ? (check.isAutobans() ? Material.EMERALD_BLOCK : Material.GOLD_BLOCK) : Material.REDSTONE_BLOCK), 1), (check.isEnabled() ? (check.isAutobans() ? ChatColor.GREEN : ChatColor.GOLD) : ChatColor.RED) + check.getCheckName() + "(" + check.getType() + ")", Arrays.asList(ChatColor.LIGHT_PURPLE + "Enabled: " + (check.isEnabled() ? ChatColor.GREEN : ChatColor.RED) + check.isEnabled(), ChatColor.LIGHT_PURPLE + "Autobans: " + (check.isAutobans() ? ChatColor.GREEN : ChatColor.RED) + check.isAutobans(), " ")));
                slot[0]++;
            }
        });

        for (int slots = 0; slots < 54; slots++) {
            if (inventory.getItem(slots) == null) inventory.setItem(slots, GUIUtils.createSpacer((byte) 14));
        }
        player.openInventory(inventory);
    }
}
