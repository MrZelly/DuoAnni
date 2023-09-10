package cz.twixout225.Annihilation.api;

import cz.twixout225.Annihilation.object.GameTeam;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

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
