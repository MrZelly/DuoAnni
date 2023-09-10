package cz.twixout225.Annihilation.listeners;

import cz.twixout225.Annihilation.Annihilation;
import cz.twixout225.Annihilation.Util;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

public class WorldListener implements Listener {
   Annihilation plugin;
   private static final Set<EntityType> hostileEntityTypes = new HashSet<EntityType>() {
      private static final long serialVersionUID = 42L;

      {
         this.add(EntityType.SKELETON);
         this.add(EntityType.CREEPER);
         this.add(EntityType.SPIDER);
         this.add(EntityType.BAT);
         this.add(EntityType.ENDERMAN);
         this.add(EntityType.SLIME);
         this.add(EntityType.WITCH);
      }
   };

   @EventHandler
   public void onWaterFlow(BlockFromToEvent e) {
      if (Util.isEmptyColumn(e.getToBlock().getLocation())) {
         e.setCancelled(true);
      }

   }

   @EventHandler
   public void onSpawn(CreatureSpawnEvent e) {
      if (this.isHostile(e.getEntityType())) {
         if (e.getSpawnReason() == SpawnReason.CUSTOM) {
            return;
         }

         e.setCancelled(true);
      }

   }

   private boolean isHostile(EntityType entityType) {
      return hostileEntityTypes.contains(entityType);
   }
}
