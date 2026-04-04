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
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sendHelp(sender);
            return true;
        }

        if (!sender.hasPermission("ariscrate.admin")) {
            sender.sendMessage(plugin.getMessageManager().getMessage("command.no-permission"));
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
            case "deleteitem":
                if (!(sender instanceof Player)) {
                    sender.sendMessage(plugin.getMessageManager().getMessage("command.only-player"));
                    return true;
                }
                handleDeleteItem((Player) sender, args);
                break;
            case "movehere":
                if (!(sender instanceof Player)) {
                    sender.sendMessage(plugin.getMessageManager().getMessage("command.only-player"));
                    return true;
                }
                handleMoveHere((Player) sender, args);
                break;
            case "givekey":
                handleGiveKey(sender, args);
                break;
            case "givekeyplayer":
                handleGiveKeyPlayer(sender, args);
                break;
            case "takekey":
                handleTakeKey(sender, args);
                break;
            case "reload":
                plugin.reloadAllConfigs();
                sender.sendMessage(plugin.getMessageManager().getMessage("command.reloaded"));
                break;
            default:
                sendHelp(sender);
                break;
        }
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ColorUtils.color(plugin.getMessageManager().getRawMessage("help.header")));
        sender.sendMessage(ColorUtils.color(plugin.getMessageManager().getRawMessage("help.crates")));
        sender.sendMessage(ColorUtils.color(plugin.getMessageManager().getRawMessage("help.delcrate")));
        sender.sendMessage(ColorUtils.color(plugin.getMessageManager().getRawMessage("help.additem")));
        sender.sendMessage(ColorUtils.color(plugin.getMessageManager().getRawMessage("help.deleteitem")));
        sender.sendMessage(ColorUtils.color(plugin.getMessageManager().getRawMessage("help.movehere")));
        sender.sendMessage(ColorUtils.color(plugin.getMessageManager().getRawMessage("help.givekey")));
        sender.sendMessage(ColorUtils.color(plugin.getMessageManager().getRawMessage("help.givekeyplayer")));
        sender.sendMessage(ColorUtils.color(plugin.getMessageManager().getRawMessage("help.takekey")));
        sender.sendMessage(ColorUtils.color(plugin.getMessageManager().getRawMessage("help.reload")));
        sender.sendMessage(ColorUtils.color(plugin.getMessageManager().getRawMessage("help.footer")));
    }

    private void handleCreateCrate(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(plugin.getMessageManager().getMessage("command.create-usage"));
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
            String msg = plugin.getMessageManager().getRawMessage("command.crate-exists").replace("{name}", name);
            player.sendMessage(ColorUtils.color(msg));
        } else {
            plugin.getCrateManager().createCrate(name, loc);
            String msg1 = plugin.getMessageManager().getRawMessage("command.crate-created")
                .replace("{name}", name)
                .replace("{x}", String.valueOf(loc.getBlockX()))
                .replace("{y}", String.valueOf(loc.getBlockY()))
                .replace("{z}", String.valueOf(loc.getBlockZ()));
            player.sendMessage(ColorUtils.color(msg1));
            
            String msg2 = plugin.getMessageManager().getRawMessage("command.config-created").replace("{name}", name.toLowerCase());
            player.sendMessage(ColorUtils.color(msg2));
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
            String msg = plugin.getMessageManager().getRawMessage("command.crate-deleted").replace("{name}", name);
            player.sendMessage(ColorUtils.color(msg));
        } else {
            String msg = plugin.getMessageManager().getRawMessage("command.crate-not-exist").replace("{name}", name);
            player.sendMessage(ColorUtils.color(msg));
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
            String msg = plugin.getMessageManager().getRawMessage("command.crate-not-exist-add").replace("{crate}", crateName);
            player.sendMessage(ColorUtils.color(msg));
            return;
        }

        ItemStack inHand = player.getInventory().getItemInMainHand();
        if (inHand != null && !inHand.getType().isAir()) {
            ItemStack store = DogAdonisHook.freeze(inHand.clone());
            crate.setItem(slot, store);
            plugin.getCrateManager().saveCrates();
            String msg = plugin.getMessageManager().getRawMessage("command.item-added")
                .replace("{crate}", crateName)
                .replace("{slot}", String.valueOf(slot));
            player.sendMessage(ColorUtils.color(msg));
        } else {
            player.sendMessage(plugin.getMessageManager().getMessage("command.hold-item"));
        }
    }
    
    private void handleDeleteItem(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(plugin.getMessageManager().getMessage("command.deleteitem-usage"));
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
            String msg = plugin.getMessageManager().getRawMessage("command.crate-not-exist").replace("{name}", crateName);
            player.sendMessage(ColorUtils.color(msg));
            return;
        }
        
        if (!crate.getItems().containsKey(slot)) {
            String msg = plugin.getMessageManager().getRawMessage("command.no-item-at-slot").replace("{slot}", String.valueOf(slot));
            player.sendMessage(ColorUtils.color(msg));
            return;
        }
        
        crate.getItems().remove(slot);
        plugin.getCrateManager().saveCrates();
        String msg = plugin.getMessageManager().getRawMessage("command.item-deleted")
            .replace("{crate}", crateName)
            .replace("{slot}", String.valueOf(slot));
        player.sendMessage(ColorUtils.color(msg));
    }
    
    private void handleMoveHere(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(plugin.getMessageManager().getMessage("command.movehere-usage"));
            return;
        }

        String crateName = args[1];
        Crate crate = plugin.getCrateManager().getCrate(crateName);
        if (crate == null) {
            String msg = plugin.getMessageManager().getRawMessage("command.crate-not-exist").replace("{name}", crateName);
            player.sendMessage(ColorUtils.color(msg));
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
        
        String msg1 = plugin.getMessageManager().getRawMessage("command.crate-moved")
            .replace("{name}", crateName)
            .replace("{old_x}", String.valueOf(oldLoc.getBlockX()))
            .replace("{old_y}", String.valueOf(oldLoc.getBlockY()))
            .replace("{old_z}", String.valueOf(oldLoc.getBlockZ()));
        player.sendMessage(ColorUtils.color(msg1));
        
        String msg2 = plugin.getMessageManager().getRawMessage("command.crate-moved-to")
            .replace("{x}", String.valueOf(loc.getBlockX()))
            .replace("{y}", String.valueOf(loc.getBlockY()))
            .replace("{z}", String.valueOf(loc.getBlockZ()));
        player.sendMessage(ColorUtils.color(msg2));
    }

    private void handleGiveKey(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(plugin.getMessageManager().getMessage("command.givekey-usage"));
            return;
        }

        String key = args[1];
        int amount;
        try {
            amount = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(plugin.getMessageManager().getMessage("command.invalid-number"));
            return;
        }

        String keyName = key.substring(0, 1).toUpperCase() + key.substring(1).toLowerCase() + " Key";
        boolean chatEnabled = plugin.getConfig().getBoolean("settings.chat", true);
        boolean actionbarEnabled = plugin.getConfig().getBoolean("settings.actionbar", true);
        boolean subtitleEnabled = plugin.getConfig().getBoolean("keyall.subtitle.enabled", true);
        String subtitleFormat = plugin.getConfig().getString("keyall.subtitle.format", "&f+{amount} &e{key_name}");
        
        for (Player p : Bukkit.getOnlinePlayers()) {
            plugin.getKeyManager().addKeys(p.getUniqueId(), key, amount);
            
            if (chatEnabled) {
                List<String> chatMessages = plugin.getMessageManager().getMessageList("keyall-message.chat");
                for (String msg : chatMessages) {
                    p.sendMessage(ColorUtils.color(msg
                        .replace("{amount}", String.valueOf(amount))
                        .replace("{key_name}", keyName)));
                }
            }
            
            if (actionbarEnabled) {
                String actionbar = plugin.getMessageManager().getRawMessage("keyall-message.actionbar");
                p.sendActionBar(ColorUtils.color(actionbar
                    .replace("{amount}", String.valueOf(amount))
                    .replace("{key_name}", keyName)));
            }
            
            if (subtitleEnabled) {
                p.sendTitle("", ColorUtils.color(subtitleFormat
                    .replace("{amount}", String.valueOf(amount))
                    .replace("{key_name}", keyName)), 10, 40, 10);
            }
            
            Sound sound = Sound.valueOf(plugin.getConfig().getString("sounds.keyall", "ENTITY_PLAYER_LEVELUP"));
            p.playSound(p.getLocation(), sound, 1.0f, 1.0f);
        }

        String msg = plugin.getMessageManager().getRawMessage("command.key-given-all")
            .replace("{amount}", String.valueOf(amount))
            .replace("{key}", key);
        sender.sendMessage(ColorUtils.color(msg));
    }
    
    private void handleGiveKeyPlayer(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage(plugin.getMessageManager().getMessage("command.givekeyplayer-usage"));
            return;
        }

        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            sender.sendMessage(plugin.getMessageManager().getMessage("command.player-not-found"));
            return;
        }

        String key = args[2];
        int amount;
        try {
            amount = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            sender.sendMessage(plugin.getMessageManager().getMessage("command.invalid-number"));
            return;
        }

        plugin.getKeyManager().addKeys(target.getUniqueId(), key, amount);
        String msg = plugin.getMessageManager().getRawMessage("command.key-given-player")
            .replace("{amount}", String.valueOf(amount))
            .replace("{key}", key)
            .replace("{target}", target.getName());
        sender.sendMessage(ColorUtils.color(msg));
    }

    private void handleTakeKey(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage(plugin.getMessageManager().getMessage("command.takekey-usage"));
            return;
        }

        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            sender.sendMessage(plugin.getMessageManager().getMessage("command.player-not-found"));
            return;
        }

        String key = args[2];
        int amount;
        try {
            amount = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            sender.sendMessage(plugin.getMessageManager().getMessage("command.invalid-number"));
            return;
        }

        boolean success = plugin.getKeyManager().takeKeys(target.getUniqueId(), key, amount);
        if (success) {
            String msg = plugin.getMessageManager().getRawMessage("command.key-taken")
                .replace("{amount}", String.valueOf(amount))
                .replace("{key}", key)
                .replace("{target}", target.getName());
            sender.sendMessage(ColorUtils.color(msg));
        } else {
            sender.sendMessage(plugin.getMessageManager().getMessage("command.not-enough"));
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
            commands.add("givekey");
            commands.add("givekeyplayer");
            commands.add("takekey");
            commands.add("reload");
            commands.add("help");
            return commands.stream()
                .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                .collect(Collectors.toList());
        }

        if (args.length == 2) {
            String subCmd = args[0].toLowerCase();
            if (subCmd.equals("additem") || subCmd.equals("deleteitem") || subCmd.equals("movehere") || subCmd.equals("delcrate")) {
                return new ArrayList<>(plugin.getCrateManager().getCrateNames());
            }
            if (subCmd.equals("givekeyplayer") || subCmd.equals("takekey")) {
                return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .collect(Collectors.toList());
            }
            if (subCmd.equals("givekey")) {
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
            if (subCmd.equals("givekey")) {
                List<String> amounts = new ArrayList<>();
                amounts.add("1");
                amounts.add("5");
                amounts.add("10");
                amounts.add("64");
                return amounts;
            }
            if (subCmd.equals("givekeyplayer")) {
                return new ArrayList<>(plugin.getCrateManager().getCrateNames());
            }
            if (subCmd.equals("takekey")) {
                return new ArrayList<>(plugin.getCrateManager().getCrateNames());
            }
        }

        if (args.length == 4) {
            String subCmd = args[0].toLowerCase();
            if (subCmd.equals("givekeyplayer") || subCmd.equals("takekey")) {
                List<String> amounts = new ArrayList<>();
                amounts.add("1");
                amounts.add("5");
                amounts.add("10");
                amounts.add("64");
                return amounts;
            }
        }

        return Collections.emptyList();
    }
            }
