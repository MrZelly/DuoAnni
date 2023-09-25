package sk.zelly.DuoAnni.manager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;

import sk.zelly.DuoAnni.Annihilation;

public class DatabaseManager {
   private static final Logger logger = Bukkit.getLogger();
   protected boolean connected = false;
   public Connection c = null;
   private String driver;
   private String connectionString;
   private Annihilation plugin;

   public DatabaseManager(Annihilation plugin) {
      this.plugin = plugin;
   }

   public DatabaseManager(String hostname, int port, String database, String username, String password, Annihilation plugin) {
      this.driver = "com.mysql.jdbc.Driver";
      this.connectionString = "jdbc:mysql://" + hostname + ":" + port + "/" + database + "?user=" + username + "&password=" + password;
      this.plugin = plugin;
   }

   public Connection open() {
      try {
         Class.forName(this.driver);
         return this.c = DriverManager.getConnection(this.connectionString);
      } catch (SQLException var2) {
         System.out.println("Could not connect to Database! because: " + var2.getMessage());
      } catch (ClassNotFoundException var3) {
         System.out.println(String.valueOf(String.valueOf(this.driver)) + " not found!");
      } catch (Exception var4) {
         System.out.println(var4.getMessage());
      }

      return this.c;
   }

   public Connection getConn() {
      return this.c;
   }

   public void close() {
      try {
         if (this.c != null) {
            this.c.close();
         }
      } catch (SQLException var2) {
         this.plugin.log(var2.getMessage(), Level.SEVERE);
      }

      this.c = null;
   }

   public boolean isConnected() {
      try {
         return this.c != null && !this.c.isClosed();
      } catch (SQLException var2) {
         return false;
      }
   }

   public DatabaseManager.Result query(String query) {
      if (!this.isConnected()) {
         this.open();
      }

      return this.query(query, true);
   }

   public DatabaseManager.Result query(final String query, boolean retry) {
      if (!this.isConnected()) {
         this.open();
      }

      try {
         PreparedStatement statement = null;

         try {
            if (!this.isConnected()) {
               this.open();
            }

            statement = this.c.prepareStatement(query);
            if (statement.execute()) {
               return new DatabaseManager.Result(statement, statement.getResultSet());
            }
         } catch (SQLException var6) {
            String msg = var6.getMessage();
            logger.severe("Database query error: " + msg);
            if (retry && msg.contains("_BUSY")) {
               logger.severe("Retrying query...");
               this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
                  public void run() {
                     DatabaseManager.this.query(query, false);
                  }
               }, 20L);
            }
         }

         if (statement != null) {
            statement.close();
         }
      } catch (SQLException var7) {
         this.plugin.log(var7.getMessage(), Level.SEVERE);
      }

      return null;
   }

   protected DatabaseManager.Statements getStatement(String query) {
      String trimmedQuery = query.trim();
      if (trimmedQuery.substring(0, 6).equalsIgnoreCase("SELECT")) {
         return DatabaseManager.Statements.SELECT;
      } else if (trimmedQuery.substring(0, 6).equalsIgnoreCase("INSERT")) {
         return DatabaseManager.Statements.INSERT;
      } else if (trimmedQuery.substring(0, 6).equalsIgnoreCase("UPDATE")) {
         return DatabaseManager.Statements.UPDATE;
      } else if (trimmedQuery.substring(0, 6).equalsIgnoreCase("DELETE")) {
         return DatabaseManager.Statements.DELETE;
      } else if (trimmedQuery.substring(0, 6).equalsIgnoreCase("CREATE")) {
         return DatabaseManager.Statements.CREATE;
      } else if (trimmedQuery.substring(0, 5).equalsIgnoreCase("ALTER")) {
         return DatabaseManager.Statements.ALTER;
      } else if (trimmedQuery.substring(0, 4).equalsIgnoreCase("DROP")) {
         return DatabaseManager.Statements.DROP;
      } else if (trimmedQuery.substring(0, 8).equalsIgnoreCase("TRUNCATE")) {
         return DatabaseManager.Statements.TRUNCATE;
      } else if (trimmedQuery.substring(0, 6).equalsIgnoreCase("RENAME")) {
         return DatabaseManager.Statements.RENAME;
      } else if (trimmedQuery.substring(0, 2).equalsIgnoreCase("DO")) {
         return DatabaseManager.Statements.DO;
      } else if (trimmedQuery.substring(0, 7).equalsIgnoreCase("REPLACE")) {
         return DatabaseManager.Statements.REPLACE;
      } else if (trimmedQuery.substring(0, 4).equalsIgnoreCase("LOAD")) {
         return DatabaseManager.Statements.LOAD;
      } else if (trimmedQuery.substring(0, 7).equalsIgnoreCase("HANDLER")) {
         return DatabaseManager.Statements.HANDLER;
      } else {
         return trimmedQuery.substring(0, 4).equalsIgnoreCase("CALL") ? DatabaseManager.Statements.CALL : DatabaseManager.Statements.SELECT;
      }
   }

   public class Result {
      private ResultSet resultSet;
      private Statement statement;

      public Result(Statement statement, ResultSet resultSet) {
         this.statement = statement;
         this.resultSet = resultSet;
      }

      public ResultSet getResultSet() {
         return this.resultSet;
      }

      public void close() {
         try {
            this.statement.close();
            this.resultSet.close();
         } catch (SQLException var2) {
         }

      }
   }

   protected static enum Statements {
      SELECT("SELECT", 0, "SELECT", 0),
      INSERT("INSERT", 1, "INSERT", 1),
      UPDATE("UPDATE", 2, "UPDATE", 2),
      DELETE("DELETE", 3, "DELETE", 3),
      DO("DO", 4, "DO", 4),
      REPLACE("REPLACE", 5, "REPLACE", 5),
      LOAD("LOAD", 6, "LOAD", 6),
      HANDLER("HANDLER", 7, "HANDLER", 7),
      CALL("CALL", 8, "CALL", 8),
      CREATE("CREATE", 9, "CREATE", 9),
      ALTER("ALTER", 10, "ALTER", 10),
      DROP("DROP", 11, "DROP", 11),
      TRUNCATE("TRUNCATE", 12, "TRUNCATE", 12),
      RENAME("RENAME", 13, "RENAME", 13),
      START("START", 14, "START", 14),
      COMMIT("COMMIT", 15, "COMMIT", 15),
      ROLLBACK("ROLLBACK", 16, "ROLLBACK", 16),
      SAVEPOINT("SAVEPOINT", 17, "SAVEPOINT", 17),
      LOCK("LOCK", 18, "LOCK", 18),
      UNLOCK("UNLOCK", 19, "UNLOCK", 19),
      PREPARE("PREPARE", 20, "PREPARE", 20),
      EXECUTE("EXECUTE", 21, "EXECUTE", 21),
      DEALLOCATE("DEALLOCATE", 22, "DEALLOCATE", 22),
      SET("SET", 23, "SET", 23),
      SHOW("SHOW", 24, "SHOW", 24),
      DESCRIBE("DESCRIBE", 25, "DESCRIBE", 25),
      EXPLAIN("EXPLAIN", 26, "EXPLAIN", 26),
      HELP("HELP", 27, "HELP", 27),
      USE("USE", 28, "USE", 28),
      ANALYZE("ANALYZE", 29, "ANALYZE", 29),
      ATTACH("ATTACH", 30, "ATTACH", 30),
      BEGIN("BEGIN", 31, "BEGIN", 31),
      DETACH("DETACH", 32, "DETACH", 32),
      END("END", 33, "END", 33),
      INDEXED("INDEXED", 34, "INDEXED", 34),
      ON("ON", 35, "ON", 35),
      PRAGMA("PRAGMA", 36, "PRAGMA", 36),
      REINDEX("REINDEX", 37, "REINDEX", 37),
      RELEASE("RELEASE", 38, "RELEASE", 38),
      VACUUM("VACUUM", 39, "VACUUM", 39);

      private Statements(String s2, int n2, String s, int n) {
      }
   }
}
