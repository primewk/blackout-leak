package bodevelopment.client.blackout.randomstuff;

import net.minecraft.class_2338;
import net.minecraft.class_2350;

public record PlaceData(class_2338 pos, class_2350 dir, boolean valid, boolean sneak) {
   public PlaceData(class_2338 pos, class_2350 dir, boolean valid, boolean sneak) {
      this.pos = pos;
      this.dir = dir;
      this.valid = valid;
      this.sneak = sneak;
   }

   public class_2338 pos() {
      return this.pos;
   }

   public class_2350 dir() {
      return this.dir;
   }

   public boolean valid() {
      return this.valid;
   }

   public boolean sneak() {
      return this.sneak;
   }
}
