package cz.twixout225.Annihilation.bar;

import cz.twixout225.Annihilation.object.Kit;
import cz.twixout225.Annihilation.object.PlayerMeta;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class ActionBar {
   public static void sendActionBar(Player player, String message) {
      CraftPlayer p = (CraftPlayer)player;
      if (message.contains("%MAP%") || message.contains("Voting...")) {
         String[] a = message.split(";");
         message = String.valueOf(a[0]) + " " + a[2];
      }

      if (player.getItemInHand().getType() == Material.FEATHER || player.getItemInHand().getType() == Material.GHAST_TEAR || player.getItemInHand().getType() == Material.FISHING_ROD && PlayerMeta.getMeta(player).getKit() == Kit.FISHERMAN || player.getItemInHand().getType() == Material.SUGAR || player.getItemInHand().getType() == Material.BLAZE_ROD || player.getItemInHand().getType() == Material.EYE_OF_ENDER) {
         if (PlayerMeta.getMeta(player).getCooldown() > 0) {
            message = "§c§l" + PlayerMeta.getMeta(player).getCooldown();
         } else {
            message = "§a§lREADY";
         }
      }

      IChatBaseComponent cbc = ChatSerializer.a("{\"text\": \"" + message + "\"}");
      PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc, (byte)2);
      p.getHandle().playerConnection.sendPacket(ppoc);
   }
}
