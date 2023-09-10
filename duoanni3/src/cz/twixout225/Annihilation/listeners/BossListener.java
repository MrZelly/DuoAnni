package cz.twixout225.Annihilation.listeners;

import cz.twixout225.Annihilation.Annihilation;
import cz.twixout225.Annihilation.Util;
import cz.twixout225.Annihilation.chat.ChatUtil;
import cz.twixout225.Annihilation.object.Boss;
import cz.twixout225.Annihilation.object.PlayerMeta;
import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class BossListener implements Listener {
   private Annihilation plugin;

   public BossListener(Annihilation instance) {
      this.plugin = instance;
   }

   @EventHandler
   public void onHit(EntityDamageEvent event) {
      if (event.getEntity() instanceof IronGolem) {
         IronGolem g = (IronGolem)event.getEntity();
         if (g.getCustomName() == null) {
            return;
         }

         final Boss b = (Boss)this.plugin.getBossManager().bossNames.get(g.getCustomName());
         if (b == null) {
            return;
         }

         if (event.getCause() == DamageCause.VOID) {
            event.getEntity().remove();
            Bukkit.getScheduler().runTask(this.plugin, new Runnable() {
               public void run() {
                  Boss n = BossListener.this.plugin.getBossManager().newBoss(b);
                  BossListener.this.plugin.getBossManager().spawn(n);
               }
            });
         }
      }

   }

   @EventHandler
   public void onHit(EntityDamageByEntityEvent event) {
      if (event.getEntity() instanceof IronGolem) {
         if (!(event.getDamager() instanceof Player)) {
            event.setCancelled(true);
         }

         final IronGolem g = (IronGolem)event.getEntity();
         if (g.getCustomName() == null) {
            return;
         }

         final Boss b = (Boss)this.plugin.getBossManager().bossNames.get(g.getCustomName());
         if (b == null) {
            return;
         }

         Bukkit.getScheduler().runTask(this.plugin, new Runnable() {
            public void run() {
               BossListener.this.plugin.getBossManager().update(b, g);
            }
         });
      }

   }

   @EventHandler
   public void onDeath(EntityDeathEvent event) {
      if (event.getEntity() instanceof IronGolem) {
         IronGolem g = (IronGolem)event.getEntity();
         if (g.getCustomName() == null) {
            return;
         }

         Boss b = (Boss)this.plugin.getBossManager().bossNames.get(g.getCustomName());
         if (b == null) {
            return;
         }

         event.getDrops().clear();
         b.spawnLootChest();
         if (g.getKiller() != null) {
            Player killer = g.getKiller();
            ChatUtil.bossDeath(b, killer, PlayerMeta.getMeta(killer).getTeam());
            this.respawn(b);
            PlayerMeta.addXp(killer, this.plugin.getConfigManager().getConfig("config.yml").getInt("Exp-boss-kill"));
            PlayerMeta.addMoney(killer, (double)this.plugin.getConfigManager().getConfig("config.yml").getInt("Money-boss-kill"));
            Util.spawnFirework(event.getEntity().getLocation(), PlayerMeta.getMeta(killer).getTeam().getColor(PlayerMeta.getMeta(killer).getTeam()), PlayerMeta.getMeta(killer).getTeam().getColor(PlayerMeta.getMeta(killer).getTeam()));
         } else {
            g.teleport(b.getSpawn());
         }
      }

   }

   private void respawn(final Boss b) {
      Bukkit.getScheduler().runTaskLater(this.plugin, new Runnable() {
         public void run() {
            Boss n = BossListener.this.plugin.getBossManager().newBoss(b);
            ChatUtil.bossRespawn(b);
            BossListener.this.plugin.getBossManager().spawn(n);
         }
      }, (long)(20 * this.plugin.respawn * 60));
   }

   @EventHandler
   public void onFall(EntityChangeBlockEvent event) {
      if (event.getEntityType() == EntityType.FALLING_BLOCK && !event.isCancelled()) {
         Iterator iter = this.plugin.chests.iterator();

         while(iter.hasNext()) {
            Block b = (Block)iter.next();
            if (b.getLocation().distance(event.getBlock().getLocation()) < 4.0D) {
               event.setCancelled(true);
            }
         }
      }

   }
}
