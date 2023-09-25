package sk.zelly.DuoAnni.listeners;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;

import sk.zelly.DuoAnni.Annihilation;
import sk.zelly.DuoAnni.object.Kit;
import sk.zelly.DuoAnni.object.PlayerMeta;

public class PvPA implements Listener {
   private Annihilation plugin;
   public List<Player> PvPPlayers;

   public PvPA(Annihilation pl) {
      this.plugin = pl;
      this.PvPPlayers = new ArrayList();
   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void onAtt(EntityDamageByEntityEvent event) {
      if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
         Player att = (Player)event.getDamager();
         Player def = (Player)event.getEntity();
         if (def.getWorld().getName().equals("lobby")) {
            event.setCancelled(true);
            return;
         }

         if (this.plugin.getPhase() < 1) {
            event.setCancelled(true);
            return;
         }

         if (this.getDistance(def, att) > 3) {
            event.setCancelled(true);
            return;
         }

         double damage = 0.0D;
         if (PlayerMeta.getMeta(att).getKit() == Kit.RUSHER) {
            ItemStack hand = att.getItemInHand();
            if (hand != null && this.isSword(hand)) {
               ++damage;
            }
         }

         double dam = this.modDamage(att, damage);
         event.setDamage(damage + dam);
      }

   }

   private Double modDamage(Player att, double damage) {
      ItemStack attHand = att.getInventory().getItemInHand();
      if (!this.isSword(attHand)) {
         damage = 1.0D;
         return damage;
      } else {
         damage = this.getSwordDamage(attHand);
         if (!attHand.getEnchantments().isEmpty() && attHand.getEnchantments().containsKey(Enchantment.DAMAGE_ALL)) {
            int eLevel = attHand.getEnchantmentLevel(Enchantment.DAMAGE_ALL);
            switch(eLevel) {
            case 1:
               damage += 0.5D;
            case 2:
               ++damage;
            case 3:
               ++damage;
            case 4:
               damage += 2.0D;
            case 5:
               damage += 3.0D;
            }
         }

         return damage;
      }
   }

   private Boolean isSword(ItemStack i) {
      ItemStack S1 = new ItemStack(Material.WOOD_SWORD);
      ItemStack S2 = new ItemStack(Material.GOLD_SWORD);
      ItemStack S3 = new ItemStack(Material.STONE_SWORD);
      ItemStack S4 = new ItemStack(Material.IRON_SWORD);
      ItemStack S5 = new ItemStack(Material.DIAMOND_SWORD);
      return !i.isSimilar(S1) && !i.isSimilar(S2) && !i.isSimilar(S3) && !i.isSimilar(S4) && !i.isSimilar(S5) ? false : true;
   }

   private Double getSwordDamage(ItemStack i) {
      ItemStack S1 = new ItemStack(Material.WOOD_SWORD);
      ItemStack S2 = new ItemStack(Material.GOLD_SWORD);
      ItemStack S3 = new ItemStack(Material.STONE_SWORD);
      ItemStack S4 = new ItemStack(Material.IRON_SWORD);
      ItemStack S5 = new ItemStack(Material.DIAMOND_SWORD);
      if (i.isSimilar(S1)) {
         return 2.0D;
      } else if (i.isSimilar(S2)) {
         return 3.0D;
      } else if (i.isSimilar(S3)) {
         return 4.0D;
      } else if (i.isSimilar(S4)) {
         return 5.0D;
      } else {
         return i.isSimilar(S5) ? 6.0D : 0.0D;
      }
   }

   public int getDistance(Player player, Player pl) {
      int playerx = (int)player.getLocation().getX();
      int playery = (int)player.getLocation().getY();
      int senderx = (int)pl.getLocation().getX();
      int sendery = (int)pl.getLocation().getY();
      int finalx = playerx - senderx;
      int finaly = playery - sendery;
      if (finalx <= 0) {
         finalx = -finalx;
      }

      if (finaly <= 0) {
         finaly = -finaly;
      }

      int c = (int)Math.sqrt(Math.pow((double)finaly, 2.0D) + Math.pow((double)finalx, 2.0D));
      return c;
   }

   public double getDistanceDouble(Player player, Player pl) {
      double playerx = player.getLocation().getX();
      double playery = player.getLocation().getY();
      double senderx = pl.getLocation().getX();
      double sendery = pl.getLocation().getY();
      double finalx = playerx - senderx;
      double finaly = playery - sendery;
      if (finalx <= 0.0D) {
         finalx = -finalx;
      }

      if (finaly <= 0.0D) {
         finaly = -finaly;
      }

      double c = Math.sqrt(Math.pow(finaly, 2.0D) + Math.pow(finalx, 2.0D));
      return c;
   }

   @EventHandler
   public void onShoot(EntityShootBowEvent event) {
      if (event.getBow() != null) {
         Arrow a = (Arrow)event.getProjectile();
         Player player = (Player)a.getShooter();
         a.setVelocity(player.getLocation().getDirection().multiply(3));
      }
   }
}
