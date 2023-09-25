package cz.twixout225.Annihilation.listeners.PlayerListeners;

import com.google.common.collect.Maps;
import cz.twixout225.Annihilation.Annihilation;
import cz.twixout225.Annihilation.Translator;
import cz.twixout225.Annihilation.Util;
import cz.twixout225.Annihilation.api.NexusDamageEvent;
import cz.twixout225.Annihilation.api.NexusDestroyEvent;
import cz.twixout225.Annihilation.chat.ChatUtil;
import cz.twixout225.Annihilation.object.BlockObject;
import cz.twixout225.Annihilation.object.GameTeam;
import cz.twixout225.Annihilation.object.PlayerMeta;
import cz.twixout225.Annihilation.stats.StatType;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.scoreboard.Score;

public class BlockListener implements Listener {
   public HashMap<Player, Integer> nukers = Maps.newHashMap();
   private Annihilation plugin;
   public static boolean BlockWater = true;
   public static boolean BlockLava = true;

   public BlockListener(Annihilation pl) {
      this.plugin = pl;
      this.startTimer();
   }

   public void startTimer() {
      Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, new Runnable() {
         public void run() {
            Set<Entry<Player, Integer>> s = BlockListener.this.nukers.entrySet();
            Iterator iter = s.iterator();

            while(iter.hasNext()) {
               Entry<Player, Integer> entry = (Entry)iter.next();
               if ((Integer)entry.getValue() <= 0) {
                  iter.remove();
                  break;
               }

               entry.setValue((Integer)entry.getValue() - 1);
            }

         }
      }, 0L, 4L);
   }

   @EventHandler
   public void onPM(BlockPistonExtendEvent e) {
      Iterator var3 = e.getBlocks().iterator();

      while(var3.hasNext()) {
         Block b = (Block)var3.next();
         if (Util.tooClose(b.getLocation(), this.plugin)) {
            e.setCancelled(true);
            b.setType(Material.AIR);
         }
      }

   }

   public boolean isOverVoid(Block b) {
      Location loc = b.getLocation();

      for(int i = 0; i < 101; ++i) {
         if (loc.add(0.0D, -1.0D, 0.0D).getBlockY() >= 0 && loc.getBlock().getType() != Material.AIR) {
            return false;
         }
      }

      return true;
   }

   @EventHandler
   public void onPlace(BlockPlaceEvent e) {
      if (this.isOverVoid(e.getBlock())) {
         e.setCancelled(true);
      }

      if (Annihilation.getInstance().getPhase() > 0) {
         if (e.getPlayer().getWorld().getName().equalsIgnoreCase("lobby")) {
            e.setCancelled(true);
            return;
         }

         if (e.getBlock().getType() == Material.BOOKSHELF) {
            if (e.getBlock().getRelative(BlockFace.DOWN).getType() == Material.BOOKSHELF) {
               e.setCancelled(false);
            } else {
               e.setCancelled(true);
               e.getPlayer().sendMessage(String.valueOf(String.valueOf(String.valueOf(Translator.change("PREFIX")))) + ChatColor.YELLOW + "You must place Bookshelfs on Bookshelfs!");
            }
         }

         if (e.getBlock().getType() == Material.BREWING_STAND) {
            if (e.getBlock().getRelative(BlockFace.DOWN).getType() == Material.NETHER_BRICK) {
               e.setCancelled(false);
            } else {
               e.setCancelled(true);
               e.getPlayer().sendMessage(String.valueOf(String.valueOf(String.valueOf(Translator.change("PREFIX")))) + ChatColor.YELLOW + "You must place Brewing Stand on Nether Brick block!");
            }
         }

         if (Util.tooClose(e.getBlock().getLocation(), this.plugin) && e.getBlock().getType() != Material.BOOKSHELF && e.getBlock().getType() != Material.BREWING_STAND) {
            e.setCancelled(true);
         }
      } else {
         e.setCancelled(true);
      }

   }

   @EventHandler
   public void onSignPlace(SignChangeEvent e) {
      if (e.getPlayer().hasPermission("annihilation.buildbypass") && e.getLine(0).toLowerCase().contains("[Shop]".toLowerCase())) {
         e.setLine(0, ChatColor.BLACK + "[Shop]");
      }

   }

   @EventHandler
   public void onBreak(BlockBreakEvent e) {
      Block b = e.getBlock();
      if (e.getPlayer().getWorld().getName().equalsIgnoreCase("lobby")) {
         e.setCancelled(true);
      } else {
         if (e.getBlock().getType() == Material.BOOKSHELF) {
            e.setCancelled(true);
         }

         if (Util.hasSignAttached(b) && Util.isShopSignAttached(b)) {
            e.setCancelled(true);
         }

      }
   }

   public Location chciLocation(String in, String world) {
      String[] params = in.split(",");
      if (params.length != 3 && params.length != 5) {
         return null;
      } else {
         double x = Double.parseDouble(params[0]);
         double y = Double.parseDouble(params[1]);
         double z = Double.parseDouble(params[2]);
         Location loc = new Location(Bukkit.getWorld(world), x, y, z);
         if (params.length == 5) {
            loc.setYaw(Float.parseFloat(params[3]));
            loc.setPitch(Float.parseFloat(params[4]));
         }

         return loc;
      }
   }

   @EventHandler
   public void onBreakIII(BlockBreakEvent e) {
      GameTeam[] teams;
      int length = (teams = GameTeam.teams()).length;
      if (e.getPlayer().getWorld().getName().equalsIgnoreCase("lobby")) {
          e.setCancelled(true);
      } else {
	
	      for(int i = 0; i < length; ++i) {
	         GameTeam t = teams[i];
	         if (t.getNexus().getLocation().equals(e.getBlock().getLocation())) {
	            e.setCancelled(true);
	            if (t.getNexus().isAlive()) {
	               this.breakNexus(t, e.getPlayer());
	            }
	
	            return;
	         }
	      }
	
	      if (Util.tooClose(e.getBlock().getLocation(), this.plugin) && e.getBlock().getType() != Material.ENDER_STONE && e.getBlock().getType() != Material.MELON_BLOCK && e.getBlock().getType() != Material.BREWING_STAND) {
	         e.setCancelled(true);
	      }
      }

   }

   @EventHandler
   public void onLava(PlayerBucketEmptyEvent event) {
      if (BlockLava && event.getBucket() == Material.LAVA_BUCKET) {
         event.setCancelled(true);
         event.getPlayer().sendMessage(String.valueOf(String.valueOf(String.valueOf(Translator.change("PREFIX")))) + ChatColor.YELLOW + "Lava Bucket is not allowed on this server!");
      }

   }

   @EventHandler
   public void onBreakII(BlockBreakEvent e) {
      Player player = e.getPlayer();
      Block b = e.getBlock();
      PlayerMeta meta = PlayerMeta.getMeta(player);
      BlockObject bbo = null;
      Iterator var7 = this.plugin.crafting.values().iterator();

      BlockObject bo;
      do {
         if (!var7.hasNext()) {
            if (bbo != null) {
               this.plugin.crafting.remove(bbo);
            }

            return;
         }

         bo = (BlockObject)var7.next();
      } while(!BlockObject.isBlock(b, bo) || !Util.getTeam(meta.getTeam()));

      e.setCancelled(true);
   }

   public void breakNexus(final GameTeam victim, Player breaker) {
      if (this.nukers.containsKey(breaker)) {
         breaker.sendMessage(String.valueOf(String.valueOf(String.valueOf(Translator.change("PREFIX")))) + ChatColor.RED + "Oh stop it Ron. Please stop.");
      } else {
         this.nukers.put(breaker, 1);
         final GameTeam attacker = PlayerMeta.getMeta(breaker).getTeam();
         if (victim == attacker) {
            breaker.sendMessage(String.valueOf(String.valueOf(String.valueOf(Translator.change("PREFIX")))) + ChatColor.RED + "You can't damage your own nexus");
         } else if (this.plugin.getPhase() == 1) {
            breaker.sendMessage(String.valueOf(String.valueOf(String.valueOf(Translator.change("PREFIX")))) + ChatColor.RED + "Nexuses are invincible in phase 1");
         } else {
            this.plugin.getScoreboardHandler().sb.getTeam(String.valueOf(String.valueOf(String.valueOf(victim.name()))) + "SB").setPrefix(ChatColor.RESET.toString());
            int kit = 0;
            int d = this.plugin.getPhase() == 5 ? 2 : 1;
            victim.getNexus().damage(d + 0);
            this.plugin.getStatsManager().incrementStat(StatType.NEXUS_DAMAGE, breaker, this.plugin.getPhase() == 5 ? 2 : 1);
            String msg = ChatUtil.nexusBreakMessage(breaker, attacker, victim);
            Iterator var8 = attacker.getPlayers().iterator();

            while(var8.hasNext()) {
               Player p = (Player)var8.next();
               p.sendMessage(msg);
            }
            // Update scoreboardu ˇˇˇˇˇ
            ((Score)this.plugin.getScoreboardHandler().scores.get(victim.name())).setScore(victim.getNexus().getHealth());
            Bukkit.getServer().getPluginManager().callEvent(new NexusDamageEvent(breaker, victim, victim.getNexus().getHealth()));
            Bukkit.getScheduler().runTaskLater(this.plugin, new Runnable() {
               public void run() {
                  BlockListener.this.plugin.getScoreboardHandler().sb.getTeam(String.valueOf(String.valueOf(String.valueOf(victim.name()))) + "SB").setPrefix(victim.color().toString());
               }
            }, 2L);
            Random r = new Random();
            float pitch = 0.5F + r.nextFloat() * 0.5F;
            victim.getNexus().getLocation().getWorld().playSound(victim.getNexus().getLocation(), Sound.ANVIL_LAND, 1.0F, pitch);
            Iterator var10 = victim.getPlayers().iterator();

            while(var10.hasNext()) {
               Player p2 = (Player)var10.next();
               Util.playSounds(p2);
            }

            Location nexus = victim.getNexus().getLocation().clone();
            nexus.add(0.5D, 0.0D, 0.5D);
            Util.ParticleEffects.sendToLocation(Util.ParticleEffects.LAVA_SPARK, nexus, 1.0F, 1.0F, 1.0F, 0.0F, 20);
            Util.ParticleEffects.sendToLocation(Util.ParticleEffects.LARGE_SMOKE, nexus, 1.0F, 1.0F, 1.0F, 0.0F, 20);
            PlayerMeta.addMoney(breaker, this.plugin.getConfigManager().getConfig("config.yml").getDouble("Money-nexus-hit"));
            if (victim.getNexus().getHealth() <= 0) {
               PlayerMeta.addMoney(breaker, this.plugin.getConfigManager().getConfig("config.yml").getDouble("Money-nexus-kill"));
               this.plugin.getScoreboardHandler().sb.resetScores(((Score)this.plugin.getScoreboardHandler().scores.remove(victim.name())).getPlayer());
               Bukkit.getServer().getPluginManager().callEvent(new NexusDestroyEvent(breaker, victim));
               ChatUtil.nexusDestroyed(attacker, victim, breaker);

               try {
                  this.plugin.checkWin();
               } catch (Exception var12) {
               }

               Iterator var11 = victim.getPlayers().iterator();

               Player bl1;
               while(var11.hasNext()) {
                  bl1 = (Player)var11.next();
                  this.plugin.getStatsManager().incrementStat(StatType.LOSSES, bl1);
               }

               var11 = Bukkit.getOnlinePlayers().iterator();

               while(var11.hasNext()) {
                  bl1 = (Player)var11.next();
                  bl1.getWorld().playSound(bl1.getLocation(), Sound.EXPLODE, 1.0F, 1.25F);
               }

               var11 = victim.getSpawns().iterator();

               while(var11.hasNext()) {
                  final Location spawn = (Location)var11.next();
                  Bukkit.getScheduler().runTaskLater(this.plugin, new Runnable() {
                     public void run() {
                        Util.spawnFirework(spawn, attacker.getColor(attacker), attacker.getColor(attacker));
                     }
                  }, (long)(new Random()).nextInt(20));
               }

               Util.ParticleEffects.sendToLocation(Util.ParticleEffects.LARGE_EXPLODE, nexus, 1.0F, 1.0F, 1.0F, 0.0F, 20);
               Bukkit.getScheduler().runTask(this.plugin, new Runnable() {
                  public void run() {
                     Location nexus = victim.getNexus().getLocation().clone();
                     boolean found = false;
                     int y = 0;

                     while(!found) {
                        ++y;
                        Block b = nexus.add(0.0D, 1.0D, 0.0D).getBlock();
                        if (b != null && b.getType() == Material.BEACON) {
                           b.setType(Material.AIR);
                        }

                        if (y > 10) {
                           found = true;
                        }
                     }

                  }
               });
            }

            this.plugin.getSignHandler().updateSigns(victim);
         }
      }

   }
}
