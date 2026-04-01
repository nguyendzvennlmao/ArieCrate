package me.aris.ariscrate;

import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import net.md_5.bungee.api.ChatColor;
import java.io.File;
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

    public String toSmallCaps(String input) {
        String normal = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        String small = "ᴀʙᴄᴅᴇꜰɢʜɪᴊᴋʟᴍɴᴏᴘǫʀsᴛᴜᴠᴡxʏᴢᴀʙᴄᴅᴇꜰɢʜɪᴊᴋʟᴍɴᴏᴘǫʀsᴛᴜᴠᴡxʏᴢ0123456789";
        StringBuilder sb = new StringBuilder();
        for (char c : input.toCharArray()) {
            int index = normal.indexOf(c);
            if (index != -1) sb.append(small.charAt(index));
            else sb.append(c);
        }
        return sb.toString();
    }

    public void playSound(Player p, String path) {
        String soundName = plugin.getConfig().getString("sounds." + path);
        if (soundName != null) {
            try { p.playSound(p.getLocation(), Sound.valueOf(soundName), 1f, 1f); } catch (Exception e) {}
        }
    }

    public void runTask(Player p, Runnable r) {
        if (plugin.isFolia()) p.getScheduler().execute(plugin, r, null, 1L);
        else r.run();
    }

    public void openPreview(Player p, String c) {
        runTask(p, () -> {
            FileConfiguration cfg = getCrateConfig(c);
            String title = translateHex(cfg.getString("display-name", "&8ᴄʀᴀᴛᴇ " + toSmallCaps(c)));
            int rows = cfg.getInt("rows", 3) * 9;
            Inventory inv = Bukkit.createInventory(null, rows, title);
            if (cfg.contains("rewards")) {
                for (String key : cfg.getConfigurationSection("rewards").getKeys(false)) {
                    inv.setItem(Integer.parseInt(key), cfg.getItemStack("rewards." + key));
                }
            }
            p.setMetadata("current_viewing_crate", new FixedMetadataValue(plugin, c));
            p.openInventory(inv);
            playSound(p, "open-preview");
        });
    }

    public void openEditMenu(Player p, String name) {
        runTask(p, () -> {
            FileConfiguration cfg = getCrateConfig(name);
            int rows = cfg.getInt("rows", 3) * 9;
            Inventory inv = Bukkit.createInventory(null, rows, "Editing: " + name);
            if (cfg.contains("rewards")) {
                for (String key : cfg.getConfigurationSection("rewards").getKeys(false)) {
                    inv.setItem(Integer.parseInt(key), cfg.getItemStack("rewards." + key));
                }
            }
            p.setMetadata("editing_crate_name", new FixedMetadataValue(plugin, name));
            p.openInventory(inv);
        });
    }

    public void openConfirmMenu(Player p, String crateName, ItemStack selectedItem) {
        runTask(p, () -> {
            p.setMetadata("opening_crate", new FixedMetadataValue(plugin, crateName));
            String title = translateHex("&#facc15xáᴄ ɴʜậɴ " + toSmallCaps(crateName));
            Inventory inv = Bukkit.createInventory(null, 27, title);
            var cfg = plugin.getConfig();
            inv.setItem(cfg.getInt("confirm-gui.cancel-slot"), createItem(cfg.getString("confirm-gui.cancel.material"), cfg.getString("confirm-gui.cancel.name"), cfg.getStringList("confirm-gui.cancel.lore")));
            inv.setItem(cfg.getInt("confirm-gui.display-slot"), selectedItem.clone());
            inv.setItem(cfg.getInt("confirm-gui.confirm-slot"), createItem(cfg.getString("confirm-gui.confirm.material"), cfg.getString("confirm-gui.confirm.name"), cfg.getStringList("confirm-gui.confirm.lore")));
            p.openInventory(inv);
        });
    }

    public void saveCrateItems(String name, Inventory inv) {
        FileConfiguration cfg = getCrateConfig(name);
        cfg.set("rewards", null);
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);
            if (item != null && item.getType() != Material.AIR) cfg.set("rewards." + i, item);
        }
        try { cfg.save(new File(plugin.getDataFolder() + "/crates", name + ".yml")); } catch (Exception e) {}
    }

    public FileConfiguration getCrateConfig(String crateName) {
        File file = new File(plugin.getDataFolder() + "/crates", crateName + ".yml");
        if (!file.exists()) {
            try { 
                file.getParentFile().mkdirs();
                file.createNewFile();
                FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
                cfg.set("display-name", "&8ᴄʀᴀᴛᴇ " + toSmallCaps(crateName));
                cfg.set("rows", 3);
                cfg.save(file);
            } catch (Exception e) {}
        }
        return YamlConfiguration.loadConfiguration(file);
    }

    public String getCrateAt(Location loc) {
        File folder = new File(plugin.getDataFolder(), "crates");
        if (!folder.exists() || folder.listFiles() == null) return null;
        for (File file : folder.listFiles()) {
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
            if (cfg.contains("location") && 
                cfg.getString("location.world", "").equals(loc.getWorld().getName()) &&
                cfg.getInt("location.x") == loc.getBlockX() &&
                cfg.getInt("location.y") == loc.getBlockY() &&
                cfg.getInt("location.z") == loc.getBlockZ()) {
                return file.getName().replace(".yml", "");
            }
        }
        return null;
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

    public boolean takeKey(String p, String c) {
        int cur = plugin.getKeyConfig().getInt(p + "." + c, 0);
        if (cur <= 0) return false;
        plugin.getKeyConfig().set(p + "." + c, cur - 1);
        plugin.saveKeyConfig();
        return true;
    }
    }
