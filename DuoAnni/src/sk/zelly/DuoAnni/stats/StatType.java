package cz.twixout225.Annihilation.stats;

public enum StatType {
   KILLS("KILLS", 0, "KILLS", 0),
   DEATHS("DEATHS", 1, "DEATHS", 1),
   WINS("WINS", 2, "WINS", 2),
   LOSSES("LOSSES", 3, "LOSSES", 3),
   NEXUS_DAMAGE("NEXUS_DAMAGE", 4, "NEXUS_DAMAGE", 4);

   private StatType(String s2, int n2, String s, int n) {
   }
}
