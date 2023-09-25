package sk.zelly.DuoAnni.listeners;

import java.util.ArrayList;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand;
import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand.EnumClientCommand;
import sk.zelly.DuoAnni.Annihilation;
import sk.zelly.DuoAnni.Translator;
import sk.zelly.DuoAnni.Util;
import sk.zelly.DuoAnni.chat.ChatUtil;
import sk.zelly.DuoAnni.object.GameTeam;
import sk.zelly.DuoAnni.object.Kit;
import sk.zelly.DuoAnni.object.PlayerMeta;
import sk.zelly.DuoAnni.stats.StatType;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.PlayerInventory;

public class PlayerListener implements Listener {
   private final Annihilation plugin;
   public ArrayList<EnchantingInventory> inventories = new ArrayList();
   private PlayerDeathEvent e;

   public PlayerListener(Annihilation plugin) {
      this.plugin = plugin;
   }

   @EventHandler
   public void onClick(InventoryClickEvent e) {
      if (this.plugin.getPhase() < 1 && e.getInventory() instanceof PlayerInventory) {
         e.setCancelled(true);
      }

   }

   @EventHandler
   public void onPlayerRespawn(PlayerRespawnEvent e) {
      Player player = e.getPlayer();
      PlayerMeta meta = PlayerMeta.getMeta(player);
      World world = Bukkit.getWorld(player.getWorld().getName());
      if (this.plugin.getJoiningPlayers().containsKey(player)) {
         this.plugin.getJoiningPlayers().remove(player);
      }

      if (meta.isAlive()) {
         if (this.plugin.kitsToGive.containsKey(e.getPlayer().getName()) && this.plugin.kitsToGive.get(e.getPlayer().getName()) != meta.getKit()) {
            if (this.plugin.kitsToGive.get(e.getPlayer().getName()) == Kit.CIVILIAN) {
               meta.setKit((Kit)this.plugin.kitsToGive.get(e.getPlayer().getName()));
               this.plugin.kitsToGive.remove(e.getPlayer().getName());
               e.setRespawnLocation(meta.getTeam().getRandomSpawn());
               meta.getKit().give(player, meta.getTeam());
               return;
            }

            if (player.hasPermission("annihilation.kits.vip")) {
               meta.setKit((Kit)this.plugin.kitsToGive.get(e.getPlayer().getName()));
               this.plugin.kitsToGive.remove(e.getPlayer().getName());
               e.setRespawnLocation(meta.getTeam().getRandomSpawn());
               meta.getKit().give(player, meta.getTeam());
               return;
            }

            if (!(Annihilation.getInstance().getEconomy().getBalance(player.getName()) < (double)Annihilation.getInstance().getConfig().getInt("kit-cost"))) {
               Annihilation.getInstance().getEconomy().withdrawPlayer(player.getName(), (double)Annihilation.getInstance().getConfig().getInt("kit-cost"));
               meta.setKit((Kit)this.plugin.kitsToGive.get(e.getPlayer().getName()));
               this.plugin.kitsToGive.remove(e.getPlayer().getName());
               e.setRespawnLocation(meta.getTeam().getRandomSpawn());
               meta.getKit().give(player, meta.getTeam());
               return;
            }

            player.sendMessage("§8[§cDuoAnni§8] §cTento kit stoji §e" + this.plugin.getConfigManager().getConfig("config.yml").getInt("kit-cost") + "§c coinu!");
            player.sendMessage("§8[§cDuoAnni§8] §7You kit was changed to Civilian!");
            meta.setKit(Kit.CIVILIAN);
            this.plugin.kitsToGive.remove(e.getPlayer().getName());
            e.setRespawnLocation(meta.getTeam().getRandomSpawn());
            meta.getKit().give(player, meta.getTeam());
            meta.setKit((Kit)this.plugin.kitsToGive.get(e.getPlayer().getName()));
            this.plugin.kitsToGive.remove(e.getPlayer().getName());
         }

         e.setRespawnLocation(meta.getTeam().getRandomSpawn());
         meta.getKit().give(player, meta.getTeam());
      }
   }

   @EventHandler
   public void onLogin(PlayerLoginEvent e) {
      Player p = e.getPlayer();
      PlayerMeta meta = PlayerMeta.getMeta(p);
      if (this.plugin.getPhase() > 0 && meta != null && meta.getTeam() != null && meta.getTeam() != GameTeam.NONE && !meta.isAlive() && !((Entity)meta).getWorld().getName().equals("lobby") && meta.getTeam() != GameTeam.NONE && !meta.isAlive()) {
         ((Entity)meta).getWorld().getName().equals("world");
      }

      if (this.plugin.getNpcPlayers().containsKey(p.getName())) {
         e.disallow(Result.KICK_OTHER, "Your NPC is still alive!");
      }

   }

   @EventHandler
   public void onPlayerDeath(PlayerDeathEvent e) {
      final Player p = e.getEntity();
      if (this.plugin.getPhase() > 0) {
         PlayerMeta meta = PlayerMeta.getMeta(p);
         if (meta.getTeam() != null && !meta.getTeam().getNexus().isAlive() && meta.getTeam() != GameTeam.NONE) {
            meta.setAlive(false);
         }
      }

      this.plugin.getStatsManager().setValue(StatType.DEATHS, p, this.plugin.getStatsManager().getStat(StatType.DEATHS, p) + 1);
      if (p.getKiller() != null && !p.getKiller().equals(p)) {
         Player killer = p.getKiller();
         PlayerMeta.addXp(killer, this.plugin.getConfigManager().getConfig("config.yml").getInt("Exp-player-kill"));
         PlayerMeta.addMoney(killer, this.plugin.getConfigManager().getConfig("config.yml").getDouble("Money-player-kill"));
         this.plugin.getStatsManager().incrementStat(StatType.KILLS, killer);
         e.setDeathMessage(ChatUtil.formatDeathMessage(p, p.getKiller(), e.getDeathMessage()));
      } else {
         e.setDeathMessage(ChatUtil.formatDeathMessage(p, e.getDeathMessage()));
      }

      e.setDroppedExp(0);
      Bukkit.getScheduler().runTaskLater(this.plugin, new Runnable() {
         public void run() {
            PacketPlayInClientCommand in = new PacketPlayInClientCommand(EnumClientCommand.PERFORM_RESPAWN);
            EntityPlayer cPlayer = ((CraftPlayer)p).getHandle();
            cPlayer.playerConnection.a(in);
         }
      }, 1L);
      Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
         public void run() {
            Util.giveEffect(p);
         }
      }, 5L);
   }

   private void addHeart(Player player) {
      double maxHealth = player.getMaxHealth();
      if (maxHealth < 30.0D) {
         double newMaxHealth = maxHealth + 2.0D;
         player.setMaxHealth(newMaxHealth);
         player.setHealth(player.getHealth() + 2.0D);
      }

   }

   @EventHandler
   public void onDrop(PlayerDropItemEvent event) {
      Player p = event.getPlayer();
      if (this.plugin.getPhase() == 0 && this.plugin.getVotingManager().isRunning()) {
         event.setCancelled(true);
      }

      if (event.getPlayer().getWorld().getName().equals("lobby")) {
         event.setCancelled(true);
      }

   }

   @EventHandler
   public void onModel(PlayerAnimationEvent e) {
      e.getHandlers();
      HandlerList.bakeAll();
   }

   @EventHandler
   public void inventoryClickEventI(InventoryClickEvent event) {
      if (event.getInventory() instanceof AnvilInventory) {
         if (event.getSlotType() == SlotType.RESULT) {
            if (event.getCurrentItem().getType() == Material.GOLD_INGOT) {
               event.setCancelled(true);
               Player player = (Player)event.getWhoClicked();
               player.sendMessage(String.valueOf(String.valueOf(String.valueOf(String.valueOf(Translator.change("PREFIX"))))) + ChatColor.RED + "You can´t rename Gold Ingot!");
            }

         }
      }
   }

   @EventHandler
   public void onPlayerPortal(PlayerPortalEvent e) {
      final Player player = e.getPlayer();
      PlayerMeta meta = PlayerMeta.getMeta(player);
      player.teleport(meta.getTeam().getRandomSpawn());
      Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
         public void run() {
            Util.showClassSelector(player);
         }
      }, 20L);
      if (!this.plugin.getPortalPlayers().containsKey(player)) {
         this.plugin.getPortalPlayers().put(player, player.getName());
      }

   }

   @EventHandler
   public void onFoodLevelChange(FoodLevelChangeEvent event) {
      if (event.getEntity().getWorld().getName().equals("lobby")) {
         event.setCancelled(true);
         event.setFoodLevel(20);
      }

   }
}
