package sk.zelly.DuoAnni.maps;

import java.util.Random;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.ChunkGenerator;

public class VoidGenerator extends ChunkGenerator {
   public Location getFixedSpawnLocation(World world, Random rand) {
      return new Location(world, 0.0D, (double)world.getHighestBlockYAt(0, 0), 0.0D);
   }

   public byte[] generate(World world, Random rand, int chunkX, int chunkZ) {
      byte[] chunk = new byte['耀'];
      if (chunkX == 0 && chunkZ == 0) {
         chunk[0] = (byte)Material.BEDROCK.getId();
      }

      for(int x = 0; x < 16; ++x) {
         for(int z = 0; z < 16; ++z) {
            world.setBiome(x, z, Biome.PLAINS);
         }
      }

      return chunk;
   }
}
