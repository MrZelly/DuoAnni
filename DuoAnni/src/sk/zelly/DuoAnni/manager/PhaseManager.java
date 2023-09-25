package cz.twixout225.Annihilation.manager;

import cz.twixout225.Annihilation.Annihilation;
import cz.twixout225.Annihilation.bar.ActionBar;
import cz.twixout225.Annihilation.chat.ChatUtil;
import cz.twixout225.Annihilation.object.GameTeam;
import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

public class PhaseManager {
   public long time;
   private long startTime;
   private long phaseTime;
   private int phase;
   private boolean isRunning;
   private boolean running = false;
   private final Annihilation plugin;
   private int taskID;

   public PhaseManager(Annihilation plugin, int start, int period) {
      this.plugin = plugin;
      this.startTime = (long)start;
      this.phaseTime = (long)period;
      this.phase = 0;
   }

   public void start() {
      if (!this.isRunning) {
         BukkitScheduler scheduler = this.plugin.getServer().getScheduler();
         this.taskID = scheduler.scheduleSyncRepeatingTask(this.plugin, new Runnable() {
            public void run() {
               PhaseManager.this.onSecond();
            }
         }, 20L, 20L);
         this.isRunning = true;
         this.startTimerC();
      }

      this.time = -this.startTime;
      String mtextstart = this.plugin.getConfig().getString("motd-lobby");
      String text = String.valueOf(String.valueOf(mtextstart)) + " &7|&f Time to start: " + -this.time;
      text = ChatColor.translateAlternateColorCodes('&', text);
      Iterator var4 = Bukkit.getOnlinePlayers().iterator();

      while(var4.hasNext()) {
         Player p = (Player)var4.next();
         ActionBar.sendActionBar(p, text);
      }

      this.plugin.getSignHandler().updateSigns(GameTeam.RED);
      this.plugin.getSignHandler().updateSigns(GameTeam.BLUE);
   }

   public void stop() {
      if (this.isRunning) {
         this.isRunning = false;
         Bukkit.getServer().getScheduler().cancelTask(this.taskID);
      }

   }

   public void reset() {
      this.stop();
      this.time = -this.startTime;
      this.phase = 0;
   }

   public long getTime() {
      return this.time;
   }

   public long getRemainingPhaseTime() {
      if (this.phase == 5) {
         return this.phaseTime;
      } else {
         return this.phase >= 1 ? this.time % this.phaseTime : -this.time;
      }
   }

   public int getPhase() {
      return this.phase;
   }

   public boolean isRunning() {
      return this.isRunning;
   }

   private void onSecond() {
      ++this.time;
      if (this.getRemainingPhaseTime() == 0L) {
         ++this.phase;
         this.plugin.advancePhase();
      }

      String mtextstart = this.plugin.getConfig().getString("motd-lobby");
      String text;
      float percent;
      if (this.phase == 0) {
         percent = (float)(-this.time) / (float)this.startTime;
         text = String.valueOf(String.valueOf(mtextstart)) + " &7|&f Time to start: " + -this.time;
         text = ChatColor.translateAlternateColorCodes('&', text);
      } else {
         if (this.phase == 5) {
            percent = 1.0F;
            Iterator var5 = Bukkit.getOnlinePlayers().iterator();

            while(var5.hasNext()) {
               Player p = (Player)var5.next();
               p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 999999999, 1));
            }
         } else {
            percent = (float)this.getRemainingPhaseTime() / (float)this.phaseTime;
         }

         String mtext = this.plugin.getConfig().getString("motd-start");
         String mtext2 = this.plugin.getConfig().getString("motd");
         if (this.phase > this.plugin.lastJoinPhase) {
            text = mtext2.replaceAll("%PHASE%", String.valueOf(ChatUtil.translateRoman(this.phase)));
            text = String.valueOf(text) + " &7| &a" + timeString(this.time);
            text = ChatColor.translateAlternateColorCodes('&', text);
         } else {
            text = mtext.replaceAll("%PHASE%", String.valueOf(ChatUtil.translateRoman(this.phase)));
            text = String.valueOf(text) + " &7| &a" + timeString(this.time);
            text = ChatColor.translateAlternateColorCodes('&', text);
         }

         if (this.phase > 0 && !this.running && Bukkit.getOnlinePlayers().size() < 1) {
            this.running = true;
            long restartDelay = this.plugin.configManager.getConfig("config.yml").getLong("restart-delay");
            RestartHandler rs = new RestartHandler(this.plugin, restartDelay);
            rs.start(1L, Color.GREEN);
         }

         this.plugin.getSignHandler().updateSigns(GameTeam.RED);
         this.plugin.getSignHandler().updateSigns(GameTeam.BLUE);
      }

      Iterator var11 = Bukkit.getOnlinePlayers().iterator();

      while(var11.hasNext()) {
         Player p = (Player)var11.next();
         ActionBar.sendActionBar(p, text);
      }

      this.plugin.onSecond();
   }

   public static String timeString(long time) {
      long hours = time / 3600L;
      long minutes = (time - hours * 3600L) / 60L;
      long seconds = time - hours * 3600L - minutes * 60L;
      return String.format(ChatColor.WHITE + "%02d" + ChatColor.GRAY + ":" + ChatColor.WHITE + "%02d" + ChatColor.GRAY + ":" + ChatColor.WHITE + "%02d", hours, minutes, seconds).replace("-", "");
   }

   public void startTimerC() {
      Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
         public void run() {
            int ch = PhaseManager.this.plugin.getConfigManager().getConfig("config.yml").getInt("Force-end.hours");
            int cm = PhaseManager.this.plugin.getConfigManager().getConfig("config.yml").getInt("Force-end.minutes");
            long hours = PhaseManager.this.time / 3600L;
            long minutes = (PhaseManager.this.time - hours * 3600L) / 60L;
            if (hours >= (long)ch && minutes >= (long)cm) {
               GameTeam gfw = PhaseManager.this.plugin.getForcedWinner(GameTeam.NONE, 0, 0, 0, 0);
               if (gfw == GameTeam.NONE) {
                  if (minutes > 44L) {
                     PhaseManager.this.plugin.endGame(gfw);
                     Bukkit.getScheduler().scheduleSyncDelayedTask(PhaseManager.this.plugin, new Runnable() {
                        public void run() {
                           Bukkit.getServer().shutdown();
                        }
                     }, 200L);
                  }

                  PhaseManager.this.startTimerC();
               } else {
                  PhaseManager.this.plugin.endGame(gfw);
               }
            } else {
               PhaseManager.this.startTimerC();
            }
         }
      }, 1200L);
   }
}
