package sk.zelly.DuoAnni.commands;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import sk.zelly.DuoAnni.Translator;
import sk.zelly.DuoAnni.stats.StatType;
import sk.zelly.DuoAnni.stats.StatsManager;

public class StatsCommand implements CommandExecutor {
   private StatsManager manager;

   public StatsCommand(StatsManager manager) {
      this.manager = manager;
   }

   public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
      if (sender instanceof Player) {
         if (args.length > 0) {
            List<StatType> types = new LinkedList(Arrays.asList(StatType.values()));
            Iterator iterator = types.iterator();

            while(iterator.hasNext()) {
               StatType type = (StatType)iterator.next();
               boolean keep = false;
               String[] var12 = args;
               int var11 = args.length;

               for(int var10 = 0; var10 < var11; ++var10) {
                  String arg = var12[var10];
                  if (type.name().toLowerCase().contains(arg.toLowerCase())) {
                     keep = true;
                  }
               }

               if (!keep) {
                  iterator.remove();
               }
            }

            this.listStats((Player)sender, (StatType[])types.toArray(new StatType[types.size()]));
         } else {
            this.listStats((Player)sender);
         }
      } else {
         sender.sendMessage(ChatColor.RED + Translator.string("ERROR_CONSOLE_PLAYERCOMMAND"));
      }

      return true;
   }

   private void listStats(Player player) {
      this.listStats(player, StatType.values());
   }

   private void listStats(Player player, StatType[] stats) {
      String GRAY = ChatColor.GRAY.toString();
      String DARK_AQUA = ChatColor.DARK_AQUA.toString();
      String AQUA = ChatColor.AQUA.toString();
      player.sendMessage(String.valueOf(String.valueOf(GRAY)) + "=========[ " + DARK_AQUA + Translator.string("INFO_COMMAND_STATS") + GRAY + " ]=========");
      StatType[] var9 = stats;
      int var8 = stats.length;

      for(int var7 = 0; var7 < var8; ++var7) {
         StatType stat = var9[var7];
         if (stat != null) {
            String name = WordUtils.capitalize(stat.name().toLowerCase().replace('_', ' '));
            player.sendMessage(String.valueOf(String.valueOf(DARK_AQUA)) + name + ": " + AQUA + this.manager.getStat(stat, player));
         }
      }

      player.sendMessage(String.valueOf(String.valueOf(GRAY)) + "=========================");
   }
}
