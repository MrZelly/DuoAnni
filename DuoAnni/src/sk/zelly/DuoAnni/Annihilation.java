package sk.zelly.DuoAnni;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;
import sk.zelly.DuoAnni.api.CooldownAPI;
import sk.zelly.DuoAnni.api.GameStartEvent;
import sk.zelly.DuoAnni.api.PhaseChangeEvent;
import sk.zelly.DuoAnni.chat.ChatListener;
import sk.zelly.DuoAnni.chat.ChatUtil;
import sk.zelly.DuoAnni.commands.AnnihilationCommand;
import sk.zelly.DuoAnni.commands.DistanceCommand;
import sk.zelly.DuoAnni.commands.MapCommand;
import sk.zelly.DuoAnni.commands.StatsCommand;
import sk.zelly.DuoAnni.commands.TeamCommand;
import sk.zelly.DuoAnni.commands.TeamShortcutCommand;
import sk.zelly.DuoAnni.commands.VoteCommand;
import sk.zelly.DuoAnni.listeners.BossListener;
import sk.zelly.DuoAnni.listeners.ClassAbilityListener;
import sk.zelly.DuoAnni.listeners.CraftingListener;
import sk.zelly.DuoAnni.listeners.EnderChestListener;
import sk.zelly.DuoAnni.listeners.MotdListener;
import sk.zelly.DuoAnni.listeners.PlayerListener;
import sk.zelly.DuoAnni.listeners.ResourceListener;
import sk.zelly.DuoAnni.listeners.SoulboundListener;
import sk.zelly.DuoAnni.listeners.WorldListener;
import sk.zelly.DuoAnni.listeners.ZombieListener;
import sk.zelly.DuoAnni.listeners.PlayerListeners.BlockListener;
import sk.zelly.DuoAnni.listeners.PlayerListeners.DamageListener;
import sk.zelly.DuoAnni.listeners.PlayerListeners.InteractListener;
import sk.zelly.DuoAnni.listeners.PlayerListeners.InventoryListener;
import sk.zelly.DuoAnni.listeners.PlayerListeners.JoinListener;
import sk.zelly.DuoAnni.listeners.PlayerListeners.QuitListener;
import sk.zelly.DuoAnni.manager.BossManager;
import sk.zelly.DuoAnni.manager.ConfigManager;
import sk.zelly.DuoAnni.manager.DatabaseManager;
import sk.zelly.DuoAnni.manager.MapManager;
import sk.zelly.DuoAnni.manager.PhaseManager;
import sk.zelly.DuoAnni.manager.PlayerSerializer;
import sk.zelly.DuoAnni.manager.RestartHandler;
import sk.zelly.DuoAnni.manager.ScoreboardManager;
import sk.zelly.DuoAnni.manager.SignManager;
import sk.zelly.DuoAnni.manager.VotingManager;
import sk.zelly.DuoAnni.maps.MapLoader;
import sk.zelly.DuoAnni.object.BlockObject;
import sk.zelly.DuoAnni.object.Boss;
import sk.zelly.DuoAnni.object.GameTeam;
import sk.zelly.DuoAnni.object.Kit;
import sk.zelly.DuoAnni.object.PlayerMeta;
import sk.zelly.DuoAnni.object.Shop;
import sk.zelly.DuoAnni.stats.StatType;
import sk.zelly.DuoAnni.stats.StatsManager;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Team;

public final class Annihilation extends JavaPlugin {
   public final HashMap<String, Kit> kitsToGive = new HashMap();
   public String prefix;
   public ConfigManager configManager;
   public VotingManager voting;
   private MapManager maps;
   private PhaseManager timer;
   public ResourceListener resources;
   private EnderChestListener enderChests;
   private StatsManager stats;
   private SignManager sign;
   public ScoreboardManager sb;
   private DatabaseManager db;
   private BossManager boss;
   private HashMap<String, Entity> zombies;
   public static HashMap<String, String> messages = new HashMap();
   private HashMap<Player, String> logoutPlayers;
   public boolean useMysql = false;
   public boolean updateAvailable = false;
   public boolean motd = true;
   public String newVersion;
   private HashMap<Player, String> portal;
   private HashMap<String, String> npcP;
   private HashMap<Player, String> joining;
   public int build = 1;
   public int lastJoinPhase = 2;
   public int respawn = 10;
   public boolean runCommand = false;
   public List<String> commands = new ArrayList();
   private static Annihilation anni;
   public String mysqlName = "annihilation";
   private Economy economy;
   private PlayerSerializer serial;
   private ChatUtil cutil;
   public HashMap<Player, BlockObject> crafting;
   private Logger log;
   public Object cabiII;
   private GameTeam player;
   public CooldownAPI cldapi;
   public List<Block> chests = new ArrayList();
   // $FF: synthetic field
   private static int[] $SWITCH_TABLE$cz$twixout225$Annihilation$object$GameTeam;

   static {
      messages = new HashMap();
   }

   public static Annihilation getInstance() {
      return anni;
   }

   public void onEnable() {
      anni = this;
      (this.configManager = new ConfigManager(this)).loadConfigFiles("config.yml", "maps.yml", "shops.yml", "stats.yml", "messages.yml");
      MapLoader mapLoader = new MapLoader(this.getLogger(), this.getDataFolder());
      this.runCommand = this.getConfig().contains("commandsToRunAtEndGame");
      if (this.runCommand) {
         this.commands = this.getConfig().getStringList("commandsToRunAtEndGame");
      } else {
         this.commands = null;
      }

      new Translator(this);
      this.log = Logger.getLogger("Minecraft");
      this.crafting = new HashMap();
      this.maps = new MapManager(this, mapLoader, this.configManager.getConfig("maps.yml"));
      Configuration shops = this.configManager.getConfig("shops.yml");
      new Shop(this, "Weapon", shops);
      new Shop(this, "Brewing", shops);
      this.serial = new PlayerSerializer(this);
      this.stats = new StatsManager(this, this.configManager);
      this.resources = new ResourceListener(this);
      this.enderChests = new EnderChestListener();
      this.sign = new SignManager(this);
      Configuration config = this.configManager.getConfig("config.yml");
      this.timer = new PhaseManager(this, config.getInt("start-delay"), config.getInt("phase-period"));
      this.voting = new VotingManager(this);
      this.sb = new ScoreboardManager();
      this.boss = new BossManager(this);
      this.portal = new HashMap();
      this.zombies = new HashMap();
      this.logoutPlayers = new HashMap();
      this.npcP = new HashMap();
      this.joining = new HashMap();
      PluginManager pm = this.getServer().getPluginManager();
      this.cutil = new ChatUtil(this);
      this.cldapi = new CooldownAPI(this);
      new PlayerMeta(this);
      File messagesFile = new File("plugins/" + this.getDescription().getName() + "/messages.yml");
      YamlConfiguration yml = YamlConfiguration.loadConfiguration(messagesFile);
      Iterator var8 = yml.getKeys(false).iterator();

      String host;
      while(var8.hasNext()) {
         host = (String)var8.next();
         messages.put(host, yml.getString(host));
      }

      this.prefix = Translator.change("PREFIX");
      this.sign.loadSigns();
      this.sb.resetScoreboard(Translator.change("SB_LOBBY_PREFIX"));
      this.build = this.getConfig().getInt("build", 15);
      this.lastJoinPhase = this.getConfig().getInt("lastJoinPhase", 2);
      this.respawn = this.getConfig().getInt("bossRespawnDelay", 10);
      pm.registerEvents(this.resources, this);
      pm.registerEvents(this.enderChests, this);
      pm.registerEvents(new ChatListener(this), this);
      pm.registerEvents(new PlayerListener(this), this);
      pm.registerEvents(new WorldListener(), this);
      pm.registerEvents(new SoulboundListener(), this);
      pm.registerEvents(new CraftingListener(), this);
      pm.registerEvents(new ClassAbilityListener(this), this);
      pm.registerEvents(new BossListener(this), this);
      pm.registerEvents(new MotdListener(this), this);
      pm.registerEvents(new BlockListener(this), this);
      pm.registerEvents(new DamageListener(this), this);
      pm.registerEvents(new InteractListener(this), this);
      pm.registerEvents(new InventoryListener(this), this);
      pm.registerEvents(new JoinListener(this), this);
      pm.registerEvents(new QuitListener(this), this);
      pm.registerEvents(new ZombieListener(this), this);
      pm.registerEvents(new EnderChestListener(), this);
      this.getCommand("annihilation").setExecutor(new AnnihilationCommand(this));
      this.getCommand("stats").setExecutor(new StatsCommand(this.stats));
      this.getCommand("team").setExecutor(new TeamCommand(this));
      this.getCommand("vote").setExecutor(new VoteCommand(this.voting));
      this.getCommand("red").setExecutor(new TeamShortcutCommand());
      this.getCommand("blue").setExecutor(new TeamShortcutCommand());
      this.getCommand("distance").setExecutor(new DistanceCommand(this));
      this.getCommand("map").setExecutor(new MapCommand(this, mapLoader));
      if (config.getString("stats").equalsIgnoreCase("sql")) {
         this.useMysql = true;
      }

      this.motd = config.getBoolean("enableMotd", true);
      if (this.useMysql) {
         host = config.getString("MySQL.host");
         Integer port = config.getInt("MySQL.port");
         String name = config.getString("MySQL.name");
         String user = config.getString("MySQL.user");
         String pass = config.getString("MySQL.pass");
         (this.db = new DatabaseManager(host, port, name, user, pass, this)).query("CREATE TABLE IF NOT EXISTS `" + this.mysqlName + "` ( `username` varchar(17) NOT NULL, " + "`kills` int(17) NOT NULL, `deaths` int(17) NOT NULL, `wins` int(17) NOT NULL, " + "`losses` int(17) NOT NULL, `nexus_damage` int(17) NOT NULL, " + "UNIQUE KEY `username` (`username`) ) ENGINE=InnoDB DEFAULT CHARSET=latin1;");
      } else {
         this.db = new DatabaseManager(this);
      }

      if (this.getServer().getPluginManager().isPluginEnabled("Vault")) {
         VaultHooks.vault = true;
         if (!VaultHooks.instance().setupPermissions()) {
            VaultHooks.vault = false;
            this.getLogger().warning("Unable to load Vault: No permission plugin found.");
         } else if (!VaultHooks.instance().setupChat()) {
            VaultHooks.vault = false;
            this.getLogger().warning("Unable to load Vault: No chat plugin found.");
         } else {
            this.getLogger().info("Vault hook initalized!");
         }
      } else {
         this.getLogger().warning("Vault not found! Permissions features disabled.");
      }

      this.reset();
      ChatUtil.setRoman(this.getConfig().getBoolean("roman", false));
      File invFile = new File("plugins/FBDuoAnni/users");
      if (invFile.isDirectory()) {
         File[] listFiles;
         int length = (listFiles = invFile.listFiles()).length;

         for(int i = 0; i < length; ++i) {
            File f = listFiles[i];
            f.delete();
         }
      }

      if (Bukkit.getPluginManager().getPlugin("Vault") instanceof Vault) {
         RegisteredServiceProvider<Economy> service = Bukkit.getServicesManager().getRegistration(Economy.class);
         if (service != null) {
            this.economy = (Economy)service.getProvider();
         }
      }

   }

   public void onDisable() {
      Iterator var2 = this.getZombies().values().iterator();

      while(var2.hasNext()) {
         Entity e = (Entity)var2.next();
         e.remove();
      }

      this.getZombies().clear();
   }

   public boolean startTimer() {
      if (this.timer.isRunning()) {
         return false;
      } else {
         this.timer.start();
         return true;
      }
   }

   public void loadMap(String map) {
      FileConfiguration config = this.configManager.getConfig("maps.yml");
      ConfigurationSection section = config.getConfigurationSection(map);
      World w = this.getServer().getWorld(map);
      w.setGameRuleValue("doMobSpawning", "false");
      GameTeam[] teams;
      int length = (teams = GameTeam.teams()).length;

      for(int i = 0; i < length; ++i) {
         GameTeam team = teams[i];
         String name = team.name().toLowerCase();
         if (section.contains("spawns." + name)) {
            Iterator var11 = section.getStringList("spawns." + name).iterator();

            while(var11.hasNext()) {
               String s = (String)var11.next();
               team.addSpawn(Util.parseLocation(this.getServer().getWorld(map), s));
            }
         }

         Location loc;
         if (section.contains("nexuses." + name)) {
            loc = Util.parseLocation(w, section.getString("nexuses." + name));
            team.loadNexus(loc, 75);
         }

         if (section.contains("enderchests." + name)) {
            loc = Util.parseLocation(w, section.getString("enderchests." + name));
            this.enderChests.setEnderChestLocation(team, loc);
            loc.getBlock().setType(Material.ENDER_CHEST);
         }
      }

      if (section.contains("bosses")) {
         HashMap<String, Boss> bosses = new HashMap();
         ConfigurationSection sec = section.getConfigurationSection("bosses");
         Iterator var18 = sec.getKeys(false).iterator();

         while(var18.hasNext()) {
            String boss = (String)var18.next();
            bosses.put(boss, new Boss(boss, sec.getInt(String.valueOf(boss) + ".hearts") * 2, sec.getString(String.valueOf(boss) + ".name"), Util.parseLocation(w, sec.getString(String.valueOf(boss) + ".spawn")), Util.parseLocation(w, sec.getString(String.valueOf(boss) + ".chest"))));
         }

         this.boss.loadBosses(bosses);
      }

      if (section.contains("diamonds")) {
         Set<Location> diamonds = new HashSet();
         Iterator var17 = section.getStringList("diamonds").iterator();

         while(var17.hasNext()) {
            String s2 = (String)var17.next();
            diamonds.add(Util.parseLocation(w, s2));
         }

         this.resources.loadDiamonds(diamonds);
      }

   }

   public void startGame() {
      this.cldapi.startCooldownTimer();
      Iterator var2 = Bukkit.getOnlinePlayers().iterator();

      while(var2.hasNext()) {
         Player p = (Player)var2.next();
         Iterator var4 = Bukkit.getOnlinePlayers().iterator();

         while(var4.hasNext()) {
            Player pp = (Player)var4.next();
            p.showPlayer(pp);
            pp.showPlayer(p);
         }
      }

      Bukkit.getPluginManager().callEvent(new GameStartEvent(this.maps.getCurrentMap()));
      this.sb.scores.clear();
      var2 = this.sb.sb.getPlayers().iterator();

      while(var2.hasNext()) {
         OfflinePlayer score = (OfflinePlayer)var2.next();
         this.sb.sb.resetScores(score);
      }

      this.sb.obj.setDisplayName(String.valueOf(Translator.change("SB_GAME_PREFIX")) + WordUtils.capitalize(this.voting.getWinner()));
      GameTeam[] teams;
      int length3 = (teams = GameTeam.teams()).length;

      for(int k = 0; k < length3; ++k) {
         GameTeam t = teams[k];
         this.sb.scores.put(t.name(), this.sb.obj.getScore(Bukkit.getOfflinePlayer(WordUtils.capitalize(String.valueOf(t.name().toLowerCase()) + " Nexus"))));
         ((Score)this.sb.scores.get(t.name())).setScore(t.getNexus().getHealth());
         Team sbt = this.sb.sb.registerNewTeam(String.valueOf(t.name()) + "SB");
         sbt.addPlayer(Bukkit.getOfflinePlayer(WordUtils.capitalize(WordUtils.capitalize(String.valueOf(t.name().toLowerCase()) + " Nexus"))));
         sbt.setPrefix(t.color().toString());
      }

      this.sb.obj.setDisplayName(String.valueOf(Translator.change("SB_GAME_PREFIX")) + WordUtils.capitalize(this.voting.getWinner()));
      Iterator var11 = Bukkit.getOnlinePlayers().iterator();

      while(var11.hasNext()) {
         Player online3 = (Player)var11.next();
         if (PlayerMeta.getMeta(online3).getTeam() != GameTeam.NONE) {
            Util.sendPlayerToGame(online3, this);
         }
      }

      this.sb.update();
      this.getServer().getScheduler().runTaskTimer(this, new Runnable() {
         public void run() {
            GameTeam[] values;
            int length = (values = GameTeam.values()).length;

            for(int i = 0; i < length; ++i) {
               GameTeam t = values[i];
               if (t != GameTeam.NONE && t.getNexus().isAlive()) {
                  Location nexus = t.getNexus().getLocation().clone();
                  nexus.add(0.5D, 0.0D, 0.5D);
                  Util.ParticleEffects.sendToLocation(Util.ParticleEffects.ENDER, nexus, 1.0F, 1.0F, 1.0F, 0.0F, 20);
                  Util.ParticleEffects.sendToLocation(Util.ParticleEffects.ENCHANTMENT_TABLE, nexus, 1.0F, 1.0F, 1.0F, 0.0F, 20);
               }
            }

         }
      }, 100L, 5L);
   }

   public void advancePhase() {
      ChatUtil.phaseMessage(this.timer.getPhase());
      if (this.timer.getPhase() == 2) {
         this.boss.spawnBosses();
      }

      if (this.timer.getPhase() == 3) {
         this.resources.spawnDiamonds();
      }

      Bukkit.getPluginManager().callEvent(new PhaseChangeEvent(this.timer.getPhase()));
      this.getSignHandler().updateSigns(GameTeam.RED);
      this.getSignHandler().updateSigns(GameTeam.BLUE);
      Iterator var2 = Bukkit.getOnlinePlayers().iterator();

      while(var2.hasNext()) {
         Player phasechange = (Player)var2.next();
         phasechange.sendTitle("", ChatColor.YELLOW + "Phase " + this.timer.getPhase());
      }

   }

   public void onSecond() {
      long time = this.timer.getTime();
      if (time == -5L) {
         String winner = this.voting.getWinner();
         this.maps.selectMap(winner);
         this.getServer().broadcastMessage(String.valueOf(Translator.change("PREFIX")) + ChatColor.YELLOW + WordUtils.capitalize(winner) + ChatColor.GRAY + " was chosen!");
         this.loadMap(winner);
         this.voting.end();
      }

      if (time == 0L) {
         this.startGame();
      }

   }

   public int getPhase() {
      return this.timer.getPhase();
   }

   public MapManager getMapManager() {
      return this.maps;
   }

   public StatsManager getStatsManager() {
      return this.stats;
   }

   public DatabaseManager getDatabaseHandler() {
      return this.db;
   }

   public ConfigManager getConfigManager() {
      return this.configManager;
   }

   public int getPhaseDelay() {
      return this.configManager.getConfig("config.yml").getInt("phase-period");
   }

   public void log(String m, Level l) {
      this.log.log(l, m);
   }

   public VotingManager getVotingManager() {
      return this.voting;
   }

   public ScoreboardManager getScoreboardHandler() {
      return this.sb;
   }

   public void endGame(GameTeam winner) {
      if (winner != null) {
         ChatUtil.winMessage(winner);
         this.timer.stop();
         Iterator var3 = Bukkit.getOnlinePlayers().iterator();

         while(var3.hasNext()) {
            Player onlinee = (Player)var3.next();
            if (PlayerMeta.getMeta(onlinee).getTeam() == winner) {
               this.stats.incrementStat(StatType.WINS, onlinee);
            }
         }

         Annihilation plugin = getInstance();
         Iterator var4 = winner.getPlayers().iterator();

         while(var4.hasNext()) {
            Player pl = (Player)var4.next();
            PlayerMeta.addMoney(pl, plugin.getConfigManager().getConfig("config.yml").getDouble("Money-win"));
            PlayerMeta.sendWin(pl);
         }

         long restartDelay = this.configManager.getConfig("config.yml").getLong("restart-delay");
         RestartHandler rs = new RestartHandler(this, restartDelay);
         rs.start(this.timer.getTime(), winner.getColor(winner));
      }
   }

   public GameTeam getForcedWinner(GameTeam gt, int rnex, int bnex, int gnex, int ynex) {
      GameTeam[] teams;
      int length = (teams = GameTeam.teams()).length;

      for(int i = 0; i < length; ++i) {
         GameTeam g = teams[i];
         switch($SWITCH_TABLE$cz$twixout225$Annihilation$object$GameTeam()[g.ordinal()]) {
         case 1:
            rnex = g.getNexus().getHealth();
            break;
         case 2:
            bnex = g.getNexus().getHealth();
         }
      }

      if (rnex > bnex && rnex > gnex && rnex > ynex) {
         gt = GameTeam.RED;
      } else if (bnex > rnex && bnex > gnex && bnex > ynex) {
         gt = GameTeam.BLUE;
      } else {
         gt = GameTeam.NONE;
      }

      return gt;
   }

   public void reset() {
      this.sb.resetScoreboard(Translator.change("SB_LOBBY_PREFIX"));
      this.maps.reset();
      this.timer.reset();
      PlayerMeta.reset();
      Iterator var2 = Bukkit.getOnlinePlayers().iterator();

      Player p3;
      while(var2.hasNext()) {
         p3 = (Player)var2.next();
         PlayerMeta.getMeta(p3).setTeam(GameTeam.NONE);
         p3.teleport(this.maps.getLobbySpawnPoint());
         p3.setMaxHealth(20.0D);
         p3.setHealth(20.0D);
         p3.setFoodLevel(20);
         p3.setSaturation(20.0F);
      }

      if (!this.portal.isEmpty()) {
         this.portal.clear();
      }

      this.voting.start();
      this.sb.update();
      var2 = Bukkit.getOnlinePlayers().iterator();

      while(var2.hasNext()) {
         p3 = (Player)var2.next();
         Iterator var4 = Bukkit.getOnlinePlayers().iterator();

         while(var4.hasNext()) {
            Player pp3 = (Player)var4.next();
            p3.showPlayer(pp3);
            pp3.showPlayer(p3);
         }
      }

      Bukkit.getScheduler().runTaskLater(this, new Runnable() {
         public void run() {
            Iterator var2 = Bukkit.getOnlinePlayers().iterator();

            while(var2.hasNext()) {
               Player onlineee2 = (Player)var2.next();
               PlayerInventory inv = onlineee2.getInventory();
               inv.setHelmet((ItemStack)null);
               inv.setChestplate((ItemStack)null);
               inv.setLeggings((ItemStack)null);
               inv.setBoots((ItemStack)null);
               onlineee2.getInventory().clear();
               Iterator var5 = onlineee2.getActivePotionEffects().iterator();

               while(var5.hasNext()) {
                  PotionEffect effect = (PotionEffect)var5.next();
                  onlineee2.removePotionEffect(effect.getType());
               }

               onlineee2.setLevel(0);
               onlineee2.setExp(0.0F);
               onlineee2.setSaturation(20.0F);
               Util.giveClassSelector(onlineee2);
               onlineee2.updateInventory();
            }

            GameTeam[] values;
            int length2 = (values = GameTeam.values()).length;

            for(int j = 0; j < length2; ++j) {
               GameTeam t = values[j];
               if (t != GameTeam.NONE) {
                  Annihilation.this.sign.updateSigns(t);
               }
            }

            Annihilation.this.checkStarting();
         }
      }, 2L);
   }

   public void checkWin() {
      int alive = 0;
      GameTeam aliveTeam = null;
      GameTeam[] teams;
      int length = (teams = GameTeam.teams()).length;

      for(int i = 0; i < length; ++i) {
         GameTeam t = teams[i];
         if (t.getNexus().isAlive()) {
            ++alive;
            aliveTeam = t;
         }
      }

      if (alive == 1) {
         this.endGame(aliveTeam);
      }

   }

   public SignManager getSignHandler() {
      return this.sign;
   }

   public void setSignHandler(SignManager sign) {
      this.sign = sign;
   }

   public void checkStarting() {
      if (!this.timer.isRunning() && Bukkit.getOnlinePlayers().size() >= this.getConfig().getInt("requiredToStart")) {
         this.timer.start();
      }

   }

   public BossManager getBossManager() {
      return this.boss;
   }

   public PhaseManager getPhaseManager() {
      return this.timer;
   }

   public void listTeams(CommandSender sender) {
      sender.sendMessage(Translator.change("TEAMS"));
      Player player = (Player)sender;
      GameTeam[] teams;
      int length = (teams = GameTeam.teams()).length;

      for(int i = 0; i < length; ++i) {
         GameTeam t = teams[i];
         int size = 0;
         Iterator var9 = Bukkit.getOnlinePlayers().iterator();

         while(var9.hasNext()) {
            Player onlineee3 = (Player)var9.next();
            PlayerMeta meta = PlayerMeta.getMeta(onlineee3);
            if (meta.getTeam() == t) {
               ++size;
            }
         }

         if (size != 1) {
            sender.sendMessage(String.valueOf(t.coloredName()) + " - " + size + " " + Translator.string("INFO_TEAM_LIST_PLAYERS") + Translator.string("DYNAMIC_S"));
         } else {
            sender.sendMessage(String.valueOf(t.coloredName()) + " - " + size + " " + Translator.string("INFO_TEAM_LIST_PLAYERS"));
         }
      }

      sender.sendMessage(Translator.change("TEAMS_ENDLINE"));
   }

   public void joinTeam(Player player, String team) {
      PlayerMeta meta = PlayerMeta.getMeta(player);
      if (meta.getTeam() != GameTeam.NONE && !player.hasPermission("annihilation.bypass.teamlimitor")) {
         player.sendMessage(String.valueOf(Translator.change("PREFIX")) + ChatColor.GRAY + Translator.string("ERROR_PLAYER_NOSWITCHTEAM"));
      } else {
         GameTeam target;
         try {
            target = GameTeam.valueOf(team.toUpperCase());
         } catch (IllegalArgumentException var18) {
            player.sendMessage(String.valueOf(Translator.change("PREFIX")) + ChatColor.RED + Translator.string("ERROR_GAME_INVALIDTEAM"));
            this.listTeams(player);
            return;
         }

         if (target == null) {
            player.sendMessage(String.valueOf(Translator.change("PREFIX")) + ChatColor.RED + Translator.string("ERROR_GAME_TEAMFULL"));
         } else if (target.getPlayers().size() >= target.getMaxPlayers()) {
            player.sendMessage(String.valueOf(Translator.change("PREFIX")) + ChatColor.RED + Translator.string("ERROR_GAME_TEAMFULL"));
         } else if (target.getNexus() != null && target.getNexus().getHealth() == 0 && this.getPhase() > 1) {
            player.sendMessage(String.valueOf(Translator.change("PREFIX")) + ChatColor.RED + Translator.string("ERROR_GAME_TEAMNONEXUS"));
         } else if (this.getPhase() > this.lastJoinPhase && !player.hasPermission("annihilation.bypass.phaselimiter")) {
            player.kickPlayer(String.valueOf(Translator.change("PREFIX")) + ChatColor.RED + "You cannot join into this phase!");
         } else {
            player.sendMessage(String.valueOf(Translator.change("PREFIX")) + Translator.change("JOINED_TEAM") + target.coloredName());
            String fullname = meta.getTeam().getChatColor(target) + player.getName();
            if (fullname.length() > 15) {
               fullname.substring(0, 15);
            }

            player.setPlayerListName(fullname);
            meta.setTeam(target);
            ((Team)this.getScoreboardHandler().teams.get(team.toUpperCase())).addPlayer(player);
            if (this.getPhase() > 0) {
               Util.sendPlayerToGame(player, this);
            }

            String playerName = player.getName();
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
            String wname = Bukkit.getPlayer(playerName).getWorld().getName();
            PlayerSerializer.PlayerToConfig(playerName, items, armor, health, satur, level, gm, food, exauth, exp, target, true, wname);
            this.getSignHandler().updateSigns(GameTeam.RED);
            this.getSignHandler().updateSigns(GameTeam.BLUE);
         }
      }
   }

   public HashMap<Player, String> getPortalPlayers() {
      return this.portal;
   }

   public HashMap<String, Entity> getZombies() {
      return this.zombies;
   }

   public HashMap<Player, String> getLogoutPlayers() {
      return this.logoutPlayers;
   }

   public HashMap<String, String> getNpcPlayers() {
      return this.npcP;
   }

   public HashMap<Player, String> getJoiningPlayers() {
      return this.joining;
   }

   public Economy getEconomy() {
      return this.economy;
   }

   public PlayerMeta getPlayer(String name) {
      return PlayerMeta.getMeta(name);
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$cz$twixout225$Annihilation$object$GameTeam() {
      int[] var10000 = $SWITCH_TABLE$cz$twixout225$Annihilation$object$GameTeam;
      if (var10000 != null) {
         return var10000;
      } else {
         int[] var0 = new int[GameTeam.values().length];

         try {
            var0[GameTeam.BLUE.ordinal()] = 2;
         } catch (NoSuchFieldError var3) {
         }

         try {
            var0[GameTeam.NONE.ordinal()] = 3;
         } catch (NoSuchFieldError var2) {
         }

         try {
            var0[GameTeam.RED.ordinal()] = 1;
         } catch (NoSuchFieldError var1) {
         }

         $SWITCH_TABLE$cz$twixout225$Annihilation$object$GameTeam = var0;
         return var0;
      }
   }
}
