package me.aris.crates;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;

public class ArisCrate extends JavaPlugin {
    private static ArisCrate instance;
    private CrateManager crateManager;
    private File crateFile;
    private FileConfiguration crateConfig;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        loadCrateFile();
        this.crateManager = new CrateManager(this);
        CrateCommand cmd = new CrateCommand(this);
        getCommand("ariscrate").setExecutor(cmd);
        getCommand("ariscrate").setTabCompleter(cmd);
        getServer().getPluginManager().registerEvents(new CrateListener(this), this);
        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new CrateExpansion(this).register();
        }
    }

    public void loadCrateFile() {
        crateFile = new File(getDataFolder(), "crate.yml");
        if (!crateFile.exists()) saveResource("crate.yml", false);
        crateConfig = YamlConfiguration.loadConfiguration(crateFile);
    }

    public void saveCrateConfig() {
        try { crateConfig.save(crateFile); } catch (Exception e) { e.printStackTrace(); }
    }

    public static ArisCrate getInstance() { return instance; }
    public CrateManager getCrateManager() { return crateManager; }
    public FileConfiguration getCrateConfig() { return crateConfig; }
}
