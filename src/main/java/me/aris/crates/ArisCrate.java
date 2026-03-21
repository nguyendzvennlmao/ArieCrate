package me.aris.crates;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;

public class ArisCrate extends JavaPlugin {
    private static ArisCrate instance;
    private CrateManager crateManager;
    private File crateFile, msgFile;
    private FileConfiguration crateConfig, msgConfig;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        loadFiles();
        crateManager = new CrateManager(this);
        CrateCommand cmd = new CrateCommand(this);
        getCommand("ariscrate").setExecutor(cmd);
        getCommand("ariscrate").setTabCompleter(cmd);
        getServer().getPluginManager().registerEvents(new CrateListener(this), this);
    }

    public void loadFiles() {
        if (!getDataFolder().exists()) getDataFolder().mkdirs();
        crateFile = new File(getDataFolder(), "crate.yml");
        if (!crateFile.exists()) saveResource("crate.yml", false);
        crateConfig = YamlConfiguration.loadConfiguration(crateFile);
        msgFile = new File(getDataFolder(), "message.yml");
        if (!msgFile.exists()) saveResource("message.yml", false);
        msgConfig = YamlConfiguration.loadConfiguration(msgFile);
    }

    public String getMessage(String path) {
        String prefix = msgConfig.getString("prefix", "&6[&bArisCrate&6] &f");
        return (prefix + msgConfig.getString(path, path)).replace("&", "§");
    }

    public void saveCrateConfig() {
        try { crateConfig.save(crateFile); } catch (Exception e) { e.printStackTrace(); }
    }

    public static ArisCrate getInstance() { return instance; }
    public CrateManager getCrateManager() { return crateManager; }
    public FileConfiguration getCrateConfig() { return crateConfig; }
            }
