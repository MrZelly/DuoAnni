package sk.zelly.DuoAnni.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import sk.zelly.DuoAnni.object.GameTeam;

public class NexusDestroyEvent extends Event {
   private static final HandlerList handlers = new HandlerList();
   private Player p;
   private GameTeam t;

   public NexusDestroyEvent(Player p, GameTeam t) {
      this.p = p;
      this.t = t;
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }

   public Player getPlayer() {
      return this.p;
   }

   public GameTeam getTeam() {
      return this.t;
   }
}
