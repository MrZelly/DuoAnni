package cz.twixout225.Annihilation.api;

import cz.twixout225.Annihilation.maps.GameMap;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameStartEvent extends Event {
   private static final HandlerList handlers = new HandlerList();
   private GameMap m;

   public GameStartEvent(GameMap m) {
      this.m = m;
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }

   public GameMap getMap() {
      return this.m;
   }
}
