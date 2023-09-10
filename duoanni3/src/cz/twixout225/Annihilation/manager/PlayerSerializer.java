package cz.twixout225.Annihilation.manager;

import cz.twixout225.Annihilation.Annihilation;
import cz.twixout225.Annihilation.Translator;
import cz.twixout225.Annihilation.object.GameTeam;
import cz.twixout225.Annihilation.object.PlayerMeta;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class PlayerSerializer {
   private static Annihilation plugin;

   public PlayerSerializer(Annihilation pl) {
      plugin = pl;
   }

   public static void PlayerToConfig(String playerName, ItemStack[] items, ItemStack[] armor, double health, float saturation, int level, int gm, int food, float exhaut, float exp, GameTeam team, Boolean bol, String wName) {
      try {
         File file = new File("plugins/FBDuoAnni/users/" + playerName + ".yml");
         FileConfiguration config = YamlConfiguration.loadConfiguration(file);
         config.set("Name", playerName);
         config.set("Health", health);
         config.set("Food", food);
         config.set("Saturation", saturation);
         config.set("Exhaustion", exhaut);
         config.set("XP-Level", level);
         config.set("Exp", exp);
         config.set("GameMode", gm);
         String teamName = GameTeam.getName(team);
         config.set("Team", teamName);
         ItemStackToConfig(config, "Armor", armor);
         ItemStackToConfig(config, "Inventaire", items);
         config.set("killed", false);
         config.set("world", wName);
         config.save(file);
      } catch (IOException var17) {
      }

   }

   public static void RetorePlayer(Player p) {
      String playerName = p.getName();
      File f = new File("plugins/FBDuoAnni/users/" + playerName + ".yml");
      FileConfiguration config = YamlConfiguration.loadConfiguration(f);
      ConfigToPlayer(p, config);
      f.delete();
   }

   public static FileConfiguration getConfig(String playerName) {
      File file = new File("plugins/FBDuoAnni/users/" + playerName + ".yml");
      FileConfiguration config = YamlConfiguration.loadConfiguration(file);
      return config;
   }

   public static void removeItems(String playerName) {
      try {
         File file = new File("plugins/FBDuoAnni/users/" + playerName + ".yml");
         FileConfiguration config = YamlConfiguration.loadConfiguration(file);
         config.set("killed", true);
         config.save(file);
         Logger.getLogger("Minecraft").log(Level.INFO, playerName + " removing player items");
      } catch (IOException var3) {
      }

   }

   public static void delete(String playerName) {
      File file = new File("plugins/FBDuoAnni/users/" + playerName + ".yml");
      file.delete();
   }

   public static void save(String playerName) {
      File file = new File("plugins/FBDuoAnni/users/" + playerName + ".yml");
      YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

      try {
         config.save(file);
      } catch (IOException var4) {
      }

   }

   public static void ConfigToPlayer(Player p, FileConfiguration config) {
      try {
         if (!config.contains("Name") || !config.getString("Name").equals(p.getName())) {
            return;
         }

         if (p == null) {
            return;
         }

         p.updateInventory();
         PlayerMeta meta = PlayerMeta.getMeta(p);
         GameTeam team = GameTeam.getTeam(config.getString("Team"));
         if (team == GameTeam.NONE && plugin.getPhase() > plugin.lastJoinPhase) {
            p.kickPlayer(Translator.change("PREFIX") + ChatColor.RED + "Your team is invalid.");
            return;
         }

         meta.setTeam(team);
         meta.setAlive(true);
         p.setHealth((double)((int)config.getDouble("Health")));
         p.setFoodLevel(config.getInt("Food"));
         p.setSaturation((float)config.getDouble("Saturation"));
         p.setExhaustion((float)config.getDouble("Exhaustion"));
         p.setLevel(config.getInt("XP-Level"));
         p.setExp((float)config.getDouble("Exp"));
         p.setGameMode(GameMode.getByValue(config.getInt("GameMode")));
         p.getInventory().clear();
         p.getInventory().setArmorContents(ConfigToItemStack(config, "Armor"));
         p.getInventory().setContents(ConfigToItemStack(config, "Inventaire"));
         p.updateInventory();
      } catch (IllegalArgumentException var4) {
      }

   }

   public static Collection<ItemStack> dropItem(String playerName) {
      ItemStack[] a = ConfigToItemStack(getConfig(playerName), "Armor");
      ItemStack[] i = ConfigToItemStack(getConfig(playerName), "Inventaire");
      Collection<ItemStack> items = new ArrayList();
      items.addAll(Arrays.asList(a));
      items.addAll(Arrays.asList(i));
      return items;
   }

   public static ItemStack[] ConfigToItemStack(FileConfiguration config, String path) {
      int nb = config.getInt(path + ".Item-Nb");
      ItemStack[] item = new ItemStack[nb];

      for(int i = 0; i < nb; ++i) {
         item[i] = config.getItemStack(path + ".Item" + i);
      }

      return item;
   }

   public static ItemStack[] BossLoot() {
      File file = new File("plugins/FBDuoAnni/config.yml");
      FileConfiguration config = YamlConfiguration.loadConfiguration(file);
      int nb = config.getInt("Boss-loot.Item-Nb");
      ItemStack[] item = new ItemStack[nb];

      for(int i = 0; i < nb; ++i) {
         item[i] = config.getItemStack("Boss-loot.Item" + i);
      }

      return item;
   }

   public static FileConfiguration ItemStackToConfig(FileConfiguration config, String path, ItemStack[] items) {
      if (config == null) {
         return null;
      } else {
         config.set(path + ".Item-Nb", items.length);
         int i = 0;
         ItemStack[] var7 = items;
         int var6 = items.length;

         for(int var5 = 0; var5 < var6; ++var5) {
            ItemStack it = var7[var5];
            config.set(path + ".Item" + i, it);
            ++i;
         }

         return config;
      }
   }

   public static String InventoryToString(Inventory invInventory) {
      String serialization = invInventory.getSize() + ";";

      for(int i = 0; i < invInventory.getSize(); ++i) {
         ItemStack is = invInventory.getItem(i);
         if (is != null) {
            String serializedItemStack = new String();
            String isType = String.valueOf(is.getType().getId());
            serializedItemStack = serializedItemStack + "t@" + isType;
            String isAmount;
            if (is.getDurability() != 0) {
               isAmount = String.valueOf(is.getDurability());
               serializedItemStack = serializedItemStack + ":d@" + isAmount;
            }

            if (is.getAmount() != 1) {
               isAmount = String.valueOf(is.getAmount());
               serializedItemStack = serializedItemStack + ":a@" + isAmount;
            }

            Map isEnch = is.getEnchantments();
            Entry ench;
            if (isEnch.size() > 0) {
               for(Iterator it = isEnch.entrySet().iterator(); it.hasNext(); serializedItemStack = serializedItemStack + ":e@" + ((Enchantment)ench.getKey()).getId() + "@" + ench.getValue()) {
                  ench = (Entry)it.next();
               }
            }

            serialization = serialization + i + "#" + serializedItemStack + ";";
         }
      }

      return serialization;
   }

   public static Inventory StringToInventory(String invString) {
      String[] serializedBlocks = invString.split(";");
      String invInfo = serializedBlocks[0];
      Inventory deserializedInventory = Bukkit.getServer().createInventory((InventoryHolder)null, Integer.valueOf(invInfo));

      for(int i = 1; i < serializedBlocks.length; ++i) {
         String[] serializedBlock = serializedBlocks[i].split("#");
         int stackPosition = Integer.valueOf(serializedBlock[0]);
         if (stackPosition < deserializedInventory.getSize()) {
            ItemStack is = null;
            Boolean createdItemStack = false;
            String[] serializedItemStack = serializedBlock[1].split(":");
            String[] var13 = serializedItemStack;
            int var12 = serializedItemStack.length;

            for(int var11 = 0; var11 < var12; ++var11) {
               String itemInfo = var13[var11];
               String[] itemAttribute = itemInfo.split("@");
               if (itemAttribute[0].equals("t")) {
                  is = new ItemStack(Material.getMaterial(Integer.valueOf(itemAttribute[1])));
                  createdItemStack = true;
               } else if (itemAttribute[0].equals("d") && createdItemStack) {
                  is.setDurability(Short.valueOf(itemAttribute[1]));
               } else if (itemAttribute[0].equals("a") && createdItemStack) {
                  is.setAmount(Integer.valueOf(itemAttribute[1]));
               } else if (itemAttribute[0].equals("e") && createdItemStack) {
                  is.addEnchantment(Enchantment.getById(Integer.valueOf(itemAttribute[1])), Integer.valueOf(itemAttribute[2]));
               }
            }

            deserializedInventory.setItem(stackPosition, is);
         }
      }

      return deserializedInventory;
   }

   public static boolean isKilled(String name) {
      return getConfig(name).getBoolean("killed");
   }
}
