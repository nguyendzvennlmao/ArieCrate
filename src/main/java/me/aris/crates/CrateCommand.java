package me.aris.crates;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.*;

public class CrateCommand implements TabExecutor {
    private final ArisCrate plugin;
    public CrateCommand(ArisCrate plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player p = (Player) sender;
        if (args.length < 2) return true;
        String sub = args[0].toLowerCase();
        String name = args[1];
        Location loc = p.getLocation().getBlock().getLocation();

        if (sub.equals("create") || sub.equals("movehere")) {
            plugin.getCrateManager().createCrate(name, loc);
            p.sendMessage("§7-----------------------");
            p.sendMessage("§aĐã lưu Crate: §f" + name);
            p.sendMessage("§aLocation: §e" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ());
            p.sendMessage("§7-----------------------");
        } else if (sub.equals("edit")) {
            plugin.getCrateManager().openEditMenu(p, name);
        } else if (sub.equals("givekey")) {
            if (args.length < 4) return true;
            Player t = Bukkit.getPlayer(args[2]);
            int amount = Integer.parseInt(args[3]);
            String mat = plugin.getCrateConfig().getString("crates." + name + ".key.material", "TRIPWIRE_HOOK");
            String dname = plugin.getCrateConfig().getString("crates." + name + ".key.name", "&eKey " + name).replace("&", "§");
            ItemStack key = new ItemStack(Material.valueOf(mat.toUpperCase()), amount);
            ItemMeta m = key.getItemMeta(); m.setDisplayName(dname); key.setItemMeta(m);
            if (t != null) t.getInventory().addItem(key);
            p.sendMessage(plugin.getMessage("give-key-success").replace("%player%", args[2]).replace("%crate%", name).replace("%amount%", ""+amount));
        } else if (sub.equals("delete")) {
            plugin.getCrateConfig().set("crates." + name, null);
            plugin.saveCrateConfig();
            p.sendMessage(plugin.getMessage("delete-success").replace("%crate%", name));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender s, Command c, String a, String[] args) {
        if (args.length == 1) return List.of("create", "edit", "movehere", "delete", "givekey");
        return Collections.emptyList();
    }
            }
