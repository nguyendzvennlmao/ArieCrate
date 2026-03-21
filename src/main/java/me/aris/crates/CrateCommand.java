package me.aris.crates;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CrateCommand implements CommandExecutor {
    private final ArisCrate plugin;
    public CrateCommand(ArisCrate plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length >= 4 && args[0].equalsIgnoreCase("givekey")) {
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) return true;
            String crateName = args[2];
            int amount = Integer.parseInt(args[3]);
            plugin.getCrateManager().giveKey(target, crateName, amount);
            sender.sendMessage("§aĐã tặng " + amount + " key cho " + target.getName());
            return true;
        }
        return false;
    }
}
