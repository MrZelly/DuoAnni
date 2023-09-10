package cz.twixout225.Annihilation.chat;

import cz.twixout225.Annihilation.Annihilation;
import cz.twixout225.Annihilation.Util;
import cz.twixout225.Annihilation.VaultHooks;
import cz.twixout225.Annihilation.object.Boss;
import cz.twixout225.Annihilation.object.GameTeam;
import cz.twixout225.Annihilation.object.PlayerMeta;
import cz.twixout225.Annihilation.stats.StatType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class ChatUtil {
   private static final String DARK_AQUA;
   private static final String DARK_GRAY;
   private static final String DARK_PURPLE;
   private static final String DARK_RED;
   private static final String RESET;
   private static final String GRAY;
   private static final String RED;
   private static final String BOLD;
   private static final String GOLD = null;
   private static boolean roman;
   private final FileConfiguration config;
   private final Annihilation plugin;
   private int sstat;

   static {
      DARK_AQUA = ChatColor.DARK_AQUA.toString();
      DARK_GRAY = ChatColor.DARK_GRAY.toString();
      DARK_PURPLE = ChatColor.DARK_PURPLE.toString();
      DARK_RED = ChatColor.DARK_RED.toString();
      RESET = ChatColor.RESET.toString();
      GRAY = ChatColor.GRAY.toString();
      RED = ChatColor.RED.toString();
      BOLD = ChatColor.BOLD.toString();
      roman = false;
   }

   public ChatUtil(Annihilation pl) {
      this.plugin = pl;
      this.config = this.plugin.configManager.getConfig("config.yml");
   }

   int getStat(Player sender) {
      this.sstat = this.plugin.getStatsManager().getStat(StatType.WINS, sender);
      return this.sstat;
   }

   public static void setRoman(boolean b) {
      roman = b;
   }

   public static String allMessage(GameTeam team, Player sender, boolean dead) {
      String playerName = sender.getName();
      World w = sender.getWorld();
      String s;
      String group;
      String primaryGroup0;
      String gprefix;
      if (team == GameTeam.NONE) {
         group = DARK_GRAY + "[" + DARK_PURPLE + "Lobby" + DARK_GRAY + "] ";
         primaryGroup0 = VaultHooks.getPermissionManager().getPrimaryGroup(Bukkit.getPlayer(playerName));
         gprefix = VaultHooks.getChatManager().getGroupPrefix(w, primaryGroup0);
         if (gprefix.equalsIgnoreCase("&7&lGuest&7")) {
            gprefix = "&r";
         }

         s = String.valueOf(String.valueOf(String.valueOf(group))) + fixDefault(ChatColor.translateAlternateColorCodes('&', gprefix)) + " " + playerName + DARK_AQUA + BOLD + " > " + RESET;
      } else {
         group = String.valueOf(String.valueOf(String.valueOf(DARK_GRAY))) + "[" + team.color() + "All" + DARK_GRAY + "] ";
         primaryGroup0 = VaultHooks.getPermissionManager().getPrimaryGroup(Bukkit.getPlayer(playerName));
         gprefix = VaultHooks.getChatManager().getGroupPrefix(w, primaryGroup0);
         if (gprefix.equalsIgnoreCase("&7&lGuest&7")) {
            gprefix = "&r";
         }

         s = String.valueOf(String.valueOf(String.valueOf(group))) + fixDefault(ChatColor.translateAlternateColorCodes('&', gprefix)) + team.getChatColor(team) + " " + playerName + DARK_AQUA + BOLD + " > " + RESET;
         if (dead) {
            group = String.valueOf(String.valueOf(String.valueOf(DARK_GRAY))) + "[" + DARK_RED + "DEAD" + DARK_GRAY + "] " + group;
            String primaryGroup3 = VaultHooks.getPermissionManager().getPrimaryGroup(Bukkit.getPlayer(playerName));
            gprefix = VaultHooks.getChatManager().getGroupPrefix(w, primaryGroup3);
            if (gprefix.equalsIgnoreCase("&7&lGuest&7")) {
               gprefix = "&r";
            }

            s = String.valueOf(String.valueOf(String.valueOf(group))) + fixDefault(ChatColor.translateAlternateColorCodes('&', gprefix)) + team.getChatColor(team) + " " + playerName + DARK_AQUA + BOLD + " > " + RESET;
         }
      }

      return s;
   }

   public static String teamMessage(GameTeam team, Player sender, boolean dead) {
      String playerName = sender.getName();
      World w = sender.getWorld();
      if (team == GameTeam.NONE) {
         return allMessage(team, sender, false);
      } else {
         String group = String.valueOf(String.valueOf(String.valueOf(GRAY))) + "[" + team.color() + "Team" + GRAY + "] ";
         String primaryGroup0 = VaultHooks.getPermissionManager().getPrimaryGroup(Bukkit.getPlayer(playerName));
         String gprefix = VaultHooks.getChatManager().getGroupPrefix(w, primaryGroup0);
         if (gprefix.equalsIgnoreCase("&7&lGuest&7")) {
            gprefix = "&r";
         }

         String s = String.valueOf(String.valueOf(String.valueOf(group))) + fixDefault(ChatColor.translateAlternateColorCodes('&', gprefix)) + team.getChatColor(team) + " " + playerName + DARK_AQUA + BOLD + " > " + RESET;
         if (dead) {
            group = String.valueOf(String.valueOf(String.valueOf(DARK_GRAY))) + "[" + DARK_RED + "DEAD" + DARK_GRAY + "] " + group;
            String primaryGroup2 = VaultHooks.getPermissionManager().getPrimaryGroup(Bukkit.getPlayer(playerName));
            gprefix = VaultHooks.getChatManager().getGroupPrefix(w, primaryGroup2);
            if (gprefix.equalsIgnoreCase("&7&lGuest&7")) {
               gprefix = "&r";
            }

            s = String.valueOf(String.valueOf(String.valueOf(group))) + fixDefault(ChatColor.translateAlternateColorCodes('&', gprefix)) + team.getChatColor(team) + " " + playerName + DARK_AQUA + BOLD + " > " + RESET;
         }

         return s;
      }
   }

   private static String fixDefault(String s) {
      if (s.contains("default")) {
         s = "";
      }

      return s;
   }

   public static void broadcast(String message) {
      Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', message));
   }

   public static void nexusDestroyed(GameTeam attacker, GameTeam victim, Player p) {
      broadcast(ChatColor.GRAY + "===============[ " + victim.color().toString() + "Nexus Destroyed" + ChatColor.GRAY + " ]===============");
      broadcast(String.valueOf(String.valueOf(String.valueOf(attacker.color().toString()))) + p.getName() + GRAY + " from " + attacker.coloredName() + GRAY + " destroyed " + victim.coloredName() + " team's Nexus!");
      broadcast(ChatColor.GRAY + "===============================================");
   }

   public static String nexusBreakMessage(Player breaker, GameTeam attacker, GameTeam victim) {
      return String.valueOf(String.valueOf(String.valueOf(colorizeName(breaker, attacker)))) + GRAY + " has damaged the " + victim.coloredName() + " team's Nexus!";
   }

   private static String colorizeName(Player player, GameTeam team) {
      return team.color() + player.getName();
   }

   public static void phaseMessage(int phase) {
      broadcast(String.valueOf(String.valueOf(String.valueOf(GRAY))) + "===========[ " + DARK_AQUA + "Progress" + GRAY + " ]===========");
      broadcast(String.valueOf(String.valueOf(String.valueOf(Util.getPhaseColor(phase)))) + "Phase " + translateRoman(phase) + GRAY + " has started");
      switch(phase) {
      case 1:
         broadcast(String.valueOf(String.valueOf(String.valueOf(GRAY))) + "Each nexus is invincible until Phase " + translateRoman(2));
         break;
      case 2:
         broadcast(String.valueOf(String.valueOf(String.valueOf(GRAY))) + "Each nexus is no longer invincible");
         broadcast(String.valueOf(String.valueOf(String.valueOf(GRAY))) + "Boss Iron Golems will now spawn");
         break;
      case 3:
         broadcast(String.valueOf(String.valueOf(String.valueOf(GRAY))) + "Diamonds will now spawn in the middle");
         break;
      case 4:
         broadcast(String.valueOf(String.valueOf(String.valueOf(GRAY))) + "You can now use Brewing");
         break;
      case 5:
         broadcast(String.valueOf(String.valueOf(String.valueOf(RED))) + "Double nexus damage");
      }

      broadcast(String.valueOf(String.valueOf(String.valueOf(GRAY))) + "================================");
   }

   public static void winMessage(GameTeam winner) {
      broadcast(String.valueOf(String.valueOf(String.valueOf(GRAY))) + "================[ " + winner.color().toString() + "End Game" + GRAY + " ]================");
      broadcast(String.valueOf(String.valueOf(String.valueOf(GRAY))) + "Team " + winner.coloredName() + GRAY + " Wins Annihilation! Restarting game...");
      broadcast(String.valueOf(String.valueOf(String.valueOf(GRAY))) + "==========================================");
   }

   public static void bossDeath(Boss b, Player killer, GameTeam team) {
      broadcast(String.valueOf(String.valueOf(String.valueOf(GRAY))) + "==========[ " + DARK_AQUA + "Boss Killed" + GRAY + " ]==========");
      broadcast(String.valueOf(String.valueOf(String.valueOf(GRAY))) + b.getBossName() + GRAY + " was killed by " + colorizeName(killer, team));
      broadcast(String.valueOf(String.valueOf(String.valueOf(GRAY))) + "================================");
   }

   public static void bossRespawn(Boss b) {
      broadcast(String.valueOf(String.valueOf(String.valueOf(GRAY))) + "================[ " + DARK_AQUA + "Boss" + GRAY + " ]================");
      broadcast(String.valueOf(String.valueOf(String.valueOf(GRAY))) + b.getBossName() + GRAY + " has respawned! Go slay the beast!");
      broadcast(String.valueOf(String.valueOf(String.valueOf(GRAY))) + "=======================================");
   }

   public static String formatDeathMessage(Player victim, Player killer, String original) {
      GameTeam killerTeam = PlayerMeta.getMeta(killer).getTeam();
      String killerColor = killerTeam != null ? killerTeam.color().toString() : ChatColor.DARK_PURPLE.toString();
      String killerName = String.valueOf(String.valueOf(String.valueOf(killerColor))) + killer.getName() + ChatColor.GRAY;
      String message = ChatColor.GRAY + formatDeathMessage(victim, original);
      message = message.replace(killer.getName(), killerName);
      return message;
   }

   public static String formatDeathMessage(Player victim, String original) {
      GameTeam victimTeam = PlayerMeta.getMeta(victim).getTeam();
      String victimColor = victimTeam != null ? victimTeam.color().toString() : ChatColor.DARK_PURPLE.toString();
      String victimName = String.valueOf(String.valueOf(String.valueOf(victimColor))) + victim.getName() + ChatColor.GRAY;
      String message = ChatColor.GRAY + original;
      message = message.replace(victim.getName(), victimName);
      if (message.contains(" §8§")) {
         String[] arr = message.split(" §8§");
         message = arr[0];
      }

      return message.replace("was slain by", "was killed by");
   }

   public static String translateRoman(int number) {
      if (!roman) {
         return String.valueOf(number);
      } else {
         switch(number) {
         case 0:
            return "0";
         case 1:
            return "I";
         case 2:
            return "II";
         case 3:
            return "III";
         case 4:
            return "IV";
         case 5:
            return "V";
         case 6:
            return "VI";
         case 7:
            return "VII";
         case 8:
            return "VIII";
         case 9:
            return "IX";
         case 10:
            return "X";
         default:
            return String.valueOf(number);
         }
      }
   }

   public FileConfiguration getConfig() {
      return this.config;
   }
}
