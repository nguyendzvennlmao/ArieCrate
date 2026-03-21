package me.aris.crates;

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

    public String toSmallCaps(String input) {
        String normal = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        String small = "ᴀʙᴄᴅᴇꜰɢʜɪᴊᴋʟᴍɴᴏᴘǫʀsᴛᴜᴠᴡxʏᴢᴀʙᴄᴅᴇꜰɢʜɪᴊᴋʟᴍɴᴏᴘǫʀsᴛᴜᴠᴡxʏᴢ₀₁₂₃₄₅₆₇₈₉";
        StringBuilder sb = new StringBuilder();
        for (char c : input.toCharArray()) {
            int index = normal.indexOf(c);
            if (index != -1) sb.append(small.charAt(index));
            else sb.append(c);
        }
        return sb.toString();
    }

    public String translateHex(String msg) {
        if (msg == null) return "";
        Matcher matcher = hexPattern.matcher(msg);
        StringBuilder buffer = new StringBuilder();
        while (matcher.find()) {
            matcher.appendReplacement(buffer, ChatColor.of("#" + matcher.group(1)).toString());
        }
        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
    }

    private void runTask(Player p, Runnable r) {
        if (plugin.isFolia()) p.getScheduler().execute(plugin, r, null, 1L);
        else r.run();
    }

    public void openPreview(Player p, String c) {
        runTask(p, () -> {
            FileConfiguration cfg = getCrateConfig(c);
            String title = translateHex(cfg.getString("menu-title", "&#facc15ʀưᴏ̛ɴɢ " + toSmallCaps(c.toUpperCase())));
            Inventory inv = Bukkit.createInventory(null, 27, title);
            if (cfg.contains("rewards")) {
                for (String key : cfg.getConfigurationSection("rewards").getKeys(false)) {
                    inv.setItem(Integer.parseInt(key), cfg.getItemStack("rewards." + key));
                }
            }
            p.openInventory(inv);
            p.playSound(p.getLocation(), Sound.BLOCK_CHEST_OPEN, 1f, 1f);
        });
    }

    public void openConfirmMenu(Player p, String crateName, ItemStack selectedItem) {
        runTask(p, () -> {
            p.setMetadata("opening_crate", new FixedMetadataValue(plugin, crateName));
            String title = translateHex("&#facc15xáᴄ ɴʜậɴ " + toSmallCaps(crateName.toUpperCase()));
            Inventory inv = Bukkit.createInventory(null, 27, title);
            var cfg = plugin.getConfig();
            inv.setItem(cfg.getInt("confirm-gui.cancel-slot"), createItem(cfg.getString("confirm-gui.cancel.material"), cfg.getString("confirm-gui.cancel.name"), cfg.getStringList("confirm-gui.cancel.lore")));
            inv.setItem(cfg.getInt("confirm-gui.display-slot"), selectedItem.clone());
            inv.setItem(cfg.getInt("confirm-gui.confirm-slot"), createItem(cfg.getString("confirm-gui.confirm.material"), cfg.getString("confirm-gui.confirm.name"), cfg.getStringList("confirm-gui.confirm.lore")));
            p.openInventory(inv);
            p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1.2f);
        });
    }

    public FileConfiguration getCrateConfig(String crateName) {
        File file = new File(plugin.getDataFolder() + "/crates", crateName + ".yml");
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

    public void giveKey(Player target, String crateName, int amount) {
        String path = target.getName() + "." + crateName;
        int current = plugin.getKeyConfig().getInt(path, 0);
        plugin.getKeyConfig().set(path, current + amount);
        plugin.saveKeyConfig();
        plugin.sendMsg(target, "receive-key", "%amount%", String.valueOf(amount), "%crate%", crateName);
        String s = plugin.getMsgConfig().getString("messages.receive-key.sound", "ENTITY_PLAYER_LEVELUP");
        target.playSound(target.getLocation(), Sound.valueOf(s), 1f, 1f);
    }

    public boolean takeKey(String p, String c) {
        int cur = plugin.getKeyConfig().getInt(p + "." + c, 0);
        if (cur <= 0) return false;
        plugin.getKeyConfig().set(p + "." + c, cur - 1);
        plugin.saveKeyConfig();
        return true;
    }
    }
