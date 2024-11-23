package bodevelopment.client.blackout.util;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.randomstuff.Rotation;
import java.util.function.Consumer;
import net.minecraft.class_243;
import net.minecraft.class_3532;

public class ProjectileUtils {
   private static double[] hitPos;

   public static Rotation calcShootingRotation(class_243 from, class_243 to, double speed, boolean playerVelocity, Consumer<double[]> velocityUpdate) {
      class_243 interpolated = to.method_1020(from).method_1031(0.0D, 0.1D, 0.0D);
      double min = -180.0D;
      double max = 180.0D;
      double pitch = 0.0D;
      double yawTo = getYaw(interpolated.field_1352, interpolated.field_1350);

      for(int i = 0; i < 100; ++i) {
         double middle = (min + max) / 2.0D;
         double yaw = yawTo + middle;
         pitch = getPitch(interpolated, yaw, speed, playerVelocity, velocityUpdate);
         if (hitPos != null) {
            double yawOffset = RotationUtils.yawAngle(getYaw(hitPos[0], hitPos[2]), yawTo);
            if (yawOffset > 0.0D) {
               min = middle;
            } else {
               max = middle;
            }
         }
      }

      return new Rotation((float)(yawTo + (min + max) / 2.0D), (float)pitch);
   }

   private static double getYaw(double x, double z) {
      return class_3532.method_15338(Math.toDegrees(Math.atan2(z, x)) - 90.0D);
   }

   private static double getPitch(class_243 to, double yaw, double speed, boolean playerVelocity, Consumer<double[]> velocityUpdate) {
      double min = -90.0D;
      double max = 90.0D;

      for(int i = 0; i < 100; ++i) {
         double middle = (min + max) / 2.0D;
         double[] hitPos = calcAngleHitPos(to, getShootingVelocity(yaw, middle, speed, playerVelocity), velocityUpdate);
         if (hitPos != null) {
            if (hitPos[1] > to.field_1351) {
               min = middle;
            } else {
               max = middle;
            }
         }
      }

      double middle = (min + max) / 2.0D;
      ProjectileUtils.hitPos = calcAngleHitPos(to, getShootingVelocity(yaw, middle, speed, playerVelocity), velocityUpdate);
      return middle;
   }

   private static double[] calcAngleHitPos(class_243 to, class_243 velocity, Consumer<double[]> velocityUpdate) {
      double[] vel = new double[]{velocity.field_1352, velocity.field_1351, velocity.field_1350};
      double distToTarget = to.method_37268();
      double x = 0.0D;
      double y = 0.0D;
      double z = 0.0D;

      for(int i = 0; i < 100; ++i) {
         velocityUpdate.accept(vel);
         x += vel[0];
         y += vel[1];
         z += vel[2];
         double dist = horizontalDistSq(x, z);
         if (!(dist < distToTarget)) {
            return new double[]{x, y, z};
         }
      }

      return null;
   }

   private static double horizontalDistSq(double x, double z) {
      return x * x + z * z;
   }

   public static class_243 getShootingVelocity(double yaw, double pitch, double speed, boolean playerVelocity) {
      class_243 vec = RotationUtils.rotationVec(yaw, pitch, speed);
      if (playerVelocity) {
         class_243 velocity = BlackOut.mc.field_1724.method_18798();
         vec = vec.method_1031(velocity.field_1352, BlackOut.mc.field_1724.method_24828() ? 0.0D : velocity.field_1351, velocity.field_1350);
      }

      return vec;
   }
}
