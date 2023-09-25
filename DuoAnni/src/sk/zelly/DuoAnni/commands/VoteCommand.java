package sk.zelly.DuoAnni.commands;

import java.util.Iterator;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import sk.zelly.DuoAnni.Translator;
import sk.zelly.DuoAnni.manager.VotingManager;

public class VoteCommand implements CommandExecutor {
   private final VotingManager manager;

   public VoteCommand(VotingManager manager) {
      this.manager = manager;
   }

   public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
      Player player = (Player)sender;
      if (!this.manager.isRunning()) {
         sender.sendMessage(Translator.change("PREFIX") + ChatColor.RED + Translator.string("INFO_COMMAND_VOTING_ENDED"));
      } else if (args.length == 0) {
         this.listMaps(sender);
      } else if (!this.manager.vote(sender, args[0])) {
         sender.sendMessage(Translator.change("PREFIX") + ChatColor.RED + Translator.string("INFO_COMMAND_VOTING_INVALID"));
         this.listMaps(sender);
      }

      return true;
   }

   private void listMaps(CommandSender sender) {
      Player player = (Player)sender;
      sender.sendMessage(ChatColor.GRAY + Translator.string("INFO_COMMAND_VOTING_MAPS"));
      int count = 0;
      Iterator var5 = this.manager.getMaps().values().iterator();

      while(var5.hasNext()) {
         String map = (String)var5.next();
         ++count;
         sender.sendMessage(ChatColor.DARK_GRAY + " - " + ChatColor.AQUA + "[" + count + "] " + ChatColor.GRAY + map);
      }

   }
}
