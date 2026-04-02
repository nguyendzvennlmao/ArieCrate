package me.aris.ariscrate.utils;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.lang.reflect.Method;
import java.time.Instant;

public class DogAdonisHook {
    private static final String FROZEN_KEY = "frozen_remaining";
    private static Class<?> pickaxeClass;
    private static Method isPickaxe;
    private static Method getExpiry;
    private static Method setExpiry;

    public static boolean available() {
        return pickaxeClass != null && Bukkit.getPluginManager().getPlugin("DogAdonis") != null;
    }

    public static ItemStack freeze(ItemStack item) {
        if (!available()) return item;

        try {
            Object plugin = Bukkit.getPluginManager().getPlugin("DogAdonis");
            boolean ok = (Boolean) isPickaxe.invoke(null, item, plugin);
            if (!ok) return item;

            long expiry = (Long) getExpiry.invoke(null, item, plugin);
            long remaining = expiry - Instant.now().toEpochMilli();

            ItemMeta meta = item.getItemMeta();
            meta.getPersistentDataContainer().set(
                new NamespacedKey(Bukkit.getPluginManager().getPlugin("ArisCrate"), FROZEN_KEY),
                PersistentDataType.LONG,
                remaining
            );
            item.setItemMeta(meta);
        } catch (Exception ignored) {}

        return item;
    }

    public static ItemStack unfreeze(ItemStack item) {
        if (!available()) return item;

        NamespacedKey key = new NamespacedKey(Bukkit.getPluginManager().getPlugin("ArisCrate"), FROZEN_KEY);
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer c = meta.getPersistentDataContainer();

        if (!c.has(key, PersistentDataType.LONG)) return item;

        long remaining = c.get(key, PersistentDataType.LONG);
        c.remove(key);
        item.setItemMeta(meta);

        try {
            Object plugin = Bukkit.getPluginManager().getPlugin("DogAdonis");
            setExpiry.invoke(null, item, Instant.now().toEpochMilli() + remaining, plugin);
        } catch (Exception ignored) {}

        return item;
    }

    static {
        try {
            pickaxeClass = Class.forName("com.devtrucanh.dogadonis.util.PickaxeItem");
            isPickaxe = pickaxeClass.getMethod("isAmethystPickaxe", ItemStack.class, Class.forName("com.devtrucanh.dogadonis.DogAdonis"));
            getExpiry = pickaxeClass.getMethod("getExpiry", ItemStack.class, Class.forName("com.devtrucanh.dogadonis.DogAdonis"));
            setExpiry = pickaxeClass.getMethod("setExpiry", ItemStack.class, Long.TYPE, Class.forName("com.devtrucanh.dogadonis.DogAdonis"));
        } catch (Exception ignored) {}
    }
              }
