package de.thiomains.infinisync.wirelesshopper;

import org.bukkit.*;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class PlaceHopperListener implements Listener {

    private WirelessHopper main;

    public PlaceHopperListener(WirelessHopper main) {
        this.main = main;
        Bukkit.getPluginManager().registerEvents(this, main);
        startScheduler();
    }

    private void startScheduler() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(main, new Runnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    ItemStack heldItem = player.getInventory().getItemInMainHand();
                    if (heldItem.getType() == Material.AIR) continue;
                    if (!heldItem.hasItemMeta()) continue;
                    if (!heldItem.getItemMeta().hasLore()) continue;
                    if (!heldItem.getItemMeta().getLore().contains("§8HRlW6yZo6Kc")) continue;
                    if (heldItem.getItemMeta().getLore().get(0).equals("")) continue;
                    String destinationLocationString = heldItem.getItemMeta().getLore().get(0);
                    String[] destinationLocationStrings = destinationLocationString.split(" ");
                    Location destinationLocation = new Location(
                            player.getWorld(),
                            Double.parseDouble(destinationLocationStrings[2]),
                            Double.parseDouble(destinationLocationStrings[3]),
                            Double.parseDouble(destinationLocationStrings[4])
                    );
                    destinationLocation.add(0.5, 0.5, 0.5);
                    RayTraceResult rayTraceResult = player.getWorld().rayTraceBlocks(player.getEyeLocation(), player.getEyeLocation().getDirection(), 4.5);
                    if (rayTraceResult == null) continue;
                    if (rayTraceResult.getHitBlockFace() == null) continue;
                    Location targetBlock = rayTraceResult.getHitBlock().getRelative(rayTraceResult.getHitBlockFace()).getLocation().add(0.5, 0.5, 0.5);
                    Vector targetBlocktoHopper = destinationLocation.clone().toVector().subtract(targetBlock.toVector());
                    int particlesPerBlock = 2;
                    double distance = targetBlocktoHopper.length();

                    for (int i = 0; i < distance * particlesPerBlock; i++) {
                        double factor = i / (double) particlesPerBlock;
                        Vector offset = targetBlocktoHopper.clone().normalize().multiply(factor);
                        Location particleLocation = targetBlock.clone().add(offset);
                        player.spawnParticle(Particle.COMPOSTER, particleLocation, 0);
                    }
                }
            }
        }, 0L, 1L);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getClickedBlock() == null) return;
        if (event.getClickedBlock().getType() != Material.HOPPER) return;
        if (((Container) event.getClickedBlock().getState()).getCustomName() == null) return;
        if (!((Container) event.getClickedBlock().getState()).getCustomName().contains("Wireless Trichter")) return;
        if (event.getItem() == null) return;
        if (!event.getItem().getItemMeta().hasLore()) return;
        if (!event.getItem().getItemMeta().getLore().contains("§8HRlW6yZo6Kc")) return;
        if (!event.getHand().equals(EquipmentSlot.HAND)) return;

        event.setCancelled(true);

        ItemStack handItem = event.getPlayer().getInventory().getItemInMainHand();
        handItem.setAmount(handItem.getAmount() - 1);
        event.getPlayer().getInventory().setItemInMainHand(handItem);

        Location clickedHopperLocation = event.getClickedBlock().getLocation();

        ItemStack hopperItem = WirelessHopper.getHopperItem();
        ItemMeta hopperItemMeta = hopperItem.getItemMeta();
        ArrayList<String> lore = (ArrayList<String>) hopperItemMeta.getLore();
        lore.set(0, "§7Verbunden mit:§a " + clickedHopperLocation.getBlockX() + " " + clickedHopperLocation.getBlockY() + " " + clickedHopperLocation.getBlockZ());
        hopperItemMeta.setLore(lore);
        hopperItem.setItemMeta(hopperItemMeta);

        event.getPlayer().getInventory().addItem(hopperItem);
        event.getPlayer().playSound(event.getPlayer(), Sound.ENTITY_ITEM_PICKUP, 1.0F, 1.0F);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getPlayer().isSneaking()) return;
        if (event.getItemInHand().getItemMeta().getLore() == null) return;
        if (!event.getItemInHand().getItemMeta().getLore().contains("§8HRlW6yZo6Kc")) return;
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
