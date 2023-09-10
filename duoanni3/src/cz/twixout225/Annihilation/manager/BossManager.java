package cz.twixout225.Annihilation.manager;

import cz.twixout225.Annihilation.Annihilation;
import cz.twixout225.Annihilation.Util;
import cz.twixout225.Annihilation.object.Boss;
import java.util.HashMap;
import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftIronGolem;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;

public class BossManager {
   public HashMap<String, Boss> bosses = new HashMap();
   public HashMap<String, Boss> bossNames = new HashMap();
   public boolean enchant = false;
   public boolean brewing = true;
   private Annihilation plugin;

   public BossManager(Annihilation instance) {
      this.plugin = instance;
   }

   public void loadBosses(HashMap<String, Boss> b) {
      this.bosses = b;
   }

   public void spawnBosses() {
      Iterator var2 = this.bosses.values().iterator();

      while(var2.hasNext()) {
         Boss b = (Boss)var2.next();
         this.spawn(b);
      }

   }

   public void spawn(Boss b) {
      Location spawn = b.getSpawn();
      if (spawn != null && spawn.getWorld() != null) {
         Bukkit.getWorld(spawn.getWorld().getName()).loadChunk(spawn.getChunk());
         IronGolem boss = (IronGolem)spawn.getWorld().spawnCreature(spawn, EntityType.IRON_GOLEM);
         boss.setMaxHealth((double)b.getHealth());
         boss.setHealth((double)b.getHealth());
         boss.setCanPickupItems(false);
         boss.setPlayerCreated(false);
         boss.setRemoveWhenFarAway(false);
         boss.setCustomNameVisible(true);
         boss.setCustomName(ChatColor.translateAlternateColorCodes('&', String.valueOf(b.getBossName()) + " &8» &a" + b.getHealth() + " HP"));
         this.bossNames.put(boss.getCustomName(), b);
         Util.spawnFirework(b.getSpawn());
         Util.spawnFirework(b.getSpawn());
         Util.spawnFirework(b.getSpawn());
      }

   }

   public void update(Boss boss, IronGolem g) {
      CraftIronGolem cig = (CraftIronGolem)g;
      boss.setHealth((int)cig.getHealth());
      g.setCustomName(ChatColor.translateAlternateColorCodes('&', String.valueOf(String.valueOf(String.valueOf(boss.getBossName()))) + " &8» &a" + boss.getHealth() + " HP"));
      this.bossNames.put(g.getCustomName(), boss);
      this.bosses.put(boss.getConfigName(), boss);
   }

   public Boss newBoss(Boss b) {
      String boss = b.getConfigName();
      this.bosses.remove(boss);
      this.bossNames.remove(boss);
      YamlConfiguration yamlConfiguration = this.plugin.getConfigManager().getConfig("maps.yml");
      ConfigurationSection section = yamlConfiguration.getConfigurationSection(this.plugin.getMapManager().getCurrentMap().getName());
      ConfigurationSection sec = section.getConfigurationSection("bosses");
      Boss bb = new Boss(boss, sec.getInt(String.valueOf(boss) + ".hearts") * 2, sec.getString(String.valueOf(boss) + ".name"), Util.parseLocation(this.plugin.getMapManager().getCurrentMap().getWorld(), sec.getString(String.valueOf(boss) + ".spawn")), Util.parseLocation(this.plugin.getMapManager().getCurrentMap().getWorld(), sec.getString(String.valueOf(boss) + ".chest")));
      this.bosses.put(boss, bb);
      return bb;
   }
}
