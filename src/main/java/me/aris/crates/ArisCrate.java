package me.aris.crates;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;

public class ArisCrate extends JavaPlugin {
    private CrateManager crateManager;
    private File crateFile, msgFile, keyFile;
    private FileConfiguration crateConfig, msgConfig, keyConfig;

    @Override
    public void onEnable() {
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
        keyFile = new File(getDataFolder(), "playerkey.yml");
        if (!keyFile.exists()) try { keyFile.createNewFile(); } catch (Exception e) {}
        keyConfig = YamlConfiguration.loadConfiguration(keyFile);
    }

    public void sendMsg(org.bukkit.entity.Player p, String path, String... replace) {
        if (!msgConfig.contains("messages." + path)) return;
        String msg = crateManager.translateHex(msgConfig.getString("messages." + path + ".text", ""));
        for (int i = 0; i < replace.length; i += 2) msg = msg.replace(replace[i], replace[i+1]);
        p.sendMessage(msg);
    }

    public FileConfiguration getCrateConfig() { return crateConfig; }
    public FileConfiguration getKeyConfig() { return keyConfig; }
    public void saveCrateConfig() { try { crateConfig.save(crateFile); } catch (Exception e) {} }
    public void saveKeyConfig() { try { keyConfig.save(keyFile); } catch (Exception e) {} }
    public CrateManager getCrateManager() { return crateManager; }
}
