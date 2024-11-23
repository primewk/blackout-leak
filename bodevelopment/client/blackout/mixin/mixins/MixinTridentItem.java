package bodevelopment.client.blackout.mixin.mixins;

import bodevelopment.client.blackout.module.modules.movement.FastRiptide;
import net.minecraft.class_1309;
import net.minecraft.class_1799;
import net.minecraft.class_1835;
import net.minecraft.class_1937;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin({class_1835.class})
public abstract class MixinTridentItem {
   @Shadow
   public abstract void method_7840(class_1799 var1, class_1937 var2, class_1309 var3, int var4);

   @ModifyVariable(
      method = {"onStoppedUsing"},
      at = @At("HEAD"),
      index = 4,
      argsOnly = true
   )
   private int preUse(int value) {
      FastRiptide fastRiptide = FastRiptide.getInstance();
      if (!fastRiptide.enabled) {
         return value;
      } else if ((double)(System.currentTimeMillis() - fastRiptide.prevRiptide) < (Double)fastRiptide.cooldown.get() * 1000.0D) {
         return Integer.MAX_VALUE;
      } else {
         fastRiptide.prevRiptide = System.currentTimeMillis();
         return 0;
      }
   }
}
