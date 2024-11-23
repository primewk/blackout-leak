package bodevelopment.client.blackout.randomstuff;

import bodevelopment.client.blackout.enums.HoleType;
import net.minecraft.class_2338;
import net.minecraft.class_243;

public class Hole {
   public static final class_2338[] POSITIONS = new class_2338[0];
   public final class_2338 pos;
   public final HoleType type;
   public final class_2338[] positions;
   public final class_243 middle;

   public Hole(class_2338 pos, HoleType type) {
      this.pos = pos;
      this.type = type;
      switch(type) {
      case Single:
         this.positions = new class_2338[]{pos};
         this.middle = new class_243((double)pos.method_10263() + 0.5D, (double)pos.method_10264(), (double)pos.method_10260() + 0.5D);
         break;
      case DoubleX:
         this.positions = new class_2338[]{pos, pos.method_10069(1, 0, 0)};
         this.middle = new class_243((double)(pos.method_10263() + 1), (double)pos.method_10264(), (double)pos.method_10260() + 0.5D);
         break;
      case DoubleZ:
         this.positions = new class_2338[]{pos, pos.method_10069(0, 0, 1)};
         this.middle = new class_243((double)pos.method_10263() + 0.5D, (double)pos.method_10264(), (double)(pos.method_10260() + 1));
         break;
      case Quad:
         this.positions = new class_2338[]{pos, pos.method_10069(1, 0, 0), pos.method_10069(0, 0, 1), pos.method_10069(1, 0, 1)};
         this.middle = new class_243((double)(pos.method_10263() + 1), (double)pos.method_10264(), (double)(pos.method_10260() + 1));
         break;
      default:
         this.positions = POSITIONS;
         this.middle = new class_243((double)pos.method_10263() + 0.5D, (double)pos.method_10264(), (double)pos.method_10260() + 0.5D);
      }

   }
}
