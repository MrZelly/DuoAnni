package cz.twixout225.Annihilation.manager;

import cz.twixout225.Annihilation.Annihilation;
import cz.twixout225.Annihilation.maps.GameMap;
import cz.twixout225.Annihilation.maps.MapLoader;
import cz.twixout225.Annihilation.maps.VoidGenerator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.Configuration;

public class MapManager {
   private final ArrayList<String> maps = new ArrayList();
   private GameMap currentMap = null;
   private Location lobbySpawn;
   private MapLoader mapLoader;

   public MapManager(Annihilation plugin, MapLoader loader, Configuration config) {
      this.mapLoader = loader;
      Iterator var5 = config.getKeys(false).iterator();

      while(var5.hasNext()) {
         String s = (String)var5.next();
         if (!s.equalsIgnoreCase("lobby")) {
            this.maps.add(s);
         }
      }

      WorldCreator wc = new WorldCreator("lobby");
      wc.generator(new VoidGenerator());
      Bukkit.createWorld(wc);
      this.lobbySpawn = this.parseLocation(config.getString("lobby.spawn"));
   }

   private Location parseLocation(String in) {
      String[] params = in.split(",");
      if (params.length != 3 && params.length != 5) {
         return null;
      } else {
         double x = Double.parseDouble(params[0]);
         double y = Double.parseDouble(params[1]);
         double z = Double.parseDouble(params[2]);
         Location loc = new Location(Bukkit.getWorld("lobby"), x, y, z);
         if (params.length == 5) {
            loc.setYaw(Float.parseFloat(params[3]));
            loc.setPitch(Float.parseFloat(params[4]));
         }

         return loc;
      }
   }

   public boolean selectMap(String mapName) {
      this.currentMap = new GameMap(this.mapLoader);
      return this.currentMap.loadIntoGame(mapName);
   }

   public boolean mapSelected() {
      return this.currentMap != null;
   }

   public GameMap getCurrentMap() {
      return this.currentMap;
   }

   public Location getLobbySpawnPoint() {
      return this.lobbySpawn;
   }

   public List<String> getRandomMaps() {
      LinkedList<String> shuffledMaps = new LinkedList(this.maps);
      Collections.shuffle(shuffledMaps);
      return shuffledMaps.subList(0, shuffledMaps.size());
   }

   public void reset() {
      this.currentMap = null;
   }
}
