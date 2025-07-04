package de.thiomains.infinisync.wirelesshopper;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class HopperBreakListener implements Listener {

    private WirelessHopper main;

    public HopperBreakListener(WirelessHopper main) {
        this.main = main;
        Bukkit.getPluginManager().registerEvents(this, main);
    }

    @EventHandler
    public void onHopperBreak(BlockBreakEvent event) {
        if (event.getBlock().getType() != Material.HOPPER) return;
        HopperConnection hc = null;
        for (HopperConnection hcs : main.getHopperConnections()) {
            if (hcs.getSource().equals(event.getBlock().getLocation())) {
                hc = hcs;
                break;
            }
            if (hcs.getDestination().equals(event.getBlock().getLocation())) {
                hc = hcs;
                break;
            }
        }
        if (hc == null) return;
        main.removeHopperConnection(hc);
        event.setCancelled(true);
        hc.getSource().getBlock().breakNaturally(new ItemStack(Material.AIR));
        hc.getDestination().getBlock().breakNaturally(new ItemStack(Material.AIR));
        for (int i = 0; i < 2; i++) {
            event.getBlock().getLocation().getWorld().dropItemNaturally(event.getBlock().getLocation(), WirelessHopper.getHopperItem());
        }
    }

}
