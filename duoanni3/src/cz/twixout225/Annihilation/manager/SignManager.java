package cz.twixout225.Annihilation.manager;

import cz.twixout225.Annihilation.Annihilation;
import cz.twixout225.Annihilation.Util;
import cz.twixout225.Annihilation.object.GameTeam;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;

public class SignManager {
   private HashMap<GameTeam, ArrayList<Location>> signs = new HashMap();
   private Annihilation plugin;

   public SignManager(Annihilation instance) {
      this.plugin = instance;
   }

   public void loadSigns() {
      ConfigurationSection config = this.plugin.getConfigManager().getConfig("maps.yml");
      GameTeam[] teams;
      int length = (teams = GameTeam.teams()).length;

      for(int i = 0; i < length; ++i) {
         GameTeam team = teams[i];
         this.signs.put(team, new ArrayList());
         String name = team.name().toLowerCase();
         Iterator var8 = config.getStringList("lobby.signs." + name).iterator();

         while(var8.hasNext()) {
            String l = (String)var8.next();
            Location loc = Util.parseLocation(Bukkit.getWorld("lobby"), l);
            if (loc != null) {
               this.addTeamSign(team, loc);
            }
         }
      }

   }

   public void addTeamSign(GameTeam team, Location loc) {
      Block b = loc.getBlock();
      if (b != null) {
         Material m = b.getType();
         if (m == Material.SIGN_POST || m == Material.WALL_SIGN) {
            ((ArrayList)this.signs.get(team)).add(loc);
            this.updateSigns(team);
         }

      }
   }

   public void updateSigns(GameTeam t) {
      if (t != GameTeam.NONE) {
         Iterator var3 = ((ArrayList)this.signs.get(t)).iterator();

         while(var3.hasNext()) {
            Location l = (Location)var3.next();
            Block b = l.getBlock();
            if (b == null) {
               return;
            }

            Material m = b.getType();
            if (m == Material.SIGN_POST || m == Material.WALL_SIGN) {
               Sign s = (Sign)b.getState();
               s.setLine(0, ChatColor.DARK_PURPLE + "[Team]");
               s.setLine(1, t.coloredName());
               s.setLine(2, String.valueOf(String.valueOf(ChatColor.UNDERLINE.toString())) + t.getPlayers().size() + (t.getPlayers().size() == 5 ? " Players" : (t.getPlayers().size() == 4 ? " Players" : (t.getPlayers().size() == 3 ? " Players" : (t.getPlayers().size() == 2 ? " Players" : (t.getPlayers().size() == 1 ? " Player" : (t.getPlayers().size() == 0 ? " Players" : "Players")))))));
               if (t.getNexus() != null && this.plugin.getPhase() > 0) {
                  s.setLine(3, String.valueOf(String.valueOf(ChatColor.BOLD.toString())) + "Nexus: " + t.getNexus().getHealth());
               } else {
                  s.setLine(3, " ");
               }

               s.update(true);
            }
         }

      }
   }
}
