package cz.twixout225.Annihilation.manager;

import cz.twixout225.Annihilation.Annihilation;
import cz.twixout225.Annihilation.Translator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Team;

public class VotingManager {
   private final Annihilation plugin;
   private static final HashMap<Integer, String> maps = new HashMap();
   public static final HashMap<String, String> votes = new HashMap();
   private boolean running = false;
   public static List<String> vmaps = new ArrayList();

   public VotingManager(Annihilation plugin) {
      this.plugin = plugin;
   }

   public void start() {
      maps.clear();
      votes.clear();
      int count = 0;
      int size = this.plugin.getMapManager().getRandomMaps().size();
      Iterator var4 = this.plugin.getMapManager().getRandomMaps().iterator();

      while(var4.hasNext()) {
         String map = (String)var4.next();
         ++count;
         --size;
         maps.put(count, map);
         this.plugin.getScoreboardHandler().scores.put(map, this.plugin.getScoreboardHandler().obj.getScore(Bukkit.getOfflinePlayer(map)));
         ((Score)this.plugin.getScoreboardHandler().scores.get(map)).setScore(size);
         this.plugin.getScoreboardHandler().teams.put(map, this.plugin.getScoreboardHandler().sb.registerNewTeam(map));
         ((Team)this.plugin.getScoreboardHandler().teams.get(map)).addPlayer(Bukkit.getOfflinePlayer(map));
         ((Team)this.plugin.getScoreboardHandler().teams.get(map)).setPrefix(ChatColor.AQUA + "[" + ChatColor.AQUA + count + ChatColor.AQUA + "] " + ChatColor.GRAY);
         ((Team)this.plugin.getScoreboardHandler().teams.get(map)).setSuffix(ChatColor.RED + " » " + ChatColor.GREEN + "0 Votes");
         vmaps.add(map);
      }

      this.running = true;
      this.plugin.getScoreboardHandler().update();
   }

   public boolean vote(CommandSender voter, String vote) {
      String map;
      Iterator var5;
      try {
         int val = Integer.parseInt(vote);
         if (maps.containsKey(val)) {
            vote = (String)maps.get(val);
            var5 = maps.values().iterator();

            while(var5.hasNext()) {
               map = (String)var5.next();
               if (vote.equalsIgnoreCase(map)) {
                  votes.put(voter.getName(), map);
                  voter.sendMessage(Translator.change("PREFIX") + ChatColor.GRAY + "You voted for " + ChatColor.YELLOW + map);
                  this.updateScoreboard();
                  return true;
               }
            }
         }
      } catch (NumberFormatException var6) {
         var5 = maps.values().iterator();

         while(var5.hasNext()) {
            map = (String)var5.next();
            if (vote.equalsIgnoreCase(map)) {
               votes.put(voter.getName(), map);
               voter.sendMessage(Translator.change("PREFIX") + ChatColor.GRAY + "You voted for " + ChatColor.YELLOW + map);
               this.updateScoreboard();
               return true;
            }
         }
      }

      voter.sendMessage(Translator.change("PREFIX") + ChatColor.YELLOW + vote + ChatColor.RED + " is not a valid map");
      return false;
   }

   public String getWinner() {
      String winner = null;
      Integer highest = -1;
      Iterator var4 = maps.values().iterator();

      while(var4.hasNext()) {
         String map = (String)var4.next();
         int totalVotes = countVotes(map);
         if (totalVotes > highest) {
            winner = map;
            highest = totalVotes;
         }
      }

      return winner;
   }

   public void end() {
      this.running = false;
   }

   public boolean isRunning() {
      return this.running;
   }

   public HashMap<Integer, String> getMaps() {
      return maps;
   }

   public static int countVotes(String map) {
      int total = 0;
      Iterator var3 = votes.values().iterator();

      while(var3.hasNext()) {
         String vote = (String)var3.next();
         if (vote.equals(map)) {
            ++total;
         }
      }

      return total;
   }

   private void updateScoreboard() {
      Iterator var2 = maps.values().iterator();

      while(var2.hasNext()) {
         String map = (String)var2.next();
         ((Team)this.plugin.getScoreboardHandler().teams.get(map)).setSuffix(ChatColor.RED + " » " + ChatColor.GREEN + countVotes(map) + " Vote" + (countVotes(map) == 1 ? "" : "s"));
      }

   }
}
