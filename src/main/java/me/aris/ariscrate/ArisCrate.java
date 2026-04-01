package me.aris.ariscrate;

import me.aris.ariscrate.command.CrateAdminCommand;
import me.aris.ariscrate.crate.CrateManager;
import me.aris.ariscrate.key.KeyManager;
import me.aris.ariscrate.listener.CrateInteractListener;
import me.aris.ariscrate.listener.MenuListener;
import me.aris.ariscrate.utils.ColorUtils;
import me.aris.ariscrate.utils.KeyExpansion;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.util.*;

public class ArisCrate extends JavaPlugin {
    private static ArisCrate instance;
    private CrateManager crateManager;
    private KeyManager keyManager;
    private FileConfiguration msgConfig;
    private FileConfiguration guiConfig;
    private final Map<UUID, String> selectingCrate = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        loadConfigs();
        this.crateManager = new CrateManager(this);
        this.keyManager = new KeyManager(this);
        getCommand("ariscrate").setExecutor(new CrateAdminCommand(this));
        getCommand("ariscrate").setTabCompleter(new CrateAdminCommand(this));
        Bukkit.getPluginManager().registerEvents(new CrateInteractListener(this), this);
        Bukkit.getPluginManager().registerEvents(new MenuListener(this), this);
        this.crateManager.load();
        this.keyManager.load();
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new KeyExpansion(this).register();
        }
    }

    public void loadConfigs() {
        reloadConfig();
        msgConfig = loadResourceFile("message.yml");
        guiConfig = loadResourceFile("gui.yml");
    }

    private FileConfiguration loadResourceFile(String name) {
        File file = new File(getDataFolder(), name);
        if (!file.exists()) saveResource(name, false);
        return YamlConfiguration.loadConfiguration(file);
    }

    public void sendNotify(Player p, String path, String... placeholders) {
        String raw = msgConfig.getString("messages." + path);
        if (raw == null) return;
        for (int i = 0; i < placeholders.length; i += 2) {
            raw = raw.replace(placeholders[i], placeholders[i + 1]);
        }
        String msg = ColorUtils.color(getConfig().getString("settings.prefix") + raw);
        if (getConfig().getBoolean("notification.chat")) p.sendMessage(msg);
        if (getConfig().getBoolean("notification.actionbar")) p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(msg));
    }

    public static ArisCrate getInstance() { return instance; }
    public CrateManager getCrateManager() { return crateManager; }
    public KeyManager getKeyManager() { return keyManager; }
    public FileConfiguration getGuiConfig() { return guiConfig; }
    public FileConfiguration getMsgConfig() { return msgConfig; }
    public Map<UUID, String> getSelectingCrate() { return selectingCrate; }
}
