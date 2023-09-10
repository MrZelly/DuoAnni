package cz.twixout225.Annihilation.manager;

import cz.twixout225.Annihilation.object.GameTeam;
import java.util.HashMap;
import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class ScoreboardManager {
   public HashMap<String, Score> scores = new HashMap();
   public HashMap<String, Team> teams = new HashMap();
   public Scoreboard sb;
   public Objective obj;

   public void update() {
      Iterator var2 = Bukkit.getOnlinePlayers().iterator();

      while(var2.hasNext()) {
         Player sb1 = (Player)var2.next();
         sb1.setScoreboard(this.sb);
      }

   }

   public void resetScoreboard(String objName) {
      this.sb = null;
      this.obj = null;
      this.scores.clear();
      this.teams.clear();
      Iterator var3 = Bukkit.getOnlinePlayers().iterator();

      while(var3.hasNext()) {
         Player sb2 = (Player)var3.next();
         sb2.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
      }

      this.sb = Bukkit.getScoreboardManager().getNewScoreboard();
      (this.obj = this.sb.registerNewObjective("anni", "dummy")).setDisplaySlot(DisplaySlot.SIDEBAR);
      this.obj.setDisplayName(objName);
      this.setTeam(GameTeam.RED);
      this.setTeam(GameTeam.BLUE);
   }

   public void setTeam(GameTeam t) {
      this.teams.put(t.name(), this.sb.registerNewTeam(t.name()));
      Team sbt = (Team)this.teams.get(t.name());
      sbt.setAllowFriendlyFire(false);
      sbt.setCanSeeFriendlyInvisibles(true);
      sbt.setPrefix(t.color().toString());
   }
}
