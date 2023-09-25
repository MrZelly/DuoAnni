package sk.zelly.DuoAnni.maps;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;
import org.bukkit.Bukkit;

public class MapLoader {
   private final Logger log;
   private final File dataFolder;

   public MapLoader(Logger log, File dataFolder) {
      this.dataFolder = dataFolder;
      this.log = log;
   }

   public boolean loadMap(String name) {
      File mapsFolder = new File(this.dataFolder, "maps");
      if (!mapsFolder.exists()) {
         return false;
      } else {
         File source = new File(mapsFolder, name);
         if (!source.exists()) {
            return false;
         } else {
            Bukkit.unloadWorld(name, false);
            File destination = new File(this.dataFolder.getParentFile().getParentFile(), String.valueOf(String.valueOf(name)) + File.separator + "region");

            try {
               this.copyFolder(source, destination);
               return true;
            } catch (IOException var6) {
               this.log.severe("Could not load map " + name);
               return false;
            }
         }
      }
   }

   public boolean saveMap(String name) {
      File mapsFolder = new File(this.dataFolder, "maps");
      if (!mapsFolder.exists()) {
         mapsFolder.mkdir();
      }

      File source = new File(this.dataFolder.getParentFile().getParentFile(), String.valueOf(String.valueOf(name)) + File.separator + "region");
      if (!source.exists()) {
         return false;
      } else {
         File destination = new File(mapsFolder, name);

         try {
            this.copyFolder(source, destination);
            return true;
         } catch (IOException var6) {
            this.log.severe("Could not save map " + name);
            return false;
         }
      }
   }

   private void copyFolder(File src, File dest) throws IOException {
      if (!src.exists()) {
         this.log.severe("File " + src.toString() + " does not exist, cannot copy");
      } else {
         int i;
         if (src.isDirectory()) {
            boolean existed = dest.exists();
            if (!existed) {
               dest.mkdir();
            }

            String[] srcFiles = src.list();
            if (existed) {
               this.log.info("Copying folder " + src.getAbsolutePath() + " and overwriting " + dest.getAbsolutePath());
            } else {
               this.log.info("Copying folder " + src.getAbsolutePath() + " to " + dest.getAbsolutePath());
            }

            String[] array = srcFiles;
            int length2 = srcFiles.length;

            for(i = 0; i < length2; ++i) {
               String file = array[i];
               File srcFile = new File(src, file);
               File destFile = new File(dest, file);
               this.copyFolder(srcFile, destFile);
            }

            if (existed) {
               this.log.info("Overwrote folder " + dest.getAbsolutePath());
            } else {
               this.log.info("Copied folder " + dest.getAbsolutePath());
            }
         } else {
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dest);
            boolean existed2 = dest.exists();
            byte[] buffer = new byte[1024];

            while((i = in.read(buffer)) > 0) {
               out.write(buffer, 0, i);
            }

            in.close();
            out.close();
            if (existed2) {
               this.log.info("Overwrote file " + dest.getName());
            } else {
               this.log.info("Copied file " + dest.getName());
            }
         }

      }
   }
}
