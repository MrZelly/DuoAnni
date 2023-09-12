package cz.twixout225.Annihilation.object;

import cz.twixout225.Annihilation.Annihilation;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class PlayerMeta {
   private static HashMap<String, PlayerMeta> metaTable = new HashMap();
   static Annihilation plugin;
   private static String username;
   private static Player playerx;
   private GameTeam team;
   private Kit kit;
   private boolean alive;
   private int cooldown;
   public boolean isTeleporting;

   public PlayerMeta() {
      this.team = GameTeam.NONE;
      this.kit = Kit.CIVILIAN;
      this.alive = false;
      this.isTeleporting = false;
   }

   public PlayerMeta(Annihilation pl) {
      plugin = pl;
   }

   public static PlayerMeta getMeta(Player player) {
      username = player.getName();
      playerx = player;
      return getMeta(player.getName());
   }

   public static PlayerMeta getMeta(String username) {
      if (!metaTable.containsKey(username)) {
         metaTable.put(username, new PlayerMeta());
      }

      return (PlayerMeta)metaTable.get(username);
   }

   public int getCooldown() {
      return this.cooldown;
   }
   
   public boolean isTeleporting() {
	   return this.isTeleporting;
   }

   public void setCooldown(int cld) {
      this.cooldown = cld;
   }

   public String getName() {
      return username;
   }

   public Player getPlayer() {
      return playerx;
   }

   public static void reset() {
      metaTable.clear();
   }

   public void addHeart(Player p) {
      if (p.getMaxHealth() <= 30.0D) {
         p.setMaxHealth(p.getMaxHealth() + 2.0D);
      }

   }

   public void setTeam(GameTeam t) {
      if (this.team != null) {
         this.team = t;
      } else {
         this.team = GameTeam.NONE;
      }

   }

   public GameTeam getTeam() {
      return this.team;
   }

   public void setKit(Kit k) {
      if (k != null) {
         this.kit = k;
      } else {
         this.kit = Kit.CIVILIAN;
      }

   }

   public Kit getKit() {
      return this.kit;
   }

   public void setAlive(boolean b) {
      this.alive = b;
   }

   public boolean isAlive() {
      return this.alive;
   }

   public static void addXp(Player p, int exp) {
      PlayerMeta m = getMeta(p);
      p.giveExp(exp);
      p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
   }

   public static void addMoney(Player p, double money) {
      if (!plugin.getEconomy().hasAccount(p.getName())) {
         plugin.getEconomy().createPlayerAccount(p.getName());
      }

      if (p.hasPermission("annihilation.vip.money")) {
         plugin.getEconomy().depositPlayer(p.getName(), money * 2.0D);
      } else {
         plugin.getEconomy().depositPlayer(p.getName(), money);
      }
   }

   public static void sendWin(final Player p) {
      Bukkit.getScheduler().runTaskLater(Annihilation.getInstance(), new Runnable() {
         private Player pl;

         public void run() {
            p.sendMessage("§8[§6Server§8] §6Congratulations your team won!");
            p.playSound(p.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
         }
      }, 30L);
   }
}
