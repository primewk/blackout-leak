package bodevelopment.client.blackout.util;

import bodevelopment.client.blackout.interfaces.mixin.IVec3d;
import java.util.List;
import net.minecraft.class_1297;
import net.minecraft.class_1313;
import net.minecraft.class_1657;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_265;

public class MovementPrediction {
   public static class_243 adjustMovementForCollisions(class_1297 entity, class_243 movement) {
      class_238 box = entity.method_5829();
      List<class_265> list = entity.method_37908().method_20743(entity, box.method_18804(movement));
      class_243 vec3d = movement.method_1027() == 0.0D ? movement : class_1297.method_20736(entity, movement, box, entity.method_37908(), list);
      boolean bl = movement.field_1352 != vec3d.field_1352;
      boolean bl2 = movement.field_1351 != vec3d.field_1351;
      boolean bl3 = movement.field_1350 != vec3d.field_1350;
      boolean bl4 = entity.method_24828() || bl2 && movement.field_1351 < 0.0D;
      if (entity.method_49476() > 0.0F && bl4 && (bl || bl3)) {
         class_243 vec3d2 = class_1297.method_20736(entity, new class_243(movement.field_1352, (double)entity.method_49476(), movement.field_1350), box, entity.method_37908(), list);
         class_243 vec3d3 = class_1297.method_20736(entity, new class_243(0.0D, (double)entity.method_49476(), 0.0D), box.method_1012(movement.field_1352, 0.0D, movement.field_1350), entity.method_37908(), list);
         if (vec3d3.field_1351 < (double)entity.method_49476()) {
            class_243 vec3d4 = class_1297.method_20736(entity, new class_243(movement.field_1352, 0.0D, movement.field_1350), box.method_997(vec3d3), entity.method_37908(), list).method_1019(vec3d3);
            if (vec3d4.method_37268() > vec3d2.method_37268()) {
               vec3d2 = vec3d4;
            }
         }

         if (vec3d2.method_37268() > vec3d.method_37268()) {
            return vec3d2.method_1019(class_1297.method_20736(entity, new class_243(0.0D, -vec3d2.field_1351 + movement.field_1351, 0.0D), box.method_997(vec3d2), entity.method_37908(), list));
         }
      }

      return vec3d;
   }

   public static class_243 predict(class_1657 player) {
      class_243 movement = new class_243(player.method_18798().field_1352, player.method_18798().field_1351, player.method_18798().field_1350);
      collide(movement, player);
      return player.method_19538().method_1019(movement);
   }

   public static void collide(class_243 movement, class_1657 player) {
      set(movement, player.method_18796(movement, class_1313.field_6308));
      set(movement, adjustMovementForCollisions(player, movement));
   }

   private static void set(class_243 vec, class_243 to) {
      ((IVec3d)vec).blackout_Client$set(to.field_1352, to.field_1351, to.field_1350);
   }

   public static double approximateYVelocity(double deltaY, int tickDelta, int iterations) {
      double min = -5.0D;
      double max = 5.0D;
      double[] array = new double[2];

      for(int i = 0; i < iterations; ++i) {
         double average = (min + max) / 2.0D;
         simulate(average, tickDelta, array);
         if (array[0] > deltaY) {
            max = average;
         } else {
            min = average;
         }
      }

      return array[1];
   }

   private static void simulate(double vel, int tickDelta, double[] array) {
      double y = 0.0D;

      for(int tick = 0; tick < tickDelta; ++tick) {
         y += vel;
         if (tick < tickDelta - 1) {
            vel = (vel - 0.08D) * 0.98D;
         }
      }

      array[0] = y;
      array[1] = vel;
   }
}
