package sk.zelly.DuoAnni;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHooks {
   public static boolean vault = false;
   private static VaultHooks inst;
   public static Permission permission;
   public static Chat chat;

   public static VaultHooks instance() {
      if (vault) {
         if (inst == null) {
            inst = new VaultHooks();
         }

         return inst;
      } else {
         return null;
      }
   }

   public static Permission getPermissionManager() {
      return permission;
   }

   public static Chat getChatManager() {
      return chat;
   }

   public boolean setupPermissions() {
      if (!vault) {
         return false;
      } else {
         RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServicesManager().getRegistration(Permission.class);
         if (permissionProvider != null) {
            permission = (Permission)permissionProvider.getProvider();
         }

         return permission != null;
      }
   }

   public boolean setupChat() {
      if (!vault) {
         return false;
      } else {
         RegisteredServiceProvider<Chat> chatProvider = Bukkit.getServicesManager().getRegistration(Chat.class);
         if (chatProvider != null) {
            chat = (Chat)chatProvider.getProvider();
         }

         return chat != null;
      }
   }

   public static String getGroup(String name) {
      if (!vault) {
         return "";
      } else {
         String prefix = getChatManager().getPlayerPrefix(Bukkit.getPlayer(name));
         String group = getPermissionManager().getPrimaryGroup(Bukkit.getPlayer(name));
         if (prefix == null || prefix.equals("")) {
            prefix = getChatManager().getGroupPrefix(Bukkit.getPlayer(name).getWorld(), group);
         }

         return ChatColor.translateAlternateColorCodes('&', prefix);
      }
   }
}
