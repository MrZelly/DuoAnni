package cz.twixout225.Annihilation.listeners;

import cz.twixout225.Annihilation.Translator;
import cz.twixout225.Annihilation.object.GameTeam;
import cz.twixout225.Annihilation.object.PlayerMeta;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class EnderChestListener implements Listener {
   private HashMap<GameTeam, Location> enderchests = new HashMap();
   private HashMap<String, Inventory> inventories = new HashMap();

   @EventHandler
   public void onChestOpen(PlayerInteractEvent e) {
      try {
         if (e.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
         }

         Block b = e.getClickedBlock();
         if (b.getType() != Material.ENDER_CHEST) {
            return;
         }

         Location loc = b.getLocation();
         Player player = e.getPlayer();
         GameTeam team = PlayerMeta.getMeta(player).getTeam();
         if (team == null || !this.enderchests.containsKey(team)) {
            return;
         }

         if (((Location)this.enderchests.get(team)).equals(loc)) {
            e.setCancelled(true);
            this.openEnderChest(player);
         } else if (this.enderchests.containsValue(loc)) {
            e.setCancelled(true);
            player.sendMessage(String.valueOf(String.valueOf(Translator.change("PREFIX"))) + ChatColor.RED + "Chest is locked!");
         }
      } catch (NullPointerException var6) {
      }

   }

   public void setEnderChestLocation(GameTeam team, Location loc) {
      this.enderchests.put(team, loc);
   }

   private void openEnderChest(Player player) {
      GameTeam team = PlayerMeta.getMeta(player).getTeam();
      String name = player.getName();
      if (!this.inventories.containsKey(name)) {
         Inventory inv = Bukkit.createInventory((InventoryHolder)null, 9, "Chest");
         this.inventories.put(name, inv);
      }

      player.openInventory((Inventory)this.inventories.get(name));
      player.sendMessage(String.valueOf(String.valueOf(Translator.change("PREFIX"))) + ChatColor.GRAY + "This is your team's Ender Chest. Any items you store here are safe from all other players.");
   }

   @EventHandler
   public void onEnderChestBreak(BlockBreakEvent e) {
      if (this.enderchests.values().contains(e.getBlock().getLocation())) {
         e.setCancelled(true);
      }

   }
}
