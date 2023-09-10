package cz.twixout225.Annihilation.object;

import cz.twixout225.Annihilation.Annihilation;
import cz.twixout225.Annihilation.Translator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

public class Shop implements Listener {
   private String name;
   private ArrayList<Shop.ShopItem> items;
   Annihilation plugin;

   public Shop(Annihilation plugin, String name, Configuration config) {
      this.plugin = plugin;
      plugin.getServer().getPluginManager().registerEvents(this, plugin);
      this.name = name;
      this.loadConfig(config);
   }

   @EventHandler
   public void onSignClick(PlayerInteractEvent e) {
      if (e.getClickedBlock() != null) {
         Material type = e.getClickedBlock().getType();
         Sign sign = (Sign)e.getClickedBlock().getState();
         String line0 = sign.getLine(0);
         String line2 = sign.getLine(1);
         if ((type == Material.WALL_SIGN || type == Material.SIGN_POST) && (line0.equalsIgnoreCase(ChatColor.DARK_PURPLE + "[Shop]") || line0.equalsIgnoreCase("[Shop]") && line2.equalsIgnoreCase(this.name))) {
            if (this.name.contains("Brewing")) {
               if (this.plugin.getPhase() >= 5) {
                  this.openShop(e.getPlayer());
               }
            } else {
               this.openShop(e.getPlayer());
            }
         }
      }

   }

   @EventHandler
   public void onShopInventoryClick(InventoryClickEvent e) {
      Player buyer = (Player)e.getWhoClicked();
      if (e.getInventory().getName().equals(String.valueOf(String.valueOf(this.name)) + " Shop")) {
         int slot = e.getRawSlot();
         if (slot < e.getInventory().getSize() && slot >= 0) {
            if (slot < this.items.size() && this.items.get(slot) != null) {
               this.sellItem(buyer, (Shop.ShopItem)this.items.get(slot));
            }

            e.setCancelled(true);
         }

         buyer.updateInventory();
      }

   }

   private void openShop(Player player) {
      int size = 9 * (int)Math.ceil((double)this.items.size() / 9.0D);
      Inventory shopInv = Bukkit.getServer().createInventory(player, size, String.valueOf(String.valueOf(this.name)) + " Shop");

      for(int i = 0; i < this.items.size(); ++i) {
         Shop.ShopItem item = (Shop.ShopItem)this.items.get(i);
         if (item != null) {
            shopInv.setItem(i, item.getShopStack());
         } else {
            shopInv.setItem(i, (ItemStack)null);
         }
      }

      player.openInventory(shopInv);
   }

   private void sellItem(Player buyer, Shop.ShopItem item) {
      PlayerInventory playerInventory = buyer.getInventory();
      ItemStack stackToGive = item.getItemStack();
      int price = item.getPrice();
      String stackName = ChatColor.WHITE + item.getName();
      if (playerInventory.contains(Material.GOLD_INGOT, price)) {
         playerInventory.removeItem(new ItemStack[]{new ItemStack(Material.GOLD_INGOT, price)});
         playerInventory.addItem(new ItemStack[]{stackToGive});
         buyer.sendMessage(String.valueOf(String.valueOf(Translator.change("PREFIX"))) + Translator.change("PURCHASED") + stackName);
      } else {
         buyer.sendMessage(String.valueOf(String.valueOf(Translator.change("PREFIX"))) + Translator.change("NOT_ENOUGHT_GOLD_TO_PURCHASE") + stackName);
      }

   }

   private void loadConfig(Configuration config) {
      this.items = new ArrayList();
      List<String> list = config.getStringList(this.name.toLowerCase());
      Iterator var4 = list.iterator();

      while(true) {
         while(var4.hasNext()) {
            String entry = (String)var4.next();
            if (entry.equalsIgnoreCase("nextline")) {
               int end = 9 * (int)Math.ceil((double)this.items.size() / 9.0D);

               for(int i = this.items.size(); i < end; ++i) {
                  this.items.add(null);
               }
            } else {
               String[] params = entry.split(",");
               if (params.length >= 3) {
                  Material type = Material.getMaterial(params[0]);
                  int qty = Integer.valueOf(params[1]);
                  int price = Integer.valueOf(params[2]);
                  Shop.ShopItem item = new Shop.ShopItem(type, qty, price);
                  if (params.length >= 4) {
                     String itemName = params[3].replace("\"", "");
                     item.setName(ChatColor.translateAlternateColorCodes('&', itemName));
                  }

                  this.items.add(item);
               }
            }
         }

         return;
      }
   }

   private static class ShopItem {
      private ItemStack item;
      private int price;

      public ShopItem(Material type, int qty, int price) {
         this.item = new ItemStack(type);
         this.price = price;
         this.item.setAmount(qty);
      }

      public Shop.ShopItem setName(String name) {
         ItemMeta meta = this.item.getItemMeta();
         meta.setDisplayName(ChatColor.WHITE + name);
         this.item.setItemMeta(meta);
         return this;
      }

      public ItemStack getShopStack() {
         ItemStack stack = this.item.clone();
         String priceStr = String.valueOf(String.valueOf(ChatColor.GOLD.toString())) + this.price + " Gold";
         ItemMeta meta = stack.getItemMeta();
         if (meta.hasLore()) {
            meta.getLore().add(priceStr);
         } else {
            meta.setLore(Arrays.asList(priceStr));
         }

         stack.setItemMeta(meta);
         return stack;
      }

      public ItemStack getItemStack() {
         return this.item;
      }

      public int getPrice() {
         return this.price;
      }

      public String getName() {
         ItemMeta meta = this.item.getItemMeta();
         String name;
         if (meta.hasDisplayName()) {
            name = meta.getDisplayName();
         } else {
            name = this.item.getType().name();
            name = name.replace("_", " ").toLowerCase();
            name = WordUtils.capitalize(name);
            name = String.valueOf(String.valueOf(name)) + ChatColor.WHITE;
         }

         if (this.item.getAmount() > 1) {
            name = String.valueOf(String.valueOf(this.item.getAmount())) + " " + name;
         }

         return name;
      }
   }
}
