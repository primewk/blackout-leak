package bodevelopment.client.blackout.mixin.mixins;

import bodevelopment.client.blackout.module.modules.visual.world.Brightness;
import net.minecraft.class_2874;
import net.minecraft.class_765;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_765.class})
public class MixinLightmapTextureManager {
   @Inject(
      method = {"getBrightness"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void getBrightnessLevel(class_2874 type, int lightLevel, CallbackInfoReturnable<Float> cir) {
      Brightness brightness = Brightness.getInstance();
      if (brightness.enabled && brightness.mode.get() == Brightness.Mode.Gamma) {
         cir.setReturnValue(1.0E7F);
      }
   }
}
