package bodevelopment.client.blackout.mixin.mixins;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.module.modules.misc.Streamer;
import net.minecraft.class_640;
import net.minecraft.class_742;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({class_742.class})
public abstract class MixinAbstractClientPlayerEntity {
   @Shadow
   @Nullable
   protected abstract class_640 method_3123();

   @Redirect(
      method = {"getSkinTextures"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;getPlayerListEntry()Lnet/minecraft/client/network/PlayerListEntry;"
)
   )
   private class_640 getEntry(class_742 instance) {
      if (instance == BlackOut.mc.field_1724) {
         Streamer streamer = Streamer.getInstance();
         if (streamer.enabled && (Boolean)streamer.skin.get()) {
            return null;
         }
      }

      return this.method_3123();
   }
}
