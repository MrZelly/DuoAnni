package cz.twixout225.Annihilation.listeners;

import cz.twixout225.Annihilation.Annihilation;
import cz.twixout225.Annihilation.manager.PhaseManager;
import cz.twixout225.Annihilation.object.GameTeam;
import cz.twixout225.Annihilation.object.PlayerMeta;
import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class MotdListener implements Listener {
   private Annihilation plugin;

   public MotdListener(Annihilation pl) {
      this.plugin = pl;
   }

   @EventHandler
   public void onMOTDPing(ServerListPingEvent e) {
      if (this.plugin.motd) {
         String motd = this.plugin.getConfig().getString("motd");
         String motdlobby = this.plugin.getConfig().getString("motd-lobby");
         String motdstart = this.plugin.getConfig().getString("motd-start");

         try {
            motd = motd.replaceAll("%PHASE%", String.valueOf(this.plugin.getPhase()));
            motd = motd.replaceAll("%TIME%", PhaseManager.timeString(this.plugin.getPhaseManager().getTime()));
            motd = motd.replaceAll("%PLAYERCOUNT", String.valueOf(Bukkit.getOnlinePlayers().size()));
            motd = motd.replaceAll("%MAXPLAYERS%", String.valueOf(Bukkit.getMaxPlayers()));
            motd = motd.replaceAll("%REDNEXUS%", String.valueOf(this.getNexus(GameTeam.RED)));
            motd = motd.replaceAll("%BLUENEXUS%", String.valueOf(this.getNexus(GameTeam.BLUE)));
            String[] a = motd.split(";");
            motd = a[0] + " - " + a[1];
            if (motd.contains("%MAP%")) {
               motd = motdstart.replaceAll("%MAP%", String.valueOf(this.plugin.getMapManager().getCurrentMap().getName()));
            }

            e.setMotd(motdstart);
            String[] b;
            if (this.plugin.getPhase() == 0) {
               b = motdlobby.split(";");
               motdlobby = b[0] + " - " + b[1];
               e.setMotd(ChatColor.translateAlternateColorCodes('&', motdlobby));
               return;
            }

            if (this.plugin.getPhase() < this.plugin.lastJoinPhase + 1 && this.plugin.getPhase() != 0) {
               b = motdstart.split(";");
               motdstart = b[0] + " - " + b[1];
               motdstart = motdstart.replaceAll("%PHASE%", String.valueOf(this.plugin.getPhase()));
               if (motdstart.contains("%MAP%")) {
                  motdstart = motdstart.replaceAll("%MAP%", String.valueOf(this.plugin.getMapManager().getCurrentMap().getName()));
               }

               e.setMotd(motdstart);
               return;
            }

            e.setMotd(ChatColor.translateAlternateColorCodes('&', motd));
         } catch (Exception var7) {
         }
      }

   }

   private int getNexus(GameTeam t) {
      int health = 0;
      if (t.getNexus() != null) {
         health = t.getNexus().getHealth();
      }

      return health;
   }

   private int getPlayers(GameTeam t) {
      int size = 0;
      Iterator var4 = Bukkit.getOnlinePlayers().iterator();

      while(var4.hasNext()) {
         Player motd = (Player)var4.next();
         PlayerMeta meta = PlayerMeta.getMeta(motd);
         if (meta.getTeam() == t) {
            ++size;
         }
      }

      return size;
   }
}
