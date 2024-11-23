package bodevelopment.client.blackout.util;

import bodevelopment.client.blackout.manager.managers.ExtrapolationManager;
import bodevelopment.client.blackout.randomstuff.MotionData;
import java.util.Iterator;
import java.util.List;
import net.minecraft.class_243;

public class HorizontalExtrapolation {
   public static MotionData getMotion(ExtrapolationManager.ExtrapolationData data) {
      List<class_243> motions = OLEPOSSUtils.reverse(data.motions);
      if (motions.size() < 5) {
         return MotionData.of(motions.isEmpty() ? new class_243(0.0D, 0.0D, 0.0D) : (class_243)motions.get(0));
      } else {
         double prev = motionYaw((class_243)motions.get(0));
         HorizontalExtrapolation.Yaw[] yaws = new HorizontalExtrapolation.Yaw[motions.size() - 1];

         double lastDiff;
         for(int i = 0; i < yaws.length; ++i) {
            class_243 motion = (class_243)motions.get(i + 1);
            lastDiff = motionYaw(motion);
            yaws[i] = new HorizontalExtrapolation.Yaw(lastDiff, lastDiff - prev, motion.method_37267());
            prev = lastDiff;
         }

         double avg = avgDiff(yaws);
         lastDiff = yaws[3].diff();
         if (Math.abs(lastDiff) > 115.0D && Math.abs(avg) > 10.0D) {
            class_243 average = averageMotion(motions);
            return average.method_37267() > 0.15D ? MotionData.of(averageMotion(motions).method_1021(0.0D)).reset() : MotionData.of(averageMotion(motions).method_1021(0.0D));
         } else {
            return MotionData.of(averageMotion(motions));
         }
      }
   }

   private static class_243 averageMotion(List<class_243> motions) {
      class_243 total = new class_243(0.0D, 0.0D, 0.0D);

      class_243 motion;
      for(Iterator var2 = motions.iterator(); var2.hasNext(); total = total.method_1019(motion)) {
         motion = (class_243)var2.next();
      }

      return total.method_18805((double)(1.0F / (float)motions.size()), 0.0D, (double)(1.0F / (float)motions.size()));
   }

   private static double avgDiff(HorizontalExtrapolation.Yaw[] yaws) {
      double avg = 0.0D;
      HorizontalExtrapolation.Yaw[] var3 = yaws;
      int var4 = yaws.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         HorizontalExtrapolation.Yaw yaw = var3[var5];
         avg += yaw.diff() / (double)yaws.length;
      }

      return avg;
   }

   private static double motionYaw(class_243 motion) {
      return RotationUtils.getYaw(class_243.field_1353, motion, 0.0D);
   }

   private static record Yaw(double yaw, double diff, double length) {
      private Yaw(double yaw, double diff, double length) {
         this.yaw = yaw;
         this.diff = diff;
         this.length = length;
      }

      public double yaw() {
         return this.yaw;
      }

      public double diff() {
         return this.diff;
      }

      public double length() {
         return this.length;
      }
   }
}
