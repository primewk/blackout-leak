package bodevelopment.client.blackout.util;

import bodevelopment.client.blackout.enums.HoleType;
import bodevelopment.client.blackout.randomstuff.Hole;
import net.minecraft.class_2338;

public class HoleUtils {
   public static Hole getHole(class_2338 pos) {
      return getHole(pos, true, true, true, 3, true);
   }

   public static Hole getHole(class_2338 pos, int depth) {
      return getHole(pos, depth, true);
   }

   public static Hole getHole(class_2338 pos, int depth, boolean floor) {
      return getHole(pos, true, true, true, depth, floor);
   }

   public static Hole getHole(class_2338 pos, boolean s, boolean d, boolean q, int depth, boolean floor) {
      if (!isHole(pos, depth, floor)) {
         return new Hole(pos, HoleType.NotHole);
      } else if (isBlock(pos.method_10067()) && isBlock(pos.method_10095())) {
         boolean x = isHole(pos.method_10078(), depth, floor) && isBlock(pos.method_10078().method_10095()) && isBlock(pos.method_10089(2));
         boolean z = isHole(pos.method_10072(), depth, floor) && isBlock(pos.method_10072().method_10067()) && isBlock(pos.method_10077(2));
         if (s && !x && !z && isBlock(pos.method_10078()) && isBlock(pos.method_10072())) {
            return new Hole(pos, HoleType.Single);
         } else if (q && x && z && isHole(pos.method_10072().method_10078(), depth, floor) && isBlock(pos.method_10078().method_10078().method_10072()) && isBlock(pos.method_10072().method_10072().method_10078())) {
            return new Hole(pos, HoleType.Quad);
         } else if (!d) {
            return new Hole(pos, HoleType.NotHole);
         } else if (x && !z && isBlock(pos.method_10072()) && isBlock(pos.method_10072().method_10078())) {
            return new Hole(pos, HoleType.DoubleX);
         } else {
            return z && !x && isBlock(pos.method_10078()) && isBlock(pos.method_10072().method_10078()) ? new Hole(pos, HoleType.DoubleZ) : new Hole(pos, HoleType.NotHole);
         }
      } else {
         return new Hole(pos, HoleType.NotHole);
      }
   }

   static boolean isBlock(class_2338 pos) {
      return OLEPOSSUtils.collidable(pos) && OLEPOSSUtils.solid2(pos);
   }

   static boolean isHole(class_2338 pos, int depth, boolean floor) {
      if (floor && !isBlock(pos.method_10074())) {
         return false;
      } else {
         for(int i = 0; i < depth; ++i) {
            if (isBlock(pos.method_10086(i))) {
               return false;
            }
         }

         return true;
      }
   }

   public static Hole currentHole(class_2338 pos) {
      Hole middle = getHole(pos, 1);
      if (middle.type != HoleType.NotHole) {
         return middle;
      } else {
         Hole x = getHole(pos.method_10069(-1, 0, 0), 1);
         if (x.type != HoleType.NotHole) {
            return x;
         } else {
            Hole z = getHole(pos.method_10069(0, 0, -1), 1);
            if (z.type != HoleType.NotHole) {
               return z;
            } else {
               Hole xz = getHole(pos.method_10069(-1, 0, -1), 1);
               return xz.type != HoleType.NotHole ? xz : new Hole(pos, HoleType.NotHole);
            }
         }
      }
   }

   public static boolean inHole(class_2338 pos) {
      return currentHole(pos).type != HoleType.NotHole;
   }
}
