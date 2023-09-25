package sk.zelly.DuoAnni;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Map.Entry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Attachable;
import org.bukkit.material.MaterialData;
import org.bukkit.util.BlockIterator;

import sk.zelly.DuoAnni.object.BlockObject;
import sk.zelly.DuoAnni.object.GameTeam;
import sk.zelly.DuoAnni.object.Kit;
import sk.zelly.DuoAnni.object.PlayerMeta;

public class Util {
   public static HashMap<Player, Boolean> firstKit = new HashMap();
   // $FF: synthetic field
   private static volatile int[] $SWITCH_TABLE$org$bukkit$block$BlockFace;

   public static Location parseLocation(World w, String in) {
      String[] params = in.split(",");
      String[] array = params;
      int length = params.length;

      for(int i = 0; i < length; ++i) {
         String s = array[i];
         s.replace("-0", "0");
      }

      if (params.length != 3 && params.length != 5) {
         return null;
      } else {
         double x = Double.parseDouble(params[0]);
         double y = Double.parseDouble(params[1]);
         double z = Double.parseDouble(params[2]);
         Location loc = new Location(w, x, y, z);
         if (params.length == 5) {
            loc.setYaw(Float.parseFloat(params[4]));
            loc.setPitch(Float.parseFloat(params[5]));
         }

         return loc;
      }
   }

   public static void sendPlayerToGame(final Player player, final Annihilation plugin) {
      final ItemStack item = new ItemStack(Material.FEATHER);
      final PlayerMeta meta = PlayerMeta.getMeta(player);
      if (meta.getTeam() != null) {
         meta.setAlive(true);
         player.teleport(meta.getTeam().getRandomSpawn());
         Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            public void run() {
               if (plugin.kitsToGive.get(player.getName()) != Kit.CIVILIAN) {
                  meta.setKit((Kit)plugin.kitsToGive.get(player.getName()));
                  meta.getKit().give(player, meta.getTeam());
                  player.getInventory().remove(item);
                  player.setCompassTarget(meta.getTeam().getNexus().getLocation());
                  player.setGameMode(GameMode.SURVIVAL);
                  player.setHealth(20.0D);
                  player.setFoodLevel(20);
                  player.setSaturation(20.0F);
                  player.setExp(0.0F);
                  player.giveExp(0);
               } else {
                  meta.setKit((Kit)plugin.kitsToGive.get(player.getName()));
                  meta.getKit().give(player, meta.getTeam());
                  player.getInventory().remove(item);
                  player.setCompassTarget(meta.getTeam().getNexus().getLocation());
                  player.setGameMode(GameMode.SURVIVAL);
                  player.setHealth(20.0D);
                  player.setFoodLevel(20);
                  player.setSaturation(20.0F);
                  player.setExp(0.0F);
                  player.giveExp(0);
               }
            }
         }, 10L);
      }

   }

   public static boolean isEmptyColumn(Location loc) {
      boolean hasBlock = false;
      Location test = loc.clone();

      for(int y = 0; y < loc.getWorld().getMaxHeight(); ++y) {
         test.setY((double)y);
         if (test.getBlock().getType() != Material.AIR) {
            hasBlock = true;
         }
      }

      return !hasBlock;
   }

   public static void showClassSelector(Player p) {
      int size = (Kit.values().length + 8) / 9 * 9;
      Inventory inv = Bukkit.createInventory(p, size, "Select Class");
      Kit[] arrayOfKit;
      int i = (arrayOfKit = Kit.values()).length;

      for(byte b = 0; b < i; ++b) {
         Kit kit = arrayOfKit[b];
         ItemStack itemStack = kit.getIcon().clone();
         ItemMeta im = itemStack.getItemMeta();
         im.setDisplayName("§6§l" + kit.getName());
         List<String> lore = im.getLore();
         lore.add(ChatColor.GRAY + "---------------");
         if (kit != Kit.CIVILIAN) {
            lore.add("§aFREE");
         } else {
            lore.add("§aFREE");
         }

         im.setLore(lore);
         itemStack.setItemMeta(im);
         inv.addItem(new ItemStack[]{itemStack});
      }

      p.openInventory(inv);
   }

   public static void spawnFirework(Location loc) {
      Random colour = new Random();
      Firework fw = (Firework)loc.getWorld().spawn(loc, Firework.class);
      FireworkMeta fwMeta = fw.getFireworkMeta();
      Type fwType = Type.BALL_LARGE;
      int c1i = colour.nextInt(17) + 1;
      int c2i = colour.nextInt(17) + 1;
      Color c1 = getFWColor(c1i);
      Color c2 = getFWColor(c2i);
      FireworkEffect effect = FireworkEffect.builder().withFade(c2).withColor(c1).with(fwType).build();
      fwMeta.addEffect(effect);
      fwMeta.setPower(1);
      fw.setFireworkMeta(fwMeta);
   }

   public static void spawnFirework(Location loc, Color c1, Color c2) {
      Firework fw = (Firework)loc.getWorld().spawn(loc, Firework.class);
      FireworkMeta fwMeta = fw.getFireworkMeta();
      Type fwType = Type.BALL_LARGE;
      FireworkEffect effect = FireworkEffect.builder().withFade(c2).withColor(c1).with(fwType).build();
      fwMeta.addEffect(effect);
      fwMeta.setPower(1);
      fw.setFireworkMeta(fwMeta);
   }

   public static Color getFWColor(int c) {
      switch(c) {
      case 1:
         return Color.TEAL;
      case 2:
      default:
         return Color.WHITE;
      case 3:
         return Color.YELLOW;
      case 4:
         return Color.AQUA;
      case 5:
         return Color.BLACK;
      case 6:
         return Color.BLUE;
      case 7:
         return Color.FUCHSIA;
      case 8:
         return Color.GRAY;
      case 9:
         return Color.GREEN;
      case 10:
         return Color.LIME;
      case 11:
         return Color.MAROON;
      case 12:
         return Color.NAVY;
      case 13:
         return Color.OLIVE;
      case 14:
         return Color.ORANGE;
      case 15:
         return Color.PURPLE;
      case 16:
         return Color.RED;
      case 17:
         return Color.SILVER;
      }
   }

   public static String getPhaseColor(int phase) {
      switch(phase) {
      case 1:
         return ChatColor.BLUE.toString();
      case 2:
         return ChatColor.GREEN.toString();
      case 3:
         return ChatColor.YELLOW.toString();
      case 4:
         return ChatColor.GOLD.toString();
      case 5:
         return ChatColor.RED.toString();
      default:
         return ChatColor.WHITE.toString();
      }
   }

   public static GameTeam whatTeamIsBiggerThan(GameTeam thisTeam) {
      int currTeamSize = thisTeam.getPlayers().size();
      if (currTeamSize != 0) {
         GameTeam[] teams;
         int length = (teams = GameTeam.teams()).length;

         for(int i = 0; i < length; ++i) {
            GameTeam t = teams[i];
            if (t != thisTeam) {
               int teamSize = t.getPlayers().size();
               if (teamSize > currTeamSize + 3) {
                  return t;
               }
            }
         }
      }

      return thisTeam;
   }

   public static boolean getTeamAllowEnter(GameTeam t) {
      int blue = GameTeam.BLUE.getPlayers().size();
      int red = GameTeam.RED.getPlayers().size();
      if (t == GameTeam.BLUE) {
         if (isBiggerThan(blue, red)) {
            return true;
         }
      } else if (t == GameTeam.RED && isBiggerThan(red, blue)) {
         return true;
      }

      return false;
   }

   public static boolean isBiggerThan(int i, int i2) {
      if (i <= 5) {
         return i >= i2;
      } else {
         return false;
      }
   }

   public static boolean tooClose(Location loc, Annihilation p) {
      double x = loc.getX();
      double y = loc.getY();
      double z = loc.getZ();
      GameTeam[] teams;
      int length = (teams = GameTeam.teams()).length;

      for(int i = 0; i < length; ++i) {
         GameTeam team = teams[i];
         Location nexusLoc = team.getNexus().getLocation();
         double nX = nexusLoc.getX();
         double nZ = nexusLoc.getZ();
         if (Math.abs(nX - x) <= (double)p.build && Math.abs(nZ - z) <= (double)p.build) {
            return true;
         }
      }

      return false;
   }

   public static void giveEffect(Player player) {
      Bukkit.getScheduler().scheduleSyncDelayedTask(Annihilation.getInstance(), new Runnable() {
         public void run() {
         }
      }, 20L);
   }

   public static boolean playerPlayed(Player p) {
      String playerName = p.getName();
      File playerdataFile = new File("plugins/FBDuoAnni/users/" + playerName + ".yml");
      return playerdataFile.exists();
   }

   public static String replaceTeamColor(String s) {
      s = s.replaceAll("(§([a-fk-or0-9]))", "");
      return s;
   }

   public static boolean getTeam(GameTeam team) {
      Iterator var2 = Annihilation.getInstance().crafting.entrySet().iterator();

      while(var2.hasNext()) {
         Entry<Player, BlockObject> bo = (Entry)var2.next();
         PlayerMeta meta = PlayerMeta.getMeta((Player)bo.getKey());
         if (meta.getTeam() == team) {
            return true;
         }
      }

      return false;
   }

   public static void playSounds(Player p) {
      for(int i = 0; i < 4; ++i) {
         p.playSound(p.getLocation(), Sound.NOTE_PLING, (float)i, (float)i);
      }

   }

   public static boolean hasSignAttached(Block block) {
      BlockFace[] array;
      int length = (array = new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP, BlockFace.SELF}).length;

      for(int i = 0; i < length; ++i) {
         BlockFace attachedOn = array[i];
         BlockState state = block.getRelative(attachedOn).getState();
         if (state instanceof Sign) {
            MaterialData m = block.getState().getData();
            BlockFace face = BlockFace.DOWN;
            if (m instanceof Attachable) {
               face = ((Attachable)m).getAttachedFace();
            }

            switch($SWITCH_TABLE$org$bukkit$block$BlockFace()[attachedOn.ordinal()]) {
            case 1:
               if (face == BlockFace.SOUTH) {
                  return true;
               }

               return false;
            case 2:
               if (face == BlockFace.WEST) {
                  return true;
               }

               return false;
            case 3:
               if (face == BlockFace.NORTH) {
                  return true;
               }

               return false;
            case 4:
               if (face == BlockFace.EAST) {
                  return true;
               }

               return false;
            case 5:
               if (face == BlockFace.DOWN) {
                  return true;
               }

               return false;
            case 19:
               return true;
            }
         }
      }

      return false;
   }

   public static boolean isShopSignAttached(Block block) {
      BlockFace[] array;
      int length = (array = new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP, BlockFace.SELF}).length;

      for(int i = 0; i < length; ++i) {
         BlockFace attachedOn = array[i];
         BlockState state = block.getRelative(attachedOn).getState();
         if (state instanceof Sign) {
            MaterialData m = block.getState().getData();
            BlockFace face = BlockFace.DOWN;
            if (m instanceof Attachable) {
               face = ((Attachable)m).getAttachedFace();
            }

            Sign s;
            String st;
            switch($SWITCH_TABLE$org$bukkit$block$BlockFace()[attachedOn.ordinal()]) {
            case 1:
               if (face == BlockFace.SOUTH) {
                  s = (Sign)state;
                  st = s.getLine(0);
                  if (st.startsWith("[Shop]")) {
                     return true;
                  }
               }
               break;
            case 2:
               if (face == BlockFace.WEST) {
                  s = (Sign)state;
                  st = s.getLine(0);
                  if (st.startsWith("[Shop]")) {
                     return true;
                  }
               }
               break;
            case 3:
               if (face == BlockFace.NORTH) {
                  s = (Sign)state;
                  st = s.getLine(0);
                  if (st.startsWith("[Shop]")) {
                     return true;
                  }
               }
               break;
            case 4:
               if (face == BlockFace.EAST) {
                  s = (Sign)state;
                  st = s.getLine(0);
                  if (st.startsWith("[Shop]")) {
                     return true;
                  }
               }
               break;
            case 5:
               if (face == BlockFace.DOWN) {
                  s = (Sign)state;
                  st = s.getLine(0);
                  if (st.startsWith("[Shop]")) {
                     return true;
                  }
               }
               break;
            case 19:
               return true;
            }
         }
      }

      return false;
   }

   public static LivingEntity getTargetEntity(Player player, Integer RANGE) {
      List<Entity> nearby = player.getNearbyEntities((double)RANGE, (double)RANGE, (double)RANGE);
      LivingEntity target = null;
      BlockIterator bit = new BlockIterator(player, RANGE);

      while(bit.hasNext()) {
         Block b = bit.next();
         Iterator var7 = nearby.iterator();

         while(var7.hasNext()) {
            Entity e = (Entity)var7.next();
            if (e instanceof LivingEntity && nearBlock(b, (LivingEntity)e)) {
               target = (LivingEntity)e;
            }
         }
      }

      return target;
   }

   public static boolean nearBlock(Block b, LivingEntity e) {
      Location bLoc = b.getLocation();
      Location eLoc = e.getLocation();
      double bx = bLoc.getX();
      double by = bLoc.getY();
      double bz = bLoc.getZ();
      double ex = eLoc.getX();
      double ey = eLoc.getY();
      double ez = eLoc.getZ();
      return Math.abs(bx - ex) < 0.5D && by - ey < e.getEyeHeight() && by - ey > -0.5D && Math.abs(bz - ez) < 0.5D;
   }

   public static void giveClassSelector(Player player) {
      ItemStack selector = new ItemStack(Material.FEATHER);
      ItemMeta itemMeta = selector.getItemMeta();
      itemMeta.setDisplayName(ChatColor.AQUA + "Right click to select class.");
      selector.setItemMeta(itemMeta);
      ItemStack red = new ItemStack(Material.INK_SACK, 1, (short)1);
      ItemMeta redmeta = red.getItemMeta();
      redmeta.setDisplayName(ChatColor.RED + "Join RED team");
      red.setItemMeta(redmeta);
      ItemStack blue = new ItemStack(Material.INK_SACK, 1, (short)4);
      ItemMeta bluemeta = blue.getItemMeta();
      bluemeta.setDisplayName(ChatColor.BLUE + "Join BLUE team");
      blue.setItemMeta(bluemeta);
      player.getInventory().setItem(3, red);
      player.getInventory().setItem(5, blue);
      player.getInventory().setItem(4, selector);
      player.updateInventory();
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$org$bukkit$block$BlockFace() {
      int[] var10000 = $SWITCH_TABLE$org$bukkit$block$BlockFace;
      if (var10000 != null) {
         return var10000;
      } else {
         int[] var0 = new int[BlockFace.values().length];

         try {
            var0[BlockFace.DOWN.ordinal()] = 6;
         } catch (NoSuchFieldError var19) {
         }

         try {
            var0[BlockFace.EAST.ordinal()] = 2;
         } catch (NoSuchFieldError var18) {
         }

         try {
            var0[BlockFace.EAST_NORTH_EAST.ordinal()] = 14;
         } catch (NoSuchFieldError var17) {
         }

         try {
            var0[BlockFace.EAST_SOUTH_EAST.ordinal()] = 15;
         } catch (NoSuchFieldError var16) {
         }

         try {
            var0[BlockFace.NORTH.ordinal()] = 1;
         } catch (NoSuchFieldError var15) {
         }

         try {
            var0[BlockFace.NORTH_EAST.ordinal()] = 7;
         } catch (NoSuchFieldError var14) {
         }

         try {
            var0[BlockFace.NORTH_NORTH_EAST.ordinal()] = 13;
         } catch (NoSuchFieldError var13) {
         }

         try {
            var0[BlockFace.NORTH_NORTH_WEST.ordinal()] = 12;
         } catch (NoSuchFieldError var12) {
         }

         try {
            var0[BlockFace.NORTH_WEST.ordinal()] = 8;
         } catch (NoSuchFieldError var11) {
         }

         try {
            var0[BlockFace.SELF.ordinal()] = 19;
         } catch (NoSuchFieldError var10) {
         }

         try {
            var0[BlockFace.SOUTH.ordinal()] = 3;
         } catch (NoSuchFieldError var9) {
         }

         try {
            var0[BlockFace.SOUTH_EAST.ordinal()] = 9;
         } catch (NoSuchFieldError var8) {
         }

         try {
            var0[BlockFace.SOUTH_SOUTH_EAST.ordinal()] = 16;
         } catch (NoSuchFieldError var7) {
         }

         try {
            var0[BlockFace.SOUTH_SOUTH_WEST.ordinal()] = 17;
         } catch (NoSuchFieldError var6) {
         }

         try {
            var0[BlockFace.SOUTH_WEST.ordinal()] = 10;
         } catch (NoSuchFieldError var5) {
         }

         try {
            var0[BlockFace.UP.ordinal()] = 5;
         } catch (NoSuchFieldError var4) {
         }

         try {
            var0[BlockFace.WEST.ordinal()] = 4;
         } catch (NoSuchFieldError var3) {
         }

         try {
            var0[BlockFace.WEST_NORTH_WEST.ordinal()] = 11;
         } catch (NoSuchFieldError var2) {
         }

         try {
            var0[BlockFace.WEST_SOUTH_WEST.ordinal()] = 18;
         } catch (NoSuchFieldError var1) {
         }

         $SWITCH_TABLE$org$bukkit$block$BlockFace = var0;
         return var0;
      }
   }

   public static enum ParticleEffects {
      HUGE_EXPLODE("HUGE_EXPLODE", 0, "hugeexplosion", 0),
      LARGE_EXPLODE("LARGE_EXPLODE", 1, "largeexplode", 1),
      FIREWORK_SPARK("FIREWORK_SPARK", 2, "fireworksSpark", 2),
      AIR_BUBBLE("AIR_BUBBLE", 3, "bubble", 3),
      SUSPEND("SUSPEND", 4, "suspend", 4),
      DEPTH_SUSPEND("DEPTH_SUSPEND", 5, "depthSuspend", 5),
      TOWN_AURA("TOWN_AURA", 6, "townaura", 6),
      CRITICAL_HIT("CRITICAL_HIT", 7, "crit", 7),
      MAGIC_CRITICAL_HIT("MAGIC_CRITICAL_HIT", 8, "magicCrit", 8),
      MOB_SPELL("MOB_SPELL", 9, "mobSpell", 9),
      MOB_SPELL_AMBIENT("MOB_SPELL_AMBIENT", 10, "mobSpellAmbient", 10),
      SPELL("SPELL", 11, "spell", 11),
      INSTANT_SPELL("INSTANT_SPELL", 12, "instantSpell", 12),
      BLUE_SPARKLE("BLUE_SPARKLE", 13, "witchMagic", 13),
      NOTE_BLOCK("NOTE_BLOCK", 14, "note", 14),
      ENDER("ENDER", 15, "portal", 15),
      ENCHANTMENT_TABLE("ENCHANTMENT_TABLE", 16, "enchantmenttable", 16),
      EXPLODE("EXPLODE", 17, "explode", 17),
      FIRE("FIRE", 18, "flame", 18),
      LAVA_SPARK("LAVA_SPARK", 19, "lava", 19),
      FOOTSTEP("FOOTSTEP", 20, "footstep", 20),
      SPLASH("SPLASH", 21, "splash", 21),
      LARGE_SMOKE("LARGE_SMOKE", 22, "largesmoke", 22),
      CLOUD("CLOUD", 23, "cloud", 23),
      REDSTONE_DUST("REDSTONE_DUST", 24, "reddust", 24),
      SNOWBALL_HIT("SNOWBALL_HIT", 25, "snowballpoof", 25),
      DRIP_WATER("DRIP_WATER", 26, "dripWater", 26),
      DRIP_LAVA("DRIP_LAVA", 27, "dripLava", 27),
      SNOW_DIG("SNOW_DIG", 28, "snowshovel", 28),
      SLIME("SLIME", 29, "slime", 29),
      HEART("HEART", 30, "heart", 30),
      ANGRY_VILLAGER("ANGRY_VILLAGER", 31, "angryVillager", 31),
      GREEN_SPARKLE("GREEN_SPARKLE", 32, "happyVillager", 32),
      ICONCRACK("ICONCRACK", 33, "iconcrack", 33),
      TILECRACK("TILECRACK", 34, "tilecrack", 34);

      private String name;
      private int id;

      private ParticleEffects(String s, int n, String name, int id) {
         this.name = name;
         this.id = id;
      }

      String getName() {
         return this.name;
      }

      int getId() {
         return this.id;
      }

      public static void sendToPlayer(Util.ParticleEffects effect, Player player, Location location, float offsetX, float offsetY, float offsetZ, float speed, int count) {
         try {
            Object packet = createPacket(effect, location, offsetX, offsetY, offsetZ, speed, count);
            sendPacket(player, packet);
         } catch (Exception var9) {
         }

      }

      public static void sendToLocation(Util.ParticleEffects effect, Location location, float offsetX, float offsetY, float offsetZ, float speed, int count) {
         try {
            Object packet = createPacket(effect, location, offsetX, offsetY, offsetZ, speed, count);
            Iterator var9 = Bukkit.getOnlinePlayers().iterator();

            while(var9.hasNext()) {
               Player u1 = (Player)var9.next();
               sendPacket(u1, packet);
            }
         } catch (Exception var10) {
         }

      }

      private static Object createPacket(Util.ParticleEffects effect, Location location, float offsetX, float offsetY, float offsetZ, float speed, int count) throws Exception {
         if (count <= 0) {
            count = 1;
         }

         Class<?> packetClass = getCraftClass("PacketPlayOutWorldParticles");
         Object packet = packetClass.getConstructor(String.class, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Integer.TYPE).newInstance(effect.name, (float)location.getX(), (float)location.getY(), (float)location.getZ(), offsetX, offsetY, offsetZ, speed, count);
         return packet;
      }

      private static void sendPacket(Player p, Object packet) throws Exception {
         Object eplayer = getHandle(p);
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

      private static Object getHandle(Entity entity) {
         Object ex6;
         try {
            Method entity_getHandle = entity.getClass().getMethod("getHandle");
            ex6 = entity_getHandle.invoke(entity);
            return ex6;
         } catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException var6) {
            ex6 = null;
            Exception ex5 = null;
            return null;
         }
      }

      private static Class<?> getCraftClass(String name) {
         String version = String.valueOf(String.valueOf(getVersion())) + ".";
         String className = "net.minecraft.server." + version + name;
         Class clazz = null;

         try {
            clazz = Class.forName(className);
         } catch (ClassNotFoundException var5) {
         }

         return clazz;
      }

      private static String getVersion() {
         return Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
      }
   }
}
