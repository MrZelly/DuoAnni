package cz.twixout225.Annihilation.object;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class NMSUtils {
   private static final String packageName = Bukkit.getServer().getClass().getPackage().getName();
   public static final String version;
   private static Class<?> craftPlayer;
   private static Class<?> packet;
   private static Method getHandle;
   private static Method sendPacket;
   private static Field connection;

   static {
      version = packageName.substring(packageName.lastIndexOf(".") + 1);
   }

   public static Object castToCraft(Player player) {
      if (craftPlayer == null) {
         try {
            craftPlayer = Class.forName("org.bukkit.craftbukkit." + version + ".entity.CraftPlayer");
         } catch (ClassNotFoundException var2) {
            return null;
         }
      }

      return craftPlayer == null ? null : craftPlayer.cast(player);
   }

   public static Object castToNMS(Player player) {
      Object craft = castToCraft(player);
      if (craft == null) {
         return null;
      } else {
         Object ex8;
         Object exc;
         Object ex6;
         if (getHandle == null) {
            try {
               getHandle = craftPlayer.getMethod("getHandle");
            } catch (SecurityException | NoSuchMethodException var8) {
               ex8 = null;
               exc = null;
               ex6 = null;
               return null;
            }
         }

         try {
            return getHandle.invoke(castToCraft(player));
         } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var7) {
            ex8 = null;
            exc = null;
            ex6 = null;
            return null;
         }
      }
   }

   public static void sendPacket(Object inPacket, Player inPlayer) throws Exception {
      if (packet == null) {
         packet = Class.forName("net.minecraft.server." + version + ".Packet");
      }

      Object handle = castToNMS(inPlayer);
      if (handle != null) {
         if (connection == null) {
            connection = handle.getClass().getField("playerConnection");
         }

         Object con = connection.get(handle);
         if (con != null) {
            if (sendPacket == null) {
               sendPacket = con.getClass().getMethod("sendPacket", packet);
            }

            if (sendPacket != null) {
               sendPacket.invoke(con, inPacket);
            }

         }
      }
   }

   public static void setDeclaredField(Object obj, String fieldName, Object value) {
      try {
         Field f = obj.getClass().getDeclaredField(fieldName);
         f.setAccessible(true);
         f.set(obj, value);
         f.setAccessible(false);
      } catch (NoSuchFieldException var4) {
      } catch (SecurityException var5) {
      } catch (IllegalArgumentException var6) {
      } catch (IllegalAccessException var7) {
      }

   }
}
