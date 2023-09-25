package cz.twixout225.Annihilation.stats;

import cz.twixout225.Annihilation.Annihilation;
import cz.twixout225.Annihilation.manager.ConfigManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.bukkit.entity.Player;

public class StatsManager {
   public static final int UNDEF_STAT = -42;
   public static final String UNDEF_STAT_S = null;
   private Annihilation plugin;
   public ConfigManager config;

   public StatsManager(Annihilation instance, ConfigManager config) {
      this.plugin = instance;
      this.config = config;
   }

   public int getStat(StatType s, Player p) {
      String uuid = p.getName();
      if (!this.plugin.useMysql) {
         return this.config.getConfig("stats.yml").getInt(String.valueOf(String.valueOf(uuid)) + "." + s.name());
      } else {
         try {
            int stat = -42;

            for(ResultSet rs = this.plugin.getDatabaseHandler().query("SELECT * FROM `" + this.plugin.mysqlName + "` WHERE `username`='" + uuid + "'").getResultSet(); rs.next(); stat = rs.getInt(s.name().toLowerCase())) {
            }

            return stat;
         } catch (SQLException var6) {
            return -42;
         }
      }
   }

   public String getStatS(StatType s, Player p) {
      String uuid = p.getName();
      if (!this.plugin.useMysql) {
         return this.config.getConfig("stats.yml").getString(String.valueOf(String.valueOf(uuid)) + "." + s.name());
      } else {
         try {
            String stat = UNDEF_STAT_S;

            for(ResultSet rs = this.plugin.getDatabaseHandler().query("SELECT * FROM `" + this.plugin.mysqlName + "` WHERE `username`='" + uuid + "'").getResultSet(); rs.next(); stat = rs.getString(s.name().toLowerCase())) {
            }

            return stat;
         } catch (SQLException var6) {
            return UNDEF_STAT_S;
         }
      }
   }

   public void setValue(StatType s, Player p, int value) {
      String uuid = p.getName();
      if (!this.plugin.useMysql) {
         this.config.getConfig("stats.yml").set(String.valueOf(String.valueOf(uuid)) + "." + s.name(), value);
         this.config.save("stats.yml");
      } else {
         this.plugin.getDatabaseHandler().query("UPDATE `" + this.plugin.mysqlName + "` SET `" + s.name().toLowerCase() + "`='" + value + "' WHERE `username`='" + uuid + "';");
      }

   }

   public void setValue(StatType s, Player p, String value) {
      String uuid = p.getName();
      if (!this.plugin.useMysql) {
         this.config.getConfig("stats.yml").set(String.valueOf(String.valueOf(uuid)) + "." + s.name(), value);
         this.config.save("stats.yml");
      } else {
         this.plugin.getDatabaseHandler().query("UPDATE `" + this.plugin.mysqlName + "` SET `" + s.name().toLowerCase() + "`='" + value + "' WHERE `username`='" + uuid + "';");
      }

   }

   public void incrementStat(StatType s, Player p) {
      this.incrementStat(s, p, 1);
   }

   public void incrementStat(StatType s, Player p, int amount) {
      this.setValue(s, p, this.getStat(s, p) + amount);
   }
}
