package cz.twixout225.Annihilation.listeners.PlayerListeners;

import cz.twixout225.Annihilation.Annihilation;
import cz.twixout225.Annihilation.Translator;
import cz.twixout225.Annihilation.Util;
import cz.twixout225.Annihilation.bar.ActionBar;
import cz.twixout225.Annihilation.manager.PlayerSerializer;
import cz.twixout225.Annihilation.object.GameTeam;
import cz.twixout225.Annihilation.object.Kit;
import cz.twixout225.Annihilation.object.PlayerMeta;
import java.io.File;
import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;

public class JoinListener implements Listener {
   private Annihilation plugin;
   private static String prefix;

   public JoinListener(Annihilation pl) {
      this.plugin = pl;
      prefix = this.plugin.prefix;
   }

   public void setMetadata(Player player, String key, Object value, Plugin plugin) {
      player.setMetadata(key, new FixedMetadataValue(plugin, value));
   }

   @EventHandler
   public void onPlayerJoin(PlayerJoinEvent e) {
      e.setJoinMessage("");
      final Player player = e.getPlayer();
      String fullname = ChatColor.RESET + player.getName();
      if (fullname.length() > 15) {
         fullname.substring(0, 15);
      }

      player.setPlayerListName(fullname);
      String uuid = player.getName();
      if (this.plugin.getNpcPlayers().containsKey(uuid)) {
         player.kickPlayer("Your NPC is still alive!");
      } else {
         if (!this.plugin.getJoiningPlayers().containsKey(player)) {
            this.plugin.getJoiningPlayers().put(player, uuid);
         }

         player.sendMessage(String.valueOf(String.valueOf(Translator.change("PREFIX"))) + Translator.change("WELCOME_TO_ANNIHILATION"));
         if (this.plugin.useMysql) {
            this.plugin.getDatabaseHandler().query("INSERT IGNORE INTO `annihilation` (`username`, `kills`, `deaths`, `wins`, `losses`, `nexus_damage`) VALUES ('" + uuid + "', '0', '0', '0', '0', '0');");
         }

         if (this.plugin.getPhase() == 0 && this.plugin.getVotingManager().isRunning()) {
            ActionBar.sendActionBar(player, Translator.change("WELCOME_TO_ANNIHILATION"));
            this.plugin.checkStarting();
         }

         PlayerMeta meta = PlayerMeta.getMeta(player);
         player.teleport(this.plugin.getMapManager().getLobbySpawnPoint());
         PlayerInventory inv = player.getInventory();
         inv.setHelmet((ItemStack)null);
         inv.setChestplate((ItemStack)null);
         inv.setLeggings((ItemStack)null);
         inv.setBoots((ItemStack)null);
         player.getInventory().clear();
         Iterator var8 = player.getActivePotionEffects().iterator();

         while(var8.hasNext()) {
            PotionEffect effect = (PotionEffect)var8.next();
            player.removePotionEffect(effect.getType());
         }

         player.setLevel(0);
         player.setExp(0.0F);
         player.setSaturation(20.0F);
         Util.giveClassSelector(player);
         player.updateInventory();
         if (!Util.firstKit.containsKey(player)) {
            Util.firstKit.put(player, true);
         }

         this.plugin.getSignHandler().updateSigns(meta.getTeam());
         this.plugin.getScoreboardHandler().update();
         Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
            public void run() {
               if (player.isOnline()) {
                  JoinListener.this.reJoinPlayer(player);
               }

            }
         }, 40L);
         this.setMetadata(player, prefix, "a", this.plugin);
         player.sendMessage("");
         player.sendMessage("§8§m----------------------------------------------------");
         player.sendMessage("");
         player.sendMessage("§8- §cDuoAnnihilation §7is modification of Annihilation for 2 teams.");
         player.sendMessage("§7  Goal is to destroy other teams nexus.");
         player.sendMessage("");
         player.sendMessage("§8§m----------------------------------------------------");
         ActionBar.sendActionBar(player, "DuoAnni by Zelly");
      }
   }

   public void reJoinPlayer(Player p) {
      if (p != null) {
         if (p.isOnline()) {
            String playerName = p.getName();
            if (!Util.playerPlayed(p)) {
               if (this.plugin.getPhase() <= this.plugin.lastJoinPhase) {
                  if (this.plugin.getJoiningPlayers().containsKey(p)) {
                     this.plugin.getJoiningPlayers().remove(p);
                  }

                  p.updateInventory();
                  return;
               }

               if (p.hasPermission("anni.phase.bypass")) {
                  return;
               }

               if (p != null && p.isOnline()) {
                  p.kickPlayer(ChatColor.RED + "You cannot join to this phase!");
               }
            } else {
               File playerdataFile = new File("plugins/FBDuoAnni/users/" + playerName + ".yml");
               YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(playerdataFile);
               p.getInventory().clear();
               PlayerSerializer.ConfigToPlayer(p, yamlConfiguration);
               PlayerMeta meta = PlayerMeta.getMeta(p);
               if (meta.getTeam().getPlayers().size() >= meta.getTeam().getMaxPlayers()) {
                  p.sendMessage(String.valueOf(String.valueOf(Translator.change("PREFIX"))) + ChatColor.RED + Translator.string("ERROR_GAME_TEAMFULL"));
                  return;
               }

               String fullname;
               if (PlayerSerializer.isKilled(playerName)) {
                  if (this.plugin.kitsToGive.containsKey(p.getName())) {
                     meta.setKit((Kit)this.plugin.kitsToGive.get(p.getName()));
                     this.plugin.kitsToGive.remove(p.getName());
                  }

                  meta.getKit().give(p, meta.getTeam());
                  meta.setAlive(true);
                  p.setCompassTarget(meta.getTeam().getNexus().getLocation());
                  p.setGameMode(GameMode.SURVIVAL);
                  p.setHealth(20.0D);
                  p.setFoodLevel(20);
                  p.setSaturation(20.0F);
                  p.updateInventory();
                  p.teleport(meta.getTeam().getRandomSpawn());
                  p.sendMessage(String.valueOf(Translator.change("PREFIX")) + Translator.change("NPC_JOIN_KILLED"));
                  fullname = meta.getTeam().getChatColor(meta.getTeam()) + p.getName();
                  if (fullname.length() > 15) {
                     fullname.substring(0, 15);
                  }

                  p.setPlayerListName(fullname);
                  if (this.plugin.getJoiningPlayers().containsKey(p)) {
                     this.plugin.getJoiningPlayers().remove(p);
                  }

                  return;
               }

               PlayerSerializer.RetorePlayer(p);
               p.updateInventory();
               meta = PlayerMeta.getMeta(p);
               if (p == null) {
                  return;
               }

               if (meta == null) {
                  p.kickPlayer(String.valueOf(Translator.change("PREFIX")) + ChatColor.RED + "Error player not exists!");
                  return;
               }

               if (meta.getTeam() == GameTeam.NONE) {
                  p.kickPlayer(String.valueOf(Translator.change("PREFIX")) + ChatColor.RED + "Nexus is destroyed or team is full!");
                  return;
               }

               p.teleport(meta.getTeam().getRandomSpawn());
               p.sendMessage(String.valueOf(Translator.change("PREFIX")) + Translator.change("NPC_JOIN_RESUMED"));
               p.playSound(p.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
               fullname = meta.getTeam().getChatColor(meta.getTeam()) + p.getName();
               if (fullname.length() > 15) {
                  fullname.substring(0, 15);
               }

               p.setPlayerListName(fullname);
               this.plugin.getSignHandler().updateSigns(meta.getTeam());
               this.plugin.getScoreboardHandler().update();
               p.setGameMode(GameMode.SURVIVAL);
               p.updateInventory();
               if (this.plugin.getJoiningPlayers().containsKey(p)) {
                  this.plugin.getJoiningPlayers().remove(p);
               }
            }

         }
      }
   }
}
