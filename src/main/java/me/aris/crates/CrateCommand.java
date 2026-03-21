package me.aris.crates;

import org.bukkit.Bukkit;
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
        if (args.length < 2) return false;
        String sub = args[0].toLowerCase();
        String name = args[1];

        if (sub.equals("create") || sub.equals("movehere")) {
            plugin.getCrateManager().createCrate(name, p.getLocation().getBlock().getLocation());
            p.sendMessage("§a[Crate] Đã tạo/di chuyển rương: " + name);
        } else if (sub.equals("edit")) {
            plugin.getCrateManager().openEditMenu(p, name);
        } else if (sub.equals("givekey")) {
            if (args.length < 4) return false;
            Player target = Bukkit.getPlayer(args[2]);
            if (target == null) return true;
            int amount = Integer.parseInt(args[3]);
            plugin.getCrateManager().giveKey(target, name, amount);
            p.sendMessage(plugin.getMessage("give-key-success").replace("%player%", args[2]).replace("%crate%", name).replace("%amount%", String.valueOf(amount)));
        } else if (sub.equals("delete")) {
            plugin.getCrateConfig().set("crates." + name, null);
            plugin.saveCrateConfig();
            p.sendMessage("§cĐã xóa rương: " + name);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender s, Command c, String a, String[] args) {
        if (args.length == 1) return Arrays.asList("create", "edit", "movehere", "delete", "givekey");
        if (args.length == 2 && plugin.getCrateConfig().getConfigurationSection("crates") != null)
            return new ArrayList<>(plugin.getCrateConfig().getConfigurationSection("crates").getKeys(false));
        return null;
    }
    }
