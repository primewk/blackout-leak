package bodevelopment.client.blackout.mixin.mixins;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.events.EntityAddEvent;
import bodevelopment.client.blackout.module.modules.visual.world.Ambience;
import net.minecraft.class_1297;
import net.minecraft.class_638;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_638.class})
public abstract class MixinClientWorld {
   @Shadow
   public abstract void method_29089(long var1);

   @Shadow
   public abstract void method_8435(long var1);

   @Inject(
      method = {"addEntity"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void preAddEntity(class_1297 entity, CallbackInfo ci) {
      if (((EntityAddEvent.Pre)BlackOut.EVENT_BUS.post(EntityAddEvent.Pre.get(entity.method_5628(), entity))).isCancelled()) {
         ci.cancel();
      }

   }

   @Inject(
      method = {"addEntity"},
      at = {@At("TAIL")}
   )
   private void postAddEntity(class_1297 entity, CallbackInfo ci) {
      BlackOut.EVENT_BUS.post(EntityAddEvent.Post.get(entity.method_5628(), entity));
   }

   @Redirect(
      method = {"tickTime"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/world/ClientWorld;setTimeOfDay(J)V"
)
   )
   private void tickGameTime(class_638 instance, long timeOfDay) {
      Ambience ambience = Ambience.getInstance();
      if (ambience.enabled && (Boolean)ambience.modifyTime.get()) {
         this.method_8435((long)(Integer)ambience.time.get());
      } else {
         instance.method_8435(timeOfDay);
      }

   }
}
