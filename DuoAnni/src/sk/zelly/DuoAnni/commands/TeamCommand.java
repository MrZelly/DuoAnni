package sk.zelly.DuoAnni.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import sk.zelly.DuoAnni.Annihilation;
import sk.zelly.DuoAnni.Translator;

public class TeamCommand implements CommandExecutor {
   private final Annihilation plugin;

   public TeamCommand(Annihilation plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
      if (args.length == 0) {
         this.plugin.listTeams(sender);
      } else if (!(sender instanceof Player)) {
         Player p = (Player)sender;
         sender.sendMessage(Translator.string("ERROR_CONSOLE_PLAYERCOMMAND"));
      } else {
         this.plugin.joinTeam((Player)sender, args[0]);
      }

      return true;
   }
}
