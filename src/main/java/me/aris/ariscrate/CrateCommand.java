package me.aris.ariscrate;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class CrateCommand implements CommandExecutor, TabCompleter {
    private final ArisCrate plugin;
    public CrateCommand(ArisCrate plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("ariscrate.admin")) return true;
        if (args.length == 0) return false;
        String sub = args[0].toLowerCase();
        if (sub.equals("reload")) {
            plugin.loadFiles();
            if (sender instanceof Player) plugin.sendMsg((Player) sender, "reload-success");
            return true;
        }
        if (sub.equals("create") || sub.equals("movehere")) {
            if (!(sender instanceof Player) || args.length < 2) return true;
            Player p = (Player) sender;
            var loc = p.getTargetBlockExact(5).getLocation();
            var cfg = plugin.getCrateManager().getCrateConfig(args[1]);
            cfg.set("location.world", loc.getWorld().getName());
            cfg.set("location.x", loc.getBlockX());
            cfg.set("location.y", loc.getBlockY());
            cfg.set("location.z", loc.getBlockZ());
            try { cfg.save(new File(plugin.getDataFolder() + "/crates", args[1] + ".yml")); } catch (Exception e) {}
            plugin.sendMsg(p, "crate-create", "%crate%", args[1]);
            return true;
        }
        if (sub.equals("edit")) {
            if (!(sender instanceof Player) || args.length < 2) return true;
            plugin.getCrateManager().openEditMenu((Player) sender, args[1]);
            return true;
        }
        if (sub.equals("givekey") && args.length >= 4) {
            Player target = Bukkit.getPlayer(args[1]);
            if (target != null) {
                plugin.getCrateManager().giveKey(target, args[2], Integer.parseInt(args[3]));
            }
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender s, Command c, String a, String[] args) {
        if (args.length == 1) return Arrays.asList("create", "movehere", "reload", "givekey", "edit");
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("givekey")) return null;
            if (Arrays.asList("create", "movehere", "edit").contains(args[0].toLowerCase())) return getCrates();
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("givekey")) return getCrates();
        if (args.length == 4 && args[0].equalsIgnoreCase("givekey")) return Arrays.asList("1", "10", "64");
        return new ArrayList<>();
    }

    private List<String> getCrates() {
        File folder = new File(plugin.getDataFolder(), "crates");
        if (!folder.exists() || folder.listFiles() == null) return new ArrayList<>();
        return Arrays.stream(folder.listFiles()).map(f -> f.getName().replace(".yml", "")).collect(Collectors.toList());
    }
              }
