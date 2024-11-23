package bodevelopment.client.blackout.randomstuff;

import net.minecraft.class_243;
import net.minecraft.class_2350.class_2351;

public class MotionData {
   public class_243 motion;
   public double yawDiff = 0.0D;
   public boolean reset = false;

   public MotionData(class_243 motion) {
      this.motion = motion;
   }

   public static MotionData of(class_243 motion) {
      return new MotionData(motion);
   }

   public MotionData yaw(double yawDiff) {
      this.yawDiff = yawDiff;
      return this;
   }

   public MotionData reset() {
      this.reset = true;
      return this;
   }

   public MotionData y(double y) {
      this.motion = this.motion.method_38499(class_2351.field_11052, y);
      return this;
   }
}
