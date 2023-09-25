package sk.zelly.DuoAnni.listeners.PlayerListeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

import sk.zelly.DuoAnni.Annihilation;

public class DamageListener implements Listener {
   private Annihilation plugin;

   public DamageListener(Annihilation pl) {
      this.plugin = pl;
   }

   @EventHandler
   public void onPlayerDamage(EntityDamageEvent e) {
      if (e.getEntity() instanceof Player && e.getEntity().getWorld().getName().equals("lobby")) {
         e.setCancelled(true);
         if (e.getCause() == DamageCause.VOID) {
            e.getEntity().teleport(this.plugin.getMapManager().getLobbySpawnPoint());
         }
      }

   }

   public static List<ItemStack> dropItem(Player player) {
      ItemStack[] a = player.getInventory().getContents();
      ItemStack[] i = player.getInventory().getArmorContents();
      List<ItemStack> items = new ArrayList();
      items.addAll(Arrays.asList(a));
      items.addAll(Arrays.asList(i));
      return items;
   }
}
