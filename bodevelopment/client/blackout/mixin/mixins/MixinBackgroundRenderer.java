package bodevelopment.client.blackout.mixin.mixins;

import bodevelopment.client.blackout.module.modules.visual.world.Ambience;
import bodevelopment.client.blackout.randomstuff.BlackOutColor;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.class_4184;
import net.minecraft.class_758;
import net.minecraft.class_758.class_4596;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_758.class})
public class MixinBackgroundRenderer {
   @Shadow
   private static float field_4034;
   @Shadow
   private static float field_4033;
   @Shadow
   private static float field_4032;

   @Redirect(
      method = {"render"},
      at = @At(
   value = "INVOKE",
   target = "Lcom/mojang/blaze3d/systems/RenderSystem;clearColor(FFFF)V"
)
   )
   private static void redirectColor(float r, float g, float b, float a) {
      Ambience ambience = Ambience.getInstance();
      if (ambience.enabled && (Boolean)ambience.modifyFog.get() && (Boolean)ambience.thickFog.get() && !(Boolean)ambience.removeFog.get()) {
         BlackOutColor color = (BlackOutColor)ambience.color.get();
         field_4034 = (float)color.red / 255.0F;
         field_4033 = (float)color.green / 255.0F;
         field_4032 = (float)color.blue / 255.0F;
      }

      RenderSystem.clearColor(field_4034, field_4033, field_4032, 0.0F);
   }

   @Inject(
      method = {"applyFog"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void applyFog(class_4184 camera, class_4596 fogType, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo info) {
      Ambience ambience = Ambience.getInstance();
      if (ambience != null && ambience.enabled && ambience.modifyFog(fogType == class_4596.field_20946)) {
         info.cancel();
      }

   }
}
