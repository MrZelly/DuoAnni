package sk.zelly.DuoAnni.object;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import sk.zelly.DuoAnni.Annihilation;
import sk.zelly.DuoAnni.Translator;

public class Bloc {
   private static Annihilation plugin;
   public int x;
   public int y;
   public int z;
   public Location location;
   private final String world;
   public final String name;
   private final Material material;
   public int x2;
   public int y2;
   public int z2;
   public Location location2;
   private final String world2;
   private final Material material2;
   private Boolean online;

   public Bloc(Location l, Location l2, String name, Boolean b, Annihilation pl) {
      plugin = pl;
      this.online = b;
      this.name = name;
      this.location = l;
      this.material = l.getBlock().getType();
      this.world = l.getWorld().getName();
      this.x = l.getBlockX();
      this.y = l.getBlockY();
      this.z = l.getBlockZ();
      this.location2 = l2;
      this.material2 = l2.getBlock().getType();
      this.world2 = l2.getWorld().getName();
      this.x2 = l2.getBlockX();
      this.y2 = l2.getBlockY();
      this.z2 = l2.getBlockZ();
      Bukkit.getWorld(this.world).getBlockAt(l).setType(Material.QUARTZ_ORE);
      Bukkit.getWorld(this.world2).getBlockAt(l2).setType(Material.QUARTZ_ORE);
   }

   private int getX() {
      return this.x;
   }

   private int getY() {
      return this.y;
   }

   private int getZ() {
      return this.z;
   }

   private String getWorld() {
      return this.world;
   }

   private Material getMaterial() {
      return this.material;
   }

   private int getX2() {
      return this.x2;
   }

   private int getY2() {
      return this.y2;
   }

   private int getZ2() {
      return this.z2;
   }

   private String getWorld2() {
      return this.world2;
   }

   private Material getMaterial2() {
      return this.material2;
   }

   public static Material getMaterial(Bloc c) {
      return c.getMaterial();
   }

   public static Material getMaterial2(Bloc c) {
      return c.getMaterial2();
   }

   public static String getName(Bloc c) {
      return c.name;
   }

   public static Location getLocation(Bloc c) {
      Location loc = new Location(Bukkit.getWorld(c.getWorld()), (double)c.getX(), (double)c.getY(), (double)c.getZ());
      return loc;
   }

   public static Location getLocation2(Bloc c) {
      Location loc = new Location(Bukkit.getWorld(c.getWorld2()), (double)c.getX2(), (double)c.getY2(), (double)c.getZ2());
      return loc;
   }

   public static void delete(Bloc c) {
      Bukkit.getWorld(c.world).getBlockAt(c.location).setType(c.material);
      Bukkit.getWorld(c.world2).getBlockAt(c.location2).setType(c.material2);
      Player pl = Bukkit.getPlayer(c.name);
      if (pl.isOnline()) {
         pl.sendMessage(String.valueOf(Translator.change("PREFIX")) + Translator.change("TELEPORTER_DESTROYED"));
      }

      c.online = false;
   }

   public Location getTeleLoc(Bloc c, Location l) {
      if (this.compareLocation(c.location, l)) {
         return c.location2;
      } else {
         return this.compareLocation(c.location2, l) ? c.location : null;
      }
   }

   public boolean isOnline(Bloc c) {
      return c.online;
   }

   public static boolean isBlock(Bloc b, Location l) {
      return b.getX() == l.getBlockX() && b.getY() == l.getBlockY() && b.getZ() == l.getBlockZ() || b.getX2() == l.getBlockX() && b.getY2() == l.getBlockY() && b.getZ2() == l.getBlockZ();
   }

   public boolean compareLocation(Location loc, Location secondLoc) {
      return loc.getBlockX() == secondLoc.getBlockX() && loc.getBlockY() == secondLoc.getBlockY() && loc.getBlockZ() == secondLoc.getBlockZ() && loc.getWorld().getName() == secondLoc.getWorld().getName();
   }
}
