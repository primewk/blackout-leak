package bodevelopment.client.blackout.mixin.mixins;

import bodevelopment.client.blackout.interfaces.mixin.IRenderTickCounter;
import net.minecraft.class_317;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({class_317.class})
public class MixinRenderTickCounter implements IRenderTickCounter {
   @Mutable
   @Shadow
   @Final
   private float field_1968;

   public void blackout_Client$set(float time) {
      this.field_1968 = time;
   }
}
