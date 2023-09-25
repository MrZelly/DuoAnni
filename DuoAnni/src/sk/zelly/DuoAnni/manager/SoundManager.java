package cz.twixout225.Annihilation.manager;

import cz.twixout225.Annihilation.object.GameTeam;
import cz.twixout225.Annihilation.object.PlayerMeta;
import java.util.Iterator;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundManager {
   private static Random rand = new Random();

   public static void playSound(Location loc, Sound sound, float volume, float minPitch, float maxPitch) {
      loc.getWorld().playSound(loc, sound, volume, randomPitch(minPitch, maxPitch));
   }

   public static void playSoundForPlayer(Player p, Sound sound, float volume, float minPitch, float maxPitch) {
      p.playSound(p.getLocation(), sound, volume, randomPitch(minPitch, maxPitch));
   }

   public static void playSoundForTeam(GameTeam team, Sound sound, float volume, float minPitch, float maxPitch) {
      Iterator var6 = Bukkit.getOnlinePlayers().iterator();

      while(var6.hasNext()) {
         Player sm1 = (Player)var6.next();
         if (PlayerMeta.getMeta(sm1).getTeam() == team) {
            playSoundForPlayer(sm1, sound, volume, minPitch, maxPitch);
         }
      }

   }

   private static float randomPitch(float min, float max) {
      return min + rand.nextFloat() * (max - min);
   }
}
