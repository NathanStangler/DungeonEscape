package net.dungeonescape.common;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class DungeonItems {
    public static final ItemStack LEAPER = createLeaper();
    public static final ItemStack PICKAXE = createPickaxe();
    public static final ItemStack BOW = createBow();

    private static ItemStack createLeaper() {
        ItemStack item = new ItemStack(Material.TOTEM_OF_UNDYING);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("Leaper").color(TextColor.color(148, 223, 255)).decoration(TextDecoration.BOLD, true).decoration(TextDecoration.ITALIC, false));
        meta.addItemFlags(ItemFlag.values());
        List<Component> lore = List.of(Component.text("Launches player towards highest block.").color(TextColor.color(180, 192, 255)).decoration(TextDecoration.ITALIC, false), Component.text("Cooldown: 10 seconds").color(TextColor.color(255, 81, 81)).decoration(TextDecoration.BOLD, true).decoration(TextDecoration.ITALIC, false));
        meta.lore(lore);
        meta.addEnchant(Enchantment.LOYALTY, 1, true);
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack createPickaxe() {
        ItemStack item = new ItemStack(Material.DIAMOND_PICKAXE);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("Pickaxe").color(TextColor.color(74, 171, 255)).decoration(TextDecoration.BOLD, true).decoration(TextDecoration.ITALIC, false));
        meta.addItemFlags(ItemFlag.values());
        meta.setUnbreakable(true);
        List<Component> lore = List.of(Component.text("Break blocks to find the key.").color(TextColor.color(180, 192, 255)).decoration(TextDecoration.ITALIC, false));
        meta.lore(lore);
        meta.addEnchant(Enchantment.DIG_SPEED, 1, true);
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack createBow() {
        ItemStack item = new ItemStack(Material.BOW);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("Bow").color(TextColor.color(255, 178, 46)).decoration(TextDecoration.BOLD, true).decoration(TextDecoration.ITALIC, false));
        meta.addItemFlags(ItemFlag.values());
        meta.setUnbreakable(true);
        List<Component> lore = List.of(Component.text("Use to attack the boss from a distance.").color(TextColor.color(180, 192, 255)).decoration(TextDecoration.ITALIC, false));
        meta.lore(lore);
        meta.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
        meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack getEnchanted(Material material, Enchantment enchantment, int level) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.values());
        meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addEnchant(enchantment, level, true);
        item.setItemMeta(meta);
        return item;
    }
}
