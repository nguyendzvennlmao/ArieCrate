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
        plugin.crateConfig().set(path + ".location.world", loc.getWorld().getName());
        plugin.crateConfig().set(path + ".location.x", loc.getBlockX());
        plugin.crateConfig().set(path + ".location.y", loc.getBlockY());
        plugin.crateConfig().set(path + ".location.z", loc.getBlockZ());
        plugin.crateConfig().set(path + ".rows", 3);
        plugin.saveCrateConfig();
    }

    public String getCrateAt(Location loc) {
        if (plugin.crateConfig().getConfigurationSection("crates") == null) return null;
        for (String key : plugin.crateConfig().getConfigurationSection("crates").getKeys(false)) {
            String world = plugin.crateConfig().getString("crates." + key + ".location.world");
            int x = plugin.crateConfig().getInt("crates." + key + ".location.x");
            int y = plugin.crateConfig().getInt("crates." + key + ".location.y");
            int z = plugin.crateConfig().getInt("crates." + key + ".location.z");
            if (loc.getWorld().getName().equals(world) && loc.getBlockX() == x && loc.getBlockY() == y && loc.getBlockZ() == z) return key;
        }
        return null;
    }

    public void openPreview(Player p, String crateName) {
        int rows = plugin.crateConfig().getInt("crates." + crateName + ".rows", 3);
        Inventory inv = Bukkit.createInventory(null, rows * 9, "Preview: " + crateName);
        if (plugin.crateConfig().contains("crates." + crateName + ".rewards")) {
            for (String key : plugin.crateConfig().getConfigurationSection("crates." + crateName + ".rewards").getKeys(false)) {
                inv.setItem(Integer.parseInt(key), plugin.crateConfig().getItemStack("crates." + crateName + ".rewards." + key));
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
        String keyName = plugin.crateConfig().getString("crates." + crateName + ".key.name", "").replace("&", "§");
        for (ItemStack item : p.getInventory().getContents()) {
            if (item != null && item.hasItemMeta() && item.getItemMeta().getDisplayName().equals(keyName)) return true;
        }
        return false;
    }

    public void takeKey(Player p, String crateName) {
        String keyName = plugin.crateConfig().getString("crates." + crateName + ".key.name", "").replace("&", "§");
        for (ItemStack item : p.getInventory().getContents()) {
            if (item != null && item.hasItemMeta() && item.getItemMeta().getDisplayName().equals(keyName)) {
                item.setAmount(item.getAmount() - 1);
                return;
            }
        }
    }

    public void giveRandomReward(Player p, String crateName) {
        var section = plugin.crateConfig().getConfigurationSection("crates." + crateName + ".rewards");
        if (section == null) return;
        ArrayList<String> keys = new ArrayList<>(section.getKeys(false));
        if (keys.isEmpty()) return;
        String randomKey = keys.get(new Random().nextInt(keys.size()));
        ItemStack reward = plugin.crateConfig().getItemStack("crates." + crateName + ".rewards." + randomKey);
        if (reward != null) p.getInventory().addItem(reward.clone());
    }

    public void openEditMenu(Player p, String name) {
        int rows = plugin.crateConfig().getInt("crates." + name + ".rows", 3);
        Inventory inv = Bukkit.createInventory(null, rows * 9, "Editing: " + name);
        if (plugin.crateConfig().contains("crates." + name + ".rewards")) {
            for (String key : plugin.crateConfig().getConfigurationSection("crates." + name + ".rewards").getKeys(false)) {
                inv.setItem(Integer.parseInt(key), plugin.crateConfig().getItemStack("crates." + name + ".rewards." + key));
            }
        }
        p.openInventory(inv);
    }

    public void saveCrateItems(String name, Inventory inv) {
        plugin.crateConfig().set("crates." + name + ".rewards", null);
        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) != null) plugin.crateConfig().set("crates." + name + ".rewards." + i, inv.getItem(i));
        }
        plugin.saveCrateConfig();
    }

    public void giveKey(Player t, String name, int amount) {
        String mat = plugin.crateConfig().getString("crates." + name + ".key.material", "TRIPWIRE_HOOK");
        String dname = plugin.crateConfig().getString("crates." + name + ".key.name", "&eKey " + name).replace("&", "§");
        ItemStack key = new ItemStack(Material.valueOf(mat.toUpperCase()), amount);
        ItemMeta m = key.getItemMeta(); 
        if (m != null) { m.setDisplayName(dname); key.setItemMeta(m); }
        if (t != null) t.getInventory().addItem(key);
    }
    }
