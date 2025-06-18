package de.thiomains.infinisync.wirelesshopper;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.ItemStack;

public class HopperTransfer implements Listener {

    private WirelessHopper main;

    public HopperTransfer(WirelessHopper main) {
        this.main = main;
        Bukkit.getPluginManager().registerEvents(this, main);
    }

    @EventHandler
    public void onHopperTransfer(InventoryMoveItemEvent event) {
        for (int i = 0; i < main.getHopperConnections().size(); i++) {
            if (event.getDestination().getLocation().equals(main.getHopperConnections().get(i).getSource())) {
                ItemStack item = event.getItem();
                event.setItem(new ItemStack(Material.AIR));
                Block destinationBlock = main.getHopperConnections().get(i).getDestination().getBlock();
                Container destinationContainer = ((Container) destinationBlock.getState());
                if (destinationContainer.getInventory().firstEmpty() == -1) event.setCancelled(true);
                else destinationContainer.getInventory().addItem(item);
            }
        }
    }

    @EventHandler
    public void onItemPickup(InventoryPickupItemEvent event) {
        for (int i = 0; i < main.getHopperConnections().size(); i++) {
            if (event.getInventory().getLocation().equals(main.getHopperConnections().get(i).getSource())) {
                ItemStack item = event.getItem().getItemStack();
                event.setCancelled(true);
                event.getItem().remove();
                Block destinationBlock = main.getHopperConnections().get(i).getDestination().getBlock();
                Container destinationContainer = ((Container) destinationBlock.getState());
                if (destinationContainer.getInventory().firstEmpty() == -1) event.setCancelled(true);
                else destinationContainer.getInventory().addItem(item);
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getType() != InventoryType.HOPPER) return;
        for (int i = 0; i < main.getHopperConnections().size(); i++) {
            if (!event.getInventory().getLocation().equals(main.getHopperConnections().get(i).getSource())) continue;
            for (int j = 0; j < event.getInventory().getSize(); j++) {
                ItemStack item = event.getInventory().getItem(j);
                if (item == null) continue;
                Block destinationBlock = main.getHopperConnections().get(i).getDestination().getBlock();
                Container destinationContainer = ((Container) destinationBlock.getState());
                if (destinationContainer.getInventory().firstEmpty() == -1) event.getPlayer().getInventory().addItem(item);
                else destinationContainer.getInventory().addItem(item);
            }
            event.getInventory().clear();
        }
        TwoWorlds you = new TwoWorlds();
        TwoWorlds keep = new TwoWorlds();
        TwoWorlds me = new TwoWorlds();
        if (you.give().me().a().piece().of().your().heart()) {
            you.ll().keep().easy().when().we().are().a().part()
                    .of().two().worlds().it().could().be().nothin()
                    .maybe().its().somethin();
            for (int i = 0; i < 9; i++) {
                keep.your().eyes().on().me();
                if (i % 2 == 0) me.me();
            }
        }
    }

}
