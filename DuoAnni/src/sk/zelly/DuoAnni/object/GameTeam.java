package cz.twixout225.Annihilation.object;

import cz.twixout225.Annihilation.Annihilation;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public enum GameTeam {
   RED,
   BLUE,
   NONE;

   private final ChatColor color;
   private List<Location> spawns;
   private Nexus nexus;
   private Annihilation plugin = Annihilation.getInstance();
   //
   private int maxPlayers;

   private GameTeam() {
      if (this.name().equals("NONE")) {
         this.color = ChatColor.WHITE;
      } else {
         this.color = ChatColor.valueOf(this.name());
      }

      this.spawns = new ArrayList();
      this.maxPlayers = 5;
   }

   public String toString() {
      return this.name().substring(0, 1) + this.name().substring(1).toLowerCase();
   }

   public String coloredName() {
      return this.color().toString() + this.toString();
   }

   public ChatColor color() {
      return this.color;
   }

   public int getMaxPlayers() {
      return this.maxPlayers;
   }

   public Nexus getNexus() {
      return this != NONE ? this.nexus : null;
   }

   public void loadNexus(Location loc, int health) {
      if (this != NONE) {
         this.nexus = new Nexus(this, loc, health);
      }

   }

   public void addSpawn(Location loc) {
      if (this != NONE) {
         this.spawns.add(loc);
      }

   }

   public Location getRandomSpawn() {
      if (!this.spawns.isEmpty() && this != NONE) {
         return (Location)this.spawns.get((new Random()).nextInt(this.spawns.size()));
      } else {
         Annihilation.getInstance().log(this.spawns.size() + " - " + this.toString(), Level.INFO);
         return this.plugin.getMapManager().getLobbySpawnPoint();
      }
   }

   public List<Location> getSpawns() {
      return this.spawns;
   }

   public List<Player> getPlayers() {
      List<Player> players = new ArrayList();
      Iterator var3 = Bukkit.getOnlinePlayers().iterator();

      while(var3.hasNext()) {
         Player p = (Player)var3.next();
         if (PlayerMeta.getMeta(p).getTeam() == this && this != NONE) {
            players.add(p);
         }
      }

      return players;
   }

   public static GameTeam[] teams() {
      return new GameTeam[]{RED, BLUE};
   }

   public Color getColor(GameTeam gt) {
      if (gt == RED) {
         return Color.RED;
      } else {
         return gt == BLUE ? Color.BLUE : null;
      }
   }

   public ChatColor getChatColor(GameTeam gt) {
      if (gt == RED) {
         return ChatColor.RED;
      } else {
         return gt == BLUE ? ChatColor.BLUE : null;
      }
   }

   public static String getName(GameTeam gt) {
      return gt.toString().toLowerCase();
   }

   public static GameTeam getTeam(String s) {
      return valueOf(s.toUpperCase());
   }

   public static String getNameChar(GameTeam gt) {
      if (gt == RED) {
         return "§c";
      } else {
         return gt == BLUE ? "§9" : "§f";
      }
   }

   public static GameTeam getTeamChar(String s) {
      if (s == null) {
         return NONE;
      } else if (s.contains("§c")) {
         return RED;
      } else {
         return s.contains("§9") ? BLUE : NONE;
      }
   }

   public static boolean isNull(GameTeam t) {
      return t == null || t == NONE;
   }

   public void sendMessage(String string) {
   }
}
