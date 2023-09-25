package cz.twixout225.Annihilation.listeners;

import cz.twixout225.Annihilation.manager.SoundManager;
import java.util.Arrays;
import java.util.Iterator;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SoulboundListener implements Listener {
   private static final String soulboundTag;

   static {
      soulboundTag = ChatColor.GOLD + "Soulbound";
   }

   @EventHandler
   public void onSoulboundDrop(PlayerDropItemEvent e) {
      if (isSoulbound(e.getItemDrop().getItemStack())) {
         Player p = e.getPlayer();
         SoundManager.playSoundForPlayer(p, Sound.BLAZE_HIT, 1.0F, 0.25F, 0.5F);
         e.getItemDrop().remove();
      }

   }

   @EventHandler
   public void onPlayerDeath(PlayerDeathEvent e) {
      Iterator it = e.getDrops().iterator();

      while(it.hasNext()) {
         if (isSoulbound((ItemStack)it.next())) {
            it.remove();
         }
      }

   }

   @EventHandler
   public void onInvClick(InventoryClickEvent event) {
      HumanEntity entity = event.getWhoClicked();
      ItemStack stack = event.getCurrentItem();
      InventoryType top = event.getView().getTopInventory().getType();
      if (stack != null && entity instanceof Player) {
         if (top == InventoryType.PLAYER || top == InventoryType.WORKBENCH || top == InventoryType.CRAFTING) {
            return;
         }

         if (isSoulbound(stack)) {
            event.setCancelled(true);
         }
      }

   }

   public static boolean isSoulbound(ItemStack item) {
      ItemMeta meta = item.getItemMeta();
      return item.hasItemMeta() && meta.hasLore() && meta.getLore().contains(soulboundTag);
   }

   public static void soulbind(ItemStack stack) {
      ItemMeta meta = stack.getItemMeta();
      if (!meta.hasLore()) {
         meta.setLore(Arrays.asList(soulboundTag));
      } else {
         meta.getLore().add(soulboundTag);
      }

      stack.setItemMeta(meta);
   }
}
