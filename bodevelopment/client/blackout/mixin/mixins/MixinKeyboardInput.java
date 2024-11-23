package bodevelopment.client.blackout.mixin.mixins;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.modules.movement.ElytraFly;
import bodevelopment.client.blackout.util.SettingUtils;
import net.minecraft.class_315;
import net.minecraft.class_3532;
import net.minecraft.class_743;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_743.class})
public abstract class MixinKeyboardInput {
   @Shadow
   @Final
   private class_315 field_3902;
   @Unique
   private boolean move = false;
   @Unique
   private int offset = 0;
   @Unique
   private boolean grim = false;

   @Inject(
      method = {"tick"},
      at = {@At("HEAD")}
   )
   private void onMovement(boolean slowDown, float slowDownFactor, CallbackInfo ci) {
      Managers.ROTATION.updateNext();
      Managers.ROTATION.moveLookYaw = class_3532.method_15393(Managers.ROTATION.nextYaw);
      this.grim = SettingUtils.grimMovement();
      float forward = this.getInput(this.field_3902.field_1894.method_1434(), this.field_3902.field_1881.method_1434());
      float strafing = this.getInput(this.field_3902.field_1913.method_1434(), this.field_3902.field_1849.method_1434());
      float yaw = this.inputYaw(forward, strafing);
      this.offset = Managers.ROTATION.updateMove(yaw, this.move);
   }

   @Redirect(
      method = {"tick"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/input/KeyboardInput;getMovementMultiplier(ZZ)F",
   ordinal = 0
)
   )
   private float movementForward(boolean positive, boolean negative) {
      ElytraFly elytraFly = ElytraFly.getInstance();
      if (elytraFly.enabled && elytraFly.isBouncing()) {
         return 1.0F;
      } else {
         float forward = this.getInput(positive, negative);
         if (this.grim && this.move) {
            float var10000;
            switch(this.offset) {
            case 0:
            case 1:
            case 7:
               var10000 = 1.0F;
               break;
            case 2:
            case 6:
               var10000 = 0.0F;
               break;
            case 3:
            case 4:
            case 5:
               var10000 = -1.0F;
               break;
            default:
               throw new IllegalStateException("Unexpected value in grim forward calculations: offset:" + this.offset + ", dir:" + Managers.ROTATION.moveLookYaw);
            }

            return var10000;
         } else {
            return forward;
         }
      }
   }

   @Redirect(
      method = {"tick"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/input/KeyboardInput;getMovementMultiplier(ZZ)F",
   ordinal = 1
)
   )
   private float movementStrafing(boolean positive, boolean negative) {
      ElytraFly elytraFly = ElytraFly.getInstance();
      if (elytraFly.enabled && elytraFly.isBouncing()) {
         return 0.0F;
      } else {
         float strafing = this.getInput(positive, negative);
         if (this.grim && this.move) {
            float var10000;
            switch(this.offset) {
            case 0:
            case 4:
               var10000 = 0.0F;
               break;
            case 1:
            case 2:
            case 3:
               var10000 = -1.0F;
               break;
            case 5:
            case 6:
            case 7:
               var10000 = 1.0F;
               break;
            default:
               throw new IllegalStateException("Unexpected value in grim strafing calculations: offset:" + this.offset + ", dir:" + Managers.ROTATION.moveLookYaw);
            }

            return var10000;
         } else {
            return strafing;
         }
      }
   }

   @Unique
   private float inputYaw(float forward, float strafing) {
      float yaw = BlackOut.mc.field_1724.method_36454();
      if (forward > 0.0F) {
         this.move = true;
         yaw += strafing > 0.0F ? -45.0F : (strafing < 0.0F ? 45.0F : 0.0F);
      } else if (forward < 0.0F) {
         this.move = true;
         yaw += strafing > 0.0F ? -135.0F : (strafing < 0.0F ? 135.0F : 180.0F);
      } else {
         this.move = strafing != 0.0F;
         yaw += strafing > 0.0F ? -90.0F : (strafing < 0.0F ? 90.0F : 0.0F);
      }

      return yaw;
   }

   @Unique
   private float getInput(boolean positive, boolean negative) {
      if (positive == negative) {
         return 0.0F;
      } else {
         return positive ? 1.0F : -1.0F;
      }
   }
}
