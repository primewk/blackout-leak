package bodevelopment.client.blackout.util;

import net.minecraft.class_1297;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_3532;

public class Simulator {
   private static final double MAX_WATER_SPEED = 0.197D;
   private static final double MAX_LAVA_SPEED = 0.0753D;

   public static class_238 extrapolate(SimulationContext ctx) {
      ctx.onGround = ctx.isOnGround();
      ctx.prevOnGround = ctx.onGround;

      for(int i = 0; i < ctx.ticks; ++i) {
         next(ctx, i);
      }

      return ctx.box;
   }

   private static void next(SimulationContext ctx, int i) {
      preMove(ctx);
      ctx.onTick.accept(ctx, i);
      handleMotion(ctx);
      handleCollisions(ctx);
      postMove(ctx);
      ctx.accept();
   }

   private static void handleMotion(SimulationContext ctx) {
      ctx.inWater = OLEPOSSUtils.inWater(ctx.box);
      ctx.inLava = OLEPOSSUtils.inLava(ctx.box);
      if (ctx.inFluid()) {
         ctx.jump = false;
      }

      if (!ctx.originalLava && ctx.inLava) {
         handleFluidMotion(ctx, 0.1D, 0.0753D);
      } else if (!ctx.originalWater && ctx.inWater) {
         handleFluidMotion(ctx, 0.04D, 0.197D);
      } else {
         handleRecover(ctx);
      }

   }

   private static void handleRecover(SimulationContext ctx) {
      approachMotionXZ(ctx, 0.05D, ctx.originalMotion.method_37267());
   }

   private static void handleFluidMotion(SimulationContext ctx, double xz, double targetXZ) {
      approachMotionXZ(ctx, xz, targetXZ);
   }

   private static void approachMotionXZ(SimulationContext ctx, double xz, double targetXZ) {
      double length = Math.sqrt(ctx.motionX * ctx.motionX + ctx.motionZ * ctx.motionZ);
      double next;
      if (targetXZ > length) {
         next = Math.min(length + xz, targetXZ);
      } else {
         next = Math.max(length - xz, targetXZ);
      }

      if (length <= 0.0D) {
         length = 0.03D;
      }

      double ratio = next / length;
      ctx.motionX *= ratio;
      ctx.motionZ *= ratio;
   }

   private static void preMove(SimulationContext ctx) {
      ctx.prevOnGround = ctx.onGround;
      ctx.onGround = ctx.isOnGround();
      if (ctx.onGround && ctx.jump && ctx.motionY <= 0.0D) {
         ctx.motionY = ctx.jumpHeight;
      } else if (ctx.onGround) {
         ctx.motionY = 0.0D;
      }

   }

   private static void postMove(SimulationContext ctx) {
      if (ctx.inFluid() && !ctx.inFluidOriginal()) {
         ctx.motionY = class_3532.method_15350(ctx.motionY, -0.0784D, 0.13D);
      }

      if (ctx.inFluidOriginal() && ctx.inFluid()) {
         ctx.motionY *= 0.99D;
      } else {
         ctx.motionY = (ctx.motionY - (ctx.inFluid() ? 0.005D : 0.08D)) * 0.98D;
      }

   }

   private static void handleCollisions(SimulationContext ctx) {
      ctx.updateCollisions();
      class_243 movement = new class_243(ctx.motionX, shouldReverse(ctx) ? -ctx.reverseStep : ctx.motionY, ctx.motionZ);
      class_243 collidedMovement = movement.method_1027() == 0.0D ? movement : ctx.collide(movement, ctx.box);
      boolean collidedHorizontally = collidedMovement.field_1352 != ctx.motionX || collidedMovement.field_1350 != ctx.motionZ;
      boolean collidingWithFloor = ctx.motionY < 0.0D && collidedMovement.field_1351 != ctx.motionY;
      if ((ctx.onGround || collidingWithFloor) && collidedHorizontally) {
         class_243 vec2 = ctx.collide(new class_243(ctx.motionX, ctx.step, ctx.motionZ), ctx.box);
         class_243 vec3 = ctx.collide(new class_243(0.0D, ctx.step, 0.0D), ctx.box.method_1012(ctx.motionX, 0.0D, ctx.motionZ));
         class_243 vec;
         if (vec3.field_1351 < ctx.step) {
            vec = ctx.collide(new class_243(movement.field_1352, 0.0D, movement.field_1350), ctx.box.method_997(vec3)).method_1019(vec3);
            if (vec.method_37268() > vec2.method_37268()) {
               vec2 = vec;
            }
         }

         if (vec2.method_37268() > collidedMovement.method_37268()) {
            vec = vec2.method_1019(ctx.collide(new class_243(0.0D, -vec2.field_1351 + movement.field_1351, 0.0D), ctx.box.method_997(vec2)));
            ctx.move(vec);
            ctx.setOnGround(true);
            return;
         }
      }

      ctx.move(collidedMovement);
   }

   private static boolean shouldReverse(SimulationContext ctx) {
      return ctx.reverseStep > 0.0D && ctx.prevOnGround && !ctx.onGround && ctx.motionY <= 0.0D && OLEPOSSUtils.inside(ctx.entity, ctx.box.method_1012(0.0D, -ctx.reverseStep, 0.0D));
   }

   public static boolean isOnGround(class_1297 entity, class_238 box) {
      return OLEPOSSUtils.inside(entity, box.method_1012(0.0D, -0.02D, 0.0D));
   }
}
