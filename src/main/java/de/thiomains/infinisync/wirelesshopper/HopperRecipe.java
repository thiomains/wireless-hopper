package de.thiomains.infinisync.wirelesshopper;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ShapedRecipe;

public class HopperRecipe {

    public HopperRecipe(WirelessHopper main) {
        NamespacedKey key = new NamespacedKey(main, "wireless_hopper");
        ShapedRecipe recipe = new ShapedRecipe(key, WirelessHopper.getHopperItem());
        recipe.shape(
                "roq",
                "ovo",
                "qor");
        recipe.setIngredient('o', Material.ENDER_EYE);
        recipe.setIngredient('v', Material.HOPPER);
        recipe.setIngredient('q', Material.QUARTZ);
        recipe.setIngredient('r', Material.REDSTONE);

        Bukkit.addRecipe(recipe);
    }

}
