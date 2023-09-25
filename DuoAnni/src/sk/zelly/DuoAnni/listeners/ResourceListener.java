package sk.zelly.DuoAnni.listeners;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import sk.zelly.DuoAnni.Annihilation;
import sk.zelly.DuoAnni.object.Kit;
import sk.zelly.DuoAnni.object.PlayerMeta;

public class ResourceListener implements Listener {
   public final HashMap<Material, ResourceListener.Resource> resources = new HashMap();
   private final HashSet<Location> queue = new HashSet();
   private final Set<Location> diamonds = new HashSet();
   private Random rand = new Random();
   private final Annihilation plugin;
   // $FF: synthetic field
   private static volatile int[] $SWITCH_TABLE$org$bukkit$Material;

   public ResourceListener(Annihilation plugin) {
      this.plugin = plugin;
      this.addResource(Material.COAL_ORE, 8, 10);
      this.addResource(Material.IRON_ORE, 10, 20);
      this.addResource(Material.GOLD_ORE, 10, 20);
      this.addResource(Material.DIAMOND_ORE, 12, 30);
      this.addResource(Material.EMERALD_ORE, 18, 40);
      this.addResource(Material.REDSTONE_ORE, 10, 20);
      this.addResource(Material.GLOWING_REDSTONE_ORE, 10, 20);
      this.addResource(Material.LOG, 2, 10);
      this.addResource(Material.GRAVEL, 2, 20);
      this.addResource(Material.LAPIS_ORE, 7, 10);
      this.addResource(Material.PUMPKIN, 1, 10);
   }

   @EventHandler(
      ignoreCancelled = false
   )
   public void onResourceBreak(BlockBreakEvent e) {
      if (e.getPlayer().getWorld().getName().equalsIgnoreCase("lobby")) {
         e.setCancelled(true);
      } else if (e.getBlock().getType() != Material.SKULL) {
         if (this.resources.containsKey(e.getBlock().getType())) {
            e.setCancelled(true);
            this.breakResource(e.getPlayer(), e.getBlock());
            e.getBlock().getWorld().playEffect(e.getBlock().getLocation(), Effect.STEP_SOUND, e.getBlock().getTypeId());
         } else if (this.queue.contains(e.getBlock().getLocation())) {
            e.setCancelled(true);
         }

      } else {
         if (e.getBlock().getType() == Material.PUMPKIN) {
            e.getBlock().setType(Material.AIR);
         }

      }
   }

   @EventHandler(
      ignoreCancelled = false
   )
   public void placeResource(BlockPlaceEvent e) {
      if (this.resources.containsKey(e.getBlock().getType())) {
         e.setCancelled(true);
      }

   }

   public void loadDiamonds(Set<Location> diamondLocations) {
      Location loc;
      for(Iterator var3 = diamondLocations.iterator(); var3.hasNext(); this.diamonds.add(loc)) {
         loc = (Location)var3.next();
         if (loc.getBlock().getType() == Material.DIAMOND_ORE) {
            loc.getBlock().setType(Material.AIR);
         }
      }

   }

   public void spawnDiamonds() {
      Iterator var2 = this.diamonds.iterator();

      while(var2.hasNext()) {
         Location loc = (Location)var2.next();
         loc.getBlock().setType(Material.DIAMOND_ORE);
      }

   }

   private void breakResource(Player player, Block block) {
      Material type = block.getType();
      Kit kit = PlayerMeta.getMeta(player).getKit();
      ResourceListener.Resource resource = (ResourceListener.Resource)this.resources.get(type);
      if (type.equals(Material.GRAVEL)) {
         ItemStack[] arr$ = this.getGravelDrops();
         ItemStack[] array = arr$;
         int length = arr$.length;

         for(int i = 0; i < length; ++i) {
            ItemStack stack = array[i];
            if (stack.getAmount() > 0) {
               player.getInventory().addItem(new ItemStack[]{stack});
            }
         }
      } else if (type.equals(Material.LAPIS_ORE)) {
         ItemStack sack = new ItemStack(Material.INK_SACK.getId(), 5, (short)0, (byte)4);
         player.getInventory().addItem(new ItemStack[]{sack});
      } else if (!type.equals(Material.EMERALD_ORE) && !type.equals(Material.EMERALD_ORE)) {
         Material dropType = resource.drop;
         int qty = this.getDropQuantity(type);
         if (type.name().contains("ORE") && kit == Kit.MINER) {
            qty *= (double)this.rand.nextFloat() < 0.9D ? 2 : 1;
         }

         player.getInventory().addItem(new ItemStack[]{new ItemStack(dropType, qty)});
      }

      if (resource.xp > 0) {
         player.giveExp(resource.xp);
         player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1.0F, this.rand.nextFloat() * 0.2F + 0.9F);
      }

      this.queueRespawn(block);
   }

   private void queueRespawn(final Block block) {
      final Material type = block.getType();
      block.setType(this.getRespawnMaterial(type));
      this.queue.add(block.getLocation());
      this.plugin.getServer().getScheduler().runTaskLater(this.plugin, new Runnable() {
         public void run() {
            block.setType(type);
            ResourceListener.this.queue.remove(block.getLocation());
            block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getTypeId());
         }
      }, (long)((ResourceListener.Resource)this.resources.get(type)).delay * 20L);
   }

   private int getDropQuantity(Material type) {
      switch($SWITCH_TABLE$org$bukkit$Material()[type.ordinal()]) {
      case 74:
      case 75:
         return 4 + (this.rand.nextBoolean() ? 1 : 0);
      default:
         return 1;
      }
   }

   private Material getRespawnMaterial(Material type) {
      switch($SWITCH_TABLE$org$bukkit$Material()[type.ordinal()]) {
      case 18:
      case 87:
         return Material.AIR;
      default:
         return Material.COBBLESTONE;
      }
   }

   private ItemStack[] getGravelDrops() {
      ItemStack arrows = new ItemStack(Material.ARROW, Math.max(this.rand.nextInt(5) - 2, 0));
      ItemStack flint = new ItemStack(Material.FLINT, Math.max(this.rand.nextInt(4) - 2, 0));
      ItemStack feathers = new ItemStack(Material.FEATHER, Math.max(this.rand.nextInt(4) - 2, 0));
      ItemStack string = new ItemStack(Material.STRING, Math.max(this.rand.nextInt(5) - 3, 0));
      ItemStack bones = new ItemStack(Material.BONE, Math.max(this.rand.nextInt(4) - 2, 0));
      return new ItemStack[]{arrows, flint, feathers, string, bones};
   }

   private void addResource(Material type, int xp, int delay) {
      this.resources.put(type, new ResourceListener.Resource(this.getDropMaterial(type), xp, delay));
   }

   private Material getDropMaterial(Material type) {
      switch($SWITCH_TABLE$org$bukkit$Material()[type.ordinal()]) {
      case 14:
         return null;
      case 15:
         return Material.GOLD_INGOT;
      case 16:
         return Material.IRON_INGOT;
      case 17:
         return Material.COAL;
      case 57:
         return Material.DIAMOND;
      case 74:
      case 75:
         return Material.REDSTONE;
      case 87:
         return Material.PUMPKIN_PIE;
      case 130:
         return Material.EMERALD;
      default:
         return type;
      }
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$org$bukkit$Material() {
      int[] var10000 = $SWITCH_TABLE$org$bukkit$Material;
      if (var10000 != null) {
         return var10000;
      } else {
         int[] var0 = new int[Material.values().length];

         try {
            var0[Material.ACACIA_DOOR.ordinal()] = 197;
         } catch (NoSuchFieldError var385) {
         }

         try {
            var0[Material.ACACIA_DOOR_ITEM.ordinal()] = 372;
         } catch (NoSuchFieldError var384) {
         }

         try {
            var0[Material.ACACIA_FENCE.ordinal()] = 193;
         } catch (NoSuchFieldError var383) {
         }

         try {
            var0[Material.ACACIA_FENCE_GATE.ordinal()] = 188;
         } catch (NoSuchFieldError var382) {
         }

         try {
            var0[Material.ACACIA_STAIRS.ordinal()] = 164;
         } catch (NoSuchFieldError var381) {
         }

         try {
            var0[Material.ACTIVATOR_RAIL.ordinal()] = 158;
         } catch (NoSuchFieldError var380) {
         }

         try {
            var0[Material.AIR.ordinal()] = 1;
         } catch (NoSuchFieldError var379) {
         }

         try {
            var0[Material.ANVIL.ordinal()] = 146;
         } catch (NoSuchFieldError var378) {
         }

         try {
            var0[Material.APPLE.ordinal()] = 203;
         } catch (NoSuchFieldError var377) {
         }

         try {
            var0[Material.ARMOR_STAND.ordinal()] = 359;
         } catch (NoSuchFieldError var376) {
         }

         try {
            var0[Material.ARROW.ordinal()] = 205;
         } catch (NoSuchFieldError var375) {
         }

         try {
            var0[Material.BAKED_POTATO.ordinal()] = 336;
         } catch (NoSuchFieldError var374) {
         }

         try {
            var0[Material.BANNER.ordinal()] = 368;
         } catch (NoSuchFieldError var373) {
         }

         try {
            var0[Material.BARRIER.ordinal()] = 167;
         } catch (NoSuchFieldError var372) {
         }

         try {
            var0[Material.BEACON.ordinal()] = 139;
         } catch (NoSuchFieldError var371) {
         }

         try {
            var0[Material.BED.ordinal()] = 298;
         } catch (NoSuchFieldError var370) {
         }

         try {
            var0[Material.BEDROCK.ordinal()] = 8;
         } catch (NoSuchFieldError var369) {
         }

         try {
            var0[Material.BED_BLOCK.ordinal()] = 27;
         } catch (NoSuchFieldError var368) {
         }

         try {
            var0[Material.BIRCH_DOOR.ordinal()] = 195;
         } catch (NoSuchFieldError var367) {
         }

         try {
            var0[Material.BIRCH_DOOR_ITEM.ordinal()] = 370;
         } catch (NoSuchFieldError var366) {
         }

         try {
            var0[Material.BIRCH_FENCE.ordinal()] = 190;
         } catch (NoSuchFieldError var365) {
         }

         try {
            var0[Material.BIRCH_FENCE_GATE.ordinal()] = 185;
         } catch (NoSuchFieldError var364) {
         }

         try {
            var0[Material.BIRCH_WOOD_STAIRS.ordinal()] = 136;
         } catch (NoSuchFieldError var363) {
         }

         try {
            var0[Material.BLAZE_POWDER.ordinal()] = 320;
         } catch (NoSuchFieldError var362) {
         }

         try {
            var0[Material.BLAZE_ROD.ordinal()] = 312;
         } catch (NoSuchFieldError var361) {
         }

         try {
            var0[Material.BOAT.ordinal()] = 276;
         } catch (NoSuchFieldError var360) {
         }

         try {
            var0[Material.BONE.ordinal()] = 295;
         } catch (NoSuchFieldError var359) {
         }

         try {
            var0[Material.BOOK.ordinal()] = 283;
         } catch (NoSuchFieldError var358) {
         }

         try {
            var0[Material.BOOKSHELF.ordinal()] = 48;
         } catch (NoSuchFieldError var357) {
         }

         try {
            var0[Material.BOOK_AND_QUILL.ordinal()] = 329;
         } catch (NoSuchFieldError var356) {
         }

         try {
            var0[Material.BOW.ordinal()] = 204;
         } catch (NoSuchFieldError var355) {
         }

         try {
            var0[Material.BOWL.ordinal()] = 224;
         } catch (NoSuchFieldError var354) {
         }

         try {
            var0[Material.BREAD.ordinal()] = 240;
         } catch (NoSuchFieldError var353) {
         }

         try {
            var0[Material.BREWING_STAND.ordinal()] = 118;
         } catch (NoSuchFieldError var352) {
         }

         try {
            var0[Material.BREWING_STAND_ITEM.ordinal()] = 322;
         } catch (NoSuchFieldError var351) {
         }

         try {
            var0[Material.BRICK.ordinal()] = 46;
         } catch (NoSuchFieldError var350) {
         }

         try {
            var0[Material.BRICK_STAIRS.ordinal()] = 109;
         } catch (NoSuchFieldError var349) {
         }

         try {
            var0[Material.BROWN_MUSHROOM.ordinal()] = 40;
         } catch (NoSuchFieldError var348) {
         }

         try {
            var0[Material.BUCKET.ordinal()] = 268;
         } catch (NoSuchFieldError var347) {
         }

         try {
            var0[Material.BURNING_FURNACE.ordinal()] = 63;
         } catch (NoSuchFieldError var346) {
         }

         try {
            var0[Material.CACTUS.ordinal()] = 82;
         } catch (NoSuchFieldError var345) {
         }

         try {
            var0[Material.CAKE.ordinal()] = 297;
         } catch (NoSuchFieldError var344) {
         }

         try {
            var0[Material.CAKE_BLOCK.ordinal()] = 93;
         } catch (NoSuchFieldError var343) {
         }

         try {
            var0[Material.CARPET.ordinal()] = 172;
         } catch (NoSuchFieldError var342) {
         }

         try {
            var0[Material.CARROT.ordinal()] = 142;
         } catch (NoSuchFieldError var341) {
         }

         try {
            var0[Material.CARROT_ITEM.ordinal()] = 334;
         } catch (NoSuchFieldError var340) {
         }

         try {
            var0[Material.CARROT_STICK.ordinal()] = 341;
         } catch (NoSuchFieldError var339) {
         }

         try {
            var0[Material.CAULDRON.ordinal()] = 119;
         } catch (NoSuchFieldError var338) {
         }

         try {
            var0[Material.CAULDRON_ITEM.ordinal()] = 323;
         } catch (NoSuchFieldError var337) {
         }

         try {
            var0[Material.CHAINMAIL_BOOTS.ordinal()] = 248;
         } catch (NoSuchFieldError var336) {
         }

         try {
            var0[Material.CHAINMAIL_CHESTPLATE.ordinal()] = 246;
         } catch (NoSuchFieldError var335) {
         }

         try {
            var0[Material.CHAINMAIL_HELMET.ordinal()] = 245;
         } catch (NoSuchFieldError var334) {
         }

         try {
            var0[Material.CHAINMAIL_LEGGINGS.ordinal()] = 247;
         } catch (NoSuchFieldError var333) {
         }

         try {
            var0[Material.CHEST.ordinal()] = 55;
         } catch (NoSuchFieldError var332) {
         }

         try {
            var0[Material.CLAY.ordinal()] = 83;
         } catch (NoSuchFieldError var331) {
         }

         try {
            var0[Material.CLAY_BALL.ordinal()] = 280;
         } catch (NoSuchFieldError var330) {
         }

         try {
            var0[Material.CLAY_BRICK.ordinal()] = 279;
         } catch (NoSuchFieldError var329) {
         }

         try {
            var0[Material.COAL.ordinal()] = 206;
         } catch (NoSuchFieldError var328) {
         }

         try {
            var0[Material.COAL_BLOCK.ordinal()] = 174;
         } catch (NoSuchFieldError var327) {
         }

         try {
            var0[Material.COAL_ORE.ordinal()] = 17;
         } catch (NoSuchFieldError var326) {
         }

         try {
            var0[Material.COBBLESTONE.ordinal()] = 5;
         } catch (NoSuchFieldError var325) {
         }

         try {
            var0[Material.COBBLESTONE_STAIRS.ordinal()] = 68;
         } catch (NoSuchFieldError var324) {
         }

         try {
            var0[Material.COBBLE_WALL.ordinal()] = 140;
         } catch (NoSuchFieldError var323) {
         }

         try {
            var0[Material.COCOA.ordinal()] = 128;
         } catch (NoSuchFieldError var322) {
         }

         try {
            var0[Material.COMMAND.ordinal()] = 138;
         } catch (NoSuchFieldError var321) {
         }

         try {
            var0[Material.COMMAND_MINECART.ordinal()] = 365;
         } catch (NoSuchFieldError var320) {
         }

         try {
            var0[Material.COMPASS.ordinal()] = 288;
         } catch (NoSuchFieldError var319) {
         }

         try {
            var0[Material.COOKED_BEEF.ordinal()] = 307;
         } catch (NoSuchFieldError var318) {
         }

         try {
            var0[Material.COOKED_CHICKEN.ordinal()] = 309;
         } catch (NoSuchFieldError var317) {
         }

         try {
            var0[Material.COOKED_FISH.ordinal()] = 293;
         } catch (NoSuchFieldError var316) {
         }

         try {
            var0[Material.COOKED_MUTTON.ordinal()] = 367;
         } catch (NoSuchFieldError var315) {
         }

         try {
            var0[Material.COOKED_RABBIT.ordinal()] = 355;
         } catch (NoSuchFieldError var314) {
         }

         try {
            var0[Material.COOKIE.ordinal()] = 300;
         } catch (NoSuchFieldError var313) {
         }

         try {
            var0[Material.CROPS.ordinal()] = 60;
         } catch (NoSuchFieldError var312) {
         }

         try {
            var0[Material.DARK_OAK_DOOR.ordinal()] = 198;
         } catch (NoSuchFieldError var311) {
         }

         try {
            var0[Material.DARK_OAK_DOOR_ITEM.ordinal()] = 373;
         } catch (NoSuchFieldError var310) {
         }

         try {
            var0[Material.DARK_OAK_FENCE.ordinal()] = 192;
         } catch (NoSuchFieldError var309) {
         }

         try {
            var0[Material.DARK_OAK_FENCE_GATE.ordinal()] = 187;
         } catch (NoSuchFieldError var308) {
         }

         try {
            var0[Material.DARK_OAK_STAIRS.ordinal()] = 165;
         } catch (NoSuchFieldError var307) {
         }

         try {
            var0[Material.DAYLIGHT_DETECTOR.ordinal()] = 152;
         } catch (NoSuchFieldError var306) {
         }

         try {
            var0[Material.DAYLIGHT_DETECTOR_INVERTED.ordinal()] = 179;
         } catch (NoSuchFieldError var305) {
         }

         try {
            var0[Material.DEAD_BUSH.ordinal()] = 33;
         } catch (NoSuchFieldError var304) {
         }

         try {
            var0[Material.DETECTOR_RAIL.ordinal()] = 29;
         } catch (NoSuchFieldError var303) {
         }

         try {
            var0[Material.DIAMOND.ordinal()] = 207;
         } catch (NoSuchFieldError var302) {
         }

         try {
            var0[Material.DIAMOND_AXE.ordinal()] = 222;
         } catch (NoSuchFieldError var301) {
         }

         try {
            var0[Material.DIAMOND_BARDING.ordinal()] = 362;
         } catch (NoSuchFieldError var300) {
         }

         try {
            var0[Material.DIAMOND_BLOCK.ordinal()] = 58;
         } catch (NoSuchFieldError var299) {
         }

         try {
            var0[Material.DIAMOND_BOOTS.ordinal()] = 256;
         } catch (NoSuchFieldError var298) {
         }

         try {
            var0[Material.DIAMOND_CHESTPLATE.ordinal()] = 254;
         } catch (NoSuchFieldError var297) {
         }

         try {
            var0[Material.DIAMOND_HELMET.ordinal()] = 253;
         } catch (NoSuchFieldError var296) {
         }

         try {
            var0[Material.DIAMOND_HOE.ordinal()] = 236;
         } catch (NoSuchFieldError var295) {
         }

         try {
            var0[Material.DIAMOND_LEGGINGS.ordinal()] = 255;
         } catch (NoSuchFieldError var294) {
         }

         try {
            var0[Material.DIAMOND_ORE.ordinal()] = 57;
         } catch (NoSuchFieldError var293) {
         }

         try {
            var0[Material.DIAMOND_PICKAXE.ordinal()] = 221;
         } catch (NoSuchFieldError var292) {
         }

         try {
            var0[Material.DIAMOND_SPADE.ordinal()] = 220;
         } catch (NoSuchFieldError var291) {
         }

         try {
            var0[Material.DIAMOND_SWORD.ordinal()] = 219;
         } catch (NoSuchFieldError var290) {
         }

         try {
            var0[Material.DIODE.ordinal()] = 299;
         } catch (NoSuchFieldError var289) {
         }

         try {
            var0[Material.DIODE_BLOCK_OFF.ordinal()] = 94;
         } catch (NoSuchFieldError var288) {
         }

         try {
            var0[Material.DIODE_BLOCK_ON.ordinal()] = 95;
         } catch (NoSuchFieldError var287) {
         }

         try {
            var0[Material.DIRT.ordinal()] = 4;
         } catch (NoSuchFieldError var286) {
         }

         try {
            var0[Material.DISPENSER.ordinal()] = 24;
         } catch (NoSuchFieldError var285) {
         }

         try {
            var0[Material.DOUBLE_PLANT.ordinal()] = 176;
         } catch (NoSuchFieldError var284) {
         }

         try {
            var0[Material.DOUBLE_STEP.ordinal()] = 44;
         } catch (NoSuchFieldError var283) {
         }

         try {
            var0[Material.DOUBLE_STONE_SLAB2.ordinal()] = 182;
         } catch (NoSuchFieldError var282) {
         }

         try {
            var0[Material.DRAGON_EGG.ordinal()] = 123;
         } catch (NoSuchFieldError var281) {
         }

         try {
            var0[Material.DROPPER.ordinal()] = 159;
         } catch (NoSuchFieldError var280) {
         }

         try {
            var0[Material.EGG.ordinal()] = 287;
         } catch (NoSuchFieldError var279) {
         }

         try {
            var0[Material.EMERALD.ordinal()] = 331;
         } catch (NoSuchFieldError var278) {
         }

         try {
            var0[Material.EMERALD_BLOCK.ordinal()] = 134;
         } catch (NoSuchFieldError var277) {
         }

         try {
            var0[Material.EMERALD_ORE.ordinal()] = 130;
         } catch (NoSuchFieldError var276) {
         }

         try {
            var0[Material.EMPTY_MAP.ordinal()] = 338;
         } catch (NoSuchFieldError var275) {
         }

         try {
            var0[Material.ENCHANTED_BOOK.ordinal()] = 346;
         } catch (NoSuchFieldError var274) {
         }

         try {
            var0[Material.ENCHANTMENT_TABLE.ordinal()] = 117;
         } catch (NoSuchFieldError var273) {
         }

         try {
            var0[Material.ENDER_CHEST.ordinal()] = 131;
         } catch (NoSuchFieldError var272) {
         }

         try {
            var0[Material.ENDER_PEARL.ordinal()] = 311;
         } catch (NoSuchFieldError var271) {
         }

         try {
            var0[Material.ENDER_PORTAL.ordinal()] = 120;
         } catch (NoSuchFieldError var270) {
         }

         try {
            var0[Material.ENDER_PORTAL_FRAME.ordinal()] = 121;
         } catch (NoSuchFieldError var269) {
         }

         try {
            var0[Material.ENDER_STONE.ordinal()] = 122;
         } catch (NoSuchFieldError var268) {
         }

         try {
            var0[Material.EXPLOSIVE_MINECART.ordinal()] = 350;
         } catch (NoSuchFieldError var267) {
         }

         try {
            var0[Material.EXP_BOTTLE.ordinal()] = 327;
         } catch (NoSuchFieldError var266) {
         }

         try {
            var0[Material.EYE_OF_ENDER.ordinal()] = 324;
         } catch (NoSuchFieldError var265) {
         }

         try {
            var0[Material.FEATHER.ordinal()] = 231;
         } catch (NoSuchFieldError var264) {
         }

         try {
            var0[Material.FENCE.ordinal()] = 86;
         } catch (NoSuchFieldError var263) {
         }

         try {
            var0[Material.FENCE_GATE.ordinal()] = 108;
         } catch (NoSuchFieldError var262) {
         }

         try {
            var0[Material.FERMENTED_SPIDER_EYE.ordinal()] = 319;
         } catch (NoSuchFieldError var261) {
         }

         try {
            var0[Material.FIRE.ordinal()] = 52;
         } catch (NoSuchFieldError var260) {
         }

         try {
            var0[Material.FIREBALL.ordinal()] = 328;
         } catch (NoSuchFieldError var259) {
         }

         try {
            var0[Material.FIREWORK.ordinal()] = 344;
         } catch (NoSuchFieldError var258) {
         }

         try {
            var0[Material.FIREWORK_CHARGE.ordinal()] = 345;
         } catch (NoSuchFieldError var257) {
         }

         try {
            var0[Material.FISHING_ROD.ordinal()] = 289;
         } catch (NoSuchFieldError var256) {
         }

         try {
            var0[Material.FLINT.ordinal()] = 261;
         } catch (NoSuchFieldError var255) {
         }

         try {
            var0[Material.FLINT_AND_STEEL.ordinal()] = 202;
         } catch (NoSuchFieldError var254) {
         }

         try {
            var0[Material.FLOWER_POT.ordinal()] = 141;
         } catch (NoSuchFieldError var253) {
         }

         try {
            var0[Material.FLOWER_POT_ITEM.ordinal()] = 333;
         } catch (NoSuchFieldError var252) {
         }

         try {
            var0[Material.FURNACE.ordinal()] = 62;
         } catch (NoSuchFieldError var251) {
         }

         try {
            var0[Material.GHAST_TEAR.ordinal()] = 313;
         } catch (NoSuchFieldError var250) {
         }

         try {
            var0[Material.GLASS.ordinal()] = 21;
         } catch (NoSuchFieldError var249) {
         }

         try {
            var0[Material.GLASS_BOTTLE.ordinal()] = 317;
         } catch (NoSuchFieldError var248) {
         }

         try {
            var0[Material.GLOWING_REDSTONE_ORE.ordinal()] = 75;
         } catch (NoSuchFieldError var247) {
         }

         try {
            var0[Material.GLOWSTONE.ordinal()] = 90;
         } catch (NoSuchFieldError var246) {
         }

         try {
            var0[Material.GLOWSTONE_DUST.ordinal()] = 291;
         } catch (NoSuchFieldError var245) {
         }

         try {
            var0[Material.GOLDEN_APPLE.ordinal()] = 265;
         } catch (NoSuchFieldError var244) {
         }

         try {
            var0[Material.GOLDEN_CARROT.ordinal()] = 339;
         } catch (NoSuchFieldError var243) {
         }

         try {
            var0[Material.GOLD_AXE.ordinal()] = 229;
         } catch (NoSuchFieldError var242) {
         }

         try {
            var0[Material.GOLD_BARDING.ordinal()] = 361;
         } catch (NoSuchFieldError var241) {
         }

         try {
            var0[Material.GOLD_BLOCK.ordinal()] = 42;
         } catch (NoSuchFieldError var240) {
         }

         try {
            var0[Material.GOLD_BOOTS.ordinal()] = 260;
         } catch (NoSuchFieldError var239) {
         }

         try {
            var0[Material.GOLD_CHESTPLATE.ordinal()] = 258;
         } catch (NoSuchFieldError var238) {
         }

         try {
            var0[Material.GOLD_HELMET.ordinal()] = 257;
         } catch (NoSuchFieldError var237) {
         }

         try {
            var0[Material.GOLD_HOE.ordinal()] = 237;
         } catch (NoSuchFieldError var236) {
         }

         try {
            var0[Material.GOLD_INGOT.ordinal()] = 209;
         } catch (NoSuchFieldError var235) {
         }

         try {
            var0[Material.GOLD_LEGGINGS.ordinal()] = 259;
         } catch (NoSuchFieldError var234) {
         }

         try {
            var0[Material.GOLD_NUGGET.ordinal()] = 314;
         } catch (NoSuchFieldError var233) {
         }

         try {
            var0[Material.GOLD_ORE.ordinal()] = 15;
         } catch (NoSuchFieldError var232) {
         }

         try {
            var0[Material.GOLD_PICKAXE.ordinal()] = 228;
         } catch (NoSuchFieldError var231) {
         }

         try {
            var0[Material.GOLD_PLATE.ordinal()] = 148;
         } catch (NoSuchFieldError var230) {
         }

         try {
            var0[Material.GOLD_RECORD.ordinal()] = 374;
         } catch (NoSuchFieldError var229) {
         }

         try {
            var0[Material.GOLD_SPADE.ordinal()] = 227;
         } catch (NoSuchFieldError var228) {
         }

         try {
            var0[Material.GOLD_SWORD.ordinal()] = 226;
         } catch (NoSuchFieldError var227) {
         }

         try {
            var0[Material.GRASS.ordinal()] = 3;
         } catch (NoSuchFieldError var226) {
         }

         try {
            var0[Material.GRAVEL.ordinal()] = 14;
         } catch (NoSuchFieldError var225) {
         }

         try {
            var0[Material.GREEN_RECORD.ordinal()] = 375;
         } catch (NoSuchFieldError var224) {
         }

         try {
            var0[Material.GRILLED_PORK.ordinal()] = 263;
         } catch (NoSuchFieldError var223) {
         }

         try {
            var0[Material.HARD_CLAY.ordinal()] = 173;
         } catch (NoSuchFieldError var222) {
         }

         try {
            var0[Material.HAY_BLOCK.ordinal()] = 171;
         } catch (NoSuchFieldError var221) {
         }

         try {
            var0[Material.HOPPER.ordinal()] = 155;
         } catch (NoSuchFieldError var220) {
         }

         try {
            var0[Material.HOPPER_MINECART.ordinal()] = 351;
         } catch (NoSuchFieldError var219) {
         }

         try {
            var0[Material.HUGE_MUSHROOM_1.ordinal()] = 100;
         } catch (NoSuchFieldError var218) {
         }

         try {
            var0[Material.HUGE_MUSHROOM_2.ordinal()] = 101;
         } catch (NoSuchFieldError var217) {
         }

         try {
            var0[Material.ICE.ordinal()] = 80;
         } catch (NoSuchFieldError var216) {
         }

         try {
            var0[Material.INK_SACK.ordinal()] = 294;
         } catch (NoSuchFieldError var215) {
         }

         try {
            var0[Material.IRON_AXE.ordinal()] = 201;
         } catch (NoSuchFieldError var214) {
         }

         try {
            var0[Material.IRON_BARDING.ordinal()] = 360;
         } catch (NoSuchFieldError var213) {
         }

         try {
            var0[Material.IRON_BLOCK.ordinal()] = 43;
         } catch (NoSuchFieldError var212) {
         }

         try {
            var0[Material.IRON_BOOTS.ordinal()] = 252;
         } catch (NoSuchFieldError var211) {
         }

         try {
            var0[Material.IRON_CHESTPLATE.ordinal()] = 250;
         } catch (NoSuchFieldError var210) {
         }

         try {
            var0[Material.IRON_DOOR.ordinal()] = 273;
         } catch (NoSuchFieldError var209) {
         }

         try {
            var0[Material.IRON_DOOR_BLOCK.ordinal()] = 72;
         } catch (NoSuchFieldError var208) {
         }

         try {
            var0[Material.IRON_FENCE.ordinal()] = 102;
         } catch (NoSuchFieldError var207) {
         }

         try {
            var0[Material.IRON_HELMET.ordinal()] = 249;
         } catch (NoSuchFieldError var206) {
         }

         try {
            var0[Material.IRON_HOE.ordinal()] = 235;
         } catch (NoSuchFieldError var205) {
         }

         try {
            var0[Material.IRON_INGOT.ordinal()] = 208;
         } catch (NoSuchFieldError var204) {
         }

         try {
            var0[Material.IRON_LEGGINGS.ordinal()] = 251;
         } catch (NoSuchFieldError var203) {
         }

         try {
            var0[Material.IRON_ORE.ordinal()] = 16;
         } catch (NoSuchFieldError var202) {
         }

         try {
            var0[Material.IRON_PICKAXE.ordinal()] = 200;
         } catch (NoSuchFieldError var201) {
         }

         try {
            var0[Material.IRON_PLATE.ordinal()] = 149;
         } catch (NoSuchFieldError var200) {
         }

         try {
            var0[Material.IRON_SPADE.ordinal()] = 199;
         } catch (NoSuchFieldError var199) {
         }

         try {
            var0[Material.IRON_SWORD.ordinal()] = 210;
         } catch (NoSuchFieldError var198) {
         }

         try {
            var0[Material.IRON_TRAPDOOR.ordinal()] = 168;
         } catch (NoSuchFieldError var197) {
         }

         try {
            var0[Material.ITEM_FRAME.ordinal()] = 332;
         } catch (NoSuchFieldError var196) {
         }

         try {
            var0[Material.JACK_O_LANTERN.ordinal()] = 92;
         } catch (NoSuchFieldError var195) {
         }

         try {
            var0[Material.JUKEBOX.ordinal()] = 85;
         } catch (NoSuchFieldError var194) {
         }

         try {
            var0[Material.JUNGLE_DOOR.ordinal()] = 196;
         } catch (NoSuchFieldError var193) {
         }

         try {
            var0[Material.JUNGLE_DOOR_ITEM.ordinal()] = 371;
         } catch (NoSuchFieldError var192) {
         }

         try {
            var0[Material.JUNGLE_FENCE.ordinal()] = 191;
         } catch (NoSuchFieldError var191) {
         }

         try {
            var0[Material.JUNGLE_FENCE_GATE.ordinal()] = 186;
         } catch (NoSuchFieldError var190) {
         }

         try {
            var0[Material.JUNGLE_WOOD_STAIRS.ordinal()] = 137;
         } catch (NoSuchFieldError var189) {
         }

         try {
            var0[Material.LADDER.ordinal()] = 66;
         } catch (NoSuchFieldError var188) {
         }

         try {
            var0[Material.LAPIS_BLOCK.ordinal()] = 23;
         } catch (NoSuchFieldError var187) {
         }

         try {
            var0[Material.LAPIS_ORE.ordinal()] = 22;
         } catch (NoSuchFieldError var186) {
         }

         try {
            var0[Material.LAVA.ordinal()] = 11;
         } catch (NoSuchFieldError var185) {
         }

         try {
            var0[Material.LAVA_BUCKET.ordinal()] = 270;
         } catch (NoSuchFieldError var184) {
         }

         try {
            var0[Material.LEASH.ordinal()] = 363;
         } catch (NoSuchFieldError var183) {
         }

         try {
            var0[Material.LEATHER.ordinal()] = 277;
         } catch (NoSuchFieldError var182) {
         }

         try {
            var0[Material.LEATHER_BOOTS.ordinal()] = 244;
         } catch (NoSuchFieldError var181) {
         }

         try {
            var0[Material.LEATHER_CHESTPLATE.ordinal()] = 242;
         } catch (NoSuchFieldError var180) {
         }

         try {
            var0[Material.LEATHER_HELMET.ordinal()] = 241;
         } catch (NoSuchFieldError var179) {
         }

         try {
            var0[Material.LEATHER_LEGGINGS.ordinal()] = 243;
         } catch (NoSuchFieldError var178) {
         }

         try {
            var0[Material.LEAVES.ordinal()] = 19;
         } catch (NoSuchFieldError var177) {
         }

         try {
            var0[Material.LEAVES_2.ordinal()] = 162;
         } catch (NoSuchFieldError var176) {
         }

         try {
            var0[Material.LEVER.ordinal()] = 70;
         } catch (NoSuchFieldError var175) {
         }

         try {
            var0[Material.LOG.ordinal()] = 18;
         } catch (NoSuchFieldError var174) {
         }

         try {
            var0[Material.LOG_2.ordinal()] = 163;
         } catch (NoSuchFieldError var173) {
         }

         try {
            var0[Material.LONG_GRASS.ordinal()] = 32;
         } catch (NoSuchFieldError var172) {
         }

         try {
            var0[Material.MAGMA_CREAM.ordinal()] = 321;
         } catch (NoSuchFieldError var171) {
         }

         try {
            var0[Material.MAP.ordinal()] = 301;
         } catch (NoSuchFieldError var170) {
         }

         try {
            var0[Material.MELON.ordinal()] = 303;
         } catch (NoSuchFieldError var169) {
         }

         try {
            var0[Material.MELON_BLOCK.ordinal()] = 104;
         } catch (NoSuchFieldError var168) {
         }

         try {
            var0[Material.MELON_SEEDS.ordinal()] = 305;
         } catch (NoSuchFieldError var167) {
         }

         try {
            var0[Material.MELON_STEM.ordinal()] = 106;
         } catch (NoSuchFieldError var166) {
         }

         try {
            var0[Material.MILK_BUCKET.ordinal()] = 278;
         } catch (NoSuchFieldError var165) {
         }

         try {
            var0[Material.MINECART.ordinal()] = 271;
         } catch (NoSuchFieldError var164) {
         }

         try {
            var0[Material.MOB_SPAWNER.ordinal()] = 53;
         } catch (NoSuchFieldError var163) {
         }

         try {
            var0[Material.MONSTER_EGG.ordinal()] = 326;
         } catch (NoSuchFieldError var162) {
         }

         try {
            var0[Material.MONSTER_EGGS.ordinal()] = 98;
         } catch (NoSuchFieldError var161) {
         }

         try {
            var0[Material.MOSSY_COBBLESTONE.ordinal()] = 49;
         } catch (NoSuchFieldError var160) {
         }

         try {
            var0[Material.MUSHROOM_SOUP.ordinal()] = 225;
         } catch (NoSuchFieldError var159) {
         }

         try {
            var0[Material.MUTTON.ordinal()] = 366;
         } catch (NoSuchFieldError var158) {
         }

         try {
            var0[Material.MYCEL.ordinal()] = 111;
         } catch (NoSuchFieldError var157) {
         }

         try {
            var0[Material.NAME_TAG.ordinal()] = 364;
         } catch (NoSuchFieldError var156) {
         }

         try {
            var0[Material.NETHERRACK.ordinal()] = 88;
         } catch (NoSuchFieldError var155) {
         }

         try {
            var0[Material.NETHER_BRICK.ordinal()] = 113;
         } catch (NoSuchFieldError var154) {
         }

         try {
            var0[Material.NETHER_BRICK_ITEM.ordinal()] = 348;
         } catch (NoSuchFieldError var153) {
         }

         try {
            var0[Material.NETHER_BRICK_STAIRS.ordinal()] = 115;
         } catch (NoSuchFieldError var152) {
         }

         try {
            var0[Material.NETHER_FENCE.ordinal()] = 114;
         } catch (NoSuchFieldError var151) {
         }

         try {
            var0[Material.NETHER_STALK.ordinal()] = 315;
         } catch (NoSuchFieldError var150) {
         }

         try {
            var0[Material.NETHER_STAR.ordinal()] = 342;
         } catch (NoSuchFieldError var149) {
         }

         try {
            var0[Material.NETHER_WARTS.ordinal()] = 116;
         } catch (NoSuchFieldError var148) {
         }

         try {
            var0[Material.NOTE_BLOCK.ordinal()] = 26;
         } catch (NoSuchFieldError var147) {
         }

         try {
            var0[Material.OBSIDIAN.ordinal()] = 50;
         } catch (NoSuchFieldError var146) {
         }

         try {
            var0[Material.PACKED_ICE.ordinal()] = 175;
         } catch (NoSuchFieldError var145) {
         }

         try {
            var0[Material.PAINTING.ordinal()] = 264;
         } catch (NoSuchFieldError var144) {
         }

         try {
            var0[Material.PAPER.ordinal()] = 282;
         } catch (NoSuchFieldError var143) {
         }

         try {
            var0[Material.PISTON_BASE.ordinal()] = 34;
         } catch (NoSuchFieldError var142) {
         }

         try {
            var0[Material.PISTON_EXTENSION.ordinal()] = 35;
         } catch (NoSuchFieldError var141) {
         }

         try {
            var0[Material.PISTON_MOVING_PIECE.ordinal()] = 37;
         } catch (NoSuchFieldError var140) {
         }

         try {
            var0[Material.PISTON_STICKY_BASE.ordinal()] = 30;
         } catch (NoSuchFieldError var139) {
         }

         try {
            var0[Material.POISONOUS_POTATO.ordinal()] = 337;
         } catch (NoSuchFieldError var138) {
         }

         try {
            var0[Material.PORK.ordinal()] = 262;
         } catch (NoSuchFieldError var137) {
         }

         try {
            var0[Material.PORTAL.ordinal()] = 91;
         } catch (NoSuchFieldError var136) {
         }

         try {
            var0[Material.POTATO.ordinal()] = 143;
         } catch (NoSuchFieldError var135) {
         }

         try {
            var0[Material.POTATO_ITEM.ordinal()] = 335;
         } catch (NoSuchFieldError var134) {
         }

         try {
            var0[Material.POTION.ordinal()] = 316;
         } catch (NoSuchFieldError var133) {
         }

         try {
            var0[Material.POWERED_MINECART.ordinal()] = 286;
         } catch (NoSuchFieldError var132) {
         }

         try {
            var0[Material.POWERED_RAIL.ordinal()] = 28;
         } catch (NoSuchFieldError var131) {
         }

         try {
            var0[Material.PRISMARINE.ordinal()] = 169;
         } catch (NoSuchFieldError var130) {
         }

         try {
            var0[Material.PRISMARINE_CRYSTALS.ordinal()] = 353;
         } catch (NoSuchFieldError var129) {
         }

         try {
            var0[Material.PRISMARINE_SHARD.ordinal()] = 352;
         } catch (NoSuchFieldError var128) {
         }

         try {
            var0[Material.PUMPKIN.ordinal()] = 87;
         } catch (NoSuchFieldError var127) {
         }

         try {
            var0[Material.PUMPKIN_PIE.ordinal()] = 343;
         } catch (NoSuchFieldError var126) {
         }

         try {
            var0[Material.PUMPKIN_SEEDS.ordinal()] = 304;
         } catch (NoSuchFieldError var125) {
         }

         try {
            var0[Material.PUMPKIN_STEM.ordinal()] = 105;
         } catch (NoSuchFieldError var124) {
         }

         try {
            var0[Material.QUARTZ.ordinal()] = 349;
         } catch (NoSuchFieldError var123) {
         }

         try {
            var0[Material.QUARTZ_BLOCK.ordinal()] = 156;
         } catch (NoSuchFieldError var122) {
         }

         try {
            var0[Material.QUARTZ_ORE.ordinal()] = 154;
         } catch (NoSuchFieldError var121) {
         }

         try {
            var0[Material.QUARTZ_STAIRS.ordinal()] = 157;
         } catch (NoSuchFieldError var120) {
         }

         try {
            var0[Material.RABBIT.ordinal()] = 354;
         } catch (NoSuchFieldError var119) {
         }

         try {
            var0[Material.RABBIT_FOOT.ordinal()] = 357;
         } catch (NoSuchFieldError var118) {
         }

         try {
            var0[Material.RABBIT_HIDE.ordinal()] = 358;
         } catch (NoSuchFieldError var117) {
         }

         try {
            var0[Material.RABBIT_STEW.ordinal()] = 356;
         } catch (NoSuchFieldError var116) {
         }

         try {
            var0[Material.RAILS.ordinal()] = 67;
         } catch (NoSuchFieldError var115) {
         }

         try {
            var0[Material.RAW_BEEF.ordinal()] = 306;
         } catch (NoSuchFieldError var114) {
         }

         try {
            var0[Material.RAW_CHICKEN.ordinal()] = 308;
         } catch (NoSuchFieldError var113) {
         }

         try {
            var0[Material.RAW_FISH.ordinal()] = 292;
         } catch (NoSuchFieldError var112) {
         }

         try {
            var0[Material.RECORD_10.ordinal()] = 383;
         } catch (NoSuchFieldError var111) {
         }

         try {
            var0[Material.RECORD_11.ordinal()] = 384;
         } catch (NoSuchFieldError var110) {
         }

         try {
            var0[Material.RECORD_12.ordinal()] = 385;
         } catch (NoSuchFieldError var109) {
         }

         try {
            var0[Material.RECORD_3.ordinal()] = 376;
         } catch (NoSuchFieldError var108) {
         }

         try {
            var0[Material.RECORD_4.ordinal()] = 377;
         } catch (NoSuchFieldError var107) {
         }

         try {
            var0[Material.RECORD_5.ordinal()] = 378;
         } catch (NoSuchFieldError var106) {
         }

         try {
            var0[Material.RECORD_6.ordinal()] = 379;
         } catch (NoSuchFieldError var105) {
         }

         try {
            var0[Material.RECORD_7.ordinal()] = 380;
         } catch (NoSuchFieldError var104) {
         }

         try {
            var0[Material.RECORD_8.ordinal()] = 381;
         } catch (NoSuchFieldError var103) {
         }

         try {
            var0[Material.RECORD_9.ordinal()] = 382;
         } catch (NoSuchFieldError var102) {
         }

         try {
            var0[Material.REDSTONE.ordinal()] = 274;
         } catch (NoSuchFieldError var101) {
         }

         try {
            var0[Material.REDSTONE_BLOCK.ordinal()] = 153;
         } catch (NoSuchFieldError var100) {
         }

         try {
            var0[Material.REDSTONE_COMPARATOR.ordinal()] = 347;
         } catch (NoSuchFieldError var99) {
         }

         try {
            var0[Material.REDSTONE_COMPARATOR_OFF.ordinal()] = 150;
         } catch (NoSuchFieldError var98) {
         }

         try {
            var0[Material.REDSTONE_COMPARATOR_ON.ordinal()] = 151;
         } catch (NoSuchFieldError var97) {
         }

         try {
            var0[Material.REDSTONE_LAMP_OFF.ordinal()] = 124;
         } catch (NoSuchFieldError var96) {
         }

         try {
            var0[Material.REDSTONE_LAMP_ON.ordinal()] = 125;
         } catch (NoSuchFieldError var95) {
         }

         try {
            var0[Material.REDSTONE_ORE.ordinal()] = 74;
         } catch (NoSuchFieldError var94) {
         }

         try {
            var0[Material.REDSTONE_TORCH_OFF.ordinal()] = 76;
         } catch (NoSuchFieldError var93) {
         }

         try {
            var0[Material.REDSTONE_TORCH_ON.ordinal()] = 77;
         } catch (NoSuchFieldError var92) {
         }

         try {
            var0[Material.REDSTONE_WIRE.ordinal()] = 56;
         } catch (NoSuchFieldError var91) {
         }

         try {
            var0[Material.RED_MUSHROOM.ordinal()] = 41;
         } catch (NoSuchFieldError var90) {
         }

         try {
            var0[Material.RED_ROSE.ordinal()] = 39;
         } catch (NoSuchFieldError var89) {
         }

         try {
            var0[Material.RED_SANDSTONE.ordinal()] = 180;
         } catch (NoSuchFieldError var88) {
         }

         try {
            var0[Material.RED_SANDSTONE_STAIRS.ordinal()] = 181;
         } catch (NoSuchFieldError var87) {
         }

         try {
            var0[Material.ROTTEN_FLESH.ordinal()] = 310;
         } catch (NoSuchFieldError var86) {
         }

         try {
            var0[Material.SADDLE.ordinal()] = 272;
         } catch (NoSuchFieldError var85) {
         }

         try {
            var0[Material.SAND.ordinal()] = 13;
         } catch (NoSuchFieldError var84) {
         }

         try {
            var0[Material.SANDSTONE.ordinal()] = 25;
         } catch (NoSuchFieldError var83) {
         }

         try {
            var0[Material.SANDSTONE_STAIRS.ordinal()] = 129;
         } catch (NoSuchFieldError var82) {
         }

         try {
            var0[Material.SAPLING.ordinal()] = 7;
         } catch (NoSuchFieldError var81) {
         }

         try {
            var0[Material.SEA_LANTERN.ordinal()] = 170;
         } catch (NoSuchFieldError var80) {
         }

         try {
            var0[Material.SEEDS.ordinal()] = 238;
         } catch (NoSuchFieldError var79) {
         }

         try {
            var0[Material.SHEARS.ordinal()] = 302;
         } catch (NoSuchFieldError var78) {
         }

         try {
            var0[Material.SIGN.ordinal()] = 266;
         } catch (NoSuchFieldError var77) {
         }

         try {
            var0[Material.SIGN_POST.ordinal()] = 64;
         } catch (NoSuchFieldError var76) {
         }

         try {
            var0[Material.SKULL.ordinal()] = 145;
         } catch (NoSuchFieldError var75) {
         }

         try {
            var0[Material.SKULL_ITEM.ordinal()] = 340;
         } catch (NoSuchFieldError var74) {
         }

         try {
            var0[Material.SLIME_BALL.ordinal()] = 284;
         } catch (NoSuchFieldError var73) {
         }

         try {
            var0[Material.SLIME_BLOCK.ordinal()] = 166;
         } catch (NoSuchFieldError var72) {
         }

         try {
            var0[Material.SMOOTH_BRICK.ordinal()] = 99;
         } catch (NoSuchFieldError var71) {
         }

         try {
            var0[Material.SMOOTH_STAIRS.ordinal()] = 110;
         } catch (NoSuchFieldError var70) {
         }

         try {
            var0[Material.SNOW.ordinal()] = 79;
         } catch (NoSuchFieldError var69) {
         }

         try {
            var0[Material.SNOW_BALL.ordinal()] = 275;
         } catch (NoSuchFieldError var68) {
         }

         try {
            var0[Material.SNOW_BLOCK.ordinal()] = 81;
         } catch (NoSuchFieldError var67) {
         }

         try {
            var0[Material.SOIL.ordinal()] = 61;
         } catch (NoSuchFieldError var66) {
         }

         try {
            var0[Material.SOUL_SAND.ordinal()] = 89;
         } catch (NoSuchFieldError var65) {
         }

         try {
            var0[Material.SPECKLED_MELON.ordinal()] = 325;
         } catch (NoSuchFieldError var64) {
         }

         try {
            var0[Material.SPIDER_EYE.ordinal()] = 318;
         } catch (NoSuchFieldError var63) {
         }

         try {
            var0[Material.SPONGE.ordinal()] = 20;
         } catch (NoSuchFieldError var62) {
         }

         try {
            var0[Material.SPRUCE_DOOR.ordinal()] = 194;
         } catch (NoSuchFieldError var61) {
         }

         try {
            var0[Material.SPRUCE_DOOR_ITEM.ordinal()] = 369;
         } catch (NoSuchFieldError var60) {
         }

         try {
            var0[Material.SPRUCE_FENCE.ordinal()] = 189;
         } catch (NoSuchFieldError var59) {
         }

         try {
            var0[Material.SPRUCE_FENCE_GATE.ordinal()] = 184;
         } catch (NoSuchFieldError var58) {
         }

         try {
            var0[Material.SPRUCE_WOOD_STAIRS.ordinal()] = 135;
         } catch (NoSuchFieldError var57) {
         }

         try {
            var0[Material.STAINED_CLAY.ordinal()] = 160;
         } catch (NoSuchFieldError var56) {
         }

         try {
            var0[Material.STAINED_GLASS.ordinal()] = 96;
         } catch (NoSuchFieldError var55) {
         }

         try {
            var0[Material.STAINED_GLASS_PANE.ordinal()] = 161;
         } catch (NoSuchFieldError var54) {
         }

         try {
            var0[Material.STANDING_BANNER.ordinal()] = 177;
         } catch (NoSuchFieldError var53) {
         }

         try {
            var0[Material.STATIONARY_LAVA.ordinal()] = 12;
         } catch (NoSuchFieldError var52) {
         }

         try {
            var0[Material.STATIONARY_WATER.ordinal()] = 10;
         } catch (NoSuchFieldError var51) {
         }

         try {
            var0[Material.STEP.ordinal()] = 45;
         } catch (NoSuchFieldError var50) {
         }

         try {
            var0[Material.STICK.ordinal()] = 223;
         } catch (NoSuchFieldError var49) {
         }

         try {
            var0[Material.STONE.ordinal()] = 2;
         } catch (NoSuchFieldError var48) {
         }

         try {
            var0[Material.STONE_AXE.ordinal()] = 218;
         } catch (NoSuchFieldError var47) {
         }

         try {
            var0[Material.STONE_BUTTON.ordinal()] = 78;
         } catch (NoSuchFieldError var46) {
         }

         try {
            var0[Material.STONE_HOE.ordinal()] = 234;
         } catch (NoSuchFieldError var45) {
         }

         try {
            var0[Material.STONE_PICKAXE.ordinal()] = 217;
         } catch (NoSuchFieldError var44) {
         }

         try {
            var0[Material.STONE_PLATE.ordinal()] = 71;
         } catch (NoSuchFieldError var43) {
         }

         try {
            var0[Material.STONE_SLAB2.ordinal()] = 183;
         } catch (NoSuchFieldError var42) {
         }

         try {
            var0[Material.STONE_SPADE.ordinal()] = 216;
         } catch (NoSuchFieldError var41) {
         }

         try {
            var0[Material.STONE_SWORD.ordinal()] = 215;
         } catch (NoSuchFieldError var40) {
         }

         try {
            var0[Material.STORAGE_MINECART.ordinal()] = 285;
         } catch (NoSuchFieldError var39) {
         }

         try {
            var0[Material.STRING.ordinal()] = 230;
         } catch (NoSuchFieldError var38) {
         }

         try {
            var0[Material.SUGAR.ordinal()] = 296;
         } catch (NoSuchFieldError var37) {
         }

         try {
            var0[Material.SUGAR_CANE.ordinal()] = 281;
         } catch (NoSuchFieldError var36) {
         }

         try {
            var0[Material.SUGAR_CANE_BLOCK.ordinal()] = 84;
         } catch (NoSuchFieldError var35) {
         }

         try {
            var0[Material.SULPHUR.ordinal()] = 232;
         } catch (NoSuchFieldError var34) {
         }

         try {
            var0[Material.THIN_GLASS.ordinal()] = 103;
         } catch (NoSuchFieldError var33) {
         }

         try {
            var0[Material.TNT.ordinal()] = 47;
         } catch (NoSuchFieldError var32) {
         }

         try {
            var0[Material.TORCH.ordinal()] = 51;
         } catch (NoSuchFieldError var31) {
         }

         try {
            var0[Material.TRAPPED_CHEST.ordinal()] = 147;
         } catch (NoSuchFieldError var30) {
         }

         try {
            var0[Material.TRAP_DOOR.ordinal()] = 97;
         } catch (NoSuchFieldError var29) {
         }

         try {
            var0[Material.TRIPWIRE.ordinal()] = 133;
         } catch (NoSuchFieldError var28) {
         }

         try {
            var0[Material.TRIPWIRE_HOOK.ordinal()] = 132;
         } catch (NoSuchFieldError var27) {
         }

         try {
            var0[Material.VINE.ordinal()] = 107;
         } catch (NoSuchFieldError var26) {
         }

         try {
            var0[Material.WALL_BANNER.ordinal()] = 178;
         } catch (NoSuchFieldError var25) {
         }

         try {
            var0[Material.WALL_SIGN.ordinal()] = 69;
         } catch (NoSuchFieldError var24) {
         }

         try {
            var0[Material.WATCH.ordinal()] = 290;
         } catch (NoSuchFieldError var23) {
         }

         try {
            var0[Material.WATER.ordinal()] = 9;
         } catch (NoSuchFieldError var22) {
         }

         try {
            var0[Material.WATER_BUCKET.ordinal()] = 269;
         } catch (NoSuchFieldError var21) {
         }

         try {
            var0[Material.WATER_LILY.ordinal()] = 112;
         } catch (NoSuchFieldError var20) {
         }

         try {
            var0[Material.WEB.ordinal()] = 31;
         } catch (NoSuchFieldError var19) {
         }

         try {
            var0[Material.WHEAT.ordinal()] = 239;
         } catch (NoSuchFieldError var18) {
         }

         try {
            var0[Material.WOOD.ordinal()] = 6;
         } catch (NoSuchFieldError var17) {
         }

         try {
            var0[Material.WOODEN_DOOR.ordinal()] = 65;
         } catch (NoSuchFieldError var16) {
         }

         try {
            var0[Material.WOOD_AXE.ordinal()] = 214;
         } catch (NoSuchFieldError var15) {
         }

         try {
            var0[Material.WOOD_BUTTON.ordinal()] = 144;
         } catch (NoSuchFieldError var14) {
         }

         try {
            var0[Material.WOOD_DOOR.ordinal()] = 267;
         } catch (NoSuchFieldError var13) {
         }

         try {
            var0[Material.WOOD_DOUBLE_STEP.ordinal()] = 126;
         } catch (NoSuchFieldError var12) {
         }

         try {
            var0[Material.WOOD_HOE.ordinal()] = 233;
         } catch (NoSuchFieldError var11) {
         }

         try {
            var0[Material.WOOD_PICKAXE.ordinal()] = 213;
         } catch (NoSuchFieldError var10) {
         }

         try {
            var0[Material.WOOD_PLATE.ordinal()] = 73;
         } catch (NoSuchFieldError var9) {
         }

         try {
            var0[Material.WOOD_SPADE.ordinal()] = 212;
         } catch (NoSuchFieldError var8) {
         }

         try {
            var0[Material.WOOD_STAIRS.ordinal()] = 54;
         } catch (NoSuchFieldError var7) {
         }

         try {
            var0[Material.WOOD_STEP.ordinal()] = 127;
         } catch (NoSuchFieldError var6) {
         }

         try {
            var0[Material.WOOD_SWORD.ordinal()] = 211;
         } catch (NoSuchFieldError var5) {
         }

         try {
            var0[Material.WOOL.ordinal()] = 36;
         } catch (NoSuchFieldError var4) {
         }

         try {
            var0[Material.WORKBENCH.ordinal()] = 59;
         } catch (NoSuchFieldError var3) {
         }

         try {
            var0[Material.WRITTEN_BOOK.ordinal()] = 330;
         } catch (NoSuchFieldError var2) {
         }

         try {
            var0[Material.YELLOW_FLOWER.ordinal()] = 38;
         } catch (NoSuchFieldError var1) {
         }

         $SWITCH_TABLE$org$bukkit$Material = var0;
         return var0;
      }
   }

   private class Resource {
      public final Material drop;
      public final Integer xp;
      public final Integer delay;

      public Resource(Material drop, Integer xp, Integer delay) {
         this.drop = drop;
         this.xp = xp;
         this.delay = delay;
      }
   }
}
