package me.aris.crates;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.Random;
import java.util.ArrayList;

public class CrateManager {
    private final ArisCrate plugin;
    public CrateManager(ArisCrate plugin) { this.plugin = plugin; }

    public void createCrate(String name, Location loc) {
        String path = "crates." + name;
        plugin.getCrateConfig().set(path + ".location.world", loc.getWorld().getName());
        plugin.getCrateConfig().set(path + ".location.x", loc.getBlockX());
        plugin.getCrateConfig().set(path + ".location.y", loc.getBlockY());
        plugin.getCrateConfig().set(path + ".location.z", loc.getBlockZ());
        plugin.saveCrateConfig();
    }

    public String getCrateAt(Location loc) {
        if (plugin.getCrateConfig().getConfigurationSection("crates") == null) return null;
        for (String key : plugin.getCrateConfig().getConfigurationSection("crates").getKeys(false)) {
            String world = plugin.getCrateConfig().getString("crates." + key + ".location.world");
            int x = plugin.getCrateConfig().getInt("crates." + key + ".location.x");
            int y = plugin.getCrateConfig().getInt("crates." + key + ".location.y");
            int z = plugin.getCrateConfig().getInt("crates." + key + ".location.z");
            if (loc.getWorld().getName().equals(world) && loc.getBlockX() == x && loc.getBlockY() == y && loc.getBlockZ() == z) return key;
        }
        return null;
    }

    public void openPreview(Player p, String crateName) {
        int rows = plugin.getCrateConfig().getInt("crates." + crateName + ".rows", 3);
        Inventory inv = Bukkit.createInventory(null, rows * 9, "Preview: " + crateName);
        if (plugin.getCrateConfig().contains("crates." + crateName + ".rewards")) {
            for (String key : plugin.getCrateConfig().getConfigurationSection("crates." + crateName + ".rewards").getKeys(false)) {
                inv.setItem(Integer.parseInt(key), plugin.getCrateConfig().getItemStack("crates." + crateName + ".rewards." + key));
            }
        }
        p.openInventory(inv);
    }

    public void openConfirmMenu(Player p, String crateName) {
        Inventory inv = Bukkit.createInventory(null, 9, "Confirm: " + crateName);
        ItemStack yes = new ItemStack(Material.LIME_WOOL);
        ItemMeta ym = yes.getItemMeta(); ym.setDisplayName("§a§lXÁC NHẬN MỞ"); yes.setItemMeta(ym);
        ItemStack no = new ItemStack(Material.RED_WOOL);
        ItemMeta nm = no.getItemMeta(); nm.setDisplayName("§c§lHỦY BỎ"); no.setItemMeta(nm);
        inv.setItem(3, yes); inv.setItem(5, no);
        p.openInventory(inv);
    }

    public boolean hasCorrectKey(Player p, String crateName) {
        String keyName = plugin.getCrateConfig().getString("crates." + crateName + ".key.name", "").replace("&", "§");
        for (ItemStack item : p.getInventory().getContents()) {
            if (item != null && item.hasItemMeta() && item.getItemMeta().getDisplayName().equals(keyName)) return true;
        }
        return false;
    }

    public void takeKey(Player p, String crateName) {
        String keyName = plugin.getCrateConfig().getString("crates." + crateName + ".key.name", "").replace("&", "§");
        for (ItemStack item : p.getInventory().getContents()) {
            if (item != null && item.hasItemMeta() && item.getItemMeta().getDisplayName().equals(keyName)) {
                item.setAmount(item.getAmount() - 1);
                return;
            }
        }
    }

    public void giveRandomReward(Player p, String crateName) {
        var section = plugin.getCrateConfig().getConfigurationSection("crates." + crateName + ".rewards");
        if (section == null) return;
        ArrayList<String> keys = new ArrayList<>(section.getKeys(false));
        String randomKey = keys.get(new Random().nextInt(keys.size()));
        ItemStack reward = plugin.getCrateConfig().getItemStack("crates." + crateName + ".rewards." + randomKey);
        if (reward != null) p.getInventory().addItem(reward.clone());
    }

    public void openEditMenu(Player p, String name) {
        int rows = plugin.getCrateConfig().getInt("crates." + name + ".rows", 3);
        Inventory inv = Bukkit.createInventory(null, rows * 9, "Editing: " + name);
        if (plugin.getCrateConfig().contains("crates." + name + ".rewards")) {
            for (String key : plugin.getCrateConfig().getConfigurationSection("crates." + name + ".rewards").getKeys(false)) {
                inv.setItem(Integer.parseInt(key), plugin.getCrateConfig().getItemStack("crates." + name + ".rewards." + key));
            }
        }
        p.openInventory(inv);
    }

    public void saveEdit(String name, Inventory inv) {
        plugin.getCrateConfig().set("crates." + name + ".rewards", null);
        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) != null) plugin.getCrateConfig().set("crates." + name + ".rewards." + i, inv.getItem(i));
        }
        plugin.saveCrateConfig();
    }
    }
