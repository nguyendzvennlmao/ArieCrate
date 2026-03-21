package me.aris.crates;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CrateCommand implements CommandExecutor, TabCompleter {
    private final ArisCrate plugin;
    public CrateCommand(ArisCrate plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player p = (Player) sender;

        if (args.length == 0) {
            p.sendMessage("§b§lArisCrate §7- Hướng dẫn:");
            p.sendMessage("§e/ac create <tên> §7- Tạo rương");
            p.sendMessage("§e/ac delete <tên> §7- Xóa rương");
            p.sendMessage("§e/ac movehere <tên> §7- Di chuyển rương đến đây");
            p.sendMessage("§e/ac edit <tên> §7- Chỉnh sửa phần thưởng");
            p.sendMessage("§e/ac givekey <người> <tên> <số> §7- Tặng key");
            p.sendMessage("§e/ac reload §7- Load lại config");
            return true;
        }

        String sub = args[0].toLowerCase();
        switch (sub) {
            case "create":
                if (args.length < 2) return true;
                plugin.getCrateManager().createCrate(args[1], p.getLocation());
                p.sendMessage("§a[!] Đã tạo rương " + args[1]);
                break;
            case "delete":
                if (args.length < 2) return true;
                plugin.getCrateManager().deleteCrate(args[1]);
                p.sendMessage("§c[!] Đã xóa rương " + args[1]);
                break;
            case "movehere":
                if (args.length < 2) return true;
                plugin.getCrateManager().createCrate(args[1], p.getLocation());
                p.sendMessage("§e[!] Đã dời rương " + args[1] + " đến vị trí của bạn.");
                break;
            case "edit":
                if (args.length < 2) return true;
                plugin.getCrateManager().openEditMenu(p, args[1]);
                break;
            case "givekey":
                if (args.length < 4) return true;
                Player target = Bukkit.getPlayer(args[1]);
                if (target != null) {
                    plugin.getCrateManager().giveKey(target, args[2], Integer.parseInt(args[3]));
                    p.sendMessage("§a[!] Đã tặng " + args[3] + " key cho " + target.getName());
                }
                break;
            case "reload":
                plugin.loadFiles();
                p.sendMessage("§a[!] Đã load lại cấu hình!");
                break;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) return Arrays.asList("create", "delete", "movehere", "edit", "givekey", "reload");
        return null;
    }
}
