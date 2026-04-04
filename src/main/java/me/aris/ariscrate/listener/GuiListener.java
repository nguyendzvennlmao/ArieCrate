package me.aris.ariscrate.listener;

import me.aris.ariscrate.ArisCrate;
import me.aris.ariscrate.crate.Crate;
import me.aris.ariscrate.utils.ColorUtils;
import me.aris.ariscrate.utils.DogAdonisHook;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GuiListener implements Listener {
    private final ArisCrate plugin;

    public GuiListener(ArisCrate plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            String title = event.getView().getTitle();
            String chooseTitle = ColorUtils.color(plugin.getGuiConfig().getString("choose-gui.title", "&8ᴄʜᴏᴏsᴇ 1 ɪᴛᴇᴍ"));
            String confirmTitle = ColorUtils.color(plugin.getGuiConfig().getString("confirm-gui.title", "&8ᴄᴏɴғɪʀᴍ"));
            
            if (title.equals(chooseTitle)) {
                handleChoose(event, player);
            } else if (title.equals(confirmTitle)) {
                handleConfirm(event, player);
            }
        }
    }

    private void handleChoose(InventoryClickEvent event, Player player) {
        if (event.getClickedInventory() != null && event.getClickedInventory() == event.getView().getTopInventory()) {
            event.setCancelled(true);
            if (event.getCurrentItem() != null && !event.getCurrentItem().getType().isAir()) {
                String bgMaterial = plugin.getGuiConfig().getString("choose-gui.background.material", "GRAY_STAINED_GLASS_PANE");
                if (event.getCurrentItem().getType() != Material.valueOf(bgMaterial)) {
                    String crateName = plugin.getSelectingCrate().get(player.getUniqueId());
                    if (crateName != null) {
                        int keys = plugin.getKeyManager().getKeys(player.getUniqueId(), crateName);
                        if (keys <= 0) {
                            Sound sound = Sound.valueOf(plugin.getConfig().getString("sounds.no-key", "ENTITY_ILLAGER_CAST_SPELL"));
                            player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
                            player.sendMessage(plugin.getMessageManager().getMessage("crate.no-key"));
                        } else {
                            Sound sound = Sound.valueOf(plugin.getConfig().getString("sounds.gui-click", "UI_BUTTON_CLICK"));
                            player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
                            ItemStack selected = event.getCurrentItem().clone();
                            
                            String confirmTitle = plugin.getGuiConfig().getString("confirm-gui.title", "&8ᴄᴏɴғɪʀᴍ");
                            int confirmSize = plugin.getGuiConfig().getInt("confirm-gui.size", 27);
                            Inventory confirm = plugin.getServer().createInventory(null, confirmSize, ColorUtils.color(confirmTitle));
                            
                            String bgMaterialConfirm = plugin.getGuiConfig().getString("confirm-gui.background.material", "BLACK_STAINED_GLASS_PANE");
                            String bgNameConfirm = plugin.getGuiConfig().getString("confirm-gui.background.name", "&7");
                            ItemStack bgGlass = new ItemStack(Material.valueOf(bgMaterialConfirm));
                            ItemMeta bgMeta = bgGlass.getItemMeta();
                            bgMeta.setDisplayName(ColorUtils.color(bgNameConfirm));
                            bgGlass.setItemMeta(bgMeta);
                            
                            for (int i = 0; i < confirm.getSize(); i++) {
                                confirm.setItem(i, bgGlass);
                            }
                            
                            String cancelMaterial = plugin.getGuiConfig().getString("confirm-gui.items.cancel.material", "RED_STAINED_GLASS_PANE");
                            String cancelName = plugin.getGuiConfig().getString("confirm-gui.items.cancel.name", "#FB0000ᴄᴀɴᴄᴇʟ");
                            int cancelSlot = plugin.getGuiConfig().getInt("confirm-gui.items.cancel.slot", 11);
                            
                            ItemStack cancel = new ItemStack(Material.valueOf(cancelMaterial));
                            ItemMeta cancelMeta = cancel.getItemMeta();
                            cancelMeta.setDisplayName(ColorUtils.color(cancelName));
                            cancel.setItemMeta(cancelMeta);
                            confirm.setItem(cancelSlot, cancel);
                            
                            confirm.setItem(13, selected);
                            
                            String confirmMaterial = plugin.getGuiConfig().getString("confirm-gui.items.confirm.material", "LIME_STAINED_GLASS_PANE");
                            String confirmName = plugin.getGuiConfig().getString("confirm-gui.items.confirm.name", "#00FB4Bᴄᴏɴғɪʀᴍ");
                            int confirmSlot = plugin.getGuiConfig().getInt("confirm-gui.items.confirm.slot", 15);
                            
                            ItemStack ok = new ItemStack(Material.valueOf(confirmMaterial));
                            ItemMeta okMeta = ok.getItemMeta();
                            okMeta.setDisplayName(ColorUtils.color(confirmName));
                            ok.setItemMeta(okMeta);
                            confirm.setItem(confirmSlot, ok);
                            
                            player.openInventory(confirm);
                        }
                    }
                }
            }
        } else {
            event.setCancelled(true);
        }
    }

    private void handleConfirm(InventoryClickEvent event, Player player) {
        if (event.getClickedInventory() != null && event.getClickedInventory() == event.getView().getTopInventory()) {
            event.setCancelled(true);
            int cancelSlot = plugin.getGuiConfig().getInt("confirm-gui.items.cancel.slot", 11);
            int confirmSlot = plugin.getGuiConfig().getInt("confirm-gui.items.confirm.slot", 15);
            int slot = event.getRawSlot();
            
            if (slot == cancelSlot) {
                player.closeInventory();
                plugin.getSelectingCrate().remove(player.getUniqueId());
            } else if (slot == confirmSlot) {
                ItemStack reward = event.getInventory().getItem(13);
                if (reward != null && !reward.getType().isAir()) {
                    String crateName = plugin.getSelectingCrate().get(player.getUniqueId());
                    Crate crate = plugin.getCrateManager().getCrate(crateName);
                    if (crate == null) {
                        Sound sound = Sound.valueOf(plugin.getConfig().getString("sounds.no-key", "ENTITY_ILLAGER_CAST_SPELL"));
                        player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
                        player.sendMessage(plugin.getMessageManager().getMessage("crate.no-key-found"));
                        player.closeInventory();
                    } else {
                        boolean removed = plugin.getKeyManager().removeKey(player.getUniqueId(), crate.getName());
                        if (!removed) {
                            Sound sound = Sound.valueOf(plugin.getConfig().getString("sounds.no-key", "ENTITY_ILLAGER_CAST_SPELL"));
                            player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
                            player.sendMessage(plugin.getMessageManager().getMessage("crate.no-key-found"));
                            player.closeInventory();
                        } else {
                            ItemStack give = DogAdonisHook.unfreeze(reward.clone());
                            player.getInventory().addItem(give);
                            
                            Sound sound = Sound.valueOf(plugin.getConfig().getString("sounds.reward", "ENTITY_PLAYER_LEVELUP"));
                            player.playSound(player.getLocation(), sound, 1.0f, 2.0f);
                            
                            String itemName;
                            if (reward.hasItemMeta() && reward.getItemMeta().hasDisplayName()) {
                                itemName = reward.getItemMeta().getDisplayName();
                            } else {
                                itemName = reward.getType().toString().toLowerCase().replace('_', ' ');
                            }
                            
                            String msg = plugin.getMessageManager().getRawMessage("crate.received-broadcast")
                                .replace("{player}", player.getName())
                                .replace("{item}", itemName);
                            Bukkit.getServer().broadcastMessage(ColorUtils.color(msg));
                            
                            player.closeInventory();
                            plugin.getSelectingCrate().remove(player.getUniqueId());
                        }
                    }
                }
            }
        } else {
            event.setCancelled(true);
        }
    }
                }
