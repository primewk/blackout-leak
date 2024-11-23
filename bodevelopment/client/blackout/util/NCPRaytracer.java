package bodevelopment.client.blackout.util;

import bodevelopment.client.blackout.BlackOut;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_2404;
import net.minecraft.class_243;
import net.minecraft.class_2510;
import net.minecraft.class_2680;
import net.minecraft.class_3532;

public class NCPRaytracer {
   public static boolean raytrace(class_243 from, class_243 to, class_238 box) {
      int lx = 0;
      int ly = 0;
      int lz = 0;

      for(double delta = 0.0D; delta < 1.0D; delta += 0.0010000000474974513D) {
         double x = class_3532.method_16436(from.field_1352, to.field_1352, delta);
         double y = class_3532.method_16436(from.field_1351, to.field_1351, delta);
         double z = class_3532.method_16436(from.field_1350, to.field_1350, delta);
         if (box.method_1008(x, y, z)) {
            return true;
         }

         int ix = (int)Math.floor(x);
         int iy = (int)Math.floor(y);
         int iz = (int)Math.floor(z);
         if (lx != ix || ly != iy || lz != iz) {
            class_2338 pos = new class_2338(ix, iy, iz);
            if (validForCheck(pos, BlackOut.mc.field_1687.method_8320(pos))) {
               return false;
            }
         }

         lx = ix;
         ly = iy;
         lz = iz;
      }

      return false;
   }

   public static boolean validForCheck(class_2338 pos, class_2680 state) {
      if (state.method_51367()) {
         return true;
      } else if (state.method_26204() instanceof class_2404) {
         return false;
      } else if (state.method_26204() instanceof class_2510) {
         return false;
      } else {
         return state.method_31709() ? false : state.method_26234(BlackOut.mc.field_1687, pos);
      }
   }
}
