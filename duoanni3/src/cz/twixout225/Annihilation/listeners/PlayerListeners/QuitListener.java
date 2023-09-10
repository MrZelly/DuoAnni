package cz.twixout225.Annihilation.listeners.PlayerListeners;

import cz.twixout225.Annihilation.Annihilation;
import cz.twixout225.Annihilation.Util;
import cz.twixout225.Annihilation.manager.PlayerSerializer;
import cz.twixout225.Annihilation.object.GameTeam;
import cz.twixout225.Annihilation.object.PlayerMeta;
import java.io.File;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class QuitListener implements Listener {
   private Annihilation plugin;

   public QuitListener(Annihilation pl) {
      this.plugin = pl;
   }

   @EventHandler
   public void onQuit(PlayerQuitEvent e) {
      Player player = e.getPlayer();
      if (player != null) {
         PlayerMeta meta = PlayerMeta.getMeta(player);
         final String playerName = player.getName();
         ItemStack[] items = player.getInventory().getContents();
         Double health = player.getHealth();
         ItemStack[] armor = player.getInventory().getArmorContents();
         Float satur = player.getSaturation();
         int level = player.getLevel();
         int gm = player.getGameMode().getValue();
         int food = player.getFoodLevel();
         float exauth = player.getExhaustion();
         float exp = player.getExp();
         boolean bol = true;
         GameTeam team = meta.getTeam();
         e.setQuitMessage((String)null);
         if (this.plugin.getPhase() == 0) {
            meta.getTeam().getPlayers().remove(player);
            meta.setTeam(GameTeam.NONE);
            PlayerSerializer.delete(playerName);
         }

         if (this.plugin.getPhase() == 0 && !GameTeam.isNull(team)) {
            meta.setTeam(GameTeam.NONE);
         } else if (meta.getTeam() == GameTeam.NONE) {
            PlayerSerializer.delete(playerName);
         } else if (this.plugin.getJoiningPlayers().containsKey(player)) {
            this.plugin.getJoiningPlayers().remove(player);
         } else if (!this.plugin.getNpcPlayers().containsKey(playerName)) {
            if (!player.getWorld().getName().equalsIgnoreCase("lobby")) {
               File file = new File("plugins/Anni/users/" + playerName + ".yml");
               FileConfiguration config = YamlConfiguration.loadConfiguration(file);
               if (!(player.getLocation().getY() <= 0.0D) && !this.isFallingToVoid(player)) {
                  String w = player.getWorld().getName();
                  this.savePlayer(playerName, items, armor, health, satur, level, gm, food, exauth, exp, team, true, w);
                  World world = Bukkit.getWorld(player.getWorld().getName());
                  final Zombie z = (Zombie)world.spawn(player.getLocation(), Zombie.class);
                  if (!this.plugin.getNpcPlayers().containsKey(playerName)) {
                     this.plugin.getNpcPlayers().put(playerName, playerName);
                  }

                  z.setBaby(false);
                  z.setVillager(false);
                  z.setHealth(player.getHealth());
                  z.setCanPickupItems(false);
                  z.setCustomName(meta.getTeam().getChatColor(team) + playerName);
                  ItemStack hand = player.getInventory().getItemInHand();
                  ItemStack[] armors = player.getInventory().getArmorContents();
                  z.getEquipment().setItemInHand(hand);
                  z.getEquipment().setArmorContents(armors);
                  if (!this.plugin.getZombies().containsValue(z.getCustomName())) {
                     this.plugin.getZombies().put(z.getCustomName(), z);
                     Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
                        public void run() {
                           if (QuitListener.this.plugin.getNpcPlayers().containsKey(playerName)) {
                              QuitListener.this.plugin.getNpcPlayers().remove(playerName);
                           }

                           if (QuitListener.this.plugin.getZombies().containsKey(z.getCustomName())) {
                              QuitListener.this.plugin.getZombies().remove(z);
                              z.remove();
                           } else {
                              if (z.getCustomName() == null) {
                                 return;
                              }

                              String zName = Util.replaceTeamColor(z.getCustomName());
                              PlayerSerializer.removeItems(zName);
                              if (z != null) {
                                 z.remove();
                              }
                           }

                        }
                     }, 400L);
                  }

               } else {
                  PlayerSerializer.removeItems(playerName);
               }
            }
         }
      }
   }

   @EventHandler
   public void onKick(PlayerKickEvent e) {
      if (this.plugin.getJoiningPlayers().containsKey(e.getPlayer())) {
         this.plugin.getJoiningPlayers().remove(e.getPlayer());
      }

      if (e.getReason().equals(ChatColor.RED + "ANNIHILATION-TRIGGER-KICK-01")) {
         e.setReason(ChatColor.RED + "ERROR_GAME_PHASE");
         e.setLeaveMessage("");
      }

   }

   public void savePlayer(String playerName, ItemStack[] items, ItemStack[] armor, double health, float saturation, int level, int gm, int food, float exhaut, float exp, GameTeam team, boolean bol, String w) {
      PlayerSerializer.PlayerToConfig(playerName, items, armor, health, saturation, level, gm, food, exhaut, exp, team, bol, w);
   }

   public boolean isFallingToVoid(Player p) {
      Location loc = p.getLocation();

      for(int i = p.getLocation().getBlockY(); i >= 0; --i) {
         loc.add(0.0D, -1.0D, 0.0D);
         if (Bukkit.getWorld(loc.getWorld().getName()).getBlockAt(loc).getType() != Material.AIR) {
            return false;
         }
      }

      return true;
   }
}
