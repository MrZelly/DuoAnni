package sk.zelly.DuoAnni.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import sk.zelly.DuoAnni.object.GameTeam;

public class NexusDamageEvent extends Event {
   private static final HandlerList handlers = new HandlerList();
   private Player p;
   private GameTeam t;
   private int h;

   public NexusDamageEvent(Player p, GameTeam t, int h) {
      this.p = p;
      this.t = t;
      this.h = h;
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

   public int getNexusDamage() {
      return this.h;
   }
}
