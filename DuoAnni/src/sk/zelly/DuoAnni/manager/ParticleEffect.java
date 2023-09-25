package sk.zelly.DuoAnni.manager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public enum ParticleEffect {
   HUGE_EXPLOSION("HUGE_EXPLOSION", 0, "hugeexplosion", 0),
   LARGE_EXPLODE("LARGE_EXPLODE", 1, "largeexplode", 1),
   FIREWORKS_SPARK("FIREWORKS_SPARK", 2, "fireworksSpark", 2),
   BUBBLE("BUBBLE", 3, "bubble", 3),
   SUSPEND("SUSPEND", 4, "suspend", 4),
   DEPTH_SUSPEND("DEPTH_SUSPEND", 5, "depthSuspend", 5),
   TOWN_AURA("TOWN_AURA", 6, "townaura", 6),
   CRIT("CRIT", 7, "crit", 7),
   MAGIC_CRIT("MAGIC_CRIT", 8, "magicCrit", 8),
   MOB_SPELL("MOB_SPELL", 9, "mobSpell", 9),
   MOB_SPELL_AMBIENT("MOB_SPELL_AMBIENT", 10, "mobSpellAmbient", 10),
   SPELL("SPELL", 11, "spell", 11),
   INSTANT_SPELL("INSTANT_SPELL", 12, "instantSpell", 12),
   WITCH_MAGIC("WITCH_MAGIC", 13, "witchMagic", 13),
   NOTE("NOTE", 14, "note", 14),
   PORTAL("PORTAL", 15, "portal", 15),
   ENCHANTMENT_TABLE("ENCHANTMENT_TABLE", 16, "enchantmenttable", 16),
   EXPLODE("EXPLODE", 17, "explode", 17),
   FLAME("FLAME", 18, "flame", 18),
   LAVA("LAVA", 19, "lava", 19),
   FOOTSTEP("FOOTSTEP", 20, "footstep", 20),
   SPLASH("SPLASH", 21, "splash", 21),
   LARGE_SMOKE("LARGE_SMOKE", 22, "largesmoke", 22),
   CLOUD("CLOUD", 23, "cloud", 23),
   RED_DUST("RED_DUST", 24, "reddust", 24),
   SNOWBALL_POOF("SNOWBALL_POOF", 25, "snowballpoof", 25),
   DRIP_WATER("DRIP_WATER", 26, "dripWater", 26),
   DRIP_LAVA("DRIP_LAVA", 27, "dripLava", 27),
   SNOW_SHOVEL("SNOW_SHOVEL", 28, "snowshovel", 28),
   SLIME("SLIME", 29, "slime", 29),
   HEART("HEART", 30, "heart", 30),
   ANGRY_VILLAGER("ANGRY_VILLAGER", 31, "angryVillager", 31),
   HAPPY_VILLAGER("HAPPY_VILLAGER", 32, "happyVillager", 32),
   ICONCRACK("ICONCRACK", 33, "iconcrack", 33),
   TILECRACK("TILECRACK", 34, "tilecrack", 34);

   private static final Map<String, ParticleEffect> NAME_MAP = new HashMap();
   private static final Map<Integer, ParticleEffect> ID_MAP = new HashMap();
   private String name;
   private int id;

   static {
      ParticleEffect[] values;
      int length = (values = values()).length;

      for(int i = 0; i < length; ++i) {
         ParticleEffect effect = values[i];
         NAME_MAP.put(effect.name, effect);
         ID_MAP.put(effect.id, effect);
      }

   }

   private ParticleEffect(String s, int n, String name, int id) {
      this.name = name;
      this.id = id;
   }

   public String getName() {
      return this.name;
   }

   public int getId() {
      return this.id;
   }

   public static ParticleEffect fromName(String name) {
      if (name == null) {
         return null;
      } else {
         Iterator var2 = NAME_MAP.entrySet().iterator();

         while(var2.hasNext()) {
            Entry<String, ParticleEffect> e = (Entry)var2.next();
            if (((String)e.getKey()).equalsIgnoreCase(name)) {
               return (ParticleEffect)e.getValue();
            }
         }

         return null;
      }
   }

   public static ParticleEffect fromId(int id) {
      return (ParticleEffect)ID_MAP.get(id);
   }

   public static void sendToPlayer(ParticleEffect effect, Player player, Location location, float offsetX, float offsetY, float offsetZ, float speed, int count) throws Exception {
      Object packet = createPacket(effect, location, offsetX, offsetY, offsetZ, speed, count);
      sendPacket(player, packet);
   }

   public static void sendToLocation(ParticleEffect effect, Location location, float offsetX, float offsetY, float offsetZ, float speed, int count) throws Exception {
      Object packet = createPacket(effect, location, offsetX, offsetY, offsetZ, speed, count);
      Iterator var9 = Bukkit.getOnlinePlayers().iterator();

      while(var9.hasNext()) {
         Player PARTICLE1 = (Player)var9.next();
         sendPacket(PARTICLE1, packet);
      }

   }

   public static void sendCrackToPlayer(boolean icon, int id, byte data, Player player, Location location, float offsetX, float offsetY, float offsetZ, int count) throws Exception {
      Object packet = createCrackPacket(icon, id, data, location, offsetX, offsetY, offsetZ, count);
      sendPacket(player, packet);
   }

   public static void sendCrackToLocation(boolean icon, int id, byte data, Location location, float offsetX, float offsetY, float offsetZ, int count) throws Exception {
      Object packet = createCrackPacket(icon, id, data, location, offsetX, offsetY, offsetZ, count);
      Iterator var10 = Bukkit.getOnlinePlayers().iterator();

      while(var10.hasNext()) {
         Player PARTICLE2 = (Player)var10.next();
         sendPacket(PARTICLE2, packet);
      }

   }

   public static Object createPacket(ParticleEffect effect, Location location, float offsetX, float offsetY, float offsetZ, float speed, int count) throws Exception {
      if (count <= 0) {
         count = 1;
      }

      Object packet = getPacket63WorldParticles();
      setValue(packet, "a", effect.name);
      setValue(packet, "b", (float)location.getX());
      setValue(packet, "c", (float)location.getY());
      setValue(packet, "d", (float)location.getZ());
      setValue(packet, "e", offsetX);
      setValue(packet, "f", offsetY);
      setValue(packet, "g", offsetZ);
      setValue(packet, "h", speed);
      setValue(packet, "i", count);
      return packet;
   }

   public static Object createCrackPacket(boolean icon, int id, byte data, Location location, float offsetX, float offsetY, float offsetZ, int count) throws Exception {
      if (count <= 0) {
         count = 1;
      }

      Object packet = getPacket63WorldParticles();
      String modifier = "iconcrack_" + id;
      if (!icon) {
         modifier = "tilecrack_" + id + "_" + data;
      }

      setValue(packet, "a", modifier);
      setValue(packet, "b", (float)location.getX());
      setValue(packet, "c", (float)location.getY());
      setValue(packet, "d", (float)location.getZ());
      setValue(packet, "e", offsetX);
      setValue(packet, "f", offsetY);
      setValue(packet, "g", offsetZ);
      setValue(packet, "h", 0.1F);
      setValue(packet, "i", count);
      return packet;
   }

   private static void setValue(Object instance, String fieldName, Object value) throws Exception {
      Field field = instance.getClass().getDeclaredField(fieldName);
      field.setAccessible(true);
      field.set(instance, value);
   }

   private static Object getEntityPlayer(Player p) throws Exception {
      Method getHandle = p.getClass().getMethod("getHandle");
      return getHandle.invoke(p);
   }

   private static String getPackageName() {
      return "net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
   }

   private static Object getPacket63WorldParticles() throws Exception {
      Class<?> packet = Class.forName(String.valueOf(getPackageName()) + ".Packet63WorldParticles");
      return packet.getConstructors()[0].newInstance();
   }

   private static void sendPacket(Player p, Object packet) throws Exception {
      Object eplayer = getEntityPlayer(p);
      Field playerConnectionField = eplayer.getClass().getField("playerConnection");
      Object playerConnection = playerConnectionField.get(eplayer);
      Method[] methods;
      int length = (methods = playerConnection.getClass().getMethods()).length;

      for(int i = 0; i < length; ++i) {
         Method m = methods[i];
         if (m.getName().equalsIgnoreCase("sendPacket")) {
            m.invoke(playerConnection, packet);
            return;
         }
      }

   }
}
