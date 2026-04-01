package me.aris.ariscrate.command;

import me.aris.ariscrate.ArisCrate;
import me.aris.ariscrate.crate.CrateData;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import java.util.*;
import java.util.stream.Collectors;

public class CrateAdminCommand implements CommandExecutor, TabCompleter {
    private final ArisCrate plugin;
    public CrateAdminCommand(ArisCrate plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player p)) return true;
        if (!p.hasPermission("ariscrate.admin")) {
            plugin.sendNotify(p, "no-permission");
            return true;
        }
        if (args.length == 0) return false;
        String sub = args[0].toLowerCase();
        switch (sub) {
            case "create" -> {
                if (args.length < 2) return false;
                plugin.getCrateManager().create(args[1], p.getLocation().getBlock().getLocation());
                plugin.sendNotify(p, "created-crate", "%crate%", args[1]);
            }
            case "delete" -> {
                if (args.length < 2) return false;
                plugin.getCrateManager().delete(args[1]);
                p.sendMessage("§aĐã xóa Crate: " + args[1]);
            }
            case "movehere" -> {
                if (args.length < 2) return false;
                CrateData c = plugin.getCrateManager().get(args[1]);
                if (c != null) {
                    c.setLocation(p.getLocation().getBlock().getLocation());
                    plugin.getCrateManager().save();
                    p.sendMessage("§aĐã dời Crate " + args[1] + " tới đây.");
                }
            }
            case "edit" -> {
                if (args.length < 2) return false;
                CrateData c = plugin.getCrateManager().get(args[1]);
                if (c != null) {
                    Inventory inv = Bukkit.createInventory(null, 54, "Edit: " + c.getName());
                    c.getItems().forEach(inv::setItem);
                    p.openInventory(inv);
                }
            }
            case "givekey" -> {
                if (args.length < 4) return false;
                Player target = Bukkit.getPlayer(args[2]);
                if (target != null) {
                    plugin.getKeyManager().addKeys(target.getUniqueId(), args[1], Integer.parseInt(args[3]));
                    p.sendMessage("§aĐã đưa " + args[3] + " chìa khóa cho " + target.getName());
                }
            }
            case "takekey" -> {
                if (args.length < 4) return false;
                Player target = Bukkit.getPlayer(args[2]);
                if (target != null) {
                    plugin.getKeyManager().takeKeys(target.getUniqueId(), args[1], Integer.parseInt(args[3]));
                    p.sendMessage("§aĐã lấy " + args[3] + " chìa khóa từ " + target.getName());
                }
            }
            case "reload" -> {
                plugin.loadConfigs();
                plugin.getCrateManager().load();
                plugin.sendNotify(p, "reload");
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender s, Command c, String l, String[] args) {
        if (args.length == 1) return Arrays.asList("create", "delete", "edit", "movehere", "givekey", "takekey", "reload");
        if (args.length == 2 && !args[0].equalsIgnoreCase("create")) return new ArrayList<>(plugin.getCrateManager().getNames());
        if (args.length == 3 && args[0].contains("key")) return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
        return Collections.emptyList();
    }
            }
