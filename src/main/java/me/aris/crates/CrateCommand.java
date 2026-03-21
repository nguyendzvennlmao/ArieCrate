package me.aris.crates;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import java.io.File;
import java.util.*;

public class CrateCommand implements CommandExecutor, TabCompleter {
    private final ArisCrate plugin;
    public CrateCommand(ArisCrate plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) return true;
        String sub = args[0].toLowerCase();

        if (sub.equals("reload")) {
            plugin.loadFiles();
            if (sender instanceof Player) plugin.sendMsg((Player) sender, "reload-success");
            else sender.sendMessage("§aReloaded successfully!");
            return true;
        }

        if (sub.equals("create") && args.length > 1 && sender instanceof Player) {
            String name = args[1];
            Player p = (Player) sender;
            Location loc = p.getLocation();
            var cfg = plugin.getCrateManager().getCrateConfig(name);
            cfg.set("location.world", loc.getWorld().getName());
            cfg.set("location.x", loc.getBlockX());
            cfg.set("location.y", loc.getBlockY());
            cfg.set("location.z", loc.getBlockZ());
            try { cfg.save(new File(plugin.getDataFolder() + "/crates", name + ".yml")); } catch (Exception e) {}
            plugin.sendMsg(p, "crate-create", "%crate%", name);
        } else if (sub.equals("edit") && args.length > 1 && sender instanceof Player) {
            plugin.getCrateManager().openEditMenu((Player) sender, args[1]);
        } else if (sub.equals("givekey") && args.length > 3) {
            Player target = Bukkit.getPlayer(args[1]);
            if (target != null) plugin.getCrateManager().giveKey(target, args[2], Integer.parseInt(args[3]));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) return Arrays.asList("create", "edit", "givekey", "reload");
        List<String> crates = new ArrayList<>();
        File folder = new File(plugin.getDataFolder(), "crates");
        if (folder.exists() && folder.listFiles() != null) {
            for (File f : folder.listFiles()) if (f.getName().endsWith(".yml")) crates.add(f.getName().replace(".yml", ""));
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("givekey")) return null;
        if (args.length == 2) return crates;
        if (args.length == 3 && args[0].equalsIgnoreCase("givekey")) return crates;
        return new ArrayList<>();
    }
                   }
