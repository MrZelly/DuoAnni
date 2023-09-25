package cz.twixout225.Annihilation.api;

import cz.twixout225.Annihilation.object.Boss;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BossSpawnEvent extends Event {
   private Boss b;
   private static final HandlerList handlers = new HandlerList();

   public BossSpawnEvent(Boss b) {
      this.b = b;
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }

   public Boss getBoss() {
      return this.b;
   }
}
