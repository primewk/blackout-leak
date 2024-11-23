package bodevelopment.client.blackout.util;

import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_3532;

public class BoxUtils {
   public static class_243 clamp(class_243 vec, class_238 box) {
      return new class_243(class_3532.method_15350(vec.field_1352, box.field_1323, box.field_1320), class_3532.method_15350(vec.field_1351, box.field_1322, box.field_1325), class_3532.method_15350(vec.field_1350, box.field_1321, box.field_1324));
   }

   public static class_238 get(class_2338 pos) {
      return new class_238((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260(), (double)(pos.method_10263() + 1), (double)(pos.method_10264() + 1), (double)(pos.method_10260() + 1));
   }

   public static class_243 middle(class_238 box) {
      return new class_243((box.field_1323 + box.field_1320) / 2.0D, (box.field_1322 + box.field_1325) / 2.0D, (box.field_1321 + box.field_1324) / 2.0D);
   }

   public static class_243 feet(class_238 box) {
      return new class_243((box.field_1323 + box.field_1320) / 2.0D, box.field_1322, (box.field_1321 + box.field_1324) / 2.0D);
   }

   public static class_238 crystalSpawnBox(class_2338 pos) {
      return new class_238((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260(), (double)(pos.method_10263() + 1), (double)(pos.method_10264() + (SettingUtils.cc() ? 1 : 2)), (double)(pos.method_10260() + 1));
   }

   public static class_238 lerp(double delta, class_238 start, class_238 end) {
      return new class_238(class_3532.method_16436(delta, start.field_1323, end.field_1323), class_3532.method_16436(delta, start.field_1322, end.field_1322), class_3532.method_16436(delta, start.field_1321, end.field_1321), class_3532.method_16436(delta, start.field_1320, end.field_1320), class_3532.method_16436(delta, start.field_1325, end.field_1325), class_3532.method_16436(delta, start.field_1324, end.field_1324));
   }
}
