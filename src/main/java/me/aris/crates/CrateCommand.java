package me.aris.crates;

import org.bukkit.Bukkit;
import org.bukkit.Location;
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
        if (args.length == 0) return true;
        String sub = args[0].toLowerCase();
        if (sub.equals("reload")) {
            plugin.loadFiles();
            if (sender instanceof Player) plugin.sendMsg((Player) sender, "reload-success");
            else sender.sendMessage("✔ Reloaded successfully!");
            return true;
        }
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (sub.equals("create") || sub.equals("movehere")) {
                if (args.length < 2) return true;
                String name = args[1];
                Location loc = p.getLocation();
                var cfg = plugin.getCrateManager().getCrateConfig(name);
                cfg.set("location.world", loc.getWorld().getName());
                cfg.set("location.x", loc.getBlockX());
                cfg.set("location.y", loc.getBlockY());
                cfg.set("location.z", loc.getBlockZ());
                try { cfg.save(new File(plugin.getDataFolder() + "/crates", name + ".yml")); } catch (Exception e) {}
                plugin.sendMsg(p, "crate-create", "%crate%", name);
                return true;
            }
            if (sub.equals("edit") && args.length > 1) { plugin.getCrateManager().openEditMenu(p, args[1]); return true; }
        }
        if (sub.equals("givekey") && args.length > 3) {
            Player target = Bukkit.getPlayer(args[1]);
            if (target != null) plugin.getCrateManager().giveKey(target, args[2], Integer.parseInt(args[3]));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) return Arrays.asList("create", "movehere", "edit", "givekey", "reload").stream().filter(s -> s.startsWith(args[0].toLowerCase())).collect(Collectors.toList());
        List<String> crates = new ArrayList<>();
        File folder = new File(plugin.getDataFolder(), "crates");
        if (folder.exists() && folder.listFiles() != null) {
            for (File f : folder.listFiles()) if (f.getName().endsWith(".yml")) crates.add(f.getName().replace(".yml", ""));
        }
        if (args[0].equalsIgnoreCase("givekey")) {
            if (args.length == 2) return Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(n -> n.toLowerCase().startsWith(args[1].toLowerCase())).collect(Collectors.toList());
            if (args.length == 3) return crates.stream().filter(c -> c.toLowerCase().startsWith(args[2].toLowerCase())).collect(Collectors.toList());
            if (args.length == 4) return Collections.singletonList("1");
        }
        if (args.length == 2 && (args[0].equalsIgnoreCase("edit") || args[0].equalsIgnoreCase("movehere") || args[0].equalsIgnoreCase("create"))) return crates.stream().filter(c -> c.toLowerCase().startsWith(args[1].toLowerCase())).collect(Collectors.toList());
        return new ArrayList<>();
    }
                }
