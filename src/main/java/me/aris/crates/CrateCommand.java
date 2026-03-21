package me.aris.crates;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import java.io.File;
import java.util.*;

public class CrateCommand implements CommandExecutor, TabCompleter {
    private final ArisCrate plugin;
    public CrateCommand(ArisCrate plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("ariscrate.admin")) return true;

        if (args.length >= 2 && args[0].equalsIgnoreCase("setup")) {
            if (!(sender instanceof Player)) return true;
            Player p = (Player) sender;
            String name = args[1];
            var loc = p.getTargetBlockExact(5).getLocation();
            var cfg = plugin.getCrateManager().getCrateConfig(name);
            cfg.set("location.world", loc.getWorld().getName());
            cfg.set("location.x", loc.getBlockX());
            cfg.set("location.y", loc.getBlockY());
            cfg.set("location.z", loc.getBlockZ());
            try { cfg.save(new File(plugin.getDataFolder() + "/crates", name + ".yml")); } catch (Exception e) {}
            plugin.sendMsg(p, "crate-create", "%crate%", name);
            String s = plugin.getMsgConfig().getString("messages.crate-create.sound", "BLOCK_ANVIL_USE");
            p.playSound(p.getLocation(), Sound.valueOf(s), 1f, 1f);
            return true;
        }

        if (args.length >= 4 && args[0].equalsIgnoreCase("givekey")) {
            Player target = Bukkit.getPlayer(args[1]);
            if (target != null) {
                try {
                    int amount = Integer.parseInt(args[3]);
                    plugin.getCrateManager().giveKey(target, args[2], amount);
                } catch (Exception e) { sender.sendMessage("§cSố lượng phải là số!"); }
            }
            return true;
        }

        if (args.length >= 1 && args[0].equalsIgnoreCase("reload")) {
            plugin.loadFiles();
            if (sender instanceof Player) {
                Player p = (Player) sender;
                plugin.sendMsg(p, "reload-success");
                p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
            } else sender.sendMessage("§aReload success!");
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender s, Command c, String a, String[] args) {
        if (args.length == 1) return Arrays.asList("setup", "givekey", "reload");
        if (args.length == 2 && args[0].equalsIgnoreCase("givekey")) return null;
        return new ArrayList<>();
    }
                                                   }
