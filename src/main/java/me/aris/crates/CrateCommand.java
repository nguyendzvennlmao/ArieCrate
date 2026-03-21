package me.aris.crates;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import java.util.*;

public class CrateCommand implements CommandExecutor, TabCompleter {
    private final ArisCrate plugin;
    public CrateCommand(ArisCrate plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§b§lArisCrate §7- Commands:");
            sender.sendMessage("§e/ac create <name> §7- Tạo rương");
            sender.sendMessage("§e/ac movehere <name> §7- Dời rương");
            sender.sendMessage("§e/ac givekey <player> <name> <amount>");
            return true;
        }

        String sub = args[0].toLowerCase();
        if (sub.equals("create") && args.length > 1 && sender instanceof Player) {
            plugin.getCrateManager().createCrate(args[1], ((Player) sender).getLocation());
            sender.sendMessage("§a[!] Created crate " + args[1]);
        } 
        else if (sub.equals("movehere") && args.length > 1 && sender instanceof Player) {
            if (plugin.getCommonConfig().contains("crates." + args[1])) {
                plugin.getCrateManager().createCrate(args[1], ((Player) sender).getLocation());
                sender.sendMessage("§e[!] Moved crate " + args[1]);
            }
        }
        else if (sub.equals("givekey") && args.length > 3) {
            Player target = Bukkit.getPlayer(args[1]);
            if (target != null) {
                plugin.getCrateManager().giveKey(target, args[2], Integer.parseInt(args[3]));
                sender.sendMessage("§a[!] Gived " + args[3] + " keys to " + target.getName());
            }
        }
        else if (sub.equals("edit") && args.length > 1 && sender instanceof Player) {
            plugin.getCrateManager().openEditMenu((Player) sender, args[1]);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) return Arrays.asList("create", "delete", "movehere", "edit", "givekey", "reload");
        
        List<String> crates = new ArrayList<>();
        if (plugin.getCommonConfig().getConfigurationSection("crates") != null) {
            crates.addAll(plugin.getCommonConfig().getConfigurationSection("crates").getKeys(false));
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("givekey")) return null; // Hiện tên player
            if (Arrays.asList("delete", "movehere", "edit").contains(args[0].toLowerCase())) return crates;
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("givekey")) return crates;
        
        return new ArrayList<>();
    }
    }
