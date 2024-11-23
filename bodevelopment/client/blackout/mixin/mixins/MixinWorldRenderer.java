package bodevelopment.client.blackout.mixin.mixins;

import bodevelopment.client.blackout.module.modules.visual.misc.Freecam;
import bodevelopment.client.blackout.module.modules.visual.world.Ambience;
import net.minecraft.class_4184;
import net.minecraft.class_4587;
import net.minecraft.class_761;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_761.class})
public class MixinWorldRenderer {
   @Redirect(
      method = {"render"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/render/Camera;isThirdPerson()Z"
)
   )
   private boolean ignoreRender(class_4184 instance) {
      return Freecam.getInstance().enabled || instance.method_19333();
   }

   @Inject(
      method = {"renderSky(Lnet/minecraft/client/util/math/MatrixStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/render/Camera;ZLjava/lang/Runnable;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onRenderSky(class_4587 matrices, Matrix4f projectionMatrix, float tickDelta, class_4184 camera, boolean thickFog, Runnable fogCallback, CallbackInfo ci) {
      Ambience ambience = Ambience.getInstance();
      if (ambience.enabled && (Boolean)ambience.thickFog.get() && !(Boolean)ambience.removeFog.get()) {
         fogCallback.run();
         ci.cancel();
      }

   }
}
