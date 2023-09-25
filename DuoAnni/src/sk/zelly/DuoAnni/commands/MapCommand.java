package sk.zelly.DuoAnni.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import sk.zelly.DuoAnni.Annihilation;
import sk.zelly.DuoAnni.Translator;
import sk.zelly.DuoAnni.maps.MapLoader;
import sk.zelly.DuoAnni.maps.VoidGenerator;

public class MapCommand implements CommandExecutor {
   private MapLoader loader;
   private Annihilation plugin;

   public MapCommand(Annihilation plugin, MapLoader loader) {
      this.plugin = plugin;
      this.loader = loader;
   }

   public boolean onCommand(final CommandSender sender, Command cmd, String label, String[] args) {
      String cyan = ChatColor.DARK_AQUA.toString();
      String gray = ChatColor.GRAY.toString();
      String red = ChatColor.RED.toString();
      final String green = ChatColor.GREEN.toString();
      String prefix = Translator.change("PREFIX");
      if (sender instanceof Player) {
         Player p = (Player)sender;
         if (args.length == 2) {
            if (args[0].equalsIgnoreCase("edit")) {
               if (p.hasPermission("annihilation.map.edit")) {
                  this.loader.loadMap(args[1]);
                  WorldCreator wc = new WorldCreator(args[1]);
                  wc.generator(new VoidGenerator());
                  Bukkit.createWorld(wc);
                  sender.sendMessage(String.valueOf(String.valueOf(green)) + "Map " + args[1] + " loaded for editing.");
                  if (sender instanceof Player) {
                     sender.sendMessage(String.valueOf(String.valueOf(green)) + "Teleporting...");
                     World w = Bukkit.getWorld(args[1]);
                     Location loc = w.getSpawnLocation();
                     loc.setY((double)w.getHighestBlockYAt(loc));
                     ((Player)sender).teleport(loc);
                  }
               } else {
                  sender.sendMessage(String.valueOf(String.valueOf(prefix)) + red + Translator.string("ERROR_PLAYER_NOPERMISSION"));
               }

               return true;
            }

            if (args[0].equalsIgnoreCase("save")) {
               if (p.hasPermission("annihilation.map.save")) {
                  if (Bukkit.getWorld(args[1]) != null) {
                     Bukkit.getWorld(args[1]).save();
                     final String mapName = args[1];
                     Bukkit.getScheduler().runTaskLater(this.plugin, new Runnable() {
                        public void run() {
                           sender.sendMessage(String.valueOf(String.valueOf(green)) + "Map " + mapName + " saved.");
                           MapCommand.this.loader.saveMap(mapName);
                        }
                     }, 40L);
                  }
               } else {
                  sender.sendMessage(String.valueOf(String.valueOf(prefix)) + red + Translator.string("ERROR_PLAYER_NOPERMISSION"));
               }

               return true;
            }
         }

         sender.sendMessage(String.valueOf(String.valueOf(prefix)) + red + "Syntax: /map <save/edit> <name>");
      } else {
         sender.sendMessage(String.valueOf(String.valueOf(prefix)) + red + Translator.string("ERROR_CONSOLE_PLAYERCOMMAND"));
      }

      return true;
   }
}
