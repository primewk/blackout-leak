package bodevelopment.client.blackout.module.setting;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.class_2394;
import net.minecraft.class_2396;
import net.minecraft.class_7923;

public class RegistryNames {
   private static final Map<class_2396<?>, String> particles = new HashMap();

   public static void init() {
      class_7923.field_41180.forEach((particleType) -> {
         if (particleType instanceof class_2394) {
            class_2394 effect = (class_2394)particleType;
            particles.put(particleType, capitalize(effect.method_10293()));
         }

      });
   }

   public static String get(class_2396<?> particleType) {
      return (String)particles.getOrDefault(particleType, "null");
   }

   private static String capitalize(String string) {
      String var10000 = String.valueOf(string.charAt(0)).toUpperCase();
      return var10000 + string.substring(1);
   }
}
