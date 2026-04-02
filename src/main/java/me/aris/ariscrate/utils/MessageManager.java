package me.aris.ariscrate.utils;

import me.aris.ariscrate.ArisCrate;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MessageManager {
    private final ArisCrate plugin;
    private File messageFile;
    private FileConfiguration messages;
    private final Map<String, String> messageCache = new HashMap<>();

    public MessageManager(ArisCrate plugin) {
        this.plugin = plugin;
    }

    public void loadMessages() {
        messageFile = new File(plugin.getDataFolder(), "message.yml");
        if (!messageFile.exists()) {
            plugin.saveResource("message.yml", false);
        }
        messages = YamlConfiguration.loadConfiguration(messageFile);
        messageCache.clear();
    }

    public String getMessage(String path) {
        String message = messages.getString(path);
        if (message == null) {
            return ColorUtils.color("&cMessage not found: " + path);
        }
        String prefix = messages.getString("prefix", "&8[&6ArisCrate&8] &7");
        return ColorUtils.color(prefix + message);
    }
    
    public String getRawMessage(String path) {
        String message = messages.getString(path);
        if (message == null) {
            return ColorUtils.color("&cMessage not found: " + path);
        }
        return ColorUtils.color(message);
    }
                               }
