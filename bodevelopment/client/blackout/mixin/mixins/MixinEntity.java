package bodevelopment.client.blackout.mixin.mixins;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.events.MoveEvent;
import bodevelopment.client.blackout.event.events.RemoveEvent;
import bodevelopment.client.blackout.interfaces.mixin.IVec3d;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.modules.legit.HitCrystal;
import bodevelopment.client.blackout.module.modules.misc.Timer;
import bodevelopment.client.blackout.module.modules.movement.CollisionShrink;
import bodevelopment.client.blackout.module.modules.movement.Step;
import bodevelopment.client.blackout.module.modules.movement.TargetStrafe;
import bodevelopment.client.blackout.module.modules.movement.Velocity;
import bodevelopment.client.blackout.util.SettingUtils;
import java.util.List;
import net.minecraft.class_1297;
import net.minecraft.class_1313;
import net.minecraft.class_1937;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2487;
import net.minecraft.class_265;
import net.minecraft.class_1297.class_5529;
import net.minecraft.class_2350.class_2351;
import net.minecraft.class_2828.class_2829;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_1297.class})
public abstract class MixinEntity {
   @Shadow
   public abstract class_238 method_5829();

   @Shadow
   public abstract class_1937 method_37908();

   @Shadow
   public abstract boolean method_24828();

   @Shadow
   public abstract float method_49476();

   @Shadow
   public abstract void method_5651(class_2487 var1);

   @Inject(
      method = {"move"},
      at = {@At("HEAD")}
   )
   private void onMove(class_1313 movementType, class_243 movement, CallbackInfo ci) {
      if (this == BlackOut.mc.field_1724) {
         BlackOut.EVENT_BUS.post(MoveEvent.Pre.get(movement, movementType));
         TargetStrafe strafe = TargetStrafe.getInstance();
         if (strafe.enabled) {
            strafe.onMove(movement);
         }
      }

   }

   @Inject(
      method = {"move"},
      at = {@At("TAIL")}
   )
   private void onMovePost(class_1313 movementType, class_243 movement, CallbackInfo ci) {
      if (this == BlackOut.mc.field_1724) {
         BlackOut.EVENT_BUS.post(MoveEvent.Post.get());
      }

      if (SettingUtils.grimPackets()) {
         HitCrystal.getInstance().onTick();
      }

   }

   @Redirect(
      method = {"updateVelocity"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/entity/Entity;getYaw()F"
)
   )
   private float getMoveYaw(class_1297 instance) {
      return this == BlackOut.mc.field_1724 && SettingUtils.grimMovement() ? Managers.ROTATION.moveLookYaw : instance.method_36454();
   }

   @Inject(
      method = {"adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void doStepStuff(class_243 movement, CallbackInfoReturnable<class_243> cir) {
      if (this == BlackOut.mc.field_1724) {
         Step step = Step.getInstance();
         if (step.isActive()) {
            cir.setReturnValue(this.getStep(step, movement));
            cir.cancel();
         }
      }
   }

   @Redirect(
      method = {"adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/entity/Entity;getBoundingBox()Lnet/minecraft/util/math/Box;"
)
   )
   private class_238 box(class_1297 instance) {
      return this.getBox();
   }

   @Unique
   private class_243 getStep(Step step, class_243 movement) {
      if (step.stepProgress > -1 && (Boolean)step.slow.get() && step.offsets != null) {
         if (movement.method_37268() <= 0.0D) {
            ((IVec3d)movement).blackout_Client$setXZ(step.prevMovement.field_1352, step.prevMovement.field_1350);
         }

         step.prevMovement = movement.method_1021(1.0D);
      }

      class_1297 entity = (class_1297)this;
      class_238 box = this.getBox();
      List<class_265> list = this.method_37908().method_20743(entity, box.method_18804(movement));
      class_243 vec3d = movement.method_1027() == 0.0D ? movement : class_1297.method_20736(entity, movement, box, this.method_37908(), list);
      boolean collidedX = movement.field_1352 != vec3d.field_1352;
      boolean collidedY = movement.field_1351 != vec3d.field_1351;
      boolean collidedZ = movement.field_1350 != vec3d.field_1350;
      boolean collidedHorizontally = collidedX || collidedZ;
      boolean bl4 = this.method_24828() || collidedY && movement.field_1351 < 0.0D;
      double vanillaHeight = step.stepMode.get() == Step.StepMode.Vanilla ? (Double)step.height.get() : (double)this.method_49476();
      class_243 stepMovement = class_1297.method_20736(entity, new class_243(movement.field_1352, vanillaHeight, movement.field_1350), box, this.method_37908(), list);
      class_243 stepMovementUp = class_1297.method_20736(entity, new class_243(0.0D, vanillaHeight, 0.0D), box.method_1012(movement.field_1352, 0.0D, movement.field_1350), this.method_37908(), list);
      if (vanillaHeight > 0.0D && bl4 && collidedHorizontally && (!(Boolean)step.slow.get() || step.stepProgress < 0)) {
         if (stepMovementUp.field_1351 < vanillaHeight) {
            class_243 vec3d4 = class_1297.method_20736(entity, new class_243(movement.field_1352, 0.0D, movement.field_1350), box.method_997(stepMovementUp), this.method_37908(), list).method_1019(stepMovementUp);
            if (vec3d4.method_37268() > stepMovement.method_37268()) {
               stepMovement = vec3d4;
            }
         }

         if (stepMovement.method_37268() > vec3d.method_37268()) {
            return stepMovement.method_1019(class_1297.method_20736(entity, new class_243(0.0D, -stepMovement.field_1351 + movement.field_1351, 0.0D), box.method_997(stepMovement), this.method_37908(), list));
         }
      }

      double height = (Double)step.height.get();
      stepMovement = class_1297.method_20736(entity, new class_243(movement.field_1352, height, movement.field_1350), box, this.method_37908(), list);
      stepMovementUp = class_1297.method_20736(entity, new class_243(0.0D, height, 0.0D), box.method_1012(movement.field_1352, 0.0D, movement.field_1350), this.method_37908(), list);
      if (height > 0.0D && entity.method_24828() && collidedHorizontally && (!(Boolean)step.slow.get() || step.stepProgress < 0 || step.offsets == null) && step.cooldownCheck()) {
         class_243 vec3d3;
         if (stepMovementUp.field_1351 < height) {
            vec3d3 = class_1297.method_20736(entity, new class_243(movement.field_1352, 0.0D, movement.field_1350), box.method_997(stepMovementUp), this.method_37908(), list).method_1019(stepMovementUp);
            if (vec3d3.method_37268() > stepMovement.method_37268()) {
               stepMovement = vec3d3;
            }
         }

         if (stepMovement.method_37268() > vec3d.method_37268()) {
            vec3d3 = stepMovement.method_1019(class_1297.method_20736(entity, new class_243(0.0D, -stepMovement.field_1351 + movement.field_1351, 0.0D), box.method_997(stepMovement), this.method_37908(), list));
            step.start(vec3d3.field_1351);
            if (step.offsets != null) {
               step.lastStep = System.currentTimeMillis();
               if (!(Boolean)step.slow.get()) {
                  double y = 0.0D;
                  double[] var21 = step.offsets;
                  int var22 = var21.length;

                  for(int var23 = 0; var23 < var22; ++var23) {
                     double offset = var21[var23];
                     y += offset;
                     BlackOut.mc.method_1562().method_52787(new class_2829(BlackOut.mc.field_1724.method_23317(), BlackOut.mc.field_1724.method_23318() + y, BlackOut.mc.field_1724.method_23321(), false));
                  }

                  return vec3d3;
               }

               step.stepProgress = 0;
            }
         }
      }

      if (step.stepProgress > -1 && (Boolean)step.slow.get() && step.offsets != null) {
         step.sinceStep = 0;
         if ((Boolean)step.useTimer.get()) {
            Timer.set(((Double)step.timer.get()).floatValue());
            step.shouldResetTimer = true;
         }

         double h;
         if (step.stepProgress < step.offsets.length) {
            h = step.offsets[step.stepProgress];
            ++step.stepProgress;
            stepMovement = class_1297.method_20736(entity, new class_243(movement.field_1352, 0.0D, movement.field_1350), box, this.method_37908(), list);
         } else {
            class_243 m;
            if (step.stepMode.get() == Step.StepMode.UpdatedNCP) {
               if (step.stepProgress == step.offsets.length) {
                  ++step.stepProgress;
                  h = step.lastSlow;
               } else {
                  h = 0.0D;
                  step.stepProgress = -1;
                  step.offsets = null;
               }

               m = new class_243(0.0D, 0.0D, 0.0D);
            } else {
               h = step.lastSlow;
               step.stepProgress = -1;
               step.offsets = null;
               m = movement.method_38499(class_2351.field_11052, 0.0D);
            }

            stepMovement = class_1297.method_20736(entity, m, box, this.method_37908(), list);
         }

         return stepMovement.method_1019(class_1297.method_20736(entity, new class_243(0.0D, h, 0.0D), box.method_997(stepMovement), this.method_37908(), list));
      } else {
         if (step.shouldResetTimer) {
            step.stepProgress = -1;
            step.offsets = null;
            Timer.reset();
            step.shouldResetTimer = false;
         }

         return vec3d;
      }
   }

   @Unique
   private class_238 getBox() {
      CollisionShrink shrink = CollisionShrink.getInstance();
      return shrink.enabled ? shrink.getBox(this.method_5829()) : this.method_5829();
   }

   @Inject(
      method = {"pushAwayFrom"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void pushAwayFromEntities(class_1297 entity, CallbackInfo ci) {
      if (this == BlackOut.mc.field_1724) {
         Velocity velocity = Velocity.getInstance();
         if (velocity.enabled && velocity.entityPush.get() != Velocity.PushMode.Disabled) {
            ci.cancel();
         }

      }
   }

   @Inject(
      method = {"setRemoved"},
      at = {@At("HEAD")}
   )
   private void onRemove(class_5529 reason, CallbackInfo ci) {
      BlackOut.EVENT_BUS.post(RemoveEvent.get((class_1297)this, reason));
   }
}
