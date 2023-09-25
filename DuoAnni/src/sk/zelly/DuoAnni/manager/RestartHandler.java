package sk.zelly.DuoAnni.manager;

import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import sk.zelly.DuoAnni.Annihilation;
import sk.zelly.DuoAnni.Util;
import sk.zelly.DuoAnni.bar.ActionBar;
import sk.zelly.DuoAnni.object.GameTeam;

public class RestartHandler {
   private final Annihilation plugin;
   private long time;
   private long delay;
   private int taskID;
   private int fwID;

   public RestartHandler(Annihilation plugin, long delay) {
      this.plugin = plugin;
      this.delay = delay;
   }

   public void start(long gameTime, final Color c) {
      Iterator iterator = this.plugin.getMapManager().getCurrentMap().getWorld().getEntities().iterator();

      while(true) {
         Entity entity;
         do {
            if (!iterator.hasNext()) {
               this.time = this.delay;
               final String totalTime = PhaseManager.timeString(gameTime);
               this.taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, new Runnable() {
                  public void run() {
                     if (RestartHandler.this.time <= 0L) {
                        RestartHandler.this.stop();
                     } else {
                        String message = ChatColor.GOLD + "Total time: " + ChatColor.WHITE + totalTime + " | " + ChatColor.GREEN + "Restarting in " + RestartHandler.this.time;
                        float percent = (float)RestartHandler.this.time / (float)RestartHandler.this.delay;
                        Iterator var4 = Bukkit.getOnlinePlayers().iterator();

                        while(var4.hasNext()) {
                           Player p = (Player)var4.next();
                           ActionBar.sendActionBar(p, message);
                        }

                        RestartHandler.this.time = RestartHandler.this.time - 1L;
                     }
                  }
               }, 0L, 20L);
               this.fwID = Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, new Runnable() {
                  public void run() {
                     GameTeam[] arrayOfGameTeam;
                     int i = (arrayOfGameTeam = GameTeam.values()).length;

                     for(byte b = 0; b < i; ++b) {
                        GameTeam gt = arrayOfGameTeam[b];
                        if (gt != GameTeam.NONE) {
                           Iterator var6 = gt.getSpawns().iterator();

                           while(var6.hasNext()) {
                              Location l = (Location)var6.next();
                              Util.spawnFirework(l, c, c);
                           }
                        }
                     }

                  }
               }, 0L, 40L);
               return;
            }

            entity = (Entity)iterator.next();
         } while(entity.getType() != EntityType.IRON_GOLEM && entity.getType() != EntityType.ZOMBIE && entity.getType() != EntityType.ARROW && entity.getType() != EntityType.BOAT);

         entity.remove();
      }
   }

   private void stop() {
      Iterator var2 = Bukkit.getOnlinePlayers().iterator();

      while(var2.hasNext()) {
         Player players = (Player)var2.next();
         Bukkit.dispatchCommand(players, "server lobby");
      }

      Bukkit.getScheduler().cancelTask(this.taskID);
      Bukkit.getScheduler().cancelTask(this.fwID);
      Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
         public void run() {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "wr reset now");
         }
      }, 3L);
   }
}
