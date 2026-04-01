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
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import java.io.File;
import java.util.*;

public class ArisCrate extends JavaPlugin implements CommandExecutor, TabCompleter {
    private KeyManager keyManager;
    private KeyAllTask keyAllTask;
    private FileConfiguration msgCfg;
    private FileConfiguration guiCfg;
    private Map<UUID, String> selectingCrate = new HashMap<>();

    @Override
    public void onEnable() {
        loadConfigs();
        this.keyManager = new KeyManager(this);
        this.keyManager.load();
        this.keyAllTask = new KeyAllTask(this);
        getCommand("acrate").setExecutor(this);
        getCommand("acrate").setTabCompleter(this);
        Bukkit.getPluginManager().registerEvents(new MenuListener(this), this);
    }

    public void loadConfigs() {
        saveDefaultConfig();
        reloadConfig();
        File fMsg = new File(getDataFolder(), "message.yml");
        if (!fMsg.exists()) saveResource("message.yml", false);
        msgCfg = YamlConfiguration.loadConfiguration(fMsg);
        File fGui = new File(getDataFolder(), "gui.yml");
        if (!fGui.exists()) saveResource("gui.yml", false);
        guiCfg = YamlConfiguration.loadConfiguration(fGui);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) return true;
        if (args[0].equalsIgnoreCase("reload")) {
            loadConfigs();
            sender.sendMessage(ColorUtils.color("&aReloaded!"));
            return true;
        }
        return true;
    }

    public void sendNotify(Player p, String path, String... ph) {
        String msg = msgCfg.getString("messages." + path);
        if (msg == null) return;
        for (int i = 0; i < ph.length; i += 2) msg = msg.replace(ph[i], ph[i + 1]);
        String fin = ColorUtils.color(getConfig().getString("settings.prefix") + msg);
        if (msgCfg.getBoolean("chat")) p.sendMessage(fin);
        if (msgCfg.getBoolean("actionbar")) p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(fin));
    }

    public KeyManager getKeyManager() { return keyManager; }
    public KeyManager getCrateManager() { return keyManager; }
    public FileConfiguration getMessageConfig() { return msgCfg; }
    public FileConfiguration getGuiConfig() { return guiCfg; }
    public Map<UUID, String> getSelectingCrate() { return selectingCrate; }
    }
