package me.aris.crates;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.*;

public class CrateManager {
    private final ArisCrate plugin;
    public CrateManager(ArisCrate plugin) { this.plugin = plugin; }

    // Thêm key vào đúng loại rương (Ví dụ: crimson, common)
    public void addKey(String playerName, String crateName, int amount) {
        int current = plugin.getKeyConfig().getInt(playerName + "." + crateName, 0);
        plugin.getKeyConfig().set(playerName + "." + crateName, current + amount);
        plugin.saveKeyConfig();
    }

    // Lấy số lượng key của đúng loại rương
    public int getKeys(String playerName, String crateName) {
        return plugin.getKeyConfig().getInt(playerName + "." + crateName, 0);
    }

    // Trừ key của đúng loại rương khi mở
    public boolean takeKey(String playerName, String crateName) {
        int current = getKeys(playerName, crateName);
        if (current <= 0) return false;
        plugin.getKeyConfig().set(playerName + "." + crateName, current - 1);
        plugin.saveKeyConfig();
        return true;
    }

    public void openPreview(Player p, String crateName) {
        int rows = plugin.getCrateConfig().getInt("crates." + crateName + ".rows", 3);
        // Đặt tiêu đề GUI chứa ID rương để Listener nhận diện đúng rương nào đang được mở
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
        ItemStack yes = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemMeta ym = yes.getItemMeta(); ym.setDisplayName("§a§lXÁC NHẬN MỞ " + crateName.toUpperCase()); yes.setItemMeta(ym);
        ItemStack no = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta nm = no.getItemMeta(); nm.setDisplayName("§c§lHỦY BỎ"); no.setItemMeta(nm);
        inv.setItem(3, yes); inv.setItem(5, no);
        p.openInventory(inv);
    }

    public void giveRandomReward(Player p, String crateName) {
        var section = plugin.getCrateConfig().getConfigurationSection("crates." + crateName + ".rewards");
        if (section == null) return;
        List<String> keys = new ArrayList<>(section.getKeys(false));
        if (keys.isEmpty()) return;
        String rKey = keys.get(new Random().nextInt(keys.size()));
        ItemStack item = plugin.getCrateConfig().getItemStack("crates." + crateName + ".rewards." + rKey);
        if (item != null) p.getInventory().addItem(item.clone());
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

    public void createCrate(String name, Location loc) {
        String path = "crates." + name;
        plugin.getCrateConfig().set(path + ".location.world", loc.getWorld().getName());
        plugin.getCrateConfig().set(path + ".location.x", loc.getBlockX());
        plugin.getCrateConfig().set(path + ".location.y", loc.getBlockY());
        plugin.getCrateConfig().set(path + ".location.z", loc.getBlockZ());
        plugin.getCrateConfig().set(path + ".rows", 3);
        plugin.saveCrateConfig();
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
    }
