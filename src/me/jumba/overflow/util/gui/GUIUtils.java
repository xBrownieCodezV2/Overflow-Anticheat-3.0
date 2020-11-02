package me.jumba.overflow.util.gui;

import me.jumba.overflow.util.math.MathUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

/**
 * Created on 07/12/2019 Package me.jumba.sparky.utils
 */
public class GUIUtils {
    public static ItemStack generateItem(ItemStack itemStack, String itemName, List<String> meta) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setLore(meta);
        itemMeta.setDisplayName(itemName);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack createSpacer() {
        ItemStack i = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 15);
        ItemMeta im = i.getItemMeta();
        im.setDisplayName(" ");
        i.setItemMeta(im);
        return i;
    }

    public static ItemStack createSpacer(byte color) {
        ItemStack i = new ItemStack(Material.STAINED_GLASS_PANE, 1, color);
        ItemMeta im = i.getItemMeta();
        im.setDisplayName(" ");
        i.setItemMeta(im);
        return i;
    }


    public static ItemStack randomColorSpacer() {
        ItemStack i = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) MathUtil.getRandomInteger(0, 20));
        ItemMeta im = i.getItemMeta();
        im.setDisplayName(" ");
        i.setItemMeta(im);
        return i;
    }
}
