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
            sender.sendMessage(ColorUtils.color("&cYou don't have permission to use this command!"));
            return true;
        }

        if (args.length == 0) {
            sendUsage(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "crates":
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ColorUtils.color("&cThis command can only be used by players."));
                    return true;
                }
                handleCreateCrate((Player) sender, args);
                break;
            case "delcrate":
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ColorUtils.color("&cThis command can only be used by players."));
                    return true;
                }
                handleDeleteCrate((Player) sender, args);
                break;
            case "additem":
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ColorUtils.color("&cThis command can only be used by players."));
                    return true;
                }
                handleAddItem((Player) sender, args);
                break;
            case "deleteitem":
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ColorUtils.color("&cThis command can only be used by players."));
                    return true;
                }
                handleDeleteItem((Player) sender, args);
                break;
            case "movehere":
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ColorUtils.color("&cThis command can only be used by players."));
                    return true;
                }
                handleMoveHere((Player) sender, args);
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
                sender.sendMessage(ColorUtils.color("&aPlugin reloaded successfully!"));
                break;
            default:
                sendUsage(sender);
                break;
        }
        return true;
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(ColorUtils.color("&cUsage: /ariscrate <crates|delcrate|additem|deleteitem|movehere|give|take|keyall|reload>"));
    }

    private void handleCreateCrate(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ColorUtils.color("&cUsage: /ariscrate crates <name>"));
            return;
        }

        String name = args[1];
        
        Block targetBlock = player.getTargetBlockExact(5);
        Location loc;
        
        if (targetBlock != null && targetBlock.getType().isSolid()) {
            loc = targetBlock.getLocation();
        } else {
            loc = player.getLocation().getBlock().getLocation();
        }

        if (plugin.getCrateManager().crateExists(name)) {
            player.sendMessage(ColorUtils.color("&cCrate &e" + name + " &calready exists!"));
        } else {
            plugin.getCrateManager().createCrate(name, loc);
            player.sendMessage(ColorUtils.color("&aCreated crate &e" + name + " &aat " + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ()));
            player.sendMessage(ColorUtils.color("&aConfig file created: &ecrate/" + name.toLowerCase() + ".yml"));
        }
    }

    private void handleDeleteCrate(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ColorUtils.color("&cUsage: /ariscrate delcrate <name>"));
            return;
        }

        String name = args[1];
        boolean success = plugin.getCrateManager().deleteCrate(name);
        if (success) {
            player.sendMessage(ColorUtils.color("&aDeleted crate &e" + name));
        } else {
            player.sendMessage(ColorUtils.color("&cCrate &e" + name + " &cdoes not exist."));
        }
    }

    private void handleAddItem(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(ColorUtils.color("&cUsage: /ariscrate additem <crate> <slot>"));
            return;
        }

        String crateName = args[1];
        int slot;
        try {
            slot = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            player.sendMessage(ColorUtils.color("&cSlot must be a number"));
            return;
        }

        Crate crate = plugin.getCrateManager().getCrate(crateName);
        if (crate == null) {
            player.sendMessage(ColorUtils.color("&cCrate &e" + crateName + " &cdoes not exist."));
            return;
        }

        ItemStack inHand = player.getInventory().getItemInMainHand();
        if (inHand != null && !inHand.getType().isAir()) {
            ItemStack store = DogAdonisHook.freeze(inHand.clone());
            crate.setItem(slot, store);
            plugin.getCrateManager().saveCrates();
            player.sendMessage(ColorUtils.color("&aAdded item to crate &e" + crateName + "&a at slot &e" + slot));
        } else {
            player.sendMessage(ColorUtils.color("&cYou must hold an item in your hand."));
        }
    }
    
    private void handleDeleteItem(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(ColorUtils.color("&cUsage: /ariscrate deleteitem <crate> <slot>"));
            return;
        }

        String crateName = args[1];
        int slot;
        try {
            slot = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            player.sendMessage(ColorUtils.color("&cSlot must be a number"));
            return;
        }

        Crate crate = plugin.getCrateManager().getCrate(crateName);
        if (crate == null) {
            player.sendMessage(ColorUtils.color("&cCrate &e" + crateName + " &cdoes not exist."));
            return;
        }
        
        if (!crate.getItems().containsKey(slot)) {
            player.sendMessage(ColorUtils.color("&cNo item found at slot &e" + slot));
            return;
        }
        
        crate.getItems().remove(slot);
        plugin.getCrateManager().saveCrates();
        player.sendMessage(ColorUtils.color("&aDeleted item from crate &e" + crateName + "&a at slot &e" + slot));
    }
    
    private void handleMoveHere(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ColorUtils.color("&cUsage: /ariscrate movehere <crate>"));
            return;
        }

        String crateName = args[1];
        Crate crate = plugin.getCrateManager().getCrate(crateName);
        if (crate == null) {
            player.sendMessage(ColorUtils.color("&cCrate &e" + crateName + " &cdoes not exist."));
            return;
        }
        
        Block targetBlock = player.getTargetBlockExact(5);
        Location loc;
        
        if (targetBlock != null && targetBlock.getType().isSolid()) {
            loc = targetBlock.getLocation();
        } else {
            loc = player.getLocation().getBlock().getLocation();
        }
        
        Location oldLoc = crate.getLocation();
        crate.setLocation(loc);
        plugin.getCrateManager().saveCrates();
        player.sendMessage(ColorUtils.color("&aMoved crate &e" + crateName + " &afrom " + oldLoc.getBlockX() + "," + oldLoc.getBlockY() + "," + oldLoc.getBlockZ()));
        player.sendMessage(ColorUtils.color("&aTo new location: &e" + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ()));
    }

    private void handleGiveKey(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ColorUtils.color("&cUsage: /ariscrate give <key> <amount> | /ariscrate give <key> <player> <amount>"));
            return;
        }

        String key = args[1];
        String possibleNumber = args[2];
        int amount;
        Player target;

        if (isInteger(possibleNumber)) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ColorUtils.color("&cConsole must specify player."));
                return;
            }
            amount = Integer.parseInt(possibleNumber);
            target = (Player) sender;
        } else {
            target = Bukkit.getPlayerExact(possibleNumber);
            if (target == null) {
                sender.sendMessage(ColorUtils.color("&cPlayer not found"));
                return;
            }
            if (args.length < 4 || !isInteger(args[3])) {
                sender.sendMessage(ColorUtils.color("&cUsage: /ariscrate give <key> <player> <amount>"));
                return;
            }
            amount = Integer.parseInt(args[3]);
        }

        plugin.getKeyManager().addKeys(target.getUniqueId(), key, amount);
        sender.sendMessage(ColorUtils.color("&aGave &e" + amount + " &akey(s) " + key + " to &e" + target.getName()));
    }

    private void handleTakeKey(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage(ColorUtils.color("&cUsage: /ariscrate take <key> <player> <amount>"));
            return;
        }

        String key = args[1];
        Player target = Bukkit.getPlayerExact(args[2]);
        if (target == null) {
            sender.sendMessage(ColorUtils.color("&cPlayer not found"));
            return;
        }

        if (!isInteger(args[3])) {
            sender.sendMessage(ColorUtils.color("&cAmount must be a number"));
            return;
        }

        int amount = Integer.parseInt(args[3]);
        boolean success = plugin.getKeyManager().takeKeys(target.getUniqueId(), key, amount);
        if (success) {
            sender.sendMessage(ColorUtils.color("&aTook &e" + amount + " &akey(s) " + key + " from &e" + target.getName()));
        } else {
            sender.sendMessage(ColorUtils.color("&cPlayer does not have enough keys."));
        }
    }

    private void handleKeyAll(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ColorUtils.color("&cUsage: /ariscrate keyall <crate> <amount>"));
            return;
        }

        String crate = args[1];
        int amount;
        try {
            amount = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ColorUtils.color("&cAmount must be a number"));
            return;
        }

        String keyName = crate.substring(0, 1).toUpperCase() + crate.substring(1).toLowerCase() + " Key";
        String suffix = amount > 1 ? "s" : "";

        for (Player p : Bukkit.getOnlinePlayers()) {
            plugin.getKeyManager().addKeys(p.getUniqueId(), crate, amount);
            
            p.sendMessage(ColorUtils.color("&8&m--------------------------------------------------"));
            p.sendMessage(ColorUtils.color(" &a&lKEYALL &7» You received &e" + amount + " &f" + keyName + suffix + "!"));
            p.sendMessage(ColorUtils.color("&8&m--------------------------------------------------"));
            
            p.sendTitle(
                ColorUtils.color("#00FB4B&lKEYALL"),
                ColorUtils.color("&f+" + amount + " &e" + keyName),
                10, 60, 20
            );
            
            Sound sound = Sound.valueOf(plugin.getConfig().getString("sounds.keyall", "ENTITY_PLAYER_LEVELUP"));
            p.playSound(p.getLocation(), sound, 1.0f, 1.0f);
            
            Sound toastSound = Sound.valueOf(plugin.getConfig().getString("sounds.keyall-toast", "UI_TOAST_CHALLENGE_COMPLETE"));
            p.playSound(p.getLocation(), toastSound, 1.0f, 1.0f);
        }

        sender.sendMessage(ColorUtils.color("&aGave everyone &e" + amount + " &akey(s) for &e" + crate));
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
            commands.add("deleteitem");
            commands.add("movehere");
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
            if (subCmd.equals("additem") || subCmd.equals("deleteitem") || subCmd.equals("movehere") || subCmd.equals("give") || subCmd.equals("take") || subCmd.equals("keyall") || subCmd.equals("delcrate")) {
                return new ArrayList<>(plugin.getCrateManager().getCrateNames());
            }
        }

        if (args.length == 3) {
            String subCmd = args[0].toLowerCase();
            if (subCmd.equals("additem") || subCmd.equals("deleteitem")) {
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
