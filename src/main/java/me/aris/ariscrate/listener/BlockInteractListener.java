package me.aris.ariscrate.listener;

import me.aris.ariscrate.ArisCrate;
import me.aris.ariscrate.crate.Crate;
import me.aris.ariscrate.utils.ColorUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class BlockInteractListener implements Listener {
    private final ArisCrate plugin;

    public BlockInteractListener(ArisCrate plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getClickedBlock() != null) {
                Location clickedLoc = event.getClickedBlock().getLocation();
                
                Crate crate = plugin.getCrateManager().getAllCrates().stream()
                    .filter(c -> isExactBlock(c.getLocation(), clickedLoc))
                    .findFirst().orElse(null);
                    
                if (crate != null) {
                    Player player = event.getPlayer();
                    event.setCancelled(true);
                    
                    if (crate.getCrateConfig() != null) {
                        String permission = crate.getCrateConfig().getString("permission");
                        if (permission != null && !player.hasPermission(permission)) {
                            player.sendMessage(plugin.getMessageManager().getMessage("crate.need-permission")
                                .replace("{permission}", permission));
                            return;
                        }
                    }
                    
                    plugin.getSelectingCrate().put(player.getUniqueId(), crate.getName());
                    openChooseGui(player, crate);
                }
            }
        }
    }

    private void openChooseGui(Player player, Crate crate) {
        String title = plugin.getGuiConfig().getString("choose-gui.title", "&8ᴄʜᴏᴏsᴇ 1 ɪᴛᴇᴍ");
        int size = plugin.getGuiConfig().getInt("choose-gui.size", 27);
        
        Inventory inv = plugin.getServer().createInventory(null, size, ColorUtils.color(title));
        
        String bgMaterial = plugin.getGuiConfig().getString("choose-gui.background.material", "GRAY_STAINED_GLASS_PANE");
        String bgName = plugin.getGuiConfig().getString("choose-gui.background.name", "&7");
        
        ItemStack glass = new ItemStack(Material.valueOf(bgMaterial));
        ItemMeta meta = glass.getItemMeta();
        meta.setDisplayName(ColorUtils.color(bgName));
        
        if (crate.getCrateConfig() != null) {
            String line1 = crate.getCrateConfig().getString("choose-line1", "&7With a {key} ᴋᴇʏ you can choose");
            String line2 = crate.getCrateConfig().getString("choose-line2", "&7which of the {items} you want");
            line1 = line1.replace("{key}", crate.getName()).replace("{items}", String.valueOf(crate.getItems().size()));
            line2 = line2.replace("{key}", crate.getName()).replace("{items}", String.valueOf(crate.getItems().size()));
            List<String> lore = Arrays.asList(ColorUtils.color(line1), ColorUtils.color(line2));
            meta.setLore(lore);
        }
        
        glass.setItemMeta(meta);

        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, glass);
        }
        
        crate.getItems().forEach(inv::setItem);
        player.openInventory(inv);
    }

    private boolean isExactBlock(Location a, Location b) {
        if (!a.getWorld().equals(b.getWorld())) return false;
        return a.getBlockX() == b.getBlockX() && 
               a.getBlockY() == b.getBlockY() && 
               a.getBlockZ() == b.getBlockZ();
    }
                                              }
