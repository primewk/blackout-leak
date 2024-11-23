package bodevelopment.client.blackout.mixin.mixins;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.module.modules.movement.Velocity;
import net.minecraft.class_1297;
import net.minecraft.class_1536;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_1536.class})
public class MixinFishingBobberEntity {
   @Inject(
      method = {"pullHookedEntity"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onPull(class_1297 entity, CallbackInfo ci) {
      if (entity == BlackOut.mc.field_1724) {
         Velocity velocity = Velocity.getInstance();
         if (velocity.enabled && (Boolean)velocity.fishingHook.get()) {
            ci.cancel();
         }

      }
   }
}
