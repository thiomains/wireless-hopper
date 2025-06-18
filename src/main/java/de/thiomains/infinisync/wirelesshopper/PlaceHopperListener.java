package de.thiomains.infinisync.wirelesshopper;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class PlaceHopperListener implements Listener {

    private WirelessHopper main;

    public PlaceHopperListener(WirelessHopper main) {
        this.main = main;
        Bukkit.getPluginManager().registerEvents(this, main);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getClickedBlock() == null) return;
        if (event.getClickedBlock().getType() != Material.HOPPER) return;
        // TODO check ob das ein Wireless Hopper ist
        if (event.getItem() == null) return;
        if (!event.getItem().getItemMeta().hasLore()) return;
        if (!event.getItem().getItemMeta().getLore().contains("§861 6D 6F 67 75 73")) return;
        if (!event.getHand().equals(EquipmentSlot.HAND)) return;

        event.setCancelled(true);

        ItemStack handItem = event.getPlayer().getInventory().getItemInMainHand();
        handItem.setAmount(handItem.getAmount() - 1);
        event.getPlayer().getInventory().setItemInMainHand(handItem);

        Location clickedHopperLocation = event.getClickedBlock().getLocation();

        ItemStack hopperItem = WirelessHopper.getHopperItem();
        ItemMeta hopperItemMeta = hopperItem.getItemMeta();
        ArrayList<String> lore = (ArrayList<String>) hopperItemMeta.getLore();
        lore.set(0, "§8Verbunden mit: " + clickedHopperLocation.getBlockX() + " " + clickedHopperLocation.getBlockY() + " " + clickedHopperLocation.getBlockZ());
        hopperItemMeta.setLore(lore);
        hopperItem.setItemMeta(hopperItemMeta);

        event.getPlayer().getInventory().addItem(hopperItem);
        event.getPlayer().playSound(event.getPlayer(), Sound.ENTITY_ITEM_PICKUP, 1.0F, 1.0F);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getItemInHand().getItemMeta().getLore() == null) return;
        if (!event.getItemInHand().getItemMeta().getLore().contains("§861 6D 6F 67 75 73")) return;
        if (event.getItemInHand().getItemMeta().getLore().get(0).equals("")) return;

        String destinationLocationString = event.getItemInHand().getItemMeta().getLore().get(0);
        String[] destinationLocationStrings = destinationLocationString.split(" ");

        main.addHopperConnection(
                event.getBlock().getLocation(),
                new Location(
                        event.getBlock().getWorld(),
                        Double.parseDouble(destinationLocationStrings[2]),
                        Double.parseDouble(destinationLocationStrings[3]),
                        Double.parseDouble(destinationLocationStrings[4])
                ));
        event.getPlayer().sendMessage("§7Der §eWireless Trichter §7wurde §aerfolgreich §7platziert.");
    }

}
