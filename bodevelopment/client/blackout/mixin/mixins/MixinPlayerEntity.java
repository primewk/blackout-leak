package bodevelopment.client.blackout.mixin.mixins;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.module.modules.misc.AntiPose;
import bodevelopment.client.blackout.module.modules.movement.SafeWalk;
import net.minecraft.class_1657;
import net.minecraft.class_4050;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({class_1657.class})
public abstract class MixinPlayerEntity {
   @Shadow
   protected abstract boolean method_52558(class_4050 var1);

   @Redirect(
      method = {"adjustMovementForSneaking"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/entity/player/PlayerEntity;clipAtLedge()Z"
)
   )
   private boolean sneakingThing(class_1657 instance) {
      return instance.method_5715() || this == BlackOut.mc.field_1724 && SafeWalk.shouldSafeWalk();
   }

   @Redirect(
      method = {"updatePose"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/entity/player/PlayerEntity;canChangeIntoPose(Lnet/minecraft/entity/EntityPose;)Z",
   ordinal = 1
)
   )
   private boolean canEnterPose(class_1657 instance, class_4050 pose) {
      return instance == BlackOut.mc.field_1724 && AntiPose.getInstance().enabled || this.method_52558(pose);
   }
}
