package sk.zelly.DuoAnni.listeners.PlayerListeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import sk.zelly.DuoAnni.Annihilation;
import sk.zelly.DuoAnni.Translator;
import sk.zelly.DuoAnni.object.Kit;
import sk.zelly.DuoAnni.object.PlayerMeta;

public class InventoryListener implements Listener {
   private Annihilation plugin;
   public static List<Player> players = new ArrayList();
   public static HashMap<Player, List<ItemStack>> inventorySaved = new HashMap();

   public InventoryListener(Annihilation pl) {
      this.plugin = pl;
   }

   @EventHandler
   public void onInventoryClick(InventoryClickEvent e) {
      Inventory inv = e.getInventory();
      Player player = (Player)e.getWhoClicked();

      try {
         if (inv.getTitle().startsWith("Select Class")) {
            if (e.getCurrentItem().getType() == Material.AIR || e.getCurrentItem().getType() == null) {
               return;
            }

            player.closeInventory();
            e.setCancelled(true);
            String name = e.getCurrentItem().getItemMeta().getDisplayName();
            PlayerMeta meta = PlayerMeta.getMeta(player);
            player.sendMessage(String.valueOf(String.valueOf(String.valueOf(Translator.change("PREFIX")))) + ChatColor.GRAY + "You will recieve this class when you respawn.");
            this.plugin.kitsToGive.put(player.getName(), Kit.getKit(ChatColor.stripColor(name)));
            player.sendMessage(String.valueOf(String.valueOf(String.valueOf(Translator.change("PREFIX")))) + ChatColor.GRAY + "Selected class " + ChatColor.YELLOW + ChatColor.stripColor(name));
            if (this.plugin.getPortalPlayers().containsKey(player)) {
               this.plugin.getPortalPlayers().remove(player);
               player.setHealth(0.0D);
            }
         }
      } catch (Exception var6) {
      }

   }

   @EventHandler
   public void onInteractIIII(PlayerInteractEvent event) {
      if (event.getPlayer().getWorld().getName().equals("lobby") && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) && event.getItem().getType().equals(Material.INK_SACK)) {
         if (event.getItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.RED + "Join RED team")) {
            this.plugin.joinTeam(event.getPlayer(), "red");
         }

         if (event.getItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.BLUE + "Join BLUE team")) {
            this.plugin.joinTeam(event.getPlayer(), "blue");
         }
      }

   }

   @EventHandler
   public void onInventoryClose(InventoryCloseEvent event) {
      if (event.getInventory() instanceof EnchantingInventory) {
         event.getInventory().setItem(1, (ItemStack)null);
      }

   }

   @EventHandler
   public void onInventoryOpen(InventoryOpenEvent e) {
      if (e.getInventory() instanceof EnchantingInventory) {
         EnchantingInventory inv = (EnchantingInventory)e.getInventory();
         ItemStack i = new ItemStack(Material.INK_SACK.getId(), 5, (short)0, (byte)4);
         i.setAmount(64);
         inv.setItem(1, i);
      }

   }

   @EventHandler
   public void onEnchantInventoryClick(InventoryClickEvent event) {
      if (event.getInventory() instanceof EnchantingInventory && event.getSlot() == 1) {
         event.setCancelled(true);
      }

   }
}
