package cz.twixout225.Annihilation.manager;

import cz.twixout225.Annihilation.Annihilation;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.TreeMap;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigManager {
   private final TreeMap<String, ConfigManager.Configuration> configs;
   private final Annihilation plugin;
   private final File configFolder;
   File playerdataFolder;

   public ConfigManager(Annihilation plugin) {
      this.configs = new TreeMap(String.CASE_INSENSITIVE_ORDER);
      this.plugin = plugin;
      this.configFolder = plugin.getDataFolder();
      this.playerdataFolder = new File(plugin.getDataFolder() + "/users/");
      if (!this.configFolder.exists()) {
         this.configFolder.mkdirs();
      }

      if (!this.playerdataFolder.exists()) {
         this.playerdataFolder.mkdirs();
      }

   }

   public void loadConfigFile(String filename) {
      this.loadConfigFiles(filename);
   }

   public void loadConfigFiles(String... filenames) {
      String[] var5 = filenames;
      int var4 = filenames.length;

      for(int var3 = 0; var3 < var4; ++var3) {
         String filename = var5[var3];
         File configFile = new File(this.configFolder, filename);

         try {
            if (!configFile.exists()) {
               configFile.createNewFile();
               InputStream in = this.plugin.getResource(filename);
               if (in != null) {
                  try {
                     OutputStream out = new FileOutputStream(configFile);
                     byte[] buf = new byte[1024];

                     int len;
                     while((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                     }

                     out.close();
                     in.close();
                  } catch (IOException var11) {
                  }
               } else {
                  this.plugin.getLogger().warning("Default configuration for " + filename + " missing");
               }
            }

            ConfigManager.Configuration config = new ConfigManager.Configuration(configFile);
            config.load();
            this.configs.put(filename, config);
         } catch (IOException var12) {
         } catch (InvalidConfigurationException var13) {
         }
      }

   }

   public void save(String filename) {
      if (this.configs.containsKey(filename)) {
         try {
            ((ConfigManager.Configuration)this.configs.get(filename)).save();
         } catch (IOException var3) {
            this.printException(var3, filename);
         }
      }

   }

   public void reload(String filename) {
      if (this.configs.containsKey(filename)) {
         try {
            ((ConfigManager.Configuration)this.configs.get(filename)).load();
         } catch (InvalidConfigurationException | IOException var7) {
            Exception ex4 = null;
            Exception e = null;
            Exception ex3 = null;
            this.printException((Exception)e, filename);
         }
      }

   }

   public YamlConfiguration getConfig(String filename) {
      return this.configs.containsKey(filename) ? ((ConfigManager.Configuration)this.configs.get(filename)).getConfig() : null;
   }

   private void printException(Exception e, String filename) {
      if (e instanceof IOException) {
         this.plugin.getLogger().severe("I/O exception while handling " + filename);
      } else if (e instanceof InvalidConfigurationException) {
         this.plugin.getLogger().severe("Invalid configuration in " + filename);
      }

   }

   private static class Configuration {
      private final File configFile;
      private YamlConfiguration config;

      public Configuration(File configFile) {
         this.configFile = configFile;
         this.config = new YamlConfiguration();
      }

      public YamlConfiguration getConfig() {
         return this.config;
      }

      public void load() throws IOException, InvalidConfigurationException {
         this.config.load(this.configFile);
      }

      public void save() throws IOException {
         this.config.save(this.configFile);
      }
   }
}
