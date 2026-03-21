package me.aris.crates;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import net.md_5.bungee.api.ChatColor;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CrateManager {
    private final ArisCrate plugin;
    private final Pattern hexPattern = Pattern.compile("&#([A-Fa-f0-9]{6})");

    public CrateManager(ArisCrate plugin) { this.plugin = plugin; }

    public String translateHex(String msg) {
        if (msg == null) return "";
        Matcher matcher = hexPattern.matcher(msg);
        StringBuilder buffer = new StringBuilder();
        while (matcher.find()) {
            matcher.appendReplacement(buffer, ChatColor.of("#" + matcher.group(1)).toString());
        }
        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
    }

    public void createCrate(String name, Location loc) {
        String path = "crates." + name;
        if (!plugin.getCommonConfig().contains(path + ".title")) {
            plugin.getCommonConfig().set(path + ".title", "&#facc15Xác nhận mở: &f" + name);
        }
        plugin.getCommonConfig().set(path + ".location.world", loc.getWorld().getName());
        plugin.getCommonConfig().set(path + ".location.x", loc.getBlockX());
        plugin.getCommonConfig().set(path + ".location.y", loc.getBlockY());
        plugin.getCommonConfig().set(path + ".location.z", loc.getBlockZ());
        plugin.saveCommonConfig();
    }

    public void openConfirmMenu(Player p, String crateName, ItemStack selectedItem) {
        String rawTitle = plugin.getCommonConfig().getString("crates." + crateName + ".title", "Confirm");
        Inventory inv = Bukkit.createInventory(null, 27, translateHex(rawTitle));
        
        var cfg = plugin.getConfig();
        inv.setItem(cfg.getInt("confirm-gui.cancel-slot", 11), createItem(cfg.getString("confirm-gui.cancel.material"), cfg.getString("confirm-gui.cancel.name"), cfg.getStringList("confirm-gui.cancel.lore")));
        inv.setItem(cfg.getInt("confirm-gui.display-slot", 13), selectedItem);
        inv.setItem(cfg.getInt("confirm-gui.confirm-slot", 15), createItem(cfg.getString("confirm-gui.confirm.material"), cfg.getString("confirm-gui.confirm.name"), cfg.getStringList("confirm-gui.confirm.lore")));
        
        p.openInventory(inv);
    }

    private ItemStack createItem(String mat, String name, List<String> lore) {
        ItemStack item = new ItemStack(Material.valueOf(mat));
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(translateHex(name));
            List<String> l = new ArrayList<>();
            for (String s : lore) l.add(translateHex(s));
            meta.setLore(l);
            item.setItemMeta(meta);
        }
        return item;
    }

    public String getCrateAt(Location loc) {
        if (plugin.getCommonConfig().getConfigurationSection("crates") == null) return null;
        for (String key : plugin.getCommonConfig().getConfigurationSection("crates").getKeys(false)) {
            String w = plugin.getCommonConfig().getString("crates." + key + ".location.world");
            int x = plugin.getCommonConfig().getInt("crates." + key + ".location.x");
            int y = plugin.getCommonConfig().getInt("crates." + key + ".location.y");
            int z = plugin.getCommonConfig().getInt("crates." + key + ".location.z");
            if (loc.getWorld().getName().equals(w) && loc.getBlockX() == x && loc.getBlockY() == y && loc.getBlockZ() == z) return key;
        }
        return null;
    }

    public void openEditMenu(Player p, String name) {
        Inventory inv = Bukkit.createInventory(null, 27, "Editing: " + name);
        if (plugin.getCommonConfig().contains("crates." + name + ".rewards")) {
            for (String key : plugin.getCommonConfig().getConfigurationSection("crates." + name + ".rewards").getKeys(false)) {
                inv.setItem(Integer.parseInt(key), plugin.getCommonConfig().getItemStack("crates." + name + ".rewards." + key));
            }
        }
        p.openInventory(inv);
    }

    public void saveCrateItems(String name, Inventory inv) {
        plugin.getCommonConfig().set("crates." + name + ".rewards", null);
        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) != null) plugin.getCommonConfig().set("crates." + name + ".rewards." + i, inv.getItem(i));
        }
        plugin.saveCommonConfig();
    }

    public void deleteCrate(String name) {
        plugin.getCommonConfig().set("crates." + name, null);
        plugin.saveCommonConfig();
    }

    public void giveKey(Player target, String crateName, int amount) {
        int current = plugin.getKeyConfig().getInt(target.getName() + "." + crateName, 0);
        plugin.getKeyConfig().set(target.getName() + "." + crateName, current + amount);
        plugin.saveKeyConfig();
    }

    public boolean takeKey(String p, String c) {
        int cur = plugin.getKeyConfig().getInt(p + "." + c, 0);
        if (cur <= 0) return false;
        plugin.getKeyConfig().set(p + "." + c, cur - 1);
        plugin.saveKeyConfig();
        return true;
    }

    public void openPreview(Player p, String c) {
        Inventory inv = Bukkit.createInventory(null, 27, "Preview: " + c);
        if (plugin.getCommonConfig().contains("crates." + c + ".rewards")) {
            for (String key : plugin.getCommonConfig().getConfigurationSection("crates." + c + ".rewards").getKeys(false)) {
                inv.setItem(Integer.parseInt(key), plugin.getCommonConfig().getItemStack("crates." + c + ".rewards." + key));
            }
        }
        p.openInventory(inv);
    }
                                     }
