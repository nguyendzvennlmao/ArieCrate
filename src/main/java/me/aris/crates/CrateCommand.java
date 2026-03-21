package me.aris.crates;

import org.bukkit.command.*;
import org.bukkit.entity.Player;
import java.util.*;

public class CrateCommand implements TabExecutor {
    private final ArisCrate plugin;
    public CrateCommand(ArisCrate plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] args) {
        if (!(s instanceof Player)) return true; // Sửa lỗi instanceof
        Player p = (Player) s;
        
        if (args.length < 2) return true;
        String sub = args[0].toLowerCase();
        String name = args[1];
        
        if (sub.equals("create")) plugin.getCrateManager().createCrate(name, p.getLocation());
        else if (sub.equals("edit")) plugin.getCrateManager().openEditMenu(p, name);
        else if (sub.equals("movehere")) plugin.getCrateManager().createCrate(name, p.getLocation());
        else if (sub.equals("delete")) { 
            plugin.getCrateConfig().set("crates." + name, null); 
            plugin.saveCrateConfig(); 
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender s, Command c, String a, String[] args) {
        if (args.length == 1) return List.of("create", "edit", "movehere", "delete");
        return Collections.emptyList();
    }
    }
