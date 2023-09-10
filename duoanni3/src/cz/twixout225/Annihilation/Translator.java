package cz.twixout225.Annihilation;

import org.bukkit.ChatColor;

public class Translator {
   static Annihilation plugin;

   public Translator(Annihilation pl) {
      plugin = pl;
   }

   public static String string(String id) {
      Annihilation plugin = Translator.plugin;
      return ChatColor.stripColor((String)Annihilation.messages.get(id));
   }

   public static String change(String s) {
      String ss = string(s);
      ss = ss.replaceAll("(&([a-fk-or0-9]))", "§$2");
      return ss;
   }
}
