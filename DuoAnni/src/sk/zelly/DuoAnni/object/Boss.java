package sk.zelly.DuoAnni.object;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import sk.zelly.DuoAnni.Annihilation;
import sk.zelly.DuoAnni.Util;
import sk.zelly.DuoAnni.manager.EffectsManager;
import sk.zelly.DuoAnni.manager.PlayerSerializer;

public class Boss {
   private String configName;
   private int health;
   private String bossName;
   private Location spawn;
   private Location chest;
   private boolean alive;
   private HashMap<ItemStack, Float> legendaries = new HashMap();
   private HashMap<ItemStack, Float> loot = new HashMap();
   private int lootItems;
   private int ingots;

   public Boss(String configName, int health, String bossName, Location spawn, Location chest) {
      this.configName = configName;
      this.health = health;
      this.bossName = bossName;
      this.spawn = spawn;
      this.chest = chest;
      Annihilation.getInstance().chests.add(this.chest.getBlock());
      this.setAlive(false);
   }

   public void spawnLootChest() {
      this.chest.getBlock().setType(Material.CHEST);
      ItemStack randomItem = this.getRandomItem();
      Location l = this.chest.getBlock().getLocation();
      double y = l.getY() - 1.0D;
      Location lf = new Location(l.getWorld(), (double)l.getBlockX(), y, (double)l.getBlockZ());
      Util.spawnFirework(lf);

      for(int i = 0; i < 5; ++i) {
         EffectsManager.playEffectLoc(this.chest.getWorld(), Effect.ENDER_SIGNAL, this.chest.getBlock().getLocation());
      }

      Chest c = (Chest)this.chest.getBlock().getState();
      Inventory inv = c.getBlockInventory();
      Random r = new Random();
      inv.setItem(r.nextInt(inv.getSize()), randomItem);
      if (this.lootItems > inv.getSize() - 2) {
         this.lootItems = inv.getSize() - 2;
      }

      int j;
      int slot;
      for(j = 0; j < this.lootItems; ++j) {
         slot = r.nextInt(inv.getSize());
         if (isEmpty(inv, slot)) {
            inv.setItem(slot, randomItem);
         } else {
            --j;
         }
      }

      for(j = 0; j < this.ingots; ++j) {
         slot = r.nextInt(inv.getSize());
         ItemStack stack = inv.getItem(slot);
         if (isEmpty(inv, slot)) {
            inv.setItem(slot, new ItemStack(Material.IRON_INGOT));
         } else if (stack.getType() == Material.IRON_INGOT) {
            inv.getItem(slot).setAmount(stack.getAmount() + 1);
         } else {
            --j;
         }
      }

   }

   public ItemStack getRandomItem() {
      if (Annihilation.getInstance().getBossManager().enchant) {
         ItemStack item = new ItemStack(Material.ENCHANTED_BOOK, 1);
         Annihilation.getInstance().getBossManager().enchant = false;
         Enchantment enchantment = Enchantment.DAMAGE_ALL;
         int level = 3;
         EnchantmentStorageMeta meta = (EnchantmentStorageMeta)item.getItemMeta();
         meta.addStoredEnchant(enchantment, level, true);
         item.setItemMeta(meta);
         return new ItemStack(item);
      } else if (!Annihilation.getInstance().getBossManager().enchant && Annihilation.getInstance().getBossManager().brewing && Annihilation.getInstance().getPhase() > 2) {
         Annihilation.getInstance().getBossManager().brewing = false;
         return new ItemStack(Material.BREWING_STAND_ITEM);
      } else {
         int randomIndex;
         if (Annihilation.getInstance().getPhase() <= 2) {
            randomIndex = (new Random()).nextInt(this.rItems().size() - 5);
            return (ItemStack)this.rItems().toArray()[randomIndex];
         } else {
            randomIndex = (new Random()).nextInt(this.rItems().size() - 4);
            return (ItemStack)this.rItems().toArray()[randomIndex + 4];
         }
      }
   }

   public Collection<ItemStack> rItems() {
      Collection<ItemStack> items = new ArrayList();
      ItemStack[] ritems = PlayerSerializer.BossLoot();
      ItemStack[] arrayOfItemStack1 = ritems;
      int i = ritems.length;

      for(byte b = 0; b < i; ++b) {
         ItemStack itemStack = arrayOfItemStack1[b];
         items.add(itemStack);
      }

      return items;
   }

   public String getConfigName() {
      return this.configName;
   }

   public void setConfigName(String configName) {
      this.configName = configName;
   }

   public int getHealth() {
      return this.health;
   }

   public void setHealth(int health) {
      this.health = health;
   }

   public String getBossName() {
      return this.bossName;
   }

   public void setBossName(String bossName) {
      this.bossName = bossName;
   }

   public Location getSpawn() {
      return this.spawn;
   }

   public void setSpawn(Location spawn) {
      this.spawn = spawn;
   }

   public Location getChest() {
      return this.chest;
   }

   public void setChest(Location chest) {
      this.chest = chest;
   }

   public boolean isAlive() {
      return this.alive;
   }

   public void setAlive(boolean alive) {
      this.alive = alive;
   }

   private static ItemStack getRandomItem(HashMap<ItemStack, Float> weighting) {
      List<ItemStack> items = new ArrayList(weighting.keySet());
      float totalWeight = 0.0F;

      Float f;
      for(Iterator var4 = weighting.values().iterator(); var4.hasNext(); totalWeight += f) {
         f = (Float)var4.next();
      }

      float rand = (new Random()).nextFloat() * totalWeight;

      for(int i = 0; i < weighting.size(); ++i) {
         ItemStack item = (ItemStack)items.get(i);
         rand -= (Float)weighting.get(item);
         if (rand <= 0.0F) {
            return item;
         }
      }

      return null;
   }

   private static boolean isEmpty(Inventory inv, int slot) {
      ItemStack stack = inv.getItem(slot);
      if (stack == null) {
         return true;
      } else {
         return stack.getType() == Material.AIR;
      }
   }
}
