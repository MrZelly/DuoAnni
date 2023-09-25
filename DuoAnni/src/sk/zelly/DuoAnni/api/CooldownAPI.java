package sk.zelly.DuoAnni.api;

import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import sk.zelly.DuoAnni.Annihilation;
import sk.zelly.DuoAnni.object.PlayerMeta;

public class CooldownAPI {
   public Annihilation pl;

   public CooldownAPI(Annihilation plugin) {
      this.pl = plugin;
   }

   public void startCooldownTimer() {
      Bukkit.getScheduler().runTaskTimer(this.pl, new Runnable() {
         public void run() {
            Iterator var3 = CooldownAPI.this.pl.getServer().getOnlinePlayers().iterator();

            while(var3.hasNext()) {
               Player u4 = (Player)var3.next();
               if (PlayerMeta.getMeta(u4).getCooldown() > 0) {
                  int coldown = PlayerMeta.getMeta(u4).getCooldown();
                  PlayerMeta.getMeta(u4).setCooldown(coldown - 1);
               }
            }

         }
      }, 20L, 20L);
   }
}
