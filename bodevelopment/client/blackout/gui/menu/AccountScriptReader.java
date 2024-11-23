package bodevelopment.client.blackout.gui.menu;

import bodevelopment.client.blackout.interfaces.functional.SingleOut;
import bodevelopment.client.blackout.randomstuff.Pair;
import bodevelopment.client.blackout.util.StringStorage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class AccountScriptReader {
   private static final Map<SingleOut<String>, String[]> commands = new HashMap();
   private static final List<Pair<String, String>> braces = new ArrayList();

   private static void addCommand(SingleOut<String> singleOut, String... usages) {
      commands.put(singleOut, usages);
   }

   public static String nameFromScript(String string) {
      Iterator var1 = commands.entrySet().iterator();

      while(var1.hasNext()) {
         Entry<SingleOut<String>, String[]> entry = (Entry)var1.next();
         SingleOut<String> mod = (SingleOut)entry.getKey();
         String[] var4 = (String[])entry.getValue();
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            String key = var4[var6];

            Pair pair;
            for(Iterator var8 = braces.iterator(); var8.hasNext(); string = string.replace((String)pair.method_15442() + key + (String)pair.method_15441(), (CharSequence)mod.get())) {
               pair = (Pair)var8.next();
            }
         }
      }

      return string;
   }

   static {
      addCommand(StringStorage::randomAdj, "rndAdj", "rndadj", "ra");
      addCommand(StringStorage::randomSub, "rndSub", "rndsub", "rs");
      addCommand(() -> {
         return String.valueOf((int)Math.floor(Math.random() * 10.0D - 1.0D));
      }, "1", "n1", "num1");
      addCommand(() -> {
         return String.valueOf((int)Math.floor(Math.random() * 100.0D - 1.0D));
      }, "2", "n2", "num2");
      addCommand(() -> {
         return String.valueOf((int)Math.floor(Math.random() * 1000.0D - 1.0D));
      }, "3", "n3", "num3");
      addCommand(() -> {
         return String.valueOf((int)Math.floor(Math.random() * 10000.0D - 1.0D));
      }, "4", "n4", "num4");
      addCommand(() -> {
         return String.valueOf((int)Math.floor(Math.random() * 100000.0D - 1.0D));
      }, "5", "n5", "num5");
      braces.add(new Pair("{", "}"));
      braces.add(new Pair("[", "]"));
      braces.add(new Pair("(", ")"));
      braces.add(new Pair("'", "'"));
      braces.add(new Pair("<", ">"));
   }
}
