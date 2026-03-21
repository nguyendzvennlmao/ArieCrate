package me.aris.crates;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.md_5.bungee.api.ChatColor;

public class CrateManager {
    private final ArisCrate plugin;
    public CrateManager(ArisCrate plugin) { this.plugin = plugin; }

    public String translateHex(String msg) {
        if (msg == null) return "";
        Pattern pattern = Pattern.compile("&#([A-Fa-f0-9]{6})");
        Matcher matcher = pattern.matcher(msg);
        StringBuilder buffer = new StringBuilder();
        while (matcher.find()) {
            matcher.appendReplacement(buffer, ChatColor.of("#" + matcher.group(1)).toString());
        }
        return matcher.appendTail(buffer).toString().replace("&", "§");
    }

    public void createCrate(String name, Location loc) {
        plugin.getCrateConfig().set("crates." + name + ".location.world", loc.getWorld().getName());
        plugin.getCrateConfig().set("crates." + name + ".location.x", loc.getBlockX());
        plugin.getCrateConfig().set("crates." + name + ".location.y", loc.getBlockY());
        plugin.getCrateConfig().set("crates." + name + ".location.z", loc.getBlockZ());
        plugin.saveCrateConfig();
    }

    public void deleteCrate(String name) {
        plugin.getCrateConfig().set("crates." + name, null);
        plugin.saveCrateConfig();
    }

    public void openEditMenu(Player p, String name) {
        Inventory inv = Bukkit.createInventory(null, 27, "Editing: " + name);
        if (plugin.getCrateConfig().contains("crates." + name + ".rewards")) {
            for (String key : plugin.getCrateConfig().getConfigurationSection("crates." + name + ".rewards").getKeys(false)) {
                inv.setItem(Integer.parseInt(key), plugin.getCrateConfig().getItemStack("crates." + name + ".rewards." + key));
            }
        }
        p.openInventory(inv);
    }

    public void saveCrateItems(String name, Inventory inv) {
        plugin.getCrateConfig().set("crates." + name + ".rewards", null);
        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) != null) plugin.getCrateConfig().set("crates." + name + ".rewards." + i, inv.getItem(i));
        }
        plugin.saveCrateConfig();
    }

    public void openConfirmMenu(Player p, String crateName, ItemStack selectedItem) {
        Inventory inv = Bukkit.createInventory(null, 27, "§8Xác nhận: §1" + crateName);
        var cfg = plugin.getConfig();
        
        ItemStack cancel = new ItemStack(Material.valueOf(cfg.getString("confirm-gui.cancel.material", "RED_STAINED_GLASS_PANE")));
        ItemMeta nm = cancel.getItemMeta();
        nm.setDisplayName(translateHex(cfg.getString("confirm-gui.cancel.name")));
        cancel.setItemMeta(nm);

        ItemStack confirm = new ItemStack(Material.valueOf(cfg.getString("confirm-gui.confirm.material", "LIME_STAINED_GLASS_PANE")));
        ItemMeta cm = confirm.getItemMeta();
        cm.setDisplayName(translateHex(cfg.getString("confirm-gui.confirm.name")));
        confirm.setItemMeta(cm);

        inv.setItem(cfg.getInt("confirm-gui.cancel-slot", 11), cancel);
        inv.setItem(cfg.getInt("confirm-gui.display-slot", 13), selectedItem);
        inv.setItem(cfg.getInt("confirm-gui.confirm-slot", 15), confirm);
        p.openInventory(inv);
    }

    public void openPreview(Player p, String crateName) {
        Inventory inv = Bukkit.createInventory(null, 27, "Preview: " + crateName);
        if (plugin.getCrateConfig().contains("crates." + crateName + ".rewards")) {
            for (String key : plugin.getCrateConfig().getConfigurationSection("crates." + crateName + ".rewards").getKeys(false)) {
                inv.setItem(Integer.parseInt(key), plugin.getCrateConfig().getItemStack("crates." + crateName + ".rewards." + key));
            }
        }
        p.openInventory(inv);
    }

    public String getCrateAt(Location loc) {
        if (plugin.getCrateConfig().getConfigurationSection("crates") == null) return null;
        for (String key : plugin.getCrateConfig().getConfigurationSection("crates").getKeys(false)) {
            String w = plugin.getCrateConfig().getString("crates." + key + ".location.world");
            int x = plugin.getCrateConfig().getInt("crates." + key + ".location.x");
            int y = plugin.getCrateConfig().getInt("crates." + key + ".location.y");
            int z = plugin.getCrateConfig().getInt("crates." + key + ".location.z");
            if (loc.getWorld().getName().equals(w) && loc.getBlockX() == x && loc.getBlockY() == y && loc.getBlockZ() == z) return key;
        }
        return null;
    }

    public void giveKey(Player target, String crateName, int amount) {
        int current = plugin.getKeyConfig().getInt(target.getName() + "." + crateName, 0);
        plugin.getKeyConfig().set(target.getName() + "." + crateName, current + amount);
        plugin.saveKeyConfig();
    }

    public int getKeys(String playerName, String crateName) {
        return plugin.getKeyConfig().getInt(playerName + "." + crateName, 0);
    }

    public boolean takeKey(String playerName, String crateName) {
        int current = getKeys(playerName, crateName);
        if (current <= 0) return false;
        plugin.getKeyConfig().set(playerName + "." + crateName, current - 1);
        plugin.saveKeyConfig();
        return true;
    }
                                             }
