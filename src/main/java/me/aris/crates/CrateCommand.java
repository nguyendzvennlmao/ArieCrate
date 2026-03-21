package me.aris.crates;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import java.util.*;

public class CrateCommand implements TabExecutor {
    private final ArisCrate plugin;
    public CrateCommand(ArisCrate plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player p = (Player) sender;
        if (!p.hasPermission("ariscrate.admin")) return true;
        if (args.length < 2) return true;

        String sub = args[0].toLowerCase();
        String name = args[1];
        Location loc = p.getLocation().getBlock().getLocation();

        if (sub.equals("create") || sub.equals("movehere")) {
            plugin.getCrateManager().createCrate(name, loc);
            String path = sub.equals("create") ? "create-success" : "move-success";
            p.sendMessage(plugin.getMessage(path).replace("%crate%", name).replace("%x%", ""+loc.getBlockX()).replace("%y%", ""+loc.getBlockY()).replace("%z%", ""+loc.getBlockZ()));
        } 
        else if (sub.equals("edit")) {
            plugin.getCrateManager().openEditMenu(p, name);
        }
        else if (sub.equals("delete")) {
            plugin.getCrateConfig().set("crates." + name, null);
            plugin.saveCrateConfig();
            p.sendMessage(plugin.getMessage("delete-success").replace("%crate%", name));
        }
        else if (sub.equals("givekey")) {
            if (args.length < 4) return true;
            Player target = Bukkit.getPlayer(args[2]);
            if (target == null) return true;
            int amount = Integer.parseInt(args[3]);
            plugin.getCrateManager().giveKey(target, name, amount);
            p.sendMessage(plugin.getMessage("give-key-success").replace("%player%", target.getName()).replace("%crate%", name).replace("%amount%", ""+amount));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender s, Command c, String a, String[] args) {
        if (args.length == 1) return List.of("create", "edit", "movehere", "delete", "givekey");
        if (args.length == 2 && plugin.getCrateConfig().getConfigurationSection("crates") != null) {
            return new ArrayList<>(plugin.getCrateConfig().getConfigurationSection("crates").getKeys(false));
        }
        return Collections.emptyList();
    }
        }
