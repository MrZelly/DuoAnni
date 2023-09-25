package sk.zelly.DuoAnni.object;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import sk.zelly.DuoAnni.listeners.SoulboundListener;

public enum Kit {
   CIVILIAN(Material.WORKBENCH) {
      {
         this.spawnItems.add(new ItemStack(Material.STONE_SWORD));
         this.spawnItems.add(new ItemStack(Material.STONE_PICKAXE));
         this.spawnItems.add(new ItemStack(Material.STONE_AXE));
         this.spawnItems.add(new ItemStack(Material.WORKBENCH));
         this.lore.add("+ Stone sword");
         this.lore.add("+ Stone pickaxe");
         this.lore.add("+ Stone axe");
         this.lore.add("+ Crafting table");
      }
   },
   RUSHER(Material.STONE_SWORD) {
      {
         this.spawnItems.add(new ItemStack(Material.STONE_SWORD));
         this.spawnItems.add(new ItemStack(Material.STONE_PICKAXE));
         this.spawnItems.add(new ItemStack(Material.STONE_AXE));
//         this.spawnItems.add((new Potion(PotionType.SPEED, 1)).toItemStack(1));
         this.lore.add("+ Stone sword");
         this.lore.add("+ Stone pickaxe");
         this.lore.add("+ Stone axe");
         this.lore.add("+ Permanent speed I");
      }
   },
   ARCHER(Material.BOW) {
      {
         this.spawnItems.add(new ItemStack(Material.STONE_SWORD));
         ItemStack bow = this.create(Material.BOW, "§bMysterious Bow");
         ItemMeta me = bow.getItemMeta();
         ArrayList<String> bowlore = new ArrayList();
         bowlore.add("Mysterious Bow");
         bowlore.add("§6Soulbound");
         me.setLore(bowlore);
         bow.setItemMeta(me);
         this.spawnItems.add(bow);
         this.spawnItems.add(new ItemStack(Material.STONE_PICKAXE));
         this.spawnItems.add(new ItemStack(Material.STONE_AXE));
         this.spawnItems.add(new ItemStack(Material.STONE_SPADE));
         this.spawnItems.add(new ItemStack(Material.ARROW, 16));
         this.lore.add("+ Stone sword");
         this.lore.add("+ Bow");
         this.lore.add("+ Stone pickaxe");
         this.lore.add("+ Stone axe");
         this.lore.add("+ Stone shovel");
         this.lore.add("+ 16 arrows");
      }
   },
   MINER(Material.STONE_PICKAXE) {
      {
         this.spawnItems.add(new ItemStack(Material.STONE_SWORD));
         this.spawnItems.add(new ItemStack(Material.STONE_PICKAXE));
         this.spawnItems.add(new ItemStack(Material.STONE_AXE));
         ((ItemStack)this.spawnItems.get(1)).addEnchantment(Enchantment.DIG_SPEED, 1);
         ((ItemStack)this.spawnItems.get(2)).addEnchantment(Enchantment.DIG_SPEED, 1);
         this.lore.add("+ Stone sword");
         this.lore.add("+ Stone pickaxe (Efficiency I)");
         this.lore.add("+ Stone axe (Efficiency I)");
      }
   },
   JUMPER(Material.FEATHER) {
      {
         this.spawnItems.add(new ItemStack(Material.STONE_SWORD));
         this.spawnItems.add(new ItemStack(Material.STONE_PICKAXE));
         this.spawnItems.add(new ItemStack(Material.STONE_AXE));
         this.spawnItems.add(this.create(Material.FEATHER, "§bLeap"));
         this.lore.add("+ Stone sword");
         this.lore.add("+ Stone pickaxe");
         this.lore.add("+ Stone axe");
         this.lore.add("+ Leap item");
      }
   },
   FIREMAN(Material.BLAZE_ROD) {
      {
         this.spawnItems.add(new ItemStack(Material.STONE_SWORD));
         this.spawnItems.add(new ItemStack(Material.STONE_PICKAXE));
         this.spawnItems.add(new ItemStack(Material.STONE_AXE));
//         Potion potion = new Potion(PotionType.FIRE_RESISTANCE, 1, true, true);
//         ItemStack itemStack = new ItemStack(Material.POTION);
//         potion.apply(itemStack);
//         this.spawnItems.add(itemStack);
         this.lore.add("+ Stone sword");
         this.lore.add("+ Stone pickaxe");
         this.lore.add("+ Stone axe");
         this.lore.add("+ Ignition at hit");
         this.lore.add("+ Permanent fire resistance");
      }
   },
   WITHER(Material.BONE) {
	      {
	         this.spawnItems.add(new ItemStack(Material.STONE_SWORD));
	         this.spawnItems.add(new ItemStack(Material.STONE_PICKAXE));
	         this.spawnItems.add(new ItemStack(Material.STONE_AXE));
	         this.lore.add("+ Stone sword");
	         this.lore.add("+ Stone pickaxe");
	         this.lore.add("+ Stone axe");
	         this.lore.add("+ Shoot wither head every 20 seconds");
	      }
   },
   HEALER(Material.GHAST_TEAR) {
      {
         this.spawnItems.add(new ItemStack(Material.STONE_SWORD));
         this.spawnItems.add(new ItemStack(Material.STONE_PICKAXE));
         this.spawnItems.add(new ItemStack(Material.STONE_AXE));
         this.spawnItems.add(this.create(Material.GHAST_TEAR, "§bHeal"));
         this.lore.add("+ Stone sword");
         this.lore.add("+ Stone pickaxe");
         this.lore.add("+ Stone axe");
         this.lore.add("+ Heal every 45 seconds");
      }
   },
   ASSASIN(Material.GOLDEN_APPLE) {
      {
         this.spawnItems.add(new ItemStack(Material.STONE_SWORD));
         this.spawnItems.add(new ItemStack(Material.STONE_PICKAXE));
         this.spawnItems.add(new ItemStack(Material.STONE_AXE));
         this.lore.add("+ Stone sword");
         this.lore.add("+ Stone pickaxe");
         this.lore.add("+ Stone axe");
         this.lore.add("+ Get gapple on kill (max 5)");
      }
   },
   MAGNET(Material.IRON_INGOT) {
      {
         this.spawnItems.add(new ItemStack(Material.STONE_SWORD));
         this.spawnItems.add(new ItemStack(Material.STONE_PICKAXE));
         this.spawnItems.add(new ItemStack(Material.STONE_AXE));
         this.spawnItems.add(new ItemStack(Material.SNOW_BALL));
         this.lore.add("+ Stone sword");
         this.lore.add("+ Stone pickaxe");
         this.lore.add("+ Stone axe");
         this.lore.add("+ Snowball that throws players to you on hit every 60 seconds");
      }
   },
   GUARD(Material.EYE_OF_ENDER) {
      {
         this.spawnItems.add(new ItemStack(Material.STONE_SWORD));
         this.spawnItems.add(new ItemStack(Material.STONE_PICKAXE));
         this.spawnItems.add(new ItemStack(Material.STONE_AXE));
         this.spawnItems.add(this.create(Material.EYE_OF_ENDER, "§bTeleporter"));
         this.lore.add("+ Stone sword");
         this.lore.add("+ Stone pickaxe");
         this.lore.add("+ Stone axe");
         this.lore.add("+ Teleport to base every 60 seconds");
      }
   };

   final ItemStack i;
   List<String> lore;
   List<ItemStack> spawnItems;
   ItemStack[] spawnArmor;
   private ItemStack icon;
   public HashMap<Player, Kit> firstKit;
   // $FF: synthetic field
   private static volatile int[] $SWITCH_TABLE$cz$twixout225$Annihilation$object$GameTeam;

   static {
      Kit[] kitArray = values();
      int n = kitArray.length;

      for(int n2 = 0; n2 < n; ++n2) {
         Kit kit = kitArray[n2];
         kit.init();
      }

   }

   private Kit(Material m) {
      this.firstKit = new HashMap();
      this.i = new ItemStack(Material.POTION, 1, (short)0, (byte)46);
      this.lore = new ArrayList();
      this.spawnItems = new ArrayList();
      this.spawnArmor = new ItemStack[]{new ItemStack(Material.LEATHER_BOOTS), new ItemStack(Material.LEATHER_LEGGINGS), new ItemStack(Material.LEATHER_CHESTPLATE), new ItemStack(Material.LEATHER_HELMET)};
      this.icon = new ItemStack(m);
      if (m == Material.POTION) {
         this.icon = this.i;
      }

      ItemMeta meta = this.icon.getItemMeta();
      meta.setDisplayName(this.getName());
      this.icon.setItemMeta(meta);
   }

   private void init() {
      for(int i = 0; i < this.lore.size(); ++i) {
         String s = (String)this.lore.get(i);
         s = ChatColor.AQUA + s;
         this.lore.set(i, s);
      }

      ItemMeta meta = this.icon.getItemMeta();
      meta.setLore(this.lore);
      this.icon.setItemMeta(meta);
   }

   public static Kit getKit(String name) {
      Kit[] kitArray = values();
      int n = kitArray.length;

      for(int n2 = 0; n2 < n; ++n2) {
         Kit type = kitArray[n2];
         if (type.name().equalsIgnoreCase(name)) {
            return type;
         }
      }

      return null;
   }

   public void give(Player recipient, GameTeam team) {
      PlayerInventory inv = recipient.getInventory();
      inv.clear();
      Iterator var5 = this.spawnItems.iterator();

      ItemStack compass;
      while(var5.hasNext()) {
         compass = (ItemStack)var5.next();
         ItemStack toGive = compass.clone();
         SoulboundListener.soulbind(toGive);
         inv.addItem(new ItemStack[]{toGive});
      }

      recipient.removePotionEffect(PotionEffectType.SPEED);
      compass = new ItemStack(Material.COMPASS);
      ItemMeta compassMeta = compass.getItemMeta();
      compassMeta.setDisplayName(team.color() + "Pointing to " + team.toString() + " Nexus");
      compass.setItemMeta(compassMeta);
      SoulboundListener.soulbind(compass);
      inv.addItem(new ItemStack[]{compass});
      recipient.setCompassTarget(team.getNexus().getLocation());
      inv.setArmorContents(this.spawnArmor);
      this.colorizeArmor(inv, this.getTeamColor(team));
      ItemStack[] itemStackArray = inv.getArmorContents();
      int n = itemStackArray.length;

      for(int n2 = 0; n2 < n; ++n2) {
         ItemStack armor = itemStackArray[n2];
         SoulboundListener.soulbind(armor);
         recipient.setMaxHealth(20.0D);
      }

   }

   private Color getTeamColor(GameTeam team) {
      switch($SWITCH_TABLE$cz$twixout225$Annihilation$object$GameTeam()[team.ordinal()]) {
      case 1:
         return Color.RED;
      case 2:
         return Color.BLUE;
      default:
         return Color.WHITE;
      }
   }

   private void colorizeArmor(PlayerInventory inv, Color color) {
      ItemStack[] itemStackArray = inv.getArmorContents();
      int n = itemStackArray.length;

      for(int n2 = 0; n2 < n; ++n2) {
         ItemStack item = itemStackArray[n2];
         if (item.getItemMeta() instanceof LeatherArmorMeta) {
            LeatherArmorMeta meta = (LeatherArmorMeta)item.getItemMeta();
            meta.setColor(color);
            item.setItemMeta(meta);
         }
      }

   }

   public String getName() {
      return String.valueOf(this.name().substring(0, 1)) + this.name().substring(1).toLowerCase();
   }

   public boolean isOwnedBy(Player p) {
      return p.isOp() || this == CIVILIAN || p.hasPermission("annihilation.class." + this.getName().toLowerCase());
   }

   public ItemStack getIcon() {
      return this.icon;
   }

   public static boolean isItem(ItemStack stack, String name) {
      if (stack == null) {
         return false;
      } else {
         ItemMeta meta = stack.getItemMeta();
         return meta != null && meta.hasDisplayName() && meta.getDisplayName().equalsIgnoreCase(name);
      }
   }

   public ItemStack create(Material m, String name) {
      ItemStack is = new ItemStack(m);
      ItemMeta im = is.getItemMeta();
      im.setDisplayName(name);
      is.setItemMeta(im);
      return is;
   }

   private Kit(String string, int n, Material material, Kit kit) {
      this(material);
   }

   // $FF: synthetic method
   Kit(Material var3, Kit var4) {
      this(var3);
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$cz$twixout225$Annihilation$object$GameTeam() {
      int[] var10000 = $SWITCH_TABLE$cz$twixout225$Annihilation$object$GameTeam;
      if (var10000 != null) {
         return var10000;
      } else {
         int[] var0 = new int[GameTeam.values().length];

         try {
            var0[GameTeam.BLUE.ordinal()] = 2;
         } catch (NoSuchFieldError var3) {
         }

         try {
            var0[GameTeam.NONE.ordinal()] = 3;
         } catch (NoSuchFieldError var2) {
         }

         try {
            var0[GameTeam.RED.ordinal()] = 1;
         } catch (NoSuchFieldError var1) {
         }

         $SWITCH_TABLE$cz$twixout225$Annihilation$object$GameTeam = var0;
         return var0;
      }
   }
}
