package bodevelopment.client.blackout.mixin.mixins;

import bodevelopment.client.blackout.module.modules.visual.entities.PlayerModifier;
import net.minecraft.class_1007;
import net.minecraft.class_742;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({class_1007.class})
public class MixinPlayerEntityRenderer {
   @Redirect(
      method = {"setModelPose"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;isInSneakingPose()Z"
)
   )
   private boolean sneak(class_742 instance) {
      PlayerModifier modifier = PlayerModifier.getInstance();
      return modifier.enabled && (Boolean)modifier.forceSneak.get() || instance.method_18276();
   }
}
