package sk.zelly.DuoAnni.listeners.PlayerListeners;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import sk.zelly.DuoAnni.Annihilation;
import sk.zelly.DuoAnni.Translator;
import sk.zelly.DuoAnni.Util;
import sk.zelly.DuoAnni.object.BlockObject;
import sk.zelly.DuoAnni.object.GameTeam;
import sk.zelly.DuoAnni.object.PlayerMeta;

public class InteractListener implements Listener {
   private Annihilation plugin;
   private final List<Player> waiting;

   public InteractListener(Annihilation pl) {
      this.plugin = pl;
      this.waiting = new ArrayList();
   }

   @EventHandler
   public void onInteractI(PlayerInteractEvent e) {
      Player player = e.getPlayer();
      PlayerMeta pmeta = PlayerMeta.getMeta(player);
      Action a = e.getAction();
      GameTeam team;
      if (a == Action.RIGHT_CLICK_AIR || a == Action.RIGHT_CLICK_BLOCK) {
         ItemStack handItem = player.getItemInHand();
         if (handItem != null) {
            if (handItem.getType() == Material.FEATHER && handItem.getItemMeta().hasDisplayName() && handItem.getItemMeta().getDisplayName().contains("Right click to select class.") && (pmeta.getTeam() == GameTeam.NONE || player.getWorld().getName().equalsIgnoreCase("lobby"))) {
               Util.showClassSelector(e.getPlayer());
               return;
            }

            if (handItem.getType() == Material.COMPASS) {
               boolean setCompass = false;
               boolean setToNext = false;

               label55:
               while(true) {
                  while(true) {
                     if (setCompass) {
                        break label55;
                     }

                     GameTeam[] var11;
                     int var10 = (var11 = GameTeam.teams()).length;

                     for(int var9 = 0; var9 < var10; ++var9) {
                        team = var11[var9];
                        if (setToNext) {
                           ItemMeta meta = handItem.getItemMeta();
                           meta.setDisplayName(team.color() + "Pointing to " + team.toString() + " Nexus");
                           handItem.setItemMeta(meta);
                           player.setCompassTarget(team.getNexus().getLocation());
                           setCompass = true;
                           break;
                        }

                        if (handItem.getItemMeta().getDisplayName().contains(team.toString())) {
                           setToNext = true;
                        }
                     }
                  }
               }
            }
         }
      }

      if (e.getClickedBlock() != null) {
         Material clickedType = e.getClickedBlock().getType();
         if (clickedType == Material.SIGN_POST || clickedType == Material.WALL_SIGN) {
            Sign s = (Sign)e.getClickedBlock().getState();
            if (s.getLine(0).contains(ChatColor.DARK_PURPLE + "[Team]")) {
               String teamName = ChatColor.stripColor(s.getLine(1));
               team = GameTeam.valueOf(teamName.toUpperCase());
               if (team != null) {
                  if (pmeta.getTeam() == GameTeam.NONE) {
                     this.plugin.joinTeam(e.getPlayer(), teamName);
                  }
               } else {
                  player.sendMessage(Translator.change("PREFIX") + ChatColor.RED + "You cannot join to this team. Team is destroyed.");
               }
            }
         }
      }

   }

   @EventHandler
   public void onFire(BlockIgniteEvent event) {
      event.setCancelled(true);
   }

   @EventHandler
   public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
      event.setCancelled(true);
   }

   @EventHandler
   public void onInteractII(PlayerInteractEvent e) {
      HumanEntity p = e.getPlayer();
      if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
         Player player = (Player)p;
         Block b = e.getClickedBlock();
         if (!e.isCancelled()) {
            if ((b.getType() == Material.WORKBENCH || b.getType() == Material.ANVIL) && b.getType().isBlock() && !this.plugin.crafting.containsKey(p)) {
               BlockObject bo = new BlockObject(b);
               this.plugin.crafting.put(player, bo);
            }

         }
      }
   }

   @EventHandler
   public void onInteractIIIIIIII(PlayerInteractEvent e) {
      final Player player = e.getPlayer();
      Action act = e.getAction();
      if (act == Action.LEFT_CLICK_AIR) {
         if (e.getClickedBlock() != null) {
            return;
         }

         if (this.waiting.contains(player)) {
            e.setCancelled(true);
            return;
         }

         this.waiting.add(player);
         Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
            public void run() {
               if (InteractListener.this.waiting.contains(player)) {
                  InteractListener.this.waiting.remove(player);
               }

            }
         }, 30L);
      }

   }

   @EventHandler
   public void onSwing(PlayerInteractEntityEvent event) {
      Player player = event.getPlayer();
      Iterator var4 = player.getNearbyEntities(4.0D, 4.0D, 4.0D).iterator();

      while(true) {
         Entity e;
         do {
            do {
               if (!var4.hasNext()) {
                  return;
               }

               e = (Entity)var4.next();
            } while(!(e instanceof Player) && !(e instanceof LivingEntity));
         } while(Util.getTargetEntity(player, 4) == event.getRightClicked() && !this.waiting.contains(player));

         event.setCancelled(true);
      }
   }
}
