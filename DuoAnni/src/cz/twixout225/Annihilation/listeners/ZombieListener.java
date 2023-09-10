package cz.twixout225.Annihilation.listeners;

import cz.twixout225.Annihilation.Annihilation;
import cz.twixout225.Annihilation.Translator;
import cz.twixout225.Annihilation.Util;
import cz.twixout225.Annihilation.manager.PlayerSerializer;
import cz.twixout225.Annihilation.object.GameTeam;
import cz.twixout225.Annihilation.object.PlayerMeta;
import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.inventory.ItemStack;

public class ZombieListener implements Listener {
   private Annihilation plugin;

   public ZombieListener(Annihilation pl) {
      this.plugin = pl;
   }

   @EventHandler
   public void onTakeDamage(EntityDamageByEntityEvent ev) {
      Entity ent = ev.getEntity();
      EntityType entt = ent.getType();
      if (entt == EntityType.ZOMBIE) {
         if (ev != null) {
            if (ent != null) {
               if (entt != null) {
                  Zombie z = (Zombie)ev.getEntity();
                  Player p;
                  PlayerMeta meta;
                  if (ev.getDamager() instanceof Snowball) {
                     if (ev.getDamager() == null) {
                        return;
                     }

                     Snowball s = (Snowball)ev.getDamager();
                     if (s == null) {
                        return;
                     }

                     if (s.getShooter() == null) {
                        ev.setCancelled(true);
                        return;
                     }

                     if (s.getShooter() instanceof Player) {
                        p = (Player)s.getShooter();
                        meta = PlayerMeta.getMeta(p);
                        if (meta == null) {
                           return;
                        }

                        if (z.getCustomName() == null) {
                           z.remove();
                           return;
                        }

                        try {
                           if (GameTeam.getTeamChar(z.getCustomName()).equals(meta.getTeam().getNexus().getTeam())) {
                              p.sendMessage(Translator.change("PREFIX") + ChatColor.RED + "You cannot hit your team.");
                              ev.setCancelled(true);
                           }
                        } catch (Exception var11) {
                        }
                     }
                  }

                  if (ev.getDamager() instanceof Arrow) {
                     if (ev.getDamager() == null) {
                        return;
                     }

                     Arrow a = (Arrow)ev.getDamager();
                     if (a == null) {
                        return;
                     }

                     if (a.getShooter() == null) {
                        ev.setCancelled(true);
                        return;
                     }

                     if (a.getShooter() instanceof Player) {
                        p = (Player)a.getShooter();
                        meta = PlayerMeta.getMeta(p);
                        if (meta == null) {
                           return;
                        }

                        if (z.getCustomName() == null) {
                           z.remove();
                           return;
                        }

                        try {
                           if (GameTeam.getTeamChar(z.getCustomName()).equals(meta.getTeam().getNexus().getTeam())) {
                              p.sendMessage(Translator.change("PREFIX") + ChatColor.RED + "You cannot hit your team.");
                              ev.setCancelled(true);
                           }
                        } catch (Exception var10) {
                        }
                     }
                  }

                  if (ev.getDamager() instanceof Player) {
                     if (ev.getDamager() == null) {
                        return;
                     }

                     Player p1 = (Player)ev.getDamager();
                     if (p1 == null) {
                        return;
                     }

                     PlayerMeta meta1 = PlayerMeta.getMeta(p1);
                     if (meta1 == null) {
                        return;
                     }

                     if (z.getCustomName() == null) {
                        z.remove();
                        return;
                     }

                     try {
                        if (GameTeam.getTeamChar(z.getCustomName()).equals(meta1.getTeam().getNexus().getTeam())) {
                           p1.sendMessage(Translator.change("PREFIX") + ChatColor.RED + "You cannot hit your team.");
                           ev.setCancelled(true);
                        }
                     } catch (Exception var9) {
                     }
                  }

               }
            }
         }
      }
   }

   @EventHandler
   public void onDeathEntity(EntityDeathEvent e) {
      if (e.getEntity() instanceof Zombie) {
         Zombie z = (Zombie)e.getEntity();
         if (z.getCustomName() == null) {
            return;
         }

         String zName = Util.replaceTeamColor(z.getCustomName());
         if (e.getEntity().getKiller() instanceof Player) {
            Player killer = e.getEntity().getKiller();
            PlayerMeta.addXp(killer, this.plugin.getConfigManager().getConfig("config.yml").getInt("Exp-player-kill"));

            Player var5;
            for(Iterator var6 = Bukkit.getOnlinePlayers().iterator(); var6.hasNext(); var5 = (Player)var6.next()) {
            }
         }

         e.getDrops().addAll(PlayerSerializer.dropItem(zName));
         e.setDroppedExp(0);
         Iterator<ItemStack> it = e.getDrops().iterator();
         if (it != null && it.hasNext()) {
            while(it.hasNext()) {
               ItemStack i = (ItemStack)it.next();
               if (i != null && i.getType() != Material.AIR) {
                  if (SoulboundListener.isSoulbound(i)) {
                     it.remove();
                  } else if (i.getType() == Material.ROTTEN_FLESH) {
                     it.remove();
                  }
               }
            }
         }

         if (this.plugin.getZombies().containsKey(z.getCustomName())) {
            this.plugin.getZombies().remove(z);
            z.remove();
            PlayerSerializer.removeItems(zName);
         }
      }

   }

   @EventHandler
   public void onDamageEntity(EntityDamageEvent e) {
      if (e.getEntity() instanceof Zombie) {
         Zombie z = (Zombie)e.getEntity();
         if (z.getCustomName() == null) {
            return;
         }

         if (e.getEntity().getLocation().getY() <= 0.0D) {
            String zName = Util.replaceTeamColor(z.getCustomName());
            if (this.plugin.getZombies().containsKey(z.getCustomName())) {
               this.plugin.getZombies().remove(z);
               z.remove();
               PlayerSerializer.removeItems(zName);
            }
         }
      }

   }

   @EventHandler
   public void onTarget(EntityTargetEvent e) {
      if (e.getEntity() instanceof Zombie) {
         Zombie z = (Zombie)e.getEntity();
         if (e.getTarget() instanceof Player) {
            Player p = (Player)e.getTarget();
            PlayerMeta meta = PlayerMeta.getMeta(p);
            if (z.getCustomName() == null) {
               z.remove();
               return;
            }

            if (GameTeam.getTeamChar(z.getCustomName()).equals(meta.getTeam().getNexus().getTeam())) {
               e.setCancelled(true);
               e.setTarget((Entity)null);
            }
         }
      }

   }
}
