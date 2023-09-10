package cz.twixout225.Annihilation.listeners;

import cz.twixout225.Annihilation.object.Kit;
import cz.twixout225.Annihilation.object.PlayerMeta;
import java.util.Arrays;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class CraftingListener implements Listener {
   private ShapedRecipe arrowRecipe;

   public CraftingListener() {
      (this.arrowRecipe = new ShapedRecipe(new ItemStack(Material.ARROW, 3))).shape(new String[]{"F", "S"});
      this.arrowRecipe.setIngredient('F', Material.FLINT);
      this.arrowRecipe.setIngredient('S', Material.STICK);
      Bukkit.addRecipe(this.arrowRecipe);
   }

   @EventHandler
   public void onPrepareCraft(PrepareItemCraftEvent e) {
      Player player = (Player)e.getView().getPlayer();
      if (e.getRecipe() instanceof ShapedRecipe) {
         ShapedRecipe recipe = (ShapedRecipe)e.getRecipe();
         if (PlayerMeta.getMeta(player).getKit() != Kit.ARCHER && this.sameRecipe(recipe, this.arrowRecipe)) {
            e.getInventory().setResult((ItemStack)null);
         }
      }

   }

   @EventHandler
   public void onCraft(CraftItemEvent e) {
      Player player = (Player)e.getWhoClicked();
      if (e.getRecipe() instanceof ShapedRecipe) {
         ShapedRecipe recipe = (ShapedRecipe)e.getRecipe();
         if (PlayerMeta.getMeta(player).getKit() != Kit.ARCHER && this.sameRecipe(recipe, this.arrowRecipe)) {
            e.setCancelled(true);
         }
      }

      if (e.getRecipe().getResult().getType() == Material.BOW) {
         e.setCancelled(true);
      }

   }

   private boolean sameRecipe(ShapedRecipe r1, ShapedRecipe r2) {
      return r1 == r2 || r1 != null && r2 != null && r1.getResult().equals(r2.getResult()) && Arrays.equals(r1.getShape(), r2.getShape());
   }
}
