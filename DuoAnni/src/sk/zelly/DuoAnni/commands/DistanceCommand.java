package sk.zelly.DuoAnni.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import sk.zelly.DuoAnni.Annihilation;
import sk.zelly.DuoAnni.Translator;
import sk.zelly.DuoAnni.object.GameTeam;
import sk.zelly.DuoAnni.object.PlayerMeta;

public class DistanceCommand implements CommandExecutor {
   private Annihilation plugin;

   public DistanceCommand(Annihilation instance) {
      this.plugin = instance;
   }

   public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
      if (sender instanceof Player) {
         Player p = (Player)sender;
         if (this.plugin.getPhase() == 0) {
            p.sendMessage(String.valueOf(String.valueOf(Translator.change("PREFIX"))) + ChatColor.RED + Translator.string("ERROR_GAME_NOTSTARTED"));
            return false;
         }

         if (PlayerMeta.getMeta(p).getTeam() == GameTeam.NONE) {
            p.sendMessage(String.valueOf(String.valueOf(Translator.change("PREFIX"))) + ChatColor.RED + Translator.string("ERROR_PLAYER_NOTEAM"));
            return false;
         }

         p.sendMessage(ChatColor.GRAY + "=========[ " + ChatColor.DARK_AQUA.toString() + Translator.string("INFO_COMMAND_DISTANCE") + ChatColor.GRAY + " ]=========");
         GameTeam[] values;
         int length = (values = GameTeam.values()).length;

         for(int i = 0; i < length; ++i) {
            GameTeam t = values[i];
            if (t != GameTeam.NONE) {
               this.showTeam(p, t);
            }
         }

         p.sendMessage(ChatColor.GRAY + "============================");
      } else {
         sender.sendMessage(ChatColor.RED + Translator.string("ERROR_CONSOLE_PLAYERCOMMAND"));
      }

      return true;
   }

   private void showTeam(Player p, GameTeam t) {
      try {
         if (t.getNexus() != null && t.getNexus().getHealth() > 0) {
            p.sendMessage(String.valueOf(String.valueOf(t.coloredName())) + ChatColor.GRAY + " Nexus Distance: " + ChatColor.WHITE + (int)p.getLocation().distance(t.getNexus().getLocation()) + ChatColor.GRAY + " Blocks");
         }
      } catch (IllegalArgumentException var4) {
      }

   }
}
