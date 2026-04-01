package me.aris.ariscrate;

import me.aris.ariscrate.key.KeyManager;
import me.aris.ariscrate.task.KeyAllTask;
import me.aris.ariscrate.utils.ColorUtils;
import me.aris.ariscrate.listener.MenuListener;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class ArisCrate extends JavaPlugin implements CommandExecutor, TabCompleter {
    private KeyManager keyManager;
    private KeyAllTask keyAllTask;
    private Map<UUID, String> selectingCrate = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        File cratesFolder = new File(getDataFolder(), "crates");
        if (!cratesFolder.exists()) cratesFolder.mkdirs();
        this.keyManager = new KeyManager(this);
        this.keyManager.load();
        this.keyAllTask = new KeyAllTask(this);
        getCommand("acrate").setExecutor(this);
        getCommand("acrate").setTabCompleter(this);
        Bukkit.getPluginManager().registerEvents(new MenuListener(this), this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length < 3) return true;
        if (args[0].equalsIgnoreCase("givekeyall")) {
            String type = args[1];
            int amt = Integer.parseInt(args[2]);
            if (args.length > 3) {
                Player target = Bukkit.getPlayer(args[3]);
                if (target != null) give(target, type, amt, false);
            } else {
                Bukkit.getOnlinePlayers().forEach(p -> give(p, type, amt, false));
            }
            return true;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if (args.length == 1) return List.of("givekeyall");
        if (args[0].equalsIgnoreCase("givekeyall")) {
            if (args.length == 2) {
                File folder = new File(getDataFolder(), "crates");
                File[] files = folder.listFiles();
                if (files != null) return Arrays.stream(files).map(f -> f.getName().replace(".yml", "")).collect(Collectors.toList());
                return List.of("common");
            }
            if (args.length == 3) return List.of("1", "10", "64");
            if (args.length == 4) return null;
        }
        return Collections.emptyList();
    }

    public void give(Player p, String type, int amt, boolean isAuto) {
        keyManager.addKeys(p.getUniqueId(), type, amt);
        String sub = isAuto ? getConfig().getString("keyall.subtitle") : 
                     getMessageConfig().getString("messages.receive-give").replace("%key%", type);
        p.sendTitle("", ColorUtils.color(sub), 10, 40, 10);
        p.playSound(p.getLocation(), Sound.valueOf(getConfig().getString("sounds.receive-key")), 1, 1);
        sendNotify(p, "receive-give", "%key%", type, "%amount%", String.valueOf(amt));
    }

    public void sendNotify(Player p, String path, String... ph) {
        var msgCfg = getMessageConfig();
        String msg = msgCfg.getString("messages." + path);
        if (msg == null) return;
        for (int i = 0; i < ph.length; i += 2) msg = msg.replace(ph[i], ph[i + 1]);
        String fin = ColorUtils.color(getConfig().getString("settings.prefix") + msg);
        if (msgCfg.getBoolean("chat")) p.sendMessage(fin);
        if (msgCfg.getBoolean("actionbar")) p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(fin));
    }

    public KeyManager getKeyManager() { return keyManager; }
    public KeyAllTask getKeyAllTask() { return keyAllTask; }
    public Map<UUID, String> getSelectingCrate() { return selectingCrate; }
            }
