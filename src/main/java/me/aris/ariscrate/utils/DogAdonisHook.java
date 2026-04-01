package me.aris.ariscrate.utils;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class DogAdonisHook {
    public static ItemStack freeze(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        meta.getPersistentDataContainer().set(new NamespacedKey(Bukkit.getPluginManager().getPlugin("ArisCrate"), "frozen"), PersistentDataType.LONG, 1000L);
        item.setItemMeta(meta);
        return item;
    }
    public static ItemStack unfreeze(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        meta.getPersistentDataContainer().remove(new NamespacedKey(Bukkit.getPluginManager().getPlugin("ArisCrate"), "frozen"));
        item.setItemMeta(meta);
        return item;
    }
          }
