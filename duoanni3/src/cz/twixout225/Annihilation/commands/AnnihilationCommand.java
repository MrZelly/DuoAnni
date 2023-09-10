package cz.twixout225.Annihilation.commands;

import cz.twixout225.Annihilation.Annihilation;
import cz.twixout225.Annihilation.Translator;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class AnnihilationCommand implements CommandExecutor {
   private Annihilation plugin;

   public AnnihilationCommand(Annihilation plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
      String cyan = ChatColor.DARK_AQUA.toString();
      String white = ChatColor.WHITE.toString();
      String gray = ChatColor.GRAY.toString();
      String red = ChatColor.RED.toString();
      String gold = ChatColor.GOLD.toString();
      String yellow = ChatColor.YELLOW.toString();
      String dgray = ChatColor.DARK_GRAY.toString();
      String green = ChatColor.GREEN.toString();
      String prefix = Translator.change("PREFIX");
      if (args.length == 0) {
         if (sender.hasPermission("anni.admin")) {
            sender.sendMessage(prefix + yellow + "This server running Annihilation v1.0 developed by Shutgorgon");
            sender.sendMessage(prefix + gray + "Command Help:");
            sender.sendMessage(prefix + gray + "/anni " + dgray + "-" + white + " Show plugin information.");
            sender.sendMessage(prefix + gray + "/anni start " + dgray + "-" + white + " Begin the game.");
         } else {
            sender.sendMessage(prefix + ChatColor.RED + "(!)");
         }
      }

      if (args.length == 1) {
         if (args[0].equalsIgnoreCase("start")) {
            if (sender.hasPermission("annihilation.command.start")) {
               if (!this.plugin.startTimer()) {
                  sender.sendMessage(String.valueOf(String.valueOf(prefix)) + red + "The game has already started");
               } else {
                  sender.sendMessage(String.valueOf(String.valueOf(prefix)) + green + "The game has been started!");
               }
            } else {
               sender.sendMessage(String.valueOf(String.valueOf(prefix)) + red + "You cannot use this command!");
            }
         } else if (args[0].equalsIgnoreCase("language")) {
            if (args[1].equalsIgnoreCase("english")) {
               return false;
            }

            if (args[1].equalsIgnoreCase("czech")) {
               return false;
            }

            if (args[1].equalsIgnoreCase("slovak")) {
               return false;
            }

            if (args[1].equalsIgnoreCase("auto")) {
               return false;
            }
         }
      }

      return false;
   }
}
