package sk.zelly.DuoAnni.api;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import sk.zelly.DuoAnni.object.Boss;

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
