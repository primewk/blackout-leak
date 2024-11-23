package bodevelopment.client.blackout.util;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.interfaces.mixin.IVec3d;
import net.minecraft.class_1294;
import net.minecraft.class_238;
import net.minecraft.class_243;

public class MovementUtils {
   public static double xMovement(double speed, double yaw) {
      return Math.cos(Math.toRadians(yaw + 90.0D)) * speed;
   }

   public static double zMovement(double speed, double yaw) {
      return Math.sin(Math.toRadians(yaw + 90.0D)) * speed;
   }

   public static double getSpeed(double baseSpeed) {
      return getSpeed(baseSpeed, 1.0D);
   }

   public static double getSpeed(double baseSpeed, double multi) {
      double effectMulti = getEffectMulti();
      if (BlackOut.mc.field_1724.method_5715()) {
         baseSpeed *= 0.3D;
         ++effectMulti;
      } else {
         effectMulti = 1.0D + effectMulti * multi;
      }

      return baseSpeed * effectMulti;
   }

   public static double getEffectMulti() {
      double multiBonus = 0.0D;
      if (BlackOut.mc.field_1724.method_6059(class_1294.field_5904)) {
         multiBonus += (double)BlackOut.mc.field_1724.method_6112(class_1294.field_5904).method_5578() * 0.2D + 0.2D;
      }

      if (BlackOut.mc.field_1724.method_6059(class_1294.field_5909)) {
         multiBonus -= (double)BlackOut.mc.field_1724.method_6112(class_1294.field_5909).method_5578() * 0.2D + 0.2D;
      }

      return multiBonus;
   }

   public static void moveTowards(class_243 movement, double baseSpeed, class_243 vec, int step, int reverseStep) {
      double speed = getSpeed(baseSpeed);
      double yaw = RotationUtils.getYaw(BlackOut.mc.field_1724.method_19538(), vec, 0.0D);
      double xm = xMovement(speed, yaw);
      double zm = zMovement(speed, yaw);
      double xd = vec.field_1352 - BlackOut.mc.field_1724.method_23317();
      double zd = vec.field_1350 - BlackOut.mc.field_1724.method_23321();
      double x = Math.abs(xm) <= Math.abs(xd) ? xm : xd;
      double z = Math.abs(zm) <= Math.abs(zd) ? zm : zd;
      y(movement, x, z, step, reverseStep);
      ((IVec3d)movement).blackout_Client$setXZ(x, z);
   }

   private static void y(class_243 movement, double x, double z, int step, int rev) {
      double s;
      if (BlackOut.mc.field_1724.method_24828() && !OLEPOSSUtils.inside(BlackOut.mc.field_1724, BlackOut.mc.field_1724.method_5829()) && OLEPOSSUtils.inside(BlackOut.mc.field_1724, BlackOut.mc.field_1724.method_5829().method_989(x, 0.0D, z))) {
         s = getStep(BlackOut.mc.field_1724.method_5829().method_989(x, 0.0D, z), step);
         if (s > 0.0D) {
            ((IVec3d)movement).blackout_Client$setY(s);
            BlackOut.mc.field_1724.method_18800(BlackOut.mc.field_1724.method_18798().field_1352, 0.0D, BlackOut.mc.field_1724.method_18798().field_1350);
         }

      } else {
         if (BlackOut.mc.field_1724.method_24828() && !OLEPOSSUtils.inside(BlackOut.mc.field_1724, BlackOut.mc.field_1724.method_5829().method_989(x, -0.04D, z))) {
            s = getReverse(BlackOut.mc.field_1724.method_5829(), rev);
            if (s > 0.0D) {
               ((IVec3d)movement).blackout_Client$setY(-s);
               BlackOut.mc.field_1724.method_18800(BlackOut.mc.field_1724.method_18798().field_1352, 0.0D, BlackOut.mc.field_1724.method_18798().field_1350);
            }
         }

      }
   }

   private static double getStep(class_238 box, int step) {
      for(double i = 0.0D; i <= (double)step + 0.125D; i += 0.125D) {
         if (!OLEPOSSUtils.inside(BlackOut.mc.field_1724, box.method_989(0.0D, i, 0.0D))) {
            return i;
         }
      }

      return 0.0D;
   }

   private static double getReverse(class_238 box, int reverse) {
      for(double i = 0.0D; i <= (double)reverse; i += 0.125D) {
         if (OLEPOSSUtils.inside(BlackOut.mc.field_1724, box.method_989(0.0D, -i - 0.125D, 0.0D))) {
            return i;
         }
      }

      return 0.0D;
   }
}
