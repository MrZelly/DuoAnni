package sk.zelly.DuoAnni.api;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PhaseChangeEvent extends Event {
   private static final HandlerList handlers = new HandlerList();
   private int p;

   public PhaseChangeEvent(int p) {
      this.p = p;
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }

   public int getNewPhase() {
      return this.p;
   }
}
