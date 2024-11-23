package bodevelopment.client.blackout.mixin.mixins;

import bodevelopment.client.blackout.module.modules.visual.misc.NoRender;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.class_1058;
import net.minecraft.class_310;
import net.minecraft.class_4587;
import net.minecraft.class_4603;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_4603.class})
public class MixinInGameOverlayRenderer {
   @Inject(
      method = {"renderInWallOverlay"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void onWallOverlay(class_1058 sprite, class_4587 matrices, CallbackInfo ci) {
      NoRender noRender = NoRender.getInstance();
      if (noRender.enabled && (Boolean)noRender.wallOverlay.get()) {
         ci.cancel();
      }

   }

   @Inject(
      method = {"renderUnderwaterOverlay"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void onWaterOverlay(class_310 client, class_4587 matrices, CallbackInfo ci) {
      NoRender noRender = NoRender.getInstance();
      if (noRender.enabled && (Boolean)noRender.waterOverlay.get()) {
         ci.cancel();
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.disableBlend();
      }

   }

   @Inject(
      method = {"renderFireOverlay"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void onFireOverlay(class_310 client, class_4587 matrices, CallbackInfo ci) {
      NoRender noRender = NoRender.getInstance();
      if (noRender.enabled && (Boolean)noRender.fireOverlay.get()) {
         ci.cancel();
         RenderSystem.disableBlend();
         RenderSystem.depthMask(true);
         RenderSystem.depthFunc(515);
      }

   }
}
