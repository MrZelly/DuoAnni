package sk.zelly.DuoAnni.object;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class BlockObject {
   Block block;
   int x;
   int y;
   int z;
   World world;
   Location location;
   Material material;

   public BlockObject(Block b) {
      this.block = b;
      this.location = b.getLocation();
      this.world = this.location.getWorld();
      this.material = this.block.getType();
      this.x = this.location.getBlockX();
      this.y = this.location.getBlockY();
      this.z = this.location.getBlockZ();
   }

   public Location getLocation() {
      return new Location(this.world, (double)this.x, (double)this.y, (double)this.z);
   }

   public Material getType() {
      return this.material;
   }

   public static boolean isBlock(Block b, BlockObject bo) {
      Location loc = b.getLocation();
      Location lo = bo.getLocation();
      return loc.getBlockX() == lo.getBlockX() && loc.getBlockY() == lo.getBlockY() && loc.getBlockZ() == lo.getBlockZ() && loc.getWorld() == lo.getWorld() && b.getType() == bo.getType();
   }
}
