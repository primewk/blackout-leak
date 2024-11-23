package bodevelopment.client.blackout.util;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.interfaces.mixin.IRaycastContext;
import bodevelopment.client.blackout.interfaces.mixin.IVec3d;
import bodevelopment.client.blackout.manager.Managers;
import java.util.Iterator;
import net.minecraft.class_1267;
import net.minecraft.class_1294;
import net.minecraft.class_1309;
import net.minecraft.class_1766;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1829;
import net.minecraft.class_1831;
import net.minecraft.class_1890;
import net.minecraft.class_1893;
import net.minecraft.class_1900;
import net.minecraft.class_1922;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2487;
import net.minecraft.class_2499;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_3532;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_5134;
import net.minecraft.class_7923;
import net.minecraft.class_1900.class_1901;
import net.minecraft.class_239.class_240;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;
import org.apache.commons.lang3.mutable.MutableInt;

public class DamageUtils {
   public static class_3959 raycastContext;

   public static double crystalDamage(class_1309 entity, class_238 box, class_243 pos) {
      return crystalDamage(entity, box, pos, (class_2338)null);
   }

   public static double crystalDamage(class_1309 entity, class_238 box, class_243 pos, class_2338 ignorePos) {
      return explosionDamage(entity, box, pos, ignorePos, 6.0D);
   }

   public static double anchorDamage(class_1309 entity, class_238 box, class_243 pos) {
      return explosionDamage(entity, box, pos, (class_2338)null, 5.0D);
   }

   public static double anchorDamage(class_1309 entity, class_238 box, class_243 pos, class_2338 ignorePos) {
      return explosionDamage(entity, box, pos, ignorePos, 5.0D);
   }

   public static double creeperDamage(class_1309 entity, class_238 box, class_243 pos) {
      return explosionDamage(entity, box, pos, (class_2338)null, 3.0D);
   }

   public static double creeperDamage(class_1309 entity, class_238 box, class_243 pos, class_2338 ignorePos) {
      return explosionDamage(entity, box, pos, ignorePos, 3.0D);
   }

   public static double chargedCreeperDamage(class_1309 entity, class_238 box, class_243 pos) {
      return explosionDamage(entity, box, pos, (class_2338)null, 6.0D);
   }

   public static double chargedCreeperDamage(class_1309 entity, class_238 box, class_243 pos, class_2338 ignorePos) {
      return explosionDamage(entity, box, pos, ignorePos, 6.0D);
   }

   private static double explosionDamage(class_1309 entity, class_238 box, class_243 pos, class_2338 ignorePos, double strength) {
      double q = strength * 2.0D;
      double dist = BoxUtils.feet(box).method_1022(pos) / q;
      if (dist > 1.0D) {
         return 0.0D;
      } else {
         double aa = getExposure(pos, box, ignorePos);
         double ab = (1.0D - dist) * aa;
         double damage = (double)((float)((int)((ab * ab + ab) * 3.5D * q + 1.0D)));
         damage = difficultyDamage(damage);
         damage = applyArmor(entity, damage);
         damage = applyResistance(entity, damage);
         damage = applyProtection(entity, damage, true);
         return damage;
      }
   }

   public static int getProtectionAmount(Iterable<class_1799> equipment, boolean explosion) {
      MutableInt mint = new MutableInt();
      Iterator var3 = equipment.iterator();

      while(true) {
         class_1799 stack;
         do {
            if (!var3.hasNext()) {
               return mint.intValue();
            }

            stack = (class_1799)var3.next();
         } while(stack.method_7960());

         class_2499 nbtList = stack.method_7921();

         for(int i = 0; i < nbtList.size(); ++i) {
            class_2487 nbtCompound = nbtList.method_10602(i);
            class_7923.field_41176.method_17966(class_1890.method_37427(nbtCompound)).ifPresent((enchantment) -> {
               if (enchantment instanceof class_1900) {
                  class_1900 protection = (class_1900)enchantment;
                  if (protection.field_9133 == class_1901.field_9138) {
                     mint.add(class_1890.method_37424(nbtCompound));
                  } else if (explosion && protection.field_9133 == class_1901.field_9141) {
                     mint.add(class_1890.method_37424(nbtCompound) * 2);
                  }
               }

            });
         }
      }
   }

   public static double difficultyDamage(double damage) {
      if (BlackOut.mc.field_1687.method_8407() == class_1267.field_5805) {
         return Math.min(damage / 2.0D + 1.0D, damage);
      } else {
         return BlackOut.mc.field_1687.method_8407() == class_1267.field_5802 ? damage : damage * 1.5D;
      }
   }

   public static double applyArmor(class_1309 entity, double damage) {
      double armor = (double)entity.method_6096();
      double f = 2.0D + entity.method_26825(class_5134.field_23725) / 4.0D;
      return damage * (1.0D - class_3532.method_15350(armor - damage / f, armor * 0.2D, 20.0D) / 25.0D);
   }

   public static double applyResistance(class_1309 entity, double damage) {
      int amplifier = entity.method_6059(class_1294.field_5907) ? entity.method_6112(class_1294.field_5907).method_5578() : 0;
      int j = 25 - (amplifier + 1) * 5;
      return Math.max(damage * (double)j / 25.0D, 0.0D);
   }

   public static double applyProtection(class_1309 entity, double damage, boolean explosions) {
      int i = getProtectionAmount(entity.method_5661(), explosions);
      if (i > 0) {
         damage *= (double)(1.0F - class_3532.method_15363((float)i, 0.0F, 20.0F) / 25.0F);
      }

      return damage;
   }

   public static double getExposure(class_243 source, class_238 box, class_2338 ignorePos) {
      ((IRaycastContext)raycastContext).blackout_Client$set(class_3960.field_17558, class_242.field_1348, BlackOut.mc.field_1724);
      ((IRaycastContext)raycastContext).blackout_Client$setStart(source);
      class_243 vec3d = new class_243(0.0D, 0.0D, 0.0D);
      double lx = box.method_17939();
      double ly = box.method_17940();
      double lz = box.method_17941();
      double deltaX = 1.0D / (lx * 2.0D + 1.0D);
      double deltaY = 1.0D / (ly * 2.0D + 1.0D);
      double deltaZ = 1.0D / (lz * 2.0D + 1.0D);
      double offsetX = (1.0D - Math.floor(1.0D / deltaX) * deltaX) / 2.0D;
      double offsetZ = (1.0D - Math.floor(1.0D / deltaZ) * deltaZ) / 2.0D;
      double stepX = deltaX * lx;
      double stepY = deltaY * ly;
      double stepZ = deltaZ * lz;
      if (!(stepX < 0.0D) && !(stepY < 0.0D) && !(stepZ < 0.0D)) {
         float i = 0.0F;
         float j = 0.0F;
         double x = box.field_1323 + offsetX;

         for(double maxX = box.field_1320 + offsetX; x <= maxX; x += stepX) {
            ((IVec3d)vec3d).blackout_Client$setX(x);

            for(double y = box.field_1322; y <= box.field_1325; y += stepY) {
               ((IVec3d)vec3d).blackout_Client$setY(y);
               double z = box.field_1321 + offsetZ;

               for(double maxZ = box.field_1324 + offsetZ; z <= maxZ; z += stepZ) {
                  ((IVec3d)vec3d).blackout_Client$setZ(z);
                  ((IRaycastContext)raycastContext).blackout_Client$setEnd(vec3d);
                  if (raycast(raycastContext, true, ignorePos).method_17783() == class_240.field_1333) {
                     ++i;
                  }

                  ++j;
               }
            }
         }

         return (double)(i / j);
      } else {
         return 0.0D;
      }
   }

   public static class_3965 raycast(class_3959 context, boolean damage) {
      return raycast(context, damage, (class_2338)null);
   }

   public static class_3965 raycast(class_3959 context, boolean damage, class_2338 ignorePos) {
      return (class_3965)class_1922.method_17744(context.method_17750(), context.method_17747(), context, (contextx, pos) -> {
         class_2680 blockState;
         if (pos.equals(ignorePos)) {
            blockState = class_2246.field_10124.method_9564();
         } else if (damage) {
            if (BlackOut.mc.field_1687.method_8320(pos).method_26204().method_9520() < 200.0F) {
               blockState = class_2246.field_10124.method_9564();
            } else {
               blockState = Managers.BLOCK.damageState(pos);
            }
         } else {
            blockState = BlackOut.mc.field_1687.method_8320(pos);
         }

         class_243 vec3d = contextx.method_17750();
         class_243 vec3d2 = contextx.method_17747();
         class_265 voxelShape = contextx.method_17748(blockState, BlackOut.mc.field_1687, pos);
         return BlackOut.mc.field_1687.method_17745(vec3d, vec3d2, pos, voxelShape, blockState);
      }, (contextx) -> {
         class_243 vec3d = contextx.method_17750().method_1020(contextx.method_17747());
         return class_3965.method_17778(contextx.method_17747(), class_2350.method_10142(vec3d.field_1352, vec3d.field_1351, vec3d.field_1350), class_2338.method_49638(contextx.method_17747()));
      });
   }

   public static double itemDamage(class_1799 stack) {
      if (stack.method_7960()) {
         return 1.0D;
      } else {
         double damage = 1.0D;
         class_1792 var6 = stack.method_7909();
         if (var6 instanceof class_1766) {
            class_1766 miningTool = (class_1766)var6;
            damage += (double)miningTool.method_26366();
         } else {
            var6 = stack.method_7909();
            if (var6 instanceof class_1829) {
               class_1829 sword = (class_1829)var6;
               damage += (double)sword.method_8020();
            } else {
               var6 = stack.method_7909();
               if (var6 instanceof class_1831) {
                  class_1831 tool = (class_1831)var6;
                  damage += (double)tool.method_8022().method_8028();
               }
            }
         }

         if (class_1890.method_8222(stack).containsKey(class_1893.field_9118)) {
            damage += (double)((Integer)class_1890.method_8222(stack).get(class_1893.field_9118) + 1) * 0.5D;
         }

         return damage;
      }
   }
}
