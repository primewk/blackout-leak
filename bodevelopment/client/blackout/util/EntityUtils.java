package bodevelopment.client.blackout.util;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.manager.Managers;
import it.unimi.dsi.fastutil.longs.LongBidirectionalIterator;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.class_1297;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_4076;
import net.minecraft.class_5572;
import net.minecraft.class_5578;

public class EntityUtils {
   public static boolean intersects(class_238 box, Predicate<class_1297> predicate) {
      return intersects(box, predicate, (Map)null);
   }

   public static boolean intersects(class_238 box, Predicate<class_1297> predicate, Map<class_1297, class_238> hitboxes) {
      int minX = class_4076.method_32204(box.field_1323 - 2.0D);
      int minY = class_4076.method_32204(box.field_1322 - 4.0D);
      int minZ = class_4076.method_32204(box.field_1321 - 2.0D);
      int maxX = class_4076.method_32204(box.field_1320 + 2.0D);
      int maxY = class_4076.method_32204(box.field_1325);
      int maxZ = class_4076.method_32204(box.field_1324 + 2.0D);
      class_5578<class_1297> lookup = (class_5578)BlackOut.mc.field_1687.method_31592();

      label71:
      for(int x = minX; x <= maxX; ++x) {
         LongBidirectionalIterator var11 = lookup.field_27259.field_27253.subSet(class_4076.method_18685(x, 0, 0), class_4076.method_18685(x, -1, -1) + 1L).iterator();

         while(true) {
            class_5572 entityTrackingSection;
            do {
               do {
                  do {
                     long chunk;
                     int z;
                     do {
                        int y;
                        do {
                           do {
                              do {
                                 if (!var11.hasNext()) {
                                    continue label71;
                                 }

                                 chunk = (Long)var11.next();
                                 y = class_4076.method_18689(chunk);
                                 z = class_4076.method_18690(chunk);
                              } while(y < minY);
                           } while(y > maxY);
                        } while(z < minZ);
                     } while(z > maxZ);

                     entityTrackingSection = (class_5572)lookup.field_27259.field_27252.get(chunk);
                  } while(entityTrackingSection == null);
               } while(entityTrackingSection.method_31761());
            } while(!entityTrackingSection.method_31768().method_31885());

            Iterator var17 = entityTrackingSection.field_27248.iterator();

            while(var17.hasNext()) {
               class_1297 entity = (class_1297)var17.next();
               if (predicate.test(entity) && !Managers.ENTITY.isDead(entity.method_5628()) && getBox(entity, hitboxes).method_994(box)) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   private static class_238 getBox(class_1297 entity, Map<class_1297, class_238> map) {
      return map != null && map.containsKey(entity) ? (class_238)map.get(entity) : entity.method_5829();
   }

   public static boolean intersectsWithSpawningItem(class_2338 crystalPos) {
      return Managers.ENTITY.containsItem(crystalPos) || Managers.ENTITY.containsItem(crystalPos.method_10084());
   }
}
