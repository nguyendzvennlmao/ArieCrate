package me.aris.ariscrate;

import me.aris.ariscrate.command.ArisCrateCommand;
import me.aris.ariscrate.crate.CrateManager;
import me.aris.ariscrate.key.KeyManager;
import me.aris.ariscrate.listener.BlockInteractListener;
import me.aris.ariscrate.listener.GuiListener;
import me.aris.ariscrate.placeholder.KeyPlaceholderExpansion;
import me.aris.ariscrate.utils.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ArisCrate extends JavaPlugin {
    private static ArisCrate instance;
    private CrateManager crateManager;
    private KeyManager keyManager;
    private MessageManager messageManager;
    private FileConfiguration guiConfig;
    private final Map<UUID, String> selectingCrate;

    public ArisCrate() {
        this.selectingCrate = new HashMap<>();
    }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        
        loadGuiConfig();
        
        messageManager = new MessageManager(this);
        messageManager.loadMessages();

        crateManager = new CrateManager(this);
        keyManager = new KeyManager(this);

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new KeyPlaceholderExpansion(this).register();
            getLogger().info("PlaceholderAPI expansion registered!");
        }

        ArisCrateCommand command = new ArisCrateCommand(this);
        if (getCommand("ariscrate") != null) {
            getCommand("ariscrate").setExecutor(command);
            getCommand("ariscrate").setTabCompleter(command);
        }

        Bukkit.getPluginManager().registerEvents(new BlockInteractListener(this), this);
        Bukkit.getPluginManager().registerEvents(new GuiListener(this), this);

        crateManager.loadCrates();
        keyManager.loadKeys();

        getLogger().info("ArisCrate has been enabled!");
    }

    @Override
    public void onDisable() {
        if (crateManager != null) {
            crateManager.saveCrates();
        }
        if (keyManager != null) {
            keyManager.saveKeys();
        }
        getLogger().info("ArisCrate has been disabled!");
    }

    private void loadGuiConfig() {
        File guiFile = new File(getDataFolder(), "gui.yml");
        if (!guiFile.exists()) {
            saveResource("gui.yml", false);
        }
        guiConfig = YamlConfiguration.loadConfiguration(guiFile);
    }

    public FileConfiguration getGuiConfig() {
        return guiConfig;
    }

    public void reloadAllConfigs() {
        reloadConfig();
        loadGuiConfig();
        messageManager.loadMessages();
        crateManager.loadCrates();
        keyManager.loadKeys();
    }

    public CrateManager getCrateManager() {
        return crateManager;
    }

    public KeyManager getKeyManager() {
        return keyManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public Map<UUID, String> getSelectingCrate() {
        return selectingCrate;
    }

    public static ArisCrate getInstance() {
        return instance;
    }
  }
