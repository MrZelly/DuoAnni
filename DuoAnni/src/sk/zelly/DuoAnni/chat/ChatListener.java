package cz.twixout225.Annihilation.chat;

import cz.twixout225.Annihilation.Annihilation;
import cz.twixout225.Annihilation.object.GameTeam;
import cz.twixout225.Annihilation.object.PlayerMeta;
import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {
   private final Annihilation plugin;

   public ChatListener(Annihilation plugin) {
      this.plugin = plugin;
   }

   @EventHandler
   public void onPlayerChat(AsyncPlayerChatEvent e) {
      Player sender = e.getPlayer();
      boolean All = false;
      PlayerMeta meta = PlayerMeta.getMeta(sender);
      GameTeam team = meta.getTeam();
      boolean var10000;
      if (!meta.isAlive() && this.plugin.getPhase() > 0) {
         var10000 = true;
      } else {
         var10000 = false;
      }

      String msg = e.getMessage();
      if (e.getMessage().startsWith("!") && !e.getMessage().equalsIgnoreCase("!")) {
         msg = msg.substring(1);
         All = true;
      }

      if (!e.isCancelled()) {
         if (!All) {
            Iterator var9 = Bukkit.getOnlinePlayers().iterator();

            while(var9.hasNext()) {
               Player cl = (Player)var9.next();
               PlayerMeta metap = PlayerMeta.getMeta(cl);
               if (metap.getTeam() != team) {
                  e.getRecipients().remove(cl);
               }
            }
         }

      }
   }
}
