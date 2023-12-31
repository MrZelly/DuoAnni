package sk.zelly.DuoAnni.bar;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class Util {
   public static boolean newProtocol = false;
   public static String version;

   static {
      String name = Bukkit.getServer().getClass().getPackage().getName();
      String mcVersion = name.substring(name.lastIndexOf(46) + 1);
      version = String.valueOf(mcVersion) + ".";
   }

   public static void sendPacket(Player p, Object packet) {
      try {
         Object nmsPlayer = getHandle((Entity)p);
         Field con_field = nmsPlayer.getClass().getField("playerConnection");
         Object con = con_field.get(nmsPlayer);
         Method packet_method = getMethod(con.getClass(), "sendPacket");
         packet_method.invoke(con, packet);
      } catch (Exception var6) {
         var6.printStackTrace();
      }

   }

   public static Class<?> getCraftClass(String ClassName) {
      String className = "net.minecraft.server." + version + ClassName;
      Class c = null;

      try {
         c = Class.forName(className);
      } catch (ClassNotFoundException var4) {
         var4.printStackTrace();
      }

      return c;
   }

   public static Object getHandle(World world) {
      Object nms_entity = null;
      Method entity_getHandle = getMethod(world.getClass(), "getHandle");

      try {
         nms_entity = entity_getHandle.invoke(world);
      } catch (Exception var4) {
         var4.printStackTrace();
      }

      return nms_entity;
   }

   public static Object getHandle(Entity entity) {
      Object nms_entity = null;
      Method entity_getHandle = getMethod(entity.getClass(), "getHandle");

      try {
         nms_entity = entity_getHandle.invoke(entity);
      } catch (Exception var4) {
         var4.printStackTrace();
      }

      return nms_entity;
   }

   public static Field getField(Class<?> cl, String field_name) {
      try {
         Field field = cl.getDeclaredField(field_name);
         return field;
      } catch (Exception var3) {
         var3.printStackTrace();
         return null;
      }
   }

   public static Method getMethod(Class<?> cl, String method, Class<?>[] args) {
      Method[] methods;
      int length = (methods = cl.getMethods()).length;

      for(int i = 0; i < length; ++i) {
         Method m = methods[i];
         if (m.getName().equals(method) && ClassListEqual(args, m.getParameterTypes())) {
            return m;
         }
      }

      return null;
   }

   public static Method getMethod(Class<?> cl, String method, Integer args) {
      Method[] methods;
      int length = (methods = cl.getMethods()).length;

      for(int i = 0; i < length; ++i) {
         Method m = methods[i];
         if (m.getName().equals(method) && args.equals(new Integer(m.getParameterTypes().length))) {
            return m;
         }
      }

      return null;
   }

   public static Method getMethod(Class<?> cl, String method) {
      Method[] methods;
      int length = (methods = cl.getMethods()).length;

      for(int i = 0; i < length; ++i) {
         Method m = methods[i];
         if (m.getName().equals(method)) {
            return m;
         }
      }

      return null;
   }

   public static boolean ClassListEqual(Class<?>[] l1, Class<?>[] l2) {
      boolean equal = true;
      if (l1.length != l2.length) {
         return false;
      } else {
         for(int i = 0; i < l1.length; ++i) {
            if (l1[i] != l2[i]) {
               equal = false;
               break;
            }
         }

         return equal;
      }
   }
}
