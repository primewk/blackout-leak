package bodevelopment.client.blackout.util;

import net.minecraft.class_1738;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1890;
import net.minecraft.class_1893;
import net.minecraft.class_2246;

public class ItemUtils {
   public static double getArmorValue(class_1799 stack) {
      class_1792 var2 = stack.method_7909();
      if (var2 instanceof class_1738) {
         class_1738 armor = (class_1738)var2;
         double value = (double)armor.method_7687();
         value += (double)(armor.method_26353() / 4.0F);
         value += (double)(armor.method_7686().method_24355() / 3.0F);
         if (class_1890.method_8222(stack).containsKey(class_1893.field_9111)) {
            value += (double)((Integer)class_1890.method_8222(stack).get(class_1893.field_9111) + 1);
         }

         if (class_1890.method_8222(stack).containsKey(class_1893.field_9095)) {
            value += (double)((Integer)class_1890.method_8222(stack).get(class_1893.field_9095) + 1) * 0.05D;
         }

         if (class_1890.method_8222(stack).containsKey(class_1893.field_9129)) {
            value += (double)((Integer)class_1890.method_8222(stack).get(class_1893.field_9129) + 1) * 0.1D;
         }

         if (class_1890.method_8222(stack).containsKey(class_1893.field_9107)) {
            value += (double)((Integer)class_1890.method_8222(stack).get(class_1893.field_9107) + 1) * 0.05D;
         }

         if (class_1890.method_8222(stack).containsKey(class_1893.field_9096)) {
            value += ((double)(Integer)class_1890.method_8222(stack).get(class_1893.field_9096) + 0.1D) * 0.15D;
         }

         return value;
      } else {
         return 0.0D;
      }
   }

   public static double getPickaxeValue(class_1799 stack) {
      float f = stack.method_7924(class_2246.field_10340.method_9564());
      if (f > 1.0F && !stack.method_7960() && class_1890.method_8222(stack).containsKey(class_1893.field_9131)) {
         int i = (Integer)class_1890.method_8222(stack).get(class_1893.field_9131);
         if (i > 0 && !stack.method_7960()) {
            f += (float)(i * i + 1);
         }
      }

      return (double)f;
   }

   public static double getAxeValue(class_1799 stack) {
      float f = stack.method_7924(class_2246.field_10431.method_9564());
      if (f > 1.0F && !stack.method_7960() && class_1890.method_8222(stack).containsKey(class_1893.field_9131)) {
         int i = (Integer)class_1890.method_8222(stack).get(class_1893.field_9131);
         if (i > 0 && !stack.method_7960()) {
            f += (float)(i * i + 1);
         }
      }

      return (double)f;
   }

   public static double getWeaponValue(class_1799 stack) {
      double damage = DamageUtils.itemDamage(stack);
      if (class_1890.method_8222(stack).containsKey(class_1893.field_9124)) {
         damage += (double)((Integer)class_1890.method_8222(stack).get(class_1893.field_9124) + 1) * 0.1D;
      }

      if (class_1890.method_8222(stack).containsKey(class_1893.field_9121)) {
         damage += (double)((Integer)class_1890.method_8222(stack).get(class_1893.field_9121) + 1) * 0.05D;
      }

      return damage;
   }

   public static double getBowValue(class_1799 stack) {
      int power = class_1890.method_8225(class_1893.field_9103, stack);
      double damage;
      if (power > 0) {
         damage = 2.5D + (double)power * 0.5D;
      } else {
         damage = 2.0D;
      }

      int punch = class_1890.method_8225(class_1893.field_9116, stack);
      return damage + (double)punch * 0.3D;
   }

   public static double getElytraValue(class_1799 stack) {
      return (double)((class_1890.method_8225(class_1893.field_9101, stack) > 0 ? 100 : 0) + class_1890.method_8225(class_1893.field_9119, stack));
   }
}
