package bodevelopment.client.blackout.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.class_1887;
import net.minecraft.class_1893;
import net.minecraft.class_2561;

public class EnchantmentNames {
   public static final List<class_1887> enchantments = new ArrayList();
   private static final Map<class_1887, String[]> map = new HashMap();

   public static String getName(class_1887 enchantment, boolean shortName) {
      return shortName ? getShortName(enchantment) : getLongName(enchantment);
   }

   public static String getLongName(class_1887 enchantment) {
      return ((String[])map.get(enchantment))[0];
   }

   public static String getShortName(class_1887 enchantment) {
      return ((String[])map.get(enchantment))[1];
   }

   public static void init() {
      Map<class_1887, String> shortNames = getShortNames();
      Field[] var1 = class_1893.class.getDeclaredFields();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Field field = var1[var3];
         if (Modifier.isPublic(field.getModifiers()) && field.getType() == class_1887.class) {
            Object object;
            try {
               object = field.get(field);
            } catch (IllegalAccessException var9) {
               throw new RuntimeException(var9);
            }

            if (!(object instanceof class_1887)) {
               return;
            }

            class_1887 enchantment = (class_1887)object;
            String name = class_2561.method_43471(enchantment.method_8184()).getString();
            String shortName = (String)shortNames.get(enchantment);
            map.put(enchantment, new String[]{name, shortName});
         }
      }

   }

   private static Map<class_1887, String> getShortNames() {
      Map<class_1887, String> map = new HashMap();
      put(map, class_1893.field_9111, "prot");
      put(map, class_1893.field_9095, "fire");
      put(map, class_1893.field_9129, "feat");
      put(map, class_1893.field_9107, "bla");
      put(map, class_1893.field_9096, "proj");
      put(map, class_1893.field_9127, "resp");
      put(map, class_1893.field_9105, "aqua");
      put(map, class_1893.field_9097, "tho");
      put(map, class_1893.field_9128, "dep");
      put(map, class_1893.field_9122, "frost");
      put(map, class_1893.field_9113, "bind");
      put(map, class_1893.field_23071, "soul");
      put(map, class_1893.field_38223, "swi");
      put(map, class_1893.field_9118, "sha");
      put(map, class_1893.field_9123, "smi");
      put(map, class_1893.field_9112, "bane");
      put(map, class_1893.field_9121, "kno");
      put(map, class_1893.field_9124, "asp");
      put(map, class_1893.field_9110, "loot");
      put(map, class_1893.field_9115, "swe");
      put(map, class_1893.field_9131, "eff");
      put(map, class_1893.field_9099, "silk");
      put(map, class_1893.field_9119, "unb");
      put(map, class_1893.field_9130, "for");
      put(map, class_1893.field_9103, "pow");
      put(map, class_1893.field_9116, "pun");
      put(map, class_1893.field_9126, "fla");
      put(map, class_1893.field_9125, "inf");
      put(map, class_1893.field_9114, "luck");
      put(map, class_1893.field_9100, "lure");
      put(map, class_1893.field_9120, "loy");
      put(map, class_1893.field_9106, "imp");
      put(map, class_1893.field_9104, "rip");
      put(map, class_1893.field_9117, "cha");
      put(map, class_1893.field_9108, "mul");
      put(map, class_1893.field_9098, "qui");
      put(map, class_1893.field_9132, "pie");
      put(map, class_1893.field_9101, "men");
      put(map, class_1893.field_9109, "van");
      return map;
   }

   private static void put(Map<class_1887, String> map, class_1887 enchantment, String name) {
      enchantments.add(enchantment);
      map.put(enchantment, name);
   }
}
