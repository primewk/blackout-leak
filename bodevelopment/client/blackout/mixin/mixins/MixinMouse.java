package bodevelopment.client.blackout.mixin.mixins;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.events.MouseButtonEvent;
import bodevelopment.client.blackout.event.events.MouseScrollEvent;
import bodevelopment.client.blackout.keys.MouseButtons;
import bodevelopment.client.blackout.util.SharedFeatures;
import net.minecraft.class_304;
import net.minecraft.class_310;
import net.minecraft.class_312;
import net.minecraft.class_3675;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_312.class})
public abstract class MixinMouse {
   @Shadow
   private boolean field_1783;
   @Shadow
   private double field_1795;
   @Shadow
   private double field_1794;
   @Shadow
   private boolean field_1784;

   @Shadow
   public abstract void method_1612();

   @Inject(
      method = {"onMouseButton"},
      at = {@At("HEAD")}
   )
   private void onClick(long window, int button, int action, int mods, CallbackInfo ci) {
      BlackOut.EVENT_BUS.post(MouseButtonEvent.get(button, action == 1));
      MouseButtons.set(button, action == 1);
   }

   @Inject(
      method = {"onMouseScroll"},
      at = {@At("HEAD")}
   )
   private void onScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
      BlackOut.EVENT_BUS.post(MouseScrollEvent.get(horizontal, vertical));
   }

   @Inject(
      method = {"isCursorLocked"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void locked(CallbackInfoReturnable<Boolean> cir) {
      if (SharedFeatures.shouldSilentScreen()) {
         this.lockWithoutClose();
         cir.setReturnValue(true);
      }

   }

   @Unique
   private void lockWithoutClose() {
      if (BlackOut.mc.method_1569()) {
         if (!this.field_1783) {
            if (!class_310.field_1703) {
               class_304.method_1424();
            }

            this.field_1783 = true;
            this.field_1784 = true;
            this.field_1795 = (double)BlackOut.mc.method_22683().method_4480() / 2.0D;
            this.field_1794 = (double)BlackOut.mc.method_22683().method_4507() / 2.0D;
            class_3675.method_15984(BlackOut.mc.method_22683().method_4490(), 212995, this.field_1795, this.field_1794);
         }
      }
   }
}
