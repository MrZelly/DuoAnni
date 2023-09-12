package cz.twixout225.Annihilation.listeners;

import cz.twixout225.Annihilation.Annihilation;
import cz.twixout225.Annihilation.object.Kit;
import cz.twixout225.Annihilation.object.PlayerMeta;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class ClassAbilityListener implements Listener {
   private final HashMap<String, Location> blockLocations = new HashMap();
   private final HashMap<String, Long> cooldowns = new HashMap();
   private final HashMap<String, Integer> archerKills = new HashMap();
   private final Annihilation plugin;
   // $FF: synthetic field
   private static volatile int[] $SWITCH_TABLE$cz$twixout225$Annihilation$object$Kit;

   public ClassAbilityListener(Annihilation plugin) {
      this.plugin = plugin;
   }

   @EventHandler
   public void onScoutGrapple(PlayerInteractEvent e) {
      Player pla = e.getPlayer();
      PlayerMeta pmeta = PlayerMeta.getMeta(pla);
      if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
         if (pla.getItemInHand().getType() == Material.FEATHER && pla.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase("§bLeap")) {
            if (pmeta.getCooldown() == 0) {
               if (pla.getInventory().getLeggings() != null && pla.getInventory().getLeggings().getItemMeta() != null && pla.getInventory().getLeggings().getItemMeta().getDisplayName() != null && pla.getInventory().getLeggings().getItemMeta().getDisplayName().contains("Quick leggings")) {
            	   pmeta.setCooldown(7);
               } else {
            	   pmeta.setCooldown(10);
               }
               pla.setVelocity(pla.getLocation().getDirection().setY(0.9D).multiply(1.2D));
               pla.playSound(pla.getLocation(), Sound.ENDERDRAGON_WINGS, 1.0F, 2.0F);
            } else {
               pla.sendMessage("§8[§eCooldown§8] §cPlease wait before using ability again §7(§e" + pmeta.getCooldown() + "§7)");
            }
         }

         if (pla.getItemInHand().getType() == Material.GHAST_TEAR && pla.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase("§bHeal")) {
            if (pmeta.getCooldown() == 0) {
               if (pla.getInventory().getLeggings() != null && pla.getInventory().getLeggings().getItemMeta() != null && pla.getInventory().getLeggings().getItemMeta().getDisplayName() != null && pla.getInventory().getLeggings().getItemMeta().getDisplayName().contains("Quick leggings")) {
             	   pmeta.setCooldown(34);
                } else {
             	   pmeta.setCooldown(45);
                }
               double currentHealth = pla.getHealth();
               pla.setHealth(currentHealth + 15.0D);
               pla.playSound(pla.getLocation(), Sound.VILLAGER_YES, 1.0F, 2.0F);
            } else {
               pla.sendMessage("§8[§eCooldown§8] §cPlease wait before using ability again §7(§e" + pmeta.getCooldown() + "§7)");
            }
         }

         ItemStack item;
         if (e.getMaterial() == Material.ENDER_PEARL) {
            item = new ItemStack(Material.ENDER_PEARL, 1);
            SoulboundListener.soulbind(item);
            if (pmeta.getCooldown() == 0) {
               if (pla.getInventory().getLeggings() != null && pla.getInventory().getLeggings().getItemMeta() != null && pla.getInventory().getLeggings().getItemMeta().getDisplayName() != null && pla.getInventory().getLeggings().getItemMeta().getDisplayName().contains("Quick leggings")) {
             	   pmeta.setCooldown(45);
                } else {
             	   pmeta.setCooldown(60);
                }
               pla.getInventory().addItem(new ItemStack[]{item});
            } else {
               e.setCancelled(true);
               pla.sendMessage("§8[§eCooldown§8] §cPlease wait before throwing ender pearl again §7(§e" + pmeta.getCooldown() + "§7)");
               pla.updateInventory();
            }
         }

         if (pla.getItemInHand().getType() == Material.SPIDER_EYE && pla.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase("§bGet Cobweb")) {
            if (pmeta.getCooldown() == 0) {
               item = new ItemStack(Material.WEB, 3);
               SoulboundListener.soulbind(item);
               if (pla.getInventory().contains(item)) {
                  pla.sendMessage("§cYou already have 3 cobwebs!");
               } else {
                  ItemStack web = new ItemStack(Material.WEB, 1);
                  SoulboundListener.soulbind(web);
                  pla.getInventory().addItem(new ItemStack[]{web});
                  pla.playSound(pla.getLocation(), Sound.SPIDER_DEATH, 1.0F, 2.0F);
                 if (pla.getInventory().getLeggings() != null && pla.getInventory().getLeggings().getItemMeta() != null && pla.getInventory().getLeggings().getItemMeta().getDisplayName() != null && pla.getInventory().getLeggings().getItemMeta().getDisplayName().contains("Quick leggings")) {
               	   pmeta.setCooldown(45);
                  } else {
               	   pmeta.setCooldown(60);
                  }               }
            } else {
               pla.sendMessage("§8[§eCooldown§8] §cPlease wait before using ability again! §7(§e" + pmeta.getCooldown() + "§7)");
            }
         }

         if (pla.getItemInHand().getType() == Material.SUGAR && pla.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase("§bInvincibility")) {
            if (pmeta.getCooldown() == 0) {
               pmeta.setCooldown(60);
               pla.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 60, 100));
               pla.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 100));
               pla.playSound(pla.getLocation(), Sound.ANVIL_LAND, 1.0F, 2.0F);
            } else {
               pla.sendMessage("§8[§eCooldown§8] §cPlease wait before using ability again §7(§e" + pmeta.getCooldown() + "§7)");
            }
         }

			/*
			 * if (e.getMaterial() == Material.EYE_OF_ENDER) { if (pmeta.getCooldown() == 0)
			 * { if (pla.getInventory().getLeggings() != null &&
			 * pla.getInventory().getLeggings().getItemMeta() != null &&
			 * pla.getInventory().getLeggings().getItemMeta().getDisplayName() != null &&
			 * pla.getInventory().getLeggings().getItemMeta().getDisplayName().
			 * contains("Quick leggings")) { pmeta.setCooldown(45); } else {
			 * pmeta.setCooldown(60); } pla.teleport(pmeta.getTeam().getRandomSpawn());
			 * e.setCancelled(true); } else { e.setCancelled(true); pla.
			 * sendMessage("§8[§eCooldown§8] §cPlease wait before teleporting again §7(§e" +
			 * pmeta.getCooldown() + "§7)"); pla.updateInventory(); } }
			 */
      }

   }

   @EventHandler
   public void Kill(PlayerDeathEvent e) {
      if (e.getEntity() instanceof Player && e.getEntity().getKiller() instanceof Player) {
         Player pla = e.getEntity();
         if (PlayerMeta.getMeta(pla.getKiller().getName()).getKit() == Kit.ARCHER) {
            if (!this.archerKills.containsKey(pla.getKiller().getName())) {
               this.archerKills.put(pla.getKiller().getName(), 1);
            } else {
               this.archerKills.replace(pla.getKiller().getName(), (Integer)this.archerKills.get(pla.getKiller().getName()) + 1);
            }

            if (pla.getKiller().getItemInHand().getType() == Material.BOW && pla.getKiller().getItemInHand() != null && pla.getKiller().getItemInHand().getItemMeta().getLore().contains("Mysterious Bow")) {
            	if(this.archerKills.get(pla.getKiller()) == 1) {
                  	pla.getKiller().getItemInHand().removeEnchantment(Enchantment.ARROW_DAMAGE);
                  	pla.getKiller().getItemInHand().addEnchantment(Enchantment.ARROW_DAMAGE, 1);
            	} else if (this.archerKills.get(pla.getKiller()) == 2){
            		pla.getKiller().getItemInHand().removeEnchantment(Enchantment.ARROW_DAMAGE);
                  	pla.getKiller().getItemInHand().addEnchantment(Enchantment.ARROW_DAMAGE, 2);
            	} else if (this.archerKills.get(pla.getKiller()) == 5){
              		pla.getKiller().getItemInHand().removeEnchantment(Enchantment.ARROW_DAMAGE);
                  	pla.getKiller().getItemInHand().addEnchantment(Enchantment.ARROW_DAMAGE, 3);
            	} else if (this.archerKills.get(pla.getKiller()) == 9){
              		pla.getKiller().getItemInHand().removeEnchantment(Enchantment.ARROW_DAMAGE);
              		pla.getKiller().getItemInHand().addEnchantment(Enchantment.ARROW_DAMAGE, 4);
            	} else if (this.archerKills.get(pla.getKiller()) == 14){
            	   pla.getKiller().getItemInHand().removeEnchantment(Enchantment.ARROW_DAMAGE);
            	   pla.getKiller().getItemInHand().addEnchantment(Enchantment.ARROW_DAMAGE, 5);
            	}
            }
         }

         if (PlayerMeta.getMeta(pla.getKiller().getName()).getKit() == Kit.ASSASIN) {
            ItemStack gapple5 = new ItemStack(Material.GOLDEN_APPLE, 5);
            ItemStack gapple = new ItemStack(Material.GOLDEN_APPLE, 1);
            SoulboundListener.soulbind(gapple);
            SoulboundListener.soulbind(gapple5);
            if (!pla.getKiller().getInventory().contains(gapple5)) {
               pla.getKiller().getInventory().addItem(new ItemStack[]{gapple});
            }
         }
         if (pla.getKiller().getInventory().getBoots() != null && 
   			 pla.getKiller().getInventory().getBoots().getItemMeta() != null && 
   			 pla.getKiller().getInventory().getBoots().getItemMeta().getDisplayName() != null && 
   			 pla.getKiller().getInventory().getBoots().getItemMeta().getDisplayName().contains("Fly boots")) {
        	 pla.getKiller().removePotionEffect(PotionEffectType.SPEED);
        	 pla.getKiller().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 0));
         }
      }
   }
   
   @EventHandler
   public void onGuardTeleport(PlayerInteractEvent e) {
      Player player = e.getPlayer();
      PlayerMeta meta = PlayerMeta.getMeta(player);
      if ((e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) && e.getMaterial() == Material.EYE_OF_ENDER) {
          e.setCancelled(true);
	      if (meta.getCooldown() == 0) {
	    	  meta.isTeleporting = true;
	    	  player.sendMessage("§8Teleporting in 5");
	          new BukkitRunnable() {
	        	  @Override
	        	  public void run() {
	        		  if (meta.isTeleporting) {
	        	    	  player.sendMessage("§8Teleporting in 4");
	        		  } else {
	        			  
	        		  }
	        	  }
	          }.runTaskLater(this.plugin, 20);
	          new BukkitRunnable() {
	        	  @Override
	        	  public void run() {
	        		  if (meta.isTeleporting) {
	        	    	  player.sendMessage("§8Teleporting in 3");
	        		  } else {
	        			  
	        		  }
	        	  }
	          }.runTaskLater(this.plugin, 40);
	          new BukkitRunnable() {
	        	  @Override
	        	  public void run() {
	        		  if (meta.isTeleporting) {
	        	    	  player.sendMessage("§8Teleporting in 2");
	        		  } else {
	        			  
	        		  }
	        	  }
	          }.runTaskLater(this.plugin, 60);
	          new BukkitRunnable() {
	        	  @Override
	        	  public void run() {
	        		  if (meta.isTeleporting) {
	        	    	  player.sendMessage("§8Teleporting in 1");
	        		  } else {
	        			  
	        		  }
	        	  }
	          }.runTaskLater(this.plugin, 80);
	          new BukkitRunnable() {
	        	  @Override
	        	  public void run() {
	        		  if (meta.isTeleporting) {
	        	    	  player.sendMessage("§8Teleporting now");
		    	          player.teleport(meta.getTeam().getRandomSpawn());
		    	          meta.isTeleporting = false;
	        		  } else {
	        			  
	        		  }
	        	  }
	          }.runTaskLater(this.plugin, 100);
	          
	          if (player.getInventory().getLeggings() != null && player.getInventory().getLeggings().getItemMeta() != null && player.getInventory().getLeggings().getItemMeta().getDisplayName() != null && player.getInventory().getLeggings().getItemMeta().getDisplayName().contains("Quick leggings")) {
	        	   meta.setCooldown(45);
	          } else {
	        	   meta.setCooldown(60);
	          }
	      } else {
	          e.setCancelled(true);
	          player.sendMessage("§8[§eCooldown§8] §cPlease wait before teleporting again §7(§e" + meta.getCooldown() + "§7)");
	          player.updateInventory();
	      }
      }
   }

   @EventHandler
   public void onHitWhileGuardIsTeleporting(EntityDamageByEntityEvent e) {
      Player player = (Player) e.getEntity();
      PlayerMeta meta = PlayerMeta.getMeta(player);
      if (meta.isTeleporting()) {
    	  meta.isTeleporting = false;
    	  player.sendMessage("§cTeleporting cancelled because you got hit");
      }
   }
   
   @EventHandler
   public void Firemanrespawn(PlayerRespawnEvent e) {	  
      PlayerMeta meta = PlayerMeta.getMeta(e.getPlayer());
      if (this.cooldowns.containsKey(meta.getName())) {
          meta.setCooldown(0);
       }
      
      new BukkitRunnable() {
    	  @Override
    	  public void run() {
	          if (meta.getKit() == Kit.FIREMAN) {
	             e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 100000, 0));
	          } else if (meta.getKit() == Kit.RUSHER) {
	             e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100000, 0));
	          }
    	  }
      }.runTaskLater(this.plugin, 20);
   }

   @EventHandler
   public void Firemanhit(EntityDamageByEntityEvent e) {
      if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
         Player damager = (Player)e.getDamager();
         PlayerMeta meta = PlayerMeta.getMeta(damager);
         if (meta.getKit() == Kit.FIREMAN) {
            e.getEntity().setFireTicks(60);
         }
      }
   }
   
   @EventHandler
   public void onArrowHit(EntityDamageByEntityEvent e) {
	   if (e.getEntity() instanceof Player && e.getDamager() instanceof Arrow) {
		   Player player = (Player) e.getEntity();
		   if(player.getInventory().getHelmet() != null && 
			  player.getInventory().getHelmet().getItemMeta() != null && 
			  player.getInventory().getHelmet().getItemMeta().getDisplayName() != null && 
			  player.getInventory().getHelmet().getItemMeta().getDisplayName().contains("Oxyger")) {
			   double damage = e.getDamage() / 100;
			   e.setDamage(damage);
		   }
	   }
   }
   
   @EventHandler
   public void onNauseaArmorHit(EntityDamageByEntityEvent e) {
	   if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
		   Player player = (Player) e.getEntity();
		   Player damager = (Player) e.getDamager();
		   if(player.getInventory().getChestplate() != null && 
			  player.getInventory().getChestplate().getItemMeta() != null && 
			  player.getInventory().getChestplate().getItemMeta().getDisplayName() != null && 
			  player.getInventory().getChestplate().getItemMeta().getDisplayName().contains("Troll plate")) {
			  damager.removePotionEffect(PotionEffectType.CONFUSION);
			  damager.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 100, 2));
		   }
	   }

   }

   @EventHandler
   public void onFallDamage(EntityDamageEvent e) {
      if (e.getEntity() instanceof Player) {
         Player player = (Player)e.getEntity();
         PlayerMeta meta = PlayerMeta.getMeta(player);
         if (meta.getKit() == Kit.JUMPER && e.getCause() == DamageCause.FALL) {
            e.setCancelled(true);
         }

      }
   }

   @EventHandler
   public void onEnderPearlDamage(PlayerTeleportEvent e) {
      Player player = e.getPlayer();
      if (e.getCause() == TeleportCause.ENDER_PEARL) {
         e.setCancelled(true);
         player.teleport(e.getTo());
      }

   }
   
/*   @EventHandler
   public void onPlayerFish(PlayerFishEvent e){
     Player player = e.getPlayer();
     PlayerMeta meta = PlayerMeta.getMeta(e.getPlayer());
    
     if (e.getState() == PlayerFishEvent.State.CAUGHT_ENTITY && meta.getKit() == Kit.FISHERMAN && meta.getCooldown() == 0){
       Entity caught = e.getCaught();    
       caught.teleport(player);
       if (player.getInventory().getLeggings().getItemMeta().getDisplayName() == "Quick leggings") {
    	   meta.setCooldown(30);
       } else {
    	   meta.setCooldown(40);
       }     }
     else if (e.getState() == PlayerFishEvent.State.CAUGHT_ENTITY && meta.getKit() == Kit.FISHERMAN && meta.getCooldown() != 0) {
       player.sendMessage("§8[§eCooldown§8] §cPlease wait before attaracting someone again §7(§e" + meta.getCooldown() + "§7)");
     }
   }*/

   private void update() {
      Iterator var2 = this.cooldowns.entrySet().iterator();

      while(var2.hasNext()) {
         Entry<String, Long> entry = (Entry)var2.next();
         long cooldown = (Long)entry.getValue();
         if (cooldown > 0L) {
            entry.setValue(--cooldown);
            String name = (String)entry.getKey();
            Player player = Bukkit.getPlayer(name);
            if (player.isOnline()) {
               int var10000 = $SWITCH_TABLE$cz$twixout225$Annihilation$object$Kit()[PlayerMeta.getMeta(player).getKit().ordinal()];
            }
         }
      }
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$cz$twixout225$Annihilation$object$Kit() {
      int[] var10000 = $SWITCH_TABLE$cz$twixout225$Annihilation$object$Kit;
      if (var10000 != null) {
         return var10000;
      } else {
         int[] var0 = new int[Kit.values().length];

         try {
            var0[Kit.ARCHER.ordinal()] = 3;
         } catch (NoSuchFieldError var9) {
         }

         try {
            var0[Kit.ASSASIN.ordinal()] = 8;
         } catch (NoSuchFieldError var8) {
         }

         try {
            var0[Kit.CIVILIAN.ordinal()] = 1;
         } catch (NoSuchFieldError var7) {
         }

         try {
            var0[Kit.FIREMAN.ordinal()] = 6;
         } catch (NoSuchFieldError var6) {
         }

         try {
            var0[Kit.GUARD.ordinal()] = 9;
         } catch (NoSuchFieldError var5) {
         }

         try {
            var0[Kit.HEALER.ordinal()] = 7;
         } catch (NoSuchFieldError var4) {
         }

         try {
            var0[Kit.JUMPER.ordinal()] = 5;
         } catch (NoSuchFieldError var3) {
         }

         try {
            var0[Kit.MINER.ordinal()] = 4;
         } catch (NoSuchFieldError var2) {
         }

         try {
            var0[Kit.RUSHER.ordinal()] = 2;
         } catch (NoSuchFieldError var1) {
         }

         $SWITCH_TABLE$cz$twixout225$Annihilation$object$Kit = var0;
         return var0;
      }
   }
}
