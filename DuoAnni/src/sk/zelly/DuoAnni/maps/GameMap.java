package sk.zelly.DuoAnni.maps;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

public class GameMap {
   private World world;
   private MapLoader mapLoader;

   public GameMap(MapLoader mapLoader) {
      this.mapLoader = mapLoader;
   }

   public boolean loadIntoGame(String worldName) {
      this.mapLoader.loadMap(worldName);
      WorldCreator wc = new WorldCreator(worldName);
      wc.generator(new VoidGenerator());
      this.world = Bukkit.createWorld(wc);
      return true;
   }

   public String getName() {
      return this.world.getName();
   }

   public World getWorld() {
      return this.world;
   }
}
