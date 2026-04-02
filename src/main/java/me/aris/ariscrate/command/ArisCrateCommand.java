package me.aris.ariscrate.command;

import me.aris.ariscrate.ArisCrate;
import me.aris.ariscrate.crate.Crate;
import me.aris.ariscrate.utils.ColorUtils;
import me.aris.ariscrate.utils.DogAdonisHook;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ArisCrateCommand implements CommandExecutor, TabCompleter {
    private final ArisCrate plugin;

    public ArisCrateCommand(ArisCrate plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("ariscrate.admin")) {
            sender.sendMessage(plugin.getMessageManager().getMessage("command.no-permission"));
            return true;
        }

        if (args.length == 0) {
            sendUsage(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "crates":
                if (!(sender instanceof Player)) {
                    sender.sendMessage(plugin.getMessageManager().getMessage("command.only-player"));
                    return true;
                }
                handleCreateCrate((Player) sender, args);
                break;
            case "delcrate":
                if (!(sender instanceof Player)) {
                    sender.sendMessage(plugin.getMessageManager().getMessage("command.only-player"));
                    return true;
                }
                handleDeleteCrate((Player) sender, args);
                break;
            case "additem":
                if (!(sender instanceof Player)) {
                    sender.sendMessage(plugin.getMessageManager().getMessage("command.only-player"));
                    return true;
                }
                handleAddItem((Player) sender, args);
                break;
            case "give":
                handleGiveKey(sender, args);
                break;
            case "take":
                handleTakeKey(sender, args);
                break;
            case "keyall":
                handleKeyAll(sender, args);
                break;
            case "reload":
                plugin.reloadAllConfigs();
                sender.sendMessage(plugin.getMessageManager().getMessage("command.reloaded"));
                break;
            default:
                sendUsage(sender);
                break;
        }
        return true;
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(plugin.getMessageManager().getMessage("command.usage"));
    }

    private void handleCreateCrate(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(plugin.getMessageManager().getMessage("command.create-usage"));
            return;
        }

        String name = args[1];
        Block target = player.getTargetBlockExact(5);
        Location loc;
        if (target != null) {
            loc = target.getLocation();
        } else {
            loc = player.getLocation().subtract(0, 1, 0).getBlock().getLocation();
        }

        if (plugin.getCrateManager().crateExists(name)) {
            player.sendMessage(plugin.getMessageManager().getMessage("command.crate-exists")
                .replace("{name}", name));
        } else {
            plugin.getCrateManager().createCrate(name, loc);
            player.sendMessage(plugin.getMessageManager().getMessage("command.crate-created")
                .replace("{name}", name)
                .replace("{x}", String.valueOf(loc.getBlockX()))
                .replace("{y}", String.valueOf(loc.getBlockY()))
                .replace("{z}", String.valueOf(loc.getBlockZ())));
        }
    }

    private void handleDeleteCrate(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(plugin.getMessageManager().getMessage("command.delete-usage"));
            return;
        }

        String name = args[1];
        boolean success = plugin.getCrateManager().deleteCrate(name);
        if (success) {
            player.sendMessage(plugin.getMessageManager().getMessage("command.crate-deleted")
                .replace("{name}", name));
        } else {
            player.sendMessage(plugin.getMessageManager().getMessage("command.crate-not-exist")
                .replace("{name}", name));
        }
    }

    private void handleAddItem(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(plugin.getMessageManager().getMessage("command.additem-usage"));
            return;
        }

        String crateName = args[1];
        int slot;
        try {
            slot = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            player.sendMessage(plugin.getMessageManager().getMessage("command.invalid-slot"));
            return;
        }

        Crate crate = plugin.getCrateManager().getCrate(crateName);
        if (crate == null) {
            player.sendMessage(plugin.getMessageManager().getMessage("command.crate-not-exist-add")
                .replace("{crate}", crateName));
            return;
        }

        ItemStack inHand = player.getInventory().getItemInMainHand();
        if (inHand != null && !inHand.getType().isAir()) {
            ItemStack store = DogAdonisHook.freeze(inHand.clone());
            crate.setItem(slot, store);
            plugin.getCrateManager().saveCrates();
            player.sendMessage(plugin.getMessageManager().getMessage("command.item-added")
                .replace("{crate}", crateName)
                .replace("{slot}", String.valueOf(slot)));
        } else {
            player.sendMessage(plugin.getMessageManager().getMessage("command.hold-item"));
        }
    }

    private void handleGiveKey(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(plugin.getMessageManager().getMessage("command.give-usage"));
            return;
        }

        String key = args[1];
        String possibleNumber = args[2];
        int amount;
        Player target;

        if (isInteger(possibleNumber)) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(plugin.getMessageManager().getMessage("command.console-must-specify"));
                return;
            }
            amount = Integer.parseInt(possibleNumber);
            target = (Player) sender;
        } else {
            target = Bukkit.getPlayerExact(possibleNumber);
            if (target == null) {
                sender.sendMessage(plugin.getMessageManager().getMessage("command.player-not-found"));
                return;
            }
            if (args.length < 4 || !isInteger(args[3])) {
                sender.sendMessage(plugin.getMessageManager().getMessage("command.give-usage"));
                return;
            }
            amount = Integer.parseInt(args[3]);
        }

        plugin.getKeyManager().addKeys(target.getUniqueId(), key, amount);
        sender.sendMessage(plugin.getMessageManager().getMessage("command.key-given")
            .replace("{amount}", String.valueOf(amount))
            .replace("{key}", key)
            .replace("{target}", target.getName()));
    }

    private void handleTakeKey(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage(plugin.getMessageManager().getMessage("command.take-usage"));
            return;
        }

        String key = args[1];
        Player target = Bukkit.getPlayerExact(args[2]);
        if (target == null) {
            sender.sendMessage(plugin.getMessageManager().getMessage("command.player-not-found"));
            return;
        }

        if (!isInteger(args[3])) {
            sender.sendMessage(plugin.getMessageManager().getMessage("command.invalid-number"));
            return;
        }

        int amount = Integer.parseInt(args[3]);
        boolean success = plugin.getKeyManager().takeKeys(target.getUniqueId(), key, amount);
        if (success) {
            sender.sendMessage(plugin.getMessageManager().getMessage("command.key-taken")
                .replace("{amount}", String.valueOf(amount))
                .replace("{key}", key)
                .replace("{target}", target.getName()));
        } else {
            sender.sendMessage(plugin.getMessageManager().getMessage("command.not-enough"));
        }
    }

    private void handleKeyAll(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(plugin.getMessageManager().getMessage("command.keyall-usage"));
            return;
        }

        String crate = args[1];
        int amount;
        try {
            amount = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(plugin.getMessageManager().getMessage("command.invalid-number"));
            return;
        }

        String keyName = crate.substring(0, 1).toUpperCase() + crate.substring(1).toLowerCase() + " Key";
        String suffix = amount > 1 ? "s" : "";

        for (Player p : Bukkit.getOnlinePlayers()) {
            plugin.getKeyManager().addKeys(p.getUniqueId(), crate, amount);
            
            p.sendMessage(plugin.getMessageManager().getRawMessage("command.keyall-header"));
            p.sendMessage(plugin.getMessageManager().getRawMessage("command.keyall-message")
                .replace("{amount}", String.valueOf(amount))
                .replace("{key_name}", keyName)
                .replace("{suffix}", suffix));
            p.sendMessage(plugin.getMessageManager().getRawMessage("command.keyall-footer"));
            
            p.sendTitle(
                ColorUtils.color(plugin.getMessageManager().getRawMessage("command.keyall-title")),
                ColorUtils.color(plugin.getMessageManager().getRawMessage("command.keyall-subtitle")
                    .replace("{amount}", String.valueOf(amount))
                    .replace("{key_name}", keyName)),
                10, 60, 20
            );
            
            Sound sound = Sound.valueOf(plugin.getConfig().getString("sounds.keyall", "ENTITY_PLAYER_LEVELUP"));
            p.playSound(p.getLocation(), sound, 1.0f, 1.0f);
            
            Sound toastSound = Sound.valueOf(plugin.getConfig().getString("sounds.keyall-toast", "UI_TOAST_CHALLENGE_COMPLETE"));
            p.playSound(p.getLocation(), toastSound, 1.0f, 1.0f);
        }

        sender.sendMessage(plugin.getMessageManager().getMessage("command.keyall-sender")
            .replace("{amount}", String.valueOf(amount))
            .replace("{crate}", crate));
    }

    private boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("ariscrate.admin")) {
            return Collections.emptyList();
        }

        if (args.length == 1) {
            List<String> commands = new ArrayList<>();
            commands.add("crates");
            commands.add("delcrate");
            commands.add("additem");
            commands.add("give");
            commands.add("take");
            commands.add("keyall");
            commands.add("reload");
            return commands.stream()
                .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                .collect(Collectors.toList());
        }

        if (args.length == 2) {
            String subCmd = args[0].toLowerCase();
            if (subCmd.equals("additem") || subCmd.equals("give") || subCmd.equals("take") || subCmd.equals("keyall") || subCmd.equals("delcrate")) {
                return new ArrayList<>(plugin.getCrateManager().getCrateNames());
            }
        }

        if (args.length == 3) {
            String subCmd = args[0].toLowerCase();
            if (subCmd.equals("additem")) {
                List<String> slots = new ArrayList<>();
                for (int i = 0; i < 27; i++) {
                    slots.add(String.valueOf(i));
                }
                return slots;
            }
            if (subCmd.equals("give")) {
                List<String> players = Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .collect(Collectors.toList());
                players.addAll(List.of("1", "5", "10", "64"));
                return players;
            }
            if (subCmd.equals("take")) {
                return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .collect(Collectors.toList());
            }
            if (subCmd.equals("keyall")) {
                List<String> amounts = new ArrayList<>();
                amounts.add("1");
                amounts.add("5");
                amounts.add("10");
                amounts.add("64");
                return amounts;
            }
        }

        if (args.length == 4 && args[0].equalsIgnoreCase("give")) {
            List<String> amounts = new ArrayList<>();
            amounts.add("1");
            amounts.add("5");
            amounts.add("10");
            amounts.add("64");
            return amounts;
        }

        return Collections.emptyList();
    }
        }
