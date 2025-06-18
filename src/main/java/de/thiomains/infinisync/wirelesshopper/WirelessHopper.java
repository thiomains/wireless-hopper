package de.thiomains.infinisync.wirelesshopper;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public class WirelessHopper extends JavaPlugin implements Listener {

    private DatabaseConnection database;
    private ArrayList<HopperConnection> hopperConnections;

    @Override
    public void onEnable() {
        this.database = new DatabaseConnection();
        hopperConnections = database.getAllHopperConnections();
        System.out.println("Hopper Connections: " + hopperConnections.size());
        this.getCommand("givehopper").setExecutor(new GiveHopperCommand());
        new PlaceHopperListener(this);
        new HopperTransfer(this);
    }

    @Override
    public void onDisable() {
        this.database.close();
    }

    public static ItemStack getHopperItem() {
        ItemStack item = new ItemStack(Material.HOPPER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§f§lWireless Trichter");
        ArrayList<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("§861 6D 6F 67 75 73");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public void addHopperConnection(Location source, Location destination) {
        hopperConnections.add(new HopperConnection(source, destination));
        database.insertHopperConnection(source, destination);
    }

    public ArrayList<HopperConnection> getHopperConnections() {
        return hopperConnections;
    }
}
