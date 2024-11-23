package bodevelopment.client.blackout.mixin.mixins;

import bodevelopment.client.blackout.module.modules.visual.entities.Nametags;
import bodevelopment.client.blackout.module.modules.visual.entities.ShaderESP;
import net.minecraft.class_1297;
import net.minecraft.class_2561;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_897;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_897.class})
public class MixinEntityRenderer {
   @Inject(
      method = {"renderLabelIfPresent"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private <T extends class_1297> void shouldRenderNametag(T entity, class_2561 text, class_4587 matrices, class_4597 vertexConsumers, int light, CallbackInfo ci) {
      if (!ShaderESP.ignore && this.shouldCancel(entity)) {
         ci.cancel();
      }

   }

   @Unique
   private boolean shouldCancel(class_1297 entity) {
      if (Nametags.shouldCancelLabel(entity)) {
         return true;
      } else {
         ShaderESP shaderESP = ShaderESP.getInstance();
         return shaderESP.enabled && shaderESP.shouldRender(entity);
      }
   }
}
